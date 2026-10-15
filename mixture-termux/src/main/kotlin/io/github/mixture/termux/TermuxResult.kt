// Mixture: result returned from Termux for one task.
package io.github.mixture.termux

data class TermuxResult(
    val taskId: String,
    val stdout: String,
    val stderr: String,
    val exitCode: Int,
    val stdoutOriginalLength: Int = stdout.length,
    val stderrOriginalLength: Int = stderr.length,
    val error: String? = null,
    val errorMessage: String? = null
) {
    fun isSuccess(): Boolean {
        return error == null && exitCode == 0
    }

    fun wasTruncated(): Boolean {
        return stdout.length < stdoutOriginalLength || stderr.length < stderrOriginalLength
    }

    companion object {
        const val RESULT_OK = -1
        const val MAX_COMBINED_STDOUT_STDERR = 100 * 1024
        const val MAX_ERROR_MESSAGE = 25 * 1024

        fun fromBundle(
            taskId: String,
            stdout: String?,
            stderr: String?,
            exitCode: Int?,
            stdoutOriginalLength: Int?,
            stderrOriginalLength: Int?,
            error: String?,
            errorMessage: String?
        ): TermuxResult {
            val out = stdout ?: ""
            val err = stderr ?: ""
            return TermuxResult(
                taskId = taskId,
                stdout = out,
                stderr = err,
                exitCode = exitCode ?: -1,
                stdoutOriginalLength = stdoutOriginalLength ?: out.length,
                stderrOriginalLength = stderrOriginalLength ?: err.length,
                error = error,
                errorMessage = errorMessage
            )
        }
    }
}
