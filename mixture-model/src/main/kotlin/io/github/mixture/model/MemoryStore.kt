// Mixture: simple memory store for user preferences. No secrets are stored here.
package io.github.mixture.model

class MemoryStore {
    private val data = mutableMapOf<String, String>()

    fun put(key: String, value: String) {
        require(key.isNotBlank()) { "Key must not be blank" }
        val lowerKey = key.lowercase()
        require("password" !in lowerKey) { "Secrets must not be stored in memory" }
        require("token" !in lowerKey) { "Secrets must not be stored in memory" }
        require("api_key" !in lowerKey && "apikey" !in lowerKey) { "Secrets must not be stored in memory" }
        require(!SecurityCheck.containsSecret(value)) { "Value contains a secret pattern" }
        data[key] = value
    }

    fun get(key: String): String? {
        return data[key]
    }

    fun remove(key: String): Boolean {
        return data.remove(key) != null
    }

    fun keys(): Set<String> {
        return data.keys.toSet()
    }

    fun size(): Int {
        return data.size
    }
}
