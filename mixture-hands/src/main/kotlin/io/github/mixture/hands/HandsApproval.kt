// Mixture: approval for sensitive actions. Each approval covers one action only.
package io.github.mixture.hands

data class ApprovalRequest(
    val taskId: String,
    val stepIndex: Int,
    val action: HandsAction,
    val category: SensitiveCategory,
    val description: String
)

sealed class ApprovalResult {
    object Approved : ApprovalResult()
    object Denied : ApprovalResult()
    object Pending : ApprovalResult()
}

interface ApprovalStore {
    fun requestApproval(request: ApprovalRequest): ApprovalResult
}

class FakeApprovalStore(
    private val decisions: Map<String, ApprovalResult> = emptyMap(),
    private val defaultResult: ApprovalResult = ApprovalResult.Denied
) : ApprovalStore {
    val requested = mutableListOf<ApprovalRequest>()

    override fun requestApproval(request: ApprovalRequest): ApprovalResult {
        requested.add(request)
        val key = request.taskId + ":" + request.stepIndex
        return decisions[key] ?: defaultResult
    }
}
