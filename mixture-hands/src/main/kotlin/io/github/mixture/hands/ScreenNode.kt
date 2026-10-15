// Mixture: one node from the accessibility tree.
package io.github.mixture.hands

data class Rect(
    val left: Int,
    val top: Int,
    val right: Int,
    val bottom: Int
) {
    fun isValid(): Boolean {
        return right > left && bottom > top && left >= 0 && top >= 0
    }

    fun centerX(): Int {
        return (left + right) / 2
    }

    fun centerY(): Int {
        return (top + bottom) / 2
    }

    fun width(): Int {
        return right - left
    }

    fun height(): Int {
        return bottom - top
    }
}

data class ScreenNode(
    val id: String,
    val text: String?,
    val contentDescription: String?,
    val bounds: Rect,
    val clickable: Boolean,
    val editable: Boolean,
    val password: Boolean,
    val packageName: String?
) {
    fun displayText(): String {
        if (!text.isNullOrBlank()) return text
        if (!contentDescription.isNullOrBlank()) return contentDescription
        return ""
    }

    fun isVisible(): Boolean {
        return bounds.isValid()
    }

    fun isPasswordField(): Boolean {
        return password && editable
    }
}
