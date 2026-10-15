// Mixture: one observation of the screen.
package io.github.mixture.hands

data class ScreenState(
    val nodes: List<ScreenNode>,
    val secure: Boolean,
    val packageName: String,
    val screenshotAvailable: Boolean,
    val timestampMs: Long
) {
    fun isSecure(): Boolean {
        return secure
    }

    fun findById(id: String): ScreenNode? {
        return nodes.firstOrNull { it.id == id }
    }

    fun findByText(text: String): ScreenNode? {
        return nodes.firstOrNull { it.displayText() == text }
    }

    fun findClickable(text: String): ScreenNode? {
        return nodes.firstOrNull { it.displayText() == text && it.clickable }
    }

    fun visibleNodes(): List<ScreenNode> {
        return nodes.filter { it.isVisible() }
    }

    fun hasPasswordField(): Boolean {
        return nodes.any { it.isPasswordField() }
    }
}
