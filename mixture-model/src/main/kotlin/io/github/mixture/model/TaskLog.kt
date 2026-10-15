// Mixture: append only task log. All text is redacted before it is stored.
package io.github.mixture.model

data class LogEntry(
    val taskId: String,
    val stepIndex: Int,
    val action: String,
    val result: String,
    val timestampMs: Long
)

class TaskLog {
    private val entries = mutableListOf<LogEntry>()

    fun append(taskId: String, stepIndex: Int, action: String, result: String, timestampMs: Long) {
        entries.add(
            LogEntry(
                taskId = taskId,
                stepIndex = stepIndex,
                action = SecurityCheck.redact(action),
                result = SecurityCheck.redact(result),
                timestampMs = timestampMs
            )
        )
    }

    fun entries(): List<LogEntry> {
        return entries.toList()
    }

    fun entriesFor(taskId: String): List<LogEntry> {
        return entries.filter { it.taskId == taskId }
    }

    fun size(): Int {
        return entries.size
    }

    fun toText(): String {
        return entries.joinToString("\n") { e ->
            "${e.taskId} step ${e.stepIndex}: ${e.action} -> ${e.result}"
        }
    }
}
