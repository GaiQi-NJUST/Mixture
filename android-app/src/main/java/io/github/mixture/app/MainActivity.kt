// Mixture: main activity. It renders the mixture-ui AppState with native Views.
// No Compose and no WebView are used here.
package io.github.mixture.app

import android.app.Activity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import io.github.mixture.model.ModelConfig
import io.github.mixture.model.ModelMessage
import io.github.mixture.model.ModelRequest
import io.github.mixture.model.MessageRole
import io.github.mixture.ui.AppState
import io.github.mixture.ui.ApprovalStatus
import io.github.mixture.ui.ChatReducer
import io.github.mixture.ui.MessageRole as UiRole
import io.github.mixture.ui.TaskStatus

class MainActivity : Activity() {
    private var state: AppState = AppState()
    private lateinit var messagesContainer: LinearLayout
    private lateinit var statusView: TextView
    private lateinit var inputView: EditText
    private lateinit var sendButton: Button
    private lateinit var stopButton: Button
    private lateinit var approvalsView: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        messagesContainer = findViewById(R.id.messages_container)
        statusView = findViewById(R.id.status_view)
        inputView = findViewById(R.id.input_view)
        sendButton = findViewById(R.id.send_button)
        stopButton = findViewById(R.id.stop_button)
        approvalsView = findViewById(R.id.approvals_view)

        pressScale(sendButton)
        pressScale(stopButton)

        sendButton.setOnClickListener {
            val text = inputView.text.toString()
            if (text.isBlank()) return@setOnClickListener
            send(text)
        }
        stopButton.setOnClickListener {
            state = ChatReducer.stopTask(state)
            render()
        }
        render()
    }

    private fun pressScale(button: Button) {
        button.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    view.scaleX = 0.98f
                    view.scaleY = 0.98f
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    view.scaleX = 1.0f
                    view.scaleY = 1.0f
                }
            }
            false
        }
    }

    private fun send(text: String) {
        try {
            state = ChatReducer.sendMessage(state, "m-" + System.currentTimeMillis(), text)
        } catch (e: IllegalArgumentException) {
            statusView.text = e.message ?: "Cannot send"
            return
        }
        render()
        Thread {
            val reply = runModel(text)
            runOnUiThread {
                state = ChatReducer.receiveMessage(state, "a-" + System.currentTimeMillis(), reply)
                if (state.taskStatus == TaskStatus.RUNNING) {
                    state = ChatReducer.finishTask(state, true)
                }
                render()
            }
        }.start()
    }

    private fun runModel(text: String): String {
        val store = KeystoreStore(this)
        val key = store.getOpenRouterKey()
        if (key.isNullOrBlank()) {
            return "Please add your OpenRouter key in Settings. No key is pasted in chat."
        }
        val client = OpenRouterHttpClient(key)
        return try {
            val request = ModelRequest(
                model = state.currentModel.ifBlank { ModelConfig.DEFAULT_CHAT },
                messages = listOf(ModelMessage(MessageRole.USER, text))
            )
            val response = client.chat(request)
            response.text
        } catch (e: Exception) {
            "Model call failed: " + (e.message ?: "unknown error")
        }
    }

    private fun render() {
        messagesContainer.removeAllViews()
        for (message in state.messages) {
            val row = TextView(this)
            row.text = message.text
            row.textSize = 16f
            row.setPadding(12, 8, 12, 8)
            row.breakStrategy = android.graphics.text.LineBreaker.BREAK_STRATEGY_BALANCED
            if (message.role == UiRole.USER) {
                row.setBackgroundResource(R.drawable.bubble_bg_user)
            } else {
                row.setBackgroundResource(R.drawable.bubble_bg_assistant)
            }
            messagesContainer.addView(row)
        }
        statusView.text = "Task: " + state.taskStatus.name + " | Model: " + state.currentModel +
            " | Termux: " + state.termuxAvailable + " | Shizuku: " + state.shizukuReady
        approvalsView.removeAllViews()
        for (card in state.approvals) {
            val container = LinearLayout(this)
            container.orientation = LinearLayout.VERTICAL
            container.setBackgroundResource(R.drawable.approval_card_bg)
            container.elevation = 2f
            container.setPadding(12, 8, 12, 8)
            val title = TextView(this)
            title.text = card.title + ": " + card.description
            container.addView(title)
            val status = TextView(this)
            status.text = "Status: " + card.status.name
            if (card.status == ApprovalStatus.ERROR) {
                status.setTextColor(resources.getColor(R.color.fill_danger, null))
            }
            container.addView(status)
            if (card.status == ApprovalStatus.PENDING) {
                val approve = Button(this)
                approve.text = "Approve"
                approve.setOnClickListener {
                    state = ChatReducer.approve(state, card.id)
                    render()
                }
                container.addView(approve)
                val reject = Button(this)
                reject.text = "Reject"
                reject.setOnClickListener {
                    state = ChatReducer.rejectApproval(state, card.id)
                    render()
                }
                container.addView(reject)
            }
            approvalsView.addView(container)
        }
    }
}
