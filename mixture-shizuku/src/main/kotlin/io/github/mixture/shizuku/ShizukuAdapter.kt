// Mixture: Shizuku adapter. It checks state, permission, read only, and approval.
package io.github.mixture.shizuku

data class ShizukuResult(
    val taskId: String,
    val stdout: String,
    val stderr: String,
    val exitCode: Int,
    val error: String? = null,
    val errorMessage: String? = null
) {
    fun isSuccess(): Boolean {
        return error == null && exitCode == 0
    }
}

sealed class ShizukuSendResult {
    data class Sent(val taskId: String) : ShizukuSendResult()
    data class NotReady(val taskId: String, val state: ShizukuState) : ShizukuSendResult()
    data class PermissionDenied(val taskId: String) : ShizukuSendResult()
    data class NeedsApproval(val taskId: String, val command: ShizukuCommand) : ShizukuSendResult()
    data class Rejected(val taskId: String, val reason: String) : ShizukuSendResult()
}

interface ShizukuBridge {
    fun send(command: ShizukuCommand): Boolean
    fun state(): ShizukuState
    fun permission(): ShizukuPermission
}

interface ShizukuApprover {
    fun approve(command: ShizukuCommand): Boolean
}

class FakeShizukuBridge(
    private val currentState: ShizukuState = ShizukuState.READY,
    private val currentPermission: ShizukuPermission = ShizukuPermission.GRANTED
) : ShizukuBridge {
    val sent = mutableListOf<ShizukuCommand>()

    override fun send(command: ShizukuCommand): Boolean {
        sent.add(command)
        return true
    }

    override fun state(): ShizukuState {
        return currentState
    }

    override fun permission(): ShizukuPermission {
        return currentPermission
    }
}

class FakeShizukuApprover(
    private val decisions: Map<String, Boolean> = emptyMap(),
    private val defaultDecision: Boolean = false
) : ShizukuApprover {
    val requested = mutableListOf<ShizukuCommand>()

    override fun approve(command: ShizukuCommand): Boolean {
        requested.add(command)
        return decisions[command.taskId] ?: defaultDecision
    }
}

class ShizukuAdapter(
    private val bridge: ShizukuBridge,
    private val approver: ShizukuApprover
) {
    private val running = mutableSetOf<String>()

    fun start(command: ShizukuCommand): ShizukuSendResult {
        try {
            command.validate()
        } catch (e: IllegalArgumentException) {
            return ShizukuSendResult.Rejected(command.taskId, e.message ?: "Invalid command")
        }
        val state = bridge.state()
        if (state != ShizukuState.READY) {
            return ShizukuSendResult.NotReady(command.taskId, state)
        }
        if (bridge.permission() != ShizukuPermission.GRANTED) {
            return ShizukuSendResult.PermissionDenied(command.taskId)
        }
        if (!command.isReadOnly()) {
            val approved = approver.approve(command)
            if (!approved) {
                return ShizukuSendResult.NeedsApproval(command.taskId, command)
            }
        }
        val ok = bridge.send(command)
        if (!ok) {
            return ShizukuSendResult.Rejected(command.taskId, "Bridge send returned false")
        }
        running.add(command.taskId)
        return ShizukuSendResult.Sent(command.taskId)
    }

    fun finish(taskId: String): Boolean {
        return running.remove(taskId)
    }

    fun isRunning(taskId: String): Boolean {
        return running.contains(taskId)
    }
}
