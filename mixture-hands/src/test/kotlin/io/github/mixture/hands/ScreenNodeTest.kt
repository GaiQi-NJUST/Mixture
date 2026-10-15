// Mixture: tests for screen nodes and state.
package io.github.mixture.hands

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ScreenNodeTest {
    private fun node(
        id: String = "n1",
        text: String? = "Send",
        clickable: Boolean = true,
        editable: Boolean = false,
        password: Boolean = false
    ): ScreenNode {
        return ScreenNode(
            id = id,
            text = text,
            contentDescription = null,
            bounds = Rect(0, 0, 100, 50),
            clickable = clickable,
            editable = editable,
            password = password,
            packageName = "com.example.app"
        )
    }

    @Test
    fun centerIsMidpointOfBounds() {
        val n = node()
        assertEquals(50, n.bounds.centerX())
        assertEquals(25, n.bounds.centerY())
    }

    @Test
    fun invalidBoundsAreNotVisible() {
        val bad = node().copy(bounds = Rect(100, 100, 50, 50))
        assertFalse(bad.isVisible())
    }

    @Test
    fun passwordFieldIsDetected() {
        val pw = node(editable = true, password = true)
        assertTrue(pw.isPasswordField())
        val plain = node(editable = true, password = false)
        assertFalse(plain.isPasswordField())
    }

    @Test
    fun stateFindsByTextAndClickable() {
        val state = ScreenState(
            nodes = listOf(node("n1", "Send", true), node("n2", "Cancel", false)),
            secure = false,
            packageName = "com.example.app",
            screenshotAvailable = true,
            timestampMs = 1L
        )
        assertEquals("n1", state.findByText("Send")?.id)
        assertEquals("n1", state.findClickable("Send")?.id)
        assertNull(state.findClickable("Cancel"))
        assertFalse(state.isSecure())
        assertEquals(2, state.visibleNodes().size)
    }

    @Test
    fun secureStateIsSecure() {
        val state = ScreenState(
            nodes = emptyList(),
            secure = true,
            packageName = "com.example.bank",
            screenshotAvailable = false,
            timestampMs = 1L
        )
        assertTrue(state.isSecure())
    }
}
