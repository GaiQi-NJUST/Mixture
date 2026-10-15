// Mixture: model client. The real client uses HTTP with the key from Secure Vault or Keystore.
// The fake client is for tests. Tests use the fake, never a real key or a network.
package io.github.mixture.model

interface ModelClient {
    fun chat(request: ModelRequest): ModelResponse
}

class FakeModelClient(
    private val fixedText: String = "Hello from the fake model.",
    private val fail: Boolean = false
) : ModelClient {
    val received = mutableListOf<ModelRequest>()

    override fun chat(request: ModelRequest): ModelResponse {
        request.validate()
        received.add(request)
        if (fail) {
            throw RuntimeException("Fake model failure")
        }
        return ModelResponse(
            id = "fake-1",
            model = request.model,
            text = fixedText,
            finishReason = "stop",
            promptTokens = 10,
            completionTokens = 5,
            totalTokens = 15
        )
    }
}
