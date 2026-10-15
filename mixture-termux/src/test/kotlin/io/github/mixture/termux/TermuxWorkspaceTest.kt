// Mixture: tests for workspace paths.
package io.github.mixture.termux

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TermuxWorkspaceTest {
    @Test
    fun validProjectNameIsAccepted() {
        assertTrue(TermuxWorkspace.isValidProjectName("demo"))
        assertTrue(TermuxWorkspace.isValidProjectName("my-project-1"))
        assertTrue(TermuxWorkspace.isValidProjectName("a"))
    }

    @Test
    fun invalidProjectNameIsRejected() {
        assertFalse(TermuxWorkspace.isValidProjectName(""))
        assertFalse(TermuxWorkspace.isValidProjectName("../escape"))
        assertFalse(TermuxWorkspace.isValidProjectName("My Project"))
        assertFalse(TermuxWorkspace.isValidProjectName("a/b"))
        assertFalse(TermuxWorkspace.isValidProjectName("a..b"))
    }

    @Test
    fun projectDirStaysInsideRoot() {
        val dir = TermuxWorkspace.projectDir("/data/data/com.termux/files/home", "demo")
        assertEquals("/data/data/com.termux/files/home/workspace/demo", dir)
    }

    @Test
    fun configLogAndSessionFilesUseProjectDir() {
        val root = "/data/data/com.termux/files/home"
        assertEquals(
            "/data/data/com.termux/files/home/workspace/demo/project.json",
            TermuxWorkspace.configFile(root, "demo")
        )
        assertEquals(
            "/data/data/com.termux/files/home/workspace/demo/task.log",
            TermuxWorkspace.logFile(root, "demo")
        )
        assertEquals(
            "/data/data/com.termux/files/home/workspace/demo/session.json",
            TermuxWorkspace.sessionFile(root, "demo")
        )
    }
}
