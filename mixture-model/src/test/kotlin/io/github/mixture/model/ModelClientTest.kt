// Mixture: tests for model config, client, log, memory, and security.
package io.github.mixture.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ModelClientTest {
    @Test
    fun defaultsMatchModelApiDoc() {
        assertEquals("openrouter", ModelConfig.PROVIDER_OPENROUTER)
        assertEquals("openai", ModelConfig.PROTOCOL_OPENAI)
        assertEquals("https://openrouter.ai/api/v1", ModelConfig.BASE_URL)
        assertEquals("deepseek/deepseek-v4.1-flash", ModelConfig.DEFAULT_CHAT)
        assertEquals("qwen/qwen3.8-27b", ModelConfig.DEFAULT_HANDS)
        assertEquals("openai/gpt-image-2", ModelConfig.DEFAULT_IMAGE)
        assertNull(ModelConfig.VIDEO_MODEL)
        assertTrue(ModelConfig.hasCapability("chat"))
        assertTrue(ModelConfig.hasCapability("vision"))
        assertTrue(ModelConfig.hasCapability("image"))
        assertFalse(ModelConfig.hasCapability("video"))
    }

    @Test
    fun validModelNameIsAccepted() {
        assertTrue(ModelConfig.isValidModelName("deepseek/deepseek-v4.1-flash"))
        assertTrue(ModelConfig.isValidModelName("qwen/qwen3.8-27b"))
    }

    @Test
    fun invalidModelNameIsRejected() {
        assertFalse(ModelConfig.isValidModelName(""))
        assertFalse(ModelConfig.isValidModelName("no-slash"))
        assertFalse(ModelConfig.isValidModelName("UPPER/model"))
    }

    @Test
    fun fakeClientReturnsFixedTextAndUsage() {
        val client = FakeModelClient()
        val request = ModelRequest(
            model = ModelConfig.DEFAULT_CHAT,
            messages = listOf(ModelMessage(MessageRole.USER, "hello"))
        )
        val response = client.chat(request)
        assertEquals("Hello from the fake model.", response.text)
        assertEquals(15, response.totalTokens)
        assertEquals(1, client.received.size)
    }

    @Test
    fun requestWithKeyInContentIsRejected() {
        val request = ModelRequest(
            model = ModelConfig.DEFAULT_CHAT,
            messages = listOf(ModelMessage(MessageRole.USER, "my key is sk-or-v1-abc123"))
        )
        try {
            request.validate()
            throw AssertionError("Expected validate to fail")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message!!.contains("secret"))
        }
    }

    @Test
    fun emptyMessagesAreRejected() {
        val request = ModelRequest(model = ModelConfig.DEFAULT_CHAT, messages = emptyList())
        try {
            request.validate()
            throw AssertionError("Expected validate to fail")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message!!.contains("empty"))
        }
    }

    @Test
    fun securityCheckDetectsAndRedactsSecrets() {
        assertTrue(SecurityCheck.containsSecret("key sk-or-v1-abc123 here"))
        assertTrue(SecurityCheck.containsSecret("password: hunter2"))
        assertTrue(SecurityCheck.containsSecret("card 4111111111111111"))
        assertFalse(SecurityCheck.containsSecret("hello world"))
        val redacted = SecurityCheck.redact("key sk-or-v1-abc123 and password: hunter2")
        assertFalse(redacted.contains("sk-or-v1-abc123"))
        assertFalse(redacted.contains("hunter2"))
        assertTrue(redacted.contains("[redacted]"))
    }

    @Test
    fun taskLogRedactsBeforeStoring() {
        val log = TaskLog()
        log.append("task-1", 0, "send sk-or-v1-abc123", "ok", 1L)
        log.append("task-1", 1, "tap", "done", 2L)
        assertEquals(2, log.size())
        assertEquals(2, log.entriesFor("task-1").size)
        assertFalse(log.toText().contains("sk-or-v1-abc123"))
        assertTrue(log.toText().contains("[redacted]"))
    }

    @Test
    fun memoryStorePutsGetsAndRemoves() {
        val store = MemoryStore()
        store.put("theme", "light")
        assertEquals("light", store.get("theme"))
        assertEquals(1, store.size())
        assertTrue(store.remove("theme"))
        assertNull(store.get("theme"))
    }

    @Test
    fun memoryStoreRejectsSecrets() {
        val store = MemoryStore()
        try {
            store.put("api_key", "sk-or-v1-abc123")
            throw AssertionError("Expected put to fail")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message!!.contains("Secrets"))
        }
        try {
            store.put("note", "my password: hunter2")
            throw AssertionError("Expected put to fail")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message!!.contains("secret"))
        }
        assertEquals(0, store.size())
    }

    @Test
    fun missingVideoCapabilityMessageIsClear() {
        val msg = ModelConfig.missingCapabilityMessage("Making clips")
        assertTrue(msg.contains("not available"))
        assertTrue(msg.contains("OpenRouter"))
    }
}
