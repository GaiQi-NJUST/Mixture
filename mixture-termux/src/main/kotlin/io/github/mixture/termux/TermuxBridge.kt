// Mixture: bridge to Termux. The Android implementation sends the RUN_COMMAND intent.
// The fake implementation is for unit tests. No Android classes are used here.
package io.github.mixture.termux

interface TermuxBridge {
    fun send(command: TermuxCommand): TermuxSendResult
    fun stop(taskId: String): Boolean
    fun isTermuxAvailable(): Boolean
}

sealed class TermuxSendResult {
    data class Sent(val taskId: String) : TermuxSendResult()
    data class Rejected(val taskId: String, val reason: String) : TermuxSendResult()
    data class Failed(val taskId: String, val reason: String) : TermuxSendResult()
}

class FakeTermuxBridge(
    private val available: Boolean = true,
    private val results: Map<String, TermuxResult> = emptyMap()
) : TermuxBridge {
    val sent = mutableListOf<TermuxCommand>()
    val stopped = mutableListOf<String>()

    override fun send(command: TermuxCommand): TermuxSendResult {
        if (!available) {
            return TermuxSendResult.Rejected(command.taskId, "Termux is not available")
        }
        return try {
            command.validate()
            sent.add(command)
            TermuxSendResult.Sent(command.taskId)
        } catch (e: IllegalArgumentException) {
            TermuxSendResult.Rejected(command.taskId, e.message ?: "Invalid command")
        }
    }

    override fun stop(taskId: String): Boolean {
        stopped.add(taskId)
        return true
    }

    override fun isTermuxAvailable(): Boolean {
        return available
    }

    fun resultFor(taskId: String): TermuxResult? {
        return results[taskId]
    }
}
