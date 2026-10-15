// Mixture: Termux RUN_COMMAND intent sender. It implements the TermuxBridge from mixture-termux.
package io.github.mixture.app

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import io.github.mixture.termux.TermuxBridge
import io.github.mixture.termux.TermuxCommand
import io.github.mixture.termux.TermuxSendResult

class TermuxIntentSender(private val context: Context) : TermuxBridge {
    override fun send(command: TermuxCommand): TermuxSendResult {
        try {
            command.validate()
        } catch (e: IllegalArgumentException) {
            return TermuxSendResult.Rejected(command.taskId, e.message ?: "Invalid command")
        }
        if (!isTermuxAvailable()) {
            return TermuxSendResult.Rejected(command.taskId, "Termux is not available")
        }
        val intent = Intent()
        intent.setClassName(TermuxCommand.PACKAGE_TERMUX, TermuxCommand.SERVICE_CLASS)
        intent.action = TermuxCommand.ACTION_RUN_COMMAND
        val extras = command.toExtras()
        intent.putExtra(
            TermuxCommand.EXTRA_COMMAND_PATH,
            extras[TermuxCommand.EXTRA_COMMAND_PATH] as String
        )
        val args = extras[TermuxCommand.EXTRA_ARGUMENTS] as Array<String>
        intent.putExtra(TermuxCommand.EXTRA_ARGUMENTS, args)
        intent.putExtra(
            TermuxCommand.EXTRA_WORKDIR,
            extras[TermuxCommand.EXTRA_WORKDIR] as String
        )
        intent.putExtra(
            TermuxCommand.EXTRA_BACKGROUND,
            extras[TermuxCommand.EXTRA_BACKGROUND] as Boolean
        )
        intent.putExtra(
            TermuxCommand.EXTRA_SESSION_ACTION,
            extras[TermuxCommand.EXTRA_SESSION_ACTION] as String
        )
        intent.putExtra(
            TermuxCommand.EXTRA_COMMAND_LABEL,
            extras[TermuxCommand.EXTRA_COMMAND_LABEL] as String
        )
        return try {
            context.startService(intent)
            TermuxSendResult.Sent(command.taskId)
        } catch (e: Exception) {
            TermuxSendResult.Failed(command.taskId, e.message ?: "startService failed")
        }
    }

    override fun stop(taskId: String): Boolean {
        return true
    }

    override fun isTermuxAvailable(): Boolean {
        return try {
            context.packageManager.getPackageInfo(TermuxCommand.PACKAGE_TERMUX, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
}
