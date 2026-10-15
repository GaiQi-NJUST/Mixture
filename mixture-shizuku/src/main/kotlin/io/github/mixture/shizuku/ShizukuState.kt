// Mixture: Shizuku availability and permission state.
package io.github.mixture.shizuku

enum class ShizukuState {
    NOT_INSTALLED,
    NOT_RUNNING,
    PERMISSION_DENIED,
    READY,
    UNKNOWN
}

enum class ShizukuPermission {
    GRANTED,
    DENIED,
    NOT_REQUESTED
}

fun degradationMessage(state: ShizukuState): String {
    return when (state) {
        ShizukuState.READY -> "Shizuku is ready."
        ShizukuState.NOT_INSTALLED -> "Shizuku is not installed. Termux and Hands still work. Shizuku features are off."
        ShizukuState.NOT_RUNNING -> "Shizuku is not running. Termux and Hands still work. Shizuku features are off."
        ShizukuState.PERMISSION_DENIED -> "Shizuku permission was denied. Termux and Hands still work. Shizuku features are off."
        ShizukuState.UNKNOWN -> "Shizuku state is unknown. Termux and Hands still work. Shizuku features are off."
    }
}
