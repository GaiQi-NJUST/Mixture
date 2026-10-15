// Mixture: a validated command for Termux RUN_COMMAND.
package io.github.mixture.termux

data class TermuxCommand(
    val executable: String,
    val arguments: List<String> = emptyList(),
    val workDir: String,
    val taskId: String,
    val label: String = "",
    val background: Boolean = true
) {
    companion object {
        val ALLOWED_EXECUTABLES = setOf("git", "ls", "pwd", "cat", "echo", "sh", "python3", "node")
        private val FORBIDDEN_ARG_CHARS = setOf(';', '|', '&', '$', '`', '(', ')', '<', '>', '\n', '\r')
        const val TERMUX_BIN_DIR = "/data/data/com.termux/files/usr/bin"
        const val TERMUX_HOME_DIR = "/data/data/com.termux/files/home"

        const val EXTRA_COMMAND_PATH = "com.termux.RUN_COMMAND_PATH"
        const val EXTRA_ARGUMENTS = "com.termux.RUN_COMMAND_ARGUMENTS"
        const val EXTRA_WORKDIR = "com.termux.RUN_COMMAND_WORKDIR"
        const val EXTRA_BACKGROUND = "com.termux.RUN_COMMAND_BACKGROUND"
        const val EXTRA_SESSION_ACTION = "com.termux.RUN_COMMAND_SESSION_ACTION"
        const val EXTRA_COMMAND_LABEL = "com.termux.RUN_COMMAND_LABEL"
        const val ACTION_RUN_COMMAND = "com.termux.RUN_COMMAND"
        const val SERVICE_CLASS = "com.termux.app.RunCommandService"
        const val PACKAGE_TERMUX = "com.termux"
        const val SESSION_ACTION_DEFAULT = "0"
    }

    fun validate() {
        require(executable in ALLOWED_EXECUTABLES) { "Executable not allowed: $executable" }
        require(!executable.contains("/")) { "Executable must not contain a slash" }
        require(taskId.isNotBlank()) { "Task id must not be blank" }
        require(workDir.isNotBlank()) { "Work dir must not be blank" }
        require(!workDir.contains("..")) { "Work dir must not contain .." }
        for (arg in arguments) {
            require(arg.none { it in FORBIDDEN_ARG_CHARS }) { "Argument contains a forbidden char: $arg" }
            require(arg.length <= 4096) { "Argument too long" }
        }
        val total = arguments.sumOf { it.length } + executable.length
        require(total <= 131072) { "Command too long" }
    }

    fun commandPath(): String {
        validate()
        return "$TERMUX_BIN_DIR/$executable"
    }

    fun toExtras(): Map<String, Any> {
        validate()
        return mapOf(
            EXTRA_COMMAND_PATH to commandPath(),
            EXTRA_ARGUMENTS to arguments.toTypedArray(),
            EXTRA_WORKDIR to workDir,
            EXTRA_BACKGROUND to background,
            EXTRA_SESSION_ACTION to SESSION_ACTION_DEFAULT,
            EXTRA_COMMAND_LABEL to if (label.isBlank()) taskId else label
        )
    }
}
