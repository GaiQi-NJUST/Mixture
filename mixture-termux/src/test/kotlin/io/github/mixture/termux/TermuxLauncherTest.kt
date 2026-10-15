// Mixture: tests for launcher start and stop.
package io.github.mixture.termux

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TermuxLauncherTest {
    private val root = "/data/data/com.termux/files/home"
    private val dir = "$root/workspace/demo"

    private fun command(): TermuxCommand {
        return TermuxCommand(
            executable = "git",
            arguments = listOf("status"),
            workDir = dir,
            taskId = "task-1",
            label = "Git status",
            background = true
        )
    }

    @Test
    fun startTracksRunningTask() {
        val bridge = FakeTermuxBridge()
        val launcher = TermuxLauncher(bridge, root)
        val result = launcher.start("demo", command())
        assertTrue(result is TermuxSendResult.Sent)
        assertTrue(launcher.isRunning("task-1"))
        assertEquals(setOf("task-1"), launcher.runningTaskIds())
    }

    @Test
    fun startRejectsWrongWorkDir() {
        val bridge = FakeTermuxBridge()
        val launcher = TermuxLauncher(bridge, root)
        val wrong = command().copy(workDir = "/tmp/other")
        val result = launcher.start("demo", wrong)
        assertTrue(result is TermuxSendResult.Rejected)
        assertFalse(launcher.isRunning("task-1"))
    }

    @Test
    fun startRejectsWhenTermuxIsUnavailable() {
        val bridge = FakeTermuxBridge(available = false)
        val launcher = TermuxLauncher(bridge, root)
        val result = launcher.start("demo", command())
        assertTrue(result is TermuxSendResult.Rejected)
    }

    @Test
    fun stopRemovesRunningTask() {
        val bridge = FakeTermuxBridge()
        val launcher = TermuxLauncher(bridge, root)
        launcher.start("demo", command())
        assertTrue(launcher.stop("task-1"))
        assertFalse(launcher.isRunning("task-1"))
        assertEquals(listOf("task-1"), bridge.stopped)
    }

    @Test
    fun resultSuccessNeedsZeroExitAndNoError() {
        val ok = TermuxResult(taskId = "task-1", stdout = "ok", stderr = "", exitCode = 0)
        assertTrue(ok.isSuccess())
        assertFalse(ok.wasTruncated())
        val failed = TermuxResult(taskId = "task-1", stdout = "", stderr = "x", exitCode = 1)
        assertFalse(failed.isSuccess())
        val truncated = TermuxResult(
            taskId = "task-1",
            stdout = "short",
            stderr = "",
            exitCode = 0,
            stdoutOriginalLength = 200000
        )
        assertTrue(truncated.wasTruncated())
    }
}
