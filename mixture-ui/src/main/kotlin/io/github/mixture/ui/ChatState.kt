// Mixture: chat and task state for the app. Pure data. No Android classes are used here.
package io.github.mixture.ui

enum class MessageRole {
    USER,
    ASSISTANT
}

enum class MessageStatus {
    SENDING,
    SENT,
    FAILED
}

data class ChatMessage(
    val id: String,
    val role: MessageRole,
    val text: String,
    val status: MessageStatus = MessageStatus.SENT
)

enum class ThinkingState {
    IDLE,
    THINKING,
    DONE
}

enum class ApprovalStatus {
    PENDING,
    RUNNING,
    SUCCESS,
    ERROR
}

data class ApprovalCard(
    val id: String,
    val title: String,
    val description: String,
    val status: ApprovalStatus = ApprovalStatus.PENDING,
    val approveLabel: String = "Approve",
    val rejectLabel: String = "Reject"
) {
    fun approve(): ApprovalCard {
        require(status == ApprovalStatus.PENDING) { "Only a pending card can be approved" }
        return copy(status = ApprovalStatus.RUNNING)
    }

    fun finish(success: Boolean): ApprovalCard {
        require(status == ApprovalStatus.RUNNING) { "Only a running card can finish" }
        return copy(status = if (success) ApprovalStatus.SUCCESS else ApprovalStatus.ERROR)
    }

    fun reject(): ApprovalCard {
        require(status == ApprovalStatus.PENDING) { "Only a pending card can be rejected" }
        return copy(status = ApprovalStatus.ERROR)
    }
}

enum class TaskStatus {
    IDLE,
    RUNNING,
    NEEDS_APPROVAL,
    STOPPED,
    DONE,
    ERROR
}

data class ComposerState(
    val text: String = "",
    val isComposing: Boolean = false
) {
    fun canSend(): Boolean {
        return text.isNotBlank() && !isComposing
    }
}

data class AppState(
    val messages: List<ChatMessage> = emptyList(),
    val composer: ComposerState = ComposerState(),
    val thinking: ThinkingState = ThinkingState.IDLE,
    val thinkingForcedOpen: Boolean = false,
    val approvals: List<ApprovalCard> = emptyList(),
    val taskStatus: TaskStatus = TaskStatus.IDLE,
    val currentModel: String = "deepseek/deepseek-v4.1-flash",
    val shizukuReady: Boolean = false,
    val termuxAvailable: Boolean = false,
    val handsRunning: Boolean = false
) {
    fun pendingApprovals(): List<ApprovalCard> {
        return approvals.filter { it.status == ApprovalStatus.PENDING }
    }

    fun runningApprovals(): List<ApprovalCard> {
        return approvals.filter { it.status == ApprovalStatus.RUNNING }
    }
}
