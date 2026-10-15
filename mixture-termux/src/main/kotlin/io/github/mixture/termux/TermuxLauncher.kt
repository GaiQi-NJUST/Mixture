// Mixture: launcher for Termux tasks. It validates, sends, and tracks one task.
package io.github.mixture.termux

class TermuxLauncher(
    private val bridge: TermuxBridge,
    private val workspaceRoot: String
) {
    private val running = mutableMapOf<String, TermuxCommand>()

    fun start(projectName: String, command: TermuxCommand): TermuxSendResult {
        TermuxWorkspace.requireValidProjectName(projectName)
        val expectedDir = TermuxWorkspace.projectDir(workspaceRoot, projectName)
        if (command.workDir.trimEnd('/') != expectedDir) {
            return TermuxSendResult.Rejected(command.taskId, "Work dir does not match project dir")
        }
        try {
            command.validate()
        } catch (e: IllegalArgumentException) {
            return TermuxSendResult.Rejected(command.taskId, e.message ?: "Invalid command")
        }
        if (!bridge.isTermuxAvailable()) {
            return TermuxSendResult.Rejected(command.taskId, "Termux is not available")
        }
        val result = bridge.send(command)
        if (result is TermuxSendResult.Sent) {
            running[command.taskId] = command
        }
        return result
    }

    fun stop(taskId: String): Boolean {
        val ok = bridge.stop(taskId)
        if (ok) {
            running.remove(taskId)
        }
        return ok
    }

    fun isRunning(taskId: String): Boolean {
        return running.containsKey(taskId)
    }

    fun runningTaskIds(): Set<String> {
        return running.keys.toSet()
    }

    fun finish(taskId: String, result: TermuxResult): TermuxResult {
        running.remove(taskId)
        return result
    }
}
