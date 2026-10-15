// Mixture: model config from docs/model-api-openrouter.md. Do not hard code one model in the app.
// The canonical catalogue is nanomuse/llm/providers.json. These defaults are the OpenRouter entry.
package io.github.mixture.model

object ModelConfig {
    const val PROVIDER_OPENROUTER = "openrouter"
    const val PROTOCOL_OPENAI = "openai"
    const val BASE_URL = "https://openrouter.ai/api/v1"
    const val DEFAULT_CHAT = "deepseek/deepseek-v4.1-flash"
    const val DEFAULT_HANDS = "qwen/qwen3.8-27b"
    const val DEFAULT_IMAGE = "openai/gpt-image-2"
    val VIDEO_MODEL: String? = null
    val CAPABILITIES = setOf("chat", "vision", "image")

    private val MODEL_PATTERN = Regex("^[a-z0-9-]+/[a-z0-9][a-z0-9._-]*$")

    fun isValidModelName(model: String): Boolean {
        return MODEL_PATTERN.matches(model)
    }

    fun hasCapability(capability: String): Boolean {
        return capability in CAPABILITIES
    }

    fun missingCapabilityMessage(job: String): String {
        return "Making clips is not available on OpenRouter. No video model is listed in the catalogue."
    }
}
