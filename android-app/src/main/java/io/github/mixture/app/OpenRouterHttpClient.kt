// Mixture: OpenRouter HTTP client. It uses HttpURLConnection and org.json, both in Android.
// The key is passed in, never logged, never stored here. The caller gets it from KeystoreStore.
package io.github.mixture.app

import io.github.mixture.model.MessageRole
import io.github.mixture.model.ModelClient
import io.github.mixture.model.ModelConfig
import io.github.mixture.model.ModelRequest
import io.github.mixture.model.ModelResponse
import io.github.mixture.model.SecurityCheck
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONArray
import org.json.JSONObject

class OpenRouterHttpClient(private val apiKey: String) : ModelClient {
    override fun chat(request: ModelRequest): ModelResponse {
        request.validate()
        require(!SecurityCheck.containsSecret(apiKey)) { "Key must not contain a secret pattern" }
        val url = URL(ModelConfig.BASE_URL + "/chat/completions")
        val conn = url.openConnection() as HttpURLConnection
        try {
            conn.requestMethod = "POST"
            conn.setRequestProperty("Authorization", "Bearer " + apiKey)
            conn.setRequestProperty("Content-Type", "application/json")
            conn.setRequestProperty("HTTP-Referer", "https://github.com/GaiQi-NJUST/Mixture")
            conn.setRequestProperty("X-Title", "Mixture")
            conn.doOutput = true
            conn.connectTimeout = 30000
            conn.readTimeout = 60000
            val body = buildBody(request)
            conn.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
            val code = conn.responseCode
            val stream = if (code in 200..299) conn.inputStream else conn.errorStream
            val text = BufferedReader(InputStreamReader(stream, Charsets.UTF_8)).use { it.readText() }
            if (code !in 200..299) {
                throw RuntimeException("OpenRouter HTTP $code: " + text.take(500))
            }
            return parse(text, request.model)
        } finally {
            conn.disconnect()
        }
    }

    private fun buildBody(request: ModelRequest): String {
        val root = JSONObject()
        root.put("model", request.model)
        root.put("max_tokens", request.maxTokens)
        root.put("temperature", request.temperature)
        val messages = JSONArray()
        for (message in request.messages) {
            val item = JSONObject()
            item.put("role", when (message.role) {
                MessageRole.SYSTEM -> "system"
                MessageRole.USER -> "user"
                MessageRole.ASSISTANT -> "assistant"
            })
            if (message.imageUrl != null) {
                val parts = JSONArray()
                val textPart = JSONObject()
                textPart.put("type", "text")
                textPart.put("text", message.content)
                parts.put(textPart)
                val imagePart = JSONObject()
                imagePart.put("type", "image_url")
                val imageUrl = JSONObject()
                imageUrl.put("url", message.imageUrl)
                imagePart.put("image_url", imageUrl)
                parts.put(imagePart)
                item.put("content", parts)
            } else {
                item.put("content", message.content)
            }
            messages.put(item)
        }
        root.put("messages", messages)
        return root.toString()
    }

    private fun parse(text: String, model: String): ModelResponse {
        val root = JSONObject(text)
        val id = root.optString("id", "unknown")
        val choices = root.optJSONArray("choices")
        var content = ""
        var finishReason = "stop"
        if (choices != null && choices.length() > 0) {
            val first = choices.getJSONObject(0)
            finishReason = first.optString("finish_reason", "stop")
            val message = first.optJSONObject("message")
            if (message != null) {
                content = message.optString("content", "")
            }
        }
        val usage = root.optJSONObject("usage")
        val prompt = usage?.optInt("prompt_tokens", 0) ?: 0
        val completion = usage?.optInt("completion_tokens", 0) ?: 0
        val total = usage?.optInt("total_tokens", prompt + completion) ?: (prompt + completion)
        return ModelResponse(
            id = id,
            model = model,
            text = content,
            finishReason = finishReason,
            promptTokens = prompt,
            completionTokens = completion,
            totalTokens = total
        )
    }
}
