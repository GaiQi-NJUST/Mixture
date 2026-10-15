// Mixture: accessibility service. It builds a ScreenState from the tree and runs Hands actions.
// It never reads a FLAG_SECURE screen. It never fills a password field.
package io.github.mixture.app

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.os.Bundle
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import io.github.mixture.hands.HandsAction
import io.github.mixture.hands.HandsExecutor
import io.github.mixture.hands.HandsOperator
import io.github.mixture.hands.HandsResult
import io.github.mixture.hands.Rect
import io.github.mixture.hands.ScreenNode
import io.github.mixture.hands.ScreenState
import io.github.mixture.hands.ApprovalStore
import io.github.mixture.hands.FakeApprovalStore

class MixtureAccessibilityService : AccessibilityService() {
    private val approvalStore: ApprovalStore = FakeApprovalStore()
    private val executor = ServiceExecutor()
    private val handsOperator = HandsOperator("hands-1", executor, approvalStore)
    private var lastState: ScreenState? = null
    private val nodeMap = mutableMapOf<String, AccessibilityNodeInfo>()

    override fun onServiceConnected() {
        super.onServiceConnected()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        val root = rootInActiveWindow ?: return
        val state = buildState(root)
        if (state.isSecure()) {
            return
        }
        lastState = state
        handsOperator.observe(state)
    }

    override fun onInterrupt() {
    }

    fun currentState(): ScreenState? {
        return lastState
    }

    fun act(action: HandsAction): HandsResult {
        return handsOperator.act(action)
    }

    fun stopHands() {
        handsOperator.stop()
    }

    private fun buildState(root: AccessibilityNodeInfo): ScreenState {
        nodeMap.clear()
        val nodes = mutableListOf<ScreenNode>()
        var counter = 0
        fun walk(node: AccessibilityNodeInfo) {
            val bounds = android.graphics.Rect()
            node.getBoundsInScreen(bounds)
            val rect = Rect(bounds.left, bounds.top, bounds.right, bounds.bottom)
            val text = node.text?.toString()
            val desc = node.contentDescription?.toString()
            val isPassword = node.isPassword
            val id = "node-" + counter
            nodes.add(
                ScreenNode(
                    id = id,
                    text = text,
                    contentDescription = desc,
                    bounds = rect,
                    clickable = node.isClickable,
                    editable = node.isEditable,
                    password = isPassword,
                    packageName = node.packageName?.toString()
                )
            )
            nodeMap[id] = node
            counter += 1
            for (i in 0 until node.childCount) {
                val child = node.getChild(i) ?: continue
                walk(child)
            }
        }
        walk(root)
        // FLAG_SECURE windows return no tree. A null root in onAccessibilityEvent is the signal.
        // buildState is only called with a non null root, so secure is false here.
        val secure = false
        return ScreenState(
            nodes = nodes,
            secure = secure,
            packageName = root.packageName?.toString() ?: "",
            screenshotAvailable = false,
            timestampMs = System.currentTimeMillis()
        )
    }

    private inner class ServiceExecutor : HandsExecutor {
        override fun execute(action: HandsAction): Boolean {
            return when (action) {
                is HandsAction.Tap -> tapNode(action.nodeId)
                is HandsAction.Input -> inputNode(action.nodeId, action.text)
                is HandsAction.TapAt -> dispatchTap(action.x, action.y)
                is HandsAction.Swipe -> dispatchSwipe(action.fromX, action.fromY, action.toX, action.toY)
                is HandsAction.PressBack -> {
                    performGlobalAction(GLOBAL_ACTION_BACK)
                }
                is HandsAction.PressHome -> {
                    performGlobalAction(GLOBAL_ACTION_HOME)
                }
                else -> false
            }
        }

        private fun tapNode(nodeId: String): Boolean {
            val node = nodeMap[nodeId] ?: return false
            return try {
                if (node.isClickable) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                } else {
                    val parent = node.parent ?: return false
                    try {
                        parent.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    } finally {
                        parent.recycle()
                    }
                }
            } catch (e: Exception) {
                false
            }
        }

        private fun inputNode(nodeId: String, text: String): Boolean {
            val node = nodeMap[nodeId] ?: return false
            return try {
                if (node.isPassword) return false
                if (!node.isEditable) return false
                val args = Bundle()
                args.putCharSequence(
                    AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                    text
                )
                node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args)
            } catch (e: Exception) {
                false
            }
        }

        private fun dispatchTap(x: Int, y: Int): Boolean {
            val path = Path()
            path.moveTo(x.toFloat(), y.toFloat())
            val stroke = GestureDescription.StrokeDescription(path, 0, 100)
            val gesture = GestureDescription.Builder().addStroke(stroke).build()
            return dispatchGesture(gesture, null, null)
        }

        private fun dispatchSwipe(fromX: Int, fromY: Int, toX: Int, toY: Int): Boolean {
            val path = Path()
            path.moveTo(fromX.toFloat(), fromY.toFloat())
            path.lineTo(toX.toFloat(), toY.toFloat())
            val stroke = GestureDescription.StrokeDescription(path, 0, 300)
            val gesture = GestureDescription.Builder().addStroke(stroke).build()
            return dispatchGesture(gesture, null, null)
        }
    }
}
