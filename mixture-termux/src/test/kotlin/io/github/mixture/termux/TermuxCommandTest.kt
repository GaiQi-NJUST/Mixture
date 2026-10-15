// Mixture: tests for command validation and RUN_COMMAND extras.
package io.github.mixture.termux

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TermuxCommandTest {
    private fun command(
        executable: String = "git",
        arguments: List<String> = listOf("status"),
        workDir: String = "/data/data/com.termux/files/home/workspace/demo"
    ): TermuxCommand {
        return TermuxCommand(
            executable = executable,
            arguments = arguments,
            workDir = workDir,
            taskId = "task-1",
            label = "Git status",
            background = true
        )
    }

    @Test
    fun allowedCommandPassesValidation() {
        command().validate()
    }

    @Test
    fun commandPathUsesTermuxBinDir() {
        assertEquals("/data/data/com.termux/files/usr/bin/git", command().commandPath())
    }

    @Test
    fun extrasUseTermuxKeys() {
        val extras = command().toExtras()
        assertEquals("/data/data/com.termux/files/usr/bin/git", extras[TermuxCommand.EXTRA_COMMAND_PATH])
        assertEquals("/data/data/com.termux/files/home/workspace/demo", extras[TermuxCommand.EXTRA_WORKDIR])
        assertEquals(true, extras[TermuxCommand.EXTRA_BACKGROUND])
        assertEquals("Git status", extras[TermuxCommand.EXTRA_COMMAND_LABEL])
        val args = extras[TermuxCommand.EXTRA_ARGUMENTS] as Array<*>
        assertEquals("status", args[0])
    }

    @Test(expected = IllegalArgumentException::class)
    fun forbiddenExecutableIsRejected() {
        command(executable = "rm", arguments = listOf("-rf", "/")).validate()
    }

    @Test(expected = IllegalArgumentException::class)
    fun shellMetacharInArgumentIsRejected() {
        command(arguments = listOf("status; rm -rf /")).validate()
    }

    @Test(expected = IllegalArgumentException::class)
    fun workDirWithDotDotIsRejected() {
        command(workDir = "/data/data/com.termux/files/home/../etc").validate()
    }
}
