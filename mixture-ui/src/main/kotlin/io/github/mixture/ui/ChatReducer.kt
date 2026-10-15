// Mixture: pure reducer for chat and task state. Each function returns a new state.
package io.github.mixture.ui

object ChatReducer {
    fun setComposerText(state: AppState, text: String): AppState {
        return state.copy(composer = state.composer.copy(text = text))
    }

    fun setComposing(state: AppState, composing: Boolean): AppState {
        return state.copy(composer = state.composer.copy(isComposing = composing))
    }

    fun sendMessage(state: AppState, id: String, text: String): AppState {
        require(text.isNotBlank()) { "Message text must not be blank" }
        require(!state.composer.isComposing) { "Cannot send while composing" }
        val message = ChatMessage(id = id, role = MessageRole.USER, text = text, status = MessageStatus.SENT)
        return state.copy(
            messages = state.messages + message,
            composer = ComposerState(),
            thinking = ThinkingState.THINKING,
            thinkingForcedOpen = true,
            taskStatus = TaskStatus.RUNNING
        )
    }

    fun receiveMessage(state: AppState, id: String, text: String): AppState {
        val message = ChatMessage(id = id, role = MessageRole.ASSISTANT, text = text, status = MessageStatus.SENT)
        return state.copy(
            messages = state.messages + message,
            thinking = ThinkingState.DONE,
            thinkingForcedOpen = false
        )
    }

    fun setThinking(state: AppState, thinking: ThinkingState): AppState {
        val forced = thinking == ThinkingState.THINKING
        return state.copy(thinking = thinking, thinkingForcedOpen = forced)
    }

    fun addApproval(state: AppState, card: ApprovalCard): AppState {
        require(state.approvals.none { it.id == card.id }) { "Approval id already exists: ${card.id}" }
        return state.copy(
            approvals = state.approvals + card,
            taskStatus = TaskStatus.NEEDS_APPROVAL
        )
    }

    fun approve(state: AppState, cardId: String): AppState {
        val updated = state.approvals.map { if (it.id == cardId) it.approve() else it }
        return state.copy(approvals = updated, taskStatus = TaskStatus.RUNNING)
    }

    fun finishApproval(state: AppState, cardId: String, success: Boolean): AppState {
        val updated = state.approvals.map { if (it.id == cardId) it.finish(success) else it }
        val stillPending = updated.any { it.status == ApprovalStatus.PENDING }
        val status = if (stillPending) TaskStatus.NEEDS_APPROVAL else TaskStatus.RUNNING
        return state.copy(approvals = updated, taskStatus = status)
    }

    fun rejectApproval(state: AppState, cardId: String): AppState {
        val updated = state.approvals.map { if (it.id == cardId) it.reject() else it }
        return state.copy(approvals = updated, taskStatus = TaskStatus.STOPPED)
    }

    fun stopTask(state: AppState): AppState {
        return state.copy(
            taskStatus = TaskStatus.STOPPED,
            thinking = ThinkingState.IDLE,
            thinkingForcedOpen = false,
            handsRunning = false
        )
    }

    fun finishTask(state: AppState, success: Boolean): AppState {
        return state.copy(
            taskStatus = if (success) TaskStatus.DONE else TaskStatus.ERROR,
            thinking = ThinkingState.IDLE,
            thinkingForcedOpen = false,
            handsRunning = false
        )
    }

    fun setModel(state: AppState, model: String): AppState {
        require(model.isNotBlank()) { "Model must not be blank" }
        return state.copy(currentModel = model)
    }

    fun setCapabilities(state: AppState, shizukuReady: Boolean, termuxAvailable: Boolean): AppState {
        return state.copy(shizukuReady = shizukuReady, termuxAvailable = termuxAvailable)
    }

    fun setHandsRunning(state: AppState, running: Boolean): AppState {
        return state.copy(handsRunning = running)
    }
}
