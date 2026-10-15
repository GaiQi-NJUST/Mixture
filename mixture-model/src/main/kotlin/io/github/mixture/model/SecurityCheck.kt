// Mixture: secret detection and redaction. Keys never go to logs, chats, or the repo.
package io.github.mixture.model

object SecurityCheck {
    private val KEY_PATTERN = Regex("sk-or-v1-[A-Za-z0-9]+")
    private val API_KEY_PATTERN = Regex("api[_-]?key\\s*[:=]\\s*\\S+", RegexOption.IGNORE_CASE)
    private val PASSWORD_PATTERN = Regex("password\\s*[:=]\\s*\\S+", RegexOption.IGNORE_CASE)
    private val CARD_PATTERN = Regex("\\b\\d{13,19}\\b")
    private val TOKEN_PATTERN = Regex("token\\s*[:=]\\s*\\S+", RegexOption.IGNORE_CASE)

    fun containsSecret(text: String): Boolean {
        if (KEY_PATTERN.containsMatchIn(text)) return true
        if (API_KEY_PATTERN.containsMatchIn(text)) return true
        if (PASSWORD_PATTERN.containsMatchIn(text)) return true
        if (TOKEN_PATTERN.containsMatchIn(text)) return true
        if (CARD_PATTERN.containsMatchIn(text)) return true
        return false
    }

    fun redact(text: String): String {
        var out = text
        out = KEY_PATTERN.replace(out, "[redacted]")
        out = API_KEY_PATTERN.replace(out, "api_key=[redacted]")
        out = PASSWORD_PATTERN.replace(out, "password=[redacted]")
        out = TOKEN_PATTERN.replace(out, "token=[redacted]")
        out = CARD_PATTERN.replace(out, "[redacted]")
        return out
    }
}
