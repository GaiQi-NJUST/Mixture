// Mixture: one Hands action. Only one action runs per turn.
package io.github.mixture.hands

enum class SensitiveCategory {
    PAY,
    TRANSFER,
    SEND,
    DELETE,
    PUBLISH,
    CONFIRM
}

sealed class HandsAction {
    abstract fun describe(): String
    abstract fun sensitive(): SensitiveCategory?

    data class Tap(val nodeId: String) : HandsAction() {
        override fun describe(): String = "Tap node $nodeId"
        override fun sensitive(): SensitiveCategory? = null
    }

    data class TapAt(val x: Int, val y: Int) : HandsAction() {
        override fun describe(): String = "Tap at $x, $y"
        override fun sensitive(): SensitiveCategory? = null
    }

    data class Swipe(val fromX: Int, val fromY: Int, val toX: Int, val toY: Int) : HandsAction() {
        override fun describe(): String = "Swipe from $fromX, $fromY to $toX, $toY"
        override fun sensitive(): SensitiveCategory? = null
    }

    data class Input(val nodeId: String, val text: String) : HandsAction() {
        override fun describe(): String = "Input into node $nodeId"
        override fun sensitive(): SensitiveCategory? = null
    }

    data class PressBack(val dummy: Int = 0) : HandsAction() {
        override fun describe(): String = "Press back"
        override fun sensitive(): SensitiveCategory? = null
    }

    data class PressHome(val dummy: Int = 0) : HandsAction() {
        override fun describe(): String = "Press home"
        override fun sensitive(): SensitiveCategory? = null
    }

    data class OpenApp(val packageName: String) : HandsAction() {
        override fun describe(): String = "Open app $packageName"
        override fun sensitive(): SensitiveCategory? = null
    }

    data class Wait(val ms: Long) : HandsAction() {
        override fun describe(): String = "Wait $ms ms"
        override fun sensitive(): SensitiveCategory? = null
    }

    data class Sensitive(val inner: HandsAction, val category: SensitiveCategory) : HandsAction() {
        override fun describe(): String = "Sensitive ${category.name}: ${inner.describe()}"
        override fun sensitive(): SensitiveCategory? = category
    }
}
