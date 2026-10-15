// Mixture: a validated command for Shizuku. Shizuku is not root. It only runs what the user authorized.
package io.github.mixture.shizuku

data class ShizukuCommand(
    val executable: String,
    val arguments: List<String> = emptyList(),
    val taskId: String,
    val label: String = ""
) {
    companion object {
        val ALLOWED_EXECUTABLES = setOf("pm", "dumpsys", "settings", "cmd", "am")
        private val FORBIDDEN_ARG_CHARS = setOf(';', '|', '&', '$', '`', '(', ')', '<', '>', '\n', '\r')
        private val READ_ONLY_PM = setOf("list", "dump", "path")
        private val BLOCKED_SUBCOMMANDS = setOf("install", "uninstall", "reboot", "su", "sudo", "rm", "push", "pull")
    }

    fun validate() {
        require(executable in ALLOWED_EXECUTABLES) { "Executable not allowed: $executable" }
        require(!executable.contains("/")) { "Executable must not contain a slash" }
        require(taskId.isNotBlank()) { "Task id must not be blank" }
        for (arg in arguments) {
            require(arg.none { it in FORBIDDEN_ARG_CHARS }) { "Argument contains a forbidden char: $arg" }
            require(arg.length <= 4096) { "Argument too long" }
            require(arg.lowercase() !in BLOCKED_SUBCOMMANDS) { "Subcommand is blocked: $arg" }
        }
        val total = arguments.sumOf { it.length } + executable.length
        require(total <= 131072) { "Command too long" }
    }

    fun isReadOnly(): Boolean {
        return when (executable) {
            "pm" -> arguments.firstOrNull() in READ_ONLY_PM
            "dumpsys" -> true
            "settings" -> arguments.firstOrNull() == "get"
            "cmd" -> arguments.getOrNull(0) == "package" && arguments.getOrNull(1) == "list"
            "am" -> arguments.firstOrNull() == "stack" && arguments.getOrNull(1) == "list"
            else -> false
        }
    }

    fun describe(): String {
        return "$executable ${arguments.joinToString(" ")}"
    }
}
