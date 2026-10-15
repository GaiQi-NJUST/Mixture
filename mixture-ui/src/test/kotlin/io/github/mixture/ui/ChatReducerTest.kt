// Mixture: tests for design tokens, approval card, and chat reducer.
package io.github.mixture.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ChatReducerTest {
    @Test
    fun newTokensMatchReview() {
        assertEquals("#F0F4F8", DesignTokens.USER_BUBBLE_TINT)
        assertEquals("#EF4444", DesignTokens.FILL_DANGER)
        assertEquals("#E6E6E6", DesignTokens.BORDER)
        assertEquals("#CCCCCC", DesignTokens.BORDER_STRONG)
        assertEquals("#F5F5F5", DesignTokens.DARK_TEXT_PRIMARY)
        assertEquals("#E08264", DesignTokens.DARK_CLAY_HOVER)
        assertEquals("#F87171", DesignTokens.DARK_FILL_DANGER)
        assertEquals("#FFFFFF", DesignTokens.LIGHT_SURFACE_3)
    }

    @Test
    fun tokensMatchOpenClaudeUiDoc() {
        assertEquals("#D97757", DesignTokens.CLAY_MAIN)
        assertEquals("#CC5A36", DesignTokens.CLAY_HOVER)
        assertEquals("#F5F5F3", DesignTokens.LIGHT_SURFACE_0)
        assertEquals("#151515", DesignTokens.TEXT_PRIMARY)
        assertEquals(18, DesignTokens.RADIUS_BUBBLE)
        assertEquals(20, DesignTokens.RADIUS_COMPOSER)
        assertEquals(10, DesignTokens.RADIUS_PANEL)
        assertEquals(16, DesignTokens.RADIUS_DIALOG)
        assertEquals(70L, DesignTokens.MOTION_TACTILE_MS)
        assertEquals(120L, DesignTokens.MOTION_SNAP_MS)
        assertEquals(300L, DesignTokens.MOTION_PANEL_SLIDE_MS)
    }

    @Test
    fun sendMessageAddsUserMessageAndStartsThinking() {
        val state = AppState()
        val next = ChatReducer.sendMessage(state, "m1", "hello")
        assertEquals(1, next.messages.size)
        assertEquals(MessageRole.USER, next.messages[0].role)
        assertEquals(ThinkingState.THINKING, next.thinking)
        assertTrue(next.thinkingForcedOpen)
        assertEquals(TaskStatus.RUNNING, next.taskStatus)
        assertEquals("", next.composer.text)
    }

    @Test
    fun receiveMessageAddsAssistantMessageAndClosesThinking() {
        val state = ChatReducer.sendMessage(AppState(), "m1", "hello")
        val next = ChatReducer.receiveMessage(state, "m2", "hi there")
        assertEquals(2, next.messages.size)
        assertEquals(MessageRole.ASSISTANT, next.messages[1].role)
        assertEquals(ThinkingState.DONE, next.thinking)
        assertFalse(next.thinkingForcedOpen)
    }

    @Test
    fun composerCannotSendWhileComposing() {
        val state = AppState(composer = ComposerState(text = "hello", isComposing = true))
        assertFalse(state.composer.canSend())
        try {
            ChatReducer.sendMessage(state, "m1", "hello")
            throw AssertionError("Expected send to fail while composing")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message!!.contains("composing"))
        }
    }

    @Test
    fun approvalCardFlowIsPendingRunningSuccess() {
        val card = ApprovalCard(id = "a1", title = "Send message", description = "Send this message")
        assertEquals(ApprovalStatus.PENDING, card.status)
        val running = card.approve()
        assertEquals(ApprovalStatus.RUNNING, running.status)
        val done = running.finish(true)
        assertEquals(ApprovalStatus.SUCCESS, done.status)
    }

    @Test
    fun approvalRejectGoesToError() {
        val card = ApprovalCard(id = "a1", title = "Send", description = "Send")
        val rejected = card.reject()
        assertEquals(ApprovalStatus.ERROR, rejected.status)
    }

    @Test
    fun addApprovalSetsNeedsApproval() {
        val state = ChatReducer.addApproval(
            AppState(),
            ApprovalCard(id = "a1", title = "Send", description = "Send")
        )
        assertEquals(TaskStatus.NEEDS_APPROVAL, state.taskStatus)
        assertEquals(1, state.pendingApprovals().size)
    }

    @Test
    fun approveMovesCardToRunningAndTaskToRunning() {
        val state = ChatReducer.addApproval(
            AppState(),
            ApprovalCard(id = "a1", title = "Send", description = "Send")
        )
        val next = ChatReducer.approve(state, "a1")
        assertEquals(ApprovalStatus.RUNNING, next.approvals[0].status)
        assertEquals(TaskStatus.RUNNING, next.taskStatus)
    }

    @Test
    fun stopTaskClearsThinkingAndHands() {
        val state = ChatReducer.sendMessage(AppState(), "m1", "hello")
        val withHands = ChatReducer.setHandsRunning(state, true)
        val stopped = ChatReducer.stopTask(withHands)
        assertEquals(TaskStatus.STOPPED, stopped.taskStatus)
        assertEquals(ThinkingState.IDLE, stopped.thinking)
        assertFalse(stopped.thinkingForcedOpen)
        assertFalse(stopped.handsRunning)
    }

    @Test
    fun setModelChangesCurrentModel() {
        val next = ChatReducer.setModel(AppState(), "qwen/qwen3.8-27b")
        assertEquals("qwen/qwen3.8-27b", next.currentModel)
    }

    @Test
    fun finishTaskSetsDone() {
        val state = ChatReducer.sendMessage(AppState(), "m1", "hello")
        val done = ChatReducer.finishTask(state, true)
        assertEquals(TaskStatus.DONE, done.taskStatus)
    }
}
