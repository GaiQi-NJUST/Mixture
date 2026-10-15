// Mixture: model request and response. No key is stored here. The key comes from Secure Vault or Keystore.
package io.github.mixture.model

enum class MessageRole {
    SYSTEM,
    USER,
    ASSISTANT
}

data class ModelMessage(
    val role: MessageRole,
    val content: String,
    val imageUrl: String? = null
)

data class ModelRequest(
    val model: String,
    val messages: List<ModelMessage>,
    val maxTokens: Int = 4096,
    val temperature: Double = 0.7
) {
    fun validate() {
        require(ModelConfig.isValidModelName(model)) { "Invalid model name: $model" }
        require(messages.isNotEmpty()) { "Messages must not be empty" }
        require(maxTokens > 0) { "maxTokens must be positive" }
        require(temperature >= 0.0 && temperature <= 2.0) { "Temperature must be between 0 and 2" }
        for (message in messages) {
            require(message.content.isNotBlank()) { "Message content must not be blank" }
            require(!SecurityCheck.containsSecret(message.content)) { "Message content contains a secret pattern" }
        }
    }
}

data class ModelResponse(
    val id: String,
    val model: String,
    val text: String,
    val finishReason: String,
    val promptTokens: Int,
    val completionTokens: Int,
    val totalTokens: Int
)
