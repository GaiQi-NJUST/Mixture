// Mixture: tests for the Shizuku adapter.
package io.github.mixture.shizuku

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ShizukuAdapterTest {
    private fun command(
        executable: String = "dumpsys",
        arguments: List<String> = listOf("package", "com.example.app"),
        taskId: String = "task-1"
    ): ShizukuCommand {
        return ShizukuCommand(executable = executable, arguments = arguments, taskId = taskId)
    }

    @Test
    fun dumpsysIsReadOnly() {
        assertTrue(command().isReadOnly())
    }

    @Test
    fun pmListIsReadOnly() {
        assertTrue(command("pm", listOf("list", "packages")).isReadOnly())
    }

    @Test
    fun pmInstallIsNotReadOnlyAndIsBlocked() {
        val cmd = command("pm", listOf("install", "app.apk"))
        assertFalse(cmd.isReadOnly())
        try {
            cmd.validate()
            throw AssertionError("Expected validate to fail")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message!!.contains("install"))
        }
    }

    @Test
    fun settingsGetIsReadOnly() {
        assertTrue(command("settings", listOf("get", "global", "airplane_mode_on")).isReadOnly())
    }

    @Test
    fun settingsPutIsNotReadOnly() {
        assertFalse(command("settings", listOf("put", "global", "airplane_mode_on", "1")).isReadOnly())
    }

    @Test
    fun readyReadOnlyCommandRuns() {
        val bridge = FakeShizukuBridge()
        val adapter = ShizukuAdapter(bridge, FakeShizukuApprover())
        val result = adapter.start(command())
        assertTrue(result is ShizukuSendResult.Sent)
        assertTrue(adapter.isRunning("task-1"))
        assertEquals(1, bridge.sent.size)
    }

    @Test
    fun notRunningBlocksCommand() {
        val bridge = FakeShizukuBridge(currentState = ShizukuState.NOT_RUNNING)
        val adapter = ShizukuAdapter(bridge, FakeShizukuApprover())
        val result = adapter.start(command())
        assertTrue(result is ShizukuSendResult.NotReady)
        assertEquals(0, bridge.sent.size)
    }

    @Test
    fun permissionDeniedBlocksCommand() {
        val bridge = FakeShizukuBridge(currentPermission = ShizukuPermission.DENIED)
        val adapter = ShizukuAdapter(bridge, FakeShizukuApprover())
        val result = adapter.start(command())
        assertTrue(result is ShizukuSendResult.PermissionDenied)
        assertEquals(0, bridge.sent.size)
    }

    @Test
    fun writeCommandNeedsApproval() {
        val bridge = FakeShizukuBridge()
        val adapter = ShizukuAdapter(bridge, FakeShizukuApprover(defaultDecision = false))
        val result = adapter.start(command("settings", listOf("put", "global", "airplane_mode_on", "1")))
        assertTrue(result is ShizukuSendResult.NeedsApproval)
        assertEquals(0, bridge.sent.size)
    }

    @Test
    fun writeCommandRunsAfterApproval() {
        val bridge = FakeShizukuBridge()
        val adapter = ShizukuAdapter(
            bridge,
            FakeShizukuApprover(decisions = mapOf("task-1" to true))
        )
        val result = adapter.start(command("settings", listOf("put", "global", "airplane_mode_on", "1")))
        assertTrue(result is ShizukuSendResult.Sent)
        assertEquals(1, bridge.sent.size)
    }

    @Test
    fun forbiddenExecutableIsRejected() {
        val bridge = FakeShizukuBridge()
        val adapter = ShizukuAdapter(bridge, FakeShizukuApprover())
        val result = adapter.start(command("rm", listOf("-rf", "/")))
        assertTrue(result is ShizukuSendResult.Rejected)
        assertEquals(0, bridge.sent.size)
    }

    @Test
    fun shellMetacharIsRejected() {
        val bridge = FakeShizukuBridge()
        val adapter = ShizukuAdapter(bridge, FakeShizukuApprover())
        val result = adapter.start(command("pm", listOf("list; rm -rf /")))
        assertTrue(result is ShizukuSendResult.Rejected)
        assertEquals(0, bridge.sent.size)
    }

    @Test
    fun degradationMessageSaysTermuxAndHandsStillWork() {
        val msg = degradationMessage(ShizukuState.NOT_RUNNING)
        assertTrue(msg.contains("Termux and Hands still work"))
    }
}
