// Mixture: main activity. It renders the mixture-ui AppState with native Views.
// No Compose and no WebView are used here.
package io.github.mixture.app

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import io.github.mixture.model.ModelConfig
import io.github.mixture.model.ModelMessage
import io.github.mixture.model.ModelRequest
import io.github.mixture.model.MessageRole
import io.github.mixture.ui.AppState
import io.github.mixture.ui.ChatReducer
import io.github.mixture.ui.TaskStatus

class MainActivity : Activity() {
    private var state: AppState = AppState()
    private lateinit var messagesView: TextView
    private lateinit var statusView: TextView
    private lateinit var inputView: EditText
    private lateinit var sendButton: Button
    private lateinit var stopButton: Button
    private lateinit var approvalsView: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        messagesView = findViewById(R.id.messages_view)
        statusView = findViewById(R.id.status_view)
        inputView = findViewById(R.id.input_view)
        sendButton = findViewById(R.id.send_button)
        stopButton = findViewById(R.id.stop_button)
        approvalsView = findViewById(R.id.approvals_view)

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
        val builder = StringBuilder()
        for (message in state.messages) {
            builder.append(message.role.name).append(": ").append(message.text).append("\n\n")
        }
        messagesView.text = builder.toString()
        statusView.text = "Task: " + state.taskStatus.name + " | Model: " + state.currentModel +
            " | Termux: " + state.termuxAvailable + " | Shizuku: " + state.shizukuReady
        approvalsView.removeAllViews()
        for (card in state.pendingApprovals()) {
            val row = TextView(this)
            row.text = card.title + ": " + card.description
            approvalsView.addView(row)
            val approve = Button(this)
            approve.text = "Approve"
            approve.setOnClickListener {
                state = ChatReducer.approve(state, card.id)
                render()
            }
            approvalsView.addView(approve)
            val reject = Button(this)
            reject.text = "Reject"
            reject.setOnClickListener {
                state = ChatReducer.rejectApproval(state, card.id)
                render()
            }
            approvalsView.addView(reject)
        }
    }
}
