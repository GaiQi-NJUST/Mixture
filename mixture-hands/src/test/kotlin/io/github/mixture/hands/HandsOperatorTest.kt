// Mixture: tests for the Hands operator.
package io.github.mixture.hands

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HandsOperatorTest {
    private fun state(secure: Boolean = false): ScreenState {
        return ScreenState(
            nodes = listOf(
                ScreenNode(
                    id = "n1",
                    text = "Send",
                    contentDescription = null,
                    bounds = Rect(0, 0, 100, 50),
                    clickable = true,
                    editable = false,
                    password = false,
                    packageName = "com.example.app"
                ),
                ScreenNode(
                    id = "pw",
                    text = null,
                    contentDescription = "Password",
                    bounds = Rect(0, 60, 200, 110),
                    clickable = true,
                    editable = true,
                    password = true,
                    packageName = "com.example.app"
                ),
                ScreenNode(
                    id = "field",
                    text = "Note",
                    contentDescription = null,
                    bounds = Rect(0, 120, 200, 170),
                    clickable = true,
                    editable = true,
                    password = false,
                    packageName = "com.example.app"
                )
            ),
            secure = secure,
            packageName = "com.example.app",
            screenshotAvailable = true,
            timestampMs = 1L
        )
    }

    private fun operator(
        approvals: Map<String, ApprovalResult> = emptyMap(),
        defaultApproval: ApprovalResult = ApprovalResult.Denied
    ): Pair<HandsOperator, FakeHandsExecutor> {
        val executor = FakeHandsExecutor()
        val store = FakeApprovalStore(approvals, defaultApproval)
        val op = HandsOperator("task-1", executor, store)
        op.observe(state())
        return Pair(op, executor)
    }

    @Test
    fun tapRunsOneAction() {
        val (op, executor) = operator()
        val result = op.act(HandsAction.Tap("n1"))
        assertTrue(result is HandsResult.Executed)
        assertEquals(1, executor.executed.size)
        assertEquals(1, op.stepCount())
    }

    @Test
    fun tapAtRunsAsCoordinateFallback() {
        val (op, executor) = operator()
        val result = op.act(HandsAction.TapAt(50, 25))
        assertTrue(result is HandsResult.Executed)
        assertEquals(1, executor.executed.size)
    }

    @Test
    fun inputIntoPlainFieldRuns() {
        val (op, _) = operator()
        val result = op.act(HandsAction.Input("field", "hello"))
        assertTrue(result is HandsResult.Executed)
    }

    @Test
    fun inputIntoPasswordFieldIsBlocked() {
        val (op, executor) = operator()
        val result = op.act(HandsAction.Input("pw", "secret"))
        assertTrue(result is HandsResult.Blocked)
        assertEquals(0, executor.executed.size)
    }

    @Test
    fun sensitiveActionNeedsApproval() {
        val (op, executor) = operator()
        val action = HandsAction.Sensitive(HandsAction.Tap("n1"), SensitiveCategory.SEND)
        val result = op.act(action)
        assertTrue(result is HandsResult.NeedsApproval)
        assertEquals(0, executor.executed.size)
    }

    @Test
    fun sensitiveActionRunsAfterApproval() {
        val (op, executor) = operator(
            approvals = mapOf("task-1:0" to ApprovalResult.Approved),
            defaultApproval = ApprovalResult.Denied
        )
        val action = HandsAction.Sensitive(HandsAction.Tap("n1"), SensitiveCategory.SEND)
        val result = op.act(action)
        assertTrue(result is HandsResult.Executed)
        assertEquals(1, executor.executed.size)
    }

    @Test
    fun secondSensitiveActionNeedsItsOwnApproval() {
        val store = FakeApprovalStore(
            decisions = mapOf("task-1:0" to ApprovalResult.Approved),
            defaultResult = ApprovalResult.Denied
        )
        val executor = FakeHandsExecutor()
        val op = HandsOperator("task-1", executor, store)
        op.observe(state())
        val first = op.act(HandsAction.Sensitive(HandsAction.Tap("n1"), SensitiveCategory.SEND))
        assertTrue(first is HandsResult.Executed)
        val second = op.act(HandsAction.Sensitive(HandsAction.Tap("n1"), SensitiveCategory.SEND))
        assertTrue(second is HandsResult.NeedsApproval)
        assertEquals(1, executor.executed.size)
    }

    @Test
    fun secureScreenBlocksActions() {
        val executor = FakeHandsExecutor()
        val store = FakeApprovalStore()
        val op = HandsOperator("task-1", executor, store)
        val secureResult = op.observe(state(secure = true))
        assertTrue(secureResult is HandsResult.SecureScreen)
        val result = op.act(HandsAction.Tap("n1"))
        assertTrue(result is HandsResult.SecureScreen)
        assertEquals(0, executor.executed.size)
    }

    @Test
    fun stopBlocksLaterActions() {
        val (op, executor) = operator()
        op.stop()
        val result = op.act(HandsAction.Tap("n1"))
        assertTrue(result is HandsResult.Stopped)
        assertEquals(0, executor.executed.size)
    }

    @Test
    fun missingNodeIsBlocked() {
        val (op, executor) = operator()
        val result = op.act(HandsAction.Tap("no-such-node"))
        assertTrue(result is HandsResult.Blocked)
        assertEquals(0, executor.executed.size)
    }

    @Test
    fun stepLimitBlocksExtraActions() {
        val executor = FakeHandsExecutor()
        val store = FakeApprovalStore()
        val op = HandsOperator("task-1", executor, store, maxSteps = 1)
        op.observe(state())
        val first = op.act(HandsAction.TapAt(10, 10))
        assertTrue(first is HandsResult.Executed)
        val second = op.act(HandsAction.TapAt(20, 20))
        assertTrue(second is HandsResult.Failed)
        assertEquals(1, executor.executed.size)
    }
}
