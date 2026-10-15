// Mixture: Hands operator. Observe, one action, re-observe, confirm. Stop at any time.
package io.github.mixture.hands

sealed class HandsResult {
    data class Executed(val stepIndex: Int, val description: String) : HandsResult()
    data class NeedsApproval(val request: ApprovalRequest) : HandsResult()
    data class Blocked(val reason: String) : HandsResult()
    data class Failed(val reason: String) : HandsResult()
    data class SecureScreen(val packageName: String) : HandsResult()
    data class Stopped(val taskId: String) : HandsResult()
}

interface HandsExecutor {
    fun execute(action: HandsAction): Boolean
}

class FakeHandsExecutor : HandsExecutor {
    val executed = mutableListOf<HandsAction>()
    var nextResult: Boolean = true

    override fun execute(action: HandsAction): Boolean {
        executed.add(action)
        return nextResult
    }
}

data class HandsStep(
    val stepIndex: Int,
    val action: HandsAction,
    val result: HandsResult
)

class HandsOperator(
    private val taskId: String,
    private val executor: HandsExecutor,
    private val approvalStore: ApprovalStore,
    private val maxSteps: Int = 30
) {
    private var current: ScreenState? = null
    private var stopped: Boolean = false
    private val steps = mutableListOf<HandsStep>()

    fun observe(state: ScreenState): HandsResult? {
        current = state
        if (state.isSecure()) {
            return HandsResult.SecureScreen(state.packageName)
        }
        return null
    }

    fun currentState(): ScreenState? {
        return current
    }

    fun stepCount(): Int {
        return steps.size
    }

    fun history(): List<HandsStep> {
        return steps.toList()
    }

    fun stop(): HandsResult.Stopped {
        stopped = true
        return HandsResult.Stopped(taskId)
    }

    fun isStopped(): Boolean {
        return stopped
    }

    fun act(action: HandsAction): HandsResult {
        if (stopped) {
            return HandsResult.Stopped(taskId)
        }
        if (steps.size >= maxSteps) {
            return HandsResult.Failed("Step limit reached: $maxSteps")
        }
        val state = current
        if (state == null) {
            return HandsResult.Failed("No screen observation yet")
        }
        if (state.isSecure()) {
            return HandsResult.SecureScreen(state.packageName)
        }
        val blocked = checkBlocked(action, state)
        if (blocked != null) {
            return blocked
        }
        val sensitive = action.sensitive()
        if (sensitive != null) {
            val request = ApprovalRequest(
                taskId = taskId,
                stepIndex = steps.size,
                action = action,
                category = sensitive,
                description = action.describe()
            )
            val decision = approvalStore.requestApproval(request)
            if (decision !is ApprovalResult.Approved) {
                return HandsResult.NeedsApproval(request)
            }
        }
        val ok = executor.execute(action)
        val stepIndex = steps.size
        val result: HandsResult = if (ok) {
            HandsResult.Executed(stepIndex, action.describe())
        } else {
            HandsResult.Failed("Executor returned false for: ${action.describe()}")
        }
        steps.add(HandsStep(stepIndex, action, result))
        return result
    }

    private fun checkBlocked(action: HandsAction, state: ScreenState): HandsResult.Blocked? {
        when (action) {
            is HandsAction.Input -> {
                val node = state.findById(action.nodeId)
                if (node == null) {
                    return HandsResult.Blocked("Node not found: ${action.nodeId}")
                }
                if (node.isPasswordField()) {
                    return HandsResult.Blocked("Password field. The user must type it personally.")
                }
                if (!node.editable) {
                    return HandsResult.Blocked("Node is not editable: ${action.nodeId}")
                }
            }
            is HandsAction.Tap -> {
                if (state.findById(action.nodeId) == null) {
                    return HandsResult.Blocked("Node not found: ${action.nodeId}")
                }
            }
            is HandsAction.Sensitive -> {
                return checkBlocked(action.inner, state)
            }
            else -> {
            }
        }
        return null
    }
}
