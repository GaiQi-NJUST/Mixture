// Mixture: Shizuku binder. This PR checks the package and reports state.
// The real Shizuku binder binding comes in a later PR. No Shizuku SDK is used here.
package io.github.mixture.app

import android.content.Context
import android.content.pm.PackageManager
import io.github.mixture.shizuku.ShizukuBridge
import io.github.mixture.shizuku.ShizukuCommand
import io.github.mixture.shizuku.ShizukuPermission
import io.github.mixture.shizuku.ShizukuState

class ShizukuBinder(private val context: Context) : ShizukuBridge {
    companion object {
        const val PACKAGE_SHIZUKU = "moe.shizuku.privileged.api"
    }

    override fun send(command: ShizukuCommand): Boolean {
        return false
    }

    override fun state(): ShizukuState {
        val installed = try {
            context.packageManager.getPackageInfo(PACKAGE_SHIZUKU, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
        if (!installed) return ShizukuState.NOT_INSTALLED
        return ShizukuState.NOT_RUNNING
    }

    override fun permission(): ShizukuPermission {
        if (state() != ShizukuState.READY) return ShizukuPermission.NOT_REQUESTED
        return ShizukuPermission.DENIED
    }
}
