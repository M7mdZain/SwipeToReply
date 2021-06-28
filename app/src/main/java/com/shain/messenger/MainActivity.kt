package com.shain.messenger

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.Animation
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.shain.messenger.model.Message
import com.shain.messenger.viewmodel.MainActivityViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.reply_layout.*
import java.util.*


class MainActivity : AppCompatActivity(), MessageAdapter.QuoteClickListener {

    private lateinit var mainActivityViewModel: MainActivityViewModel
    private var messageList = ArrayList<Message>()
    private var quotedMessagePos = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)

        val adapter = MessageAdapter(this)
        adapter.setQuoteClickListener(this)
        recyclerView.adapter = adapter
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linearLayoutManager

        val messageSwipeController = MessageSwipeController(this, object : SwipeControllerActions {
            override fun showReplyUI(position: Int) {
                quotedMessagePos = position
                showQuotedMessage(messageList[position])
            }
        })

        val itemTouchHelper = ItemTouchHelper(messageSwipeController)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        send_button.setOnClickListener {
            if (edit_message.text.trim().isNotEmpty()) {

                if (reply_layout.visibility == View.VISIBLE) {
                    hideReplyLayout()
                    mainActivityViewModel.addQuotedMessage(
                        edit_message.text.trim().toString(),
                        textQuotedMessage.text.toString(),
                        quotedMessagePos
                    )

                } else {
                    mainActivityViewModel.addMessage(edit_message.text.trim().toString())
                }
                edit_message.text.clear()
            }
        }

        mainActivityViewModel.getDisplayMessage()
            .observe(this, Observer<List<Message>> { messages ->
                messageList.clear()
                messageList.addAll(messages)
                adapter.setMessages(messages)
                Handler().postDelayed({
                    recyclerView.scrollToPosition(adapter.itemCount - 1);
                }, 100)
            })

        cancelButton.setOnClickListener {
            hideReplyLayout()

        }

    }

    companion object {
        const val ANIMATION_DURATION: Long = 300
    }

    private fun hideReplyLayout() {

        val resizeAnim = ResizeAnim(reply_layout, mainActivityViewModel.currentMessageHeight, 0)
        resizeAnim.duration = ANIMATION_DURATION

        Handler().postDelayed({
            reply_layout.layout(0, -reply_layout.height, reply_layout.width, 0)
            reply_layout.requestLayout()
            reply_layout.forceLayout()
            reply_layout.visibility = View.GONE

        }, ANIMATION_DURATION - 50)

        reply_layout.startAnimation(resizeAnim)
        mainActivityViewModel.currentMessageHeight = 0

        resizeAnim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                val params = reply_layout.layoutParams
                params.height = 0
                reply_layout.layoutParams = params
            }

            override fun onAnimationRepeat(animation: Animation?) {
            }
        })

    }

    private fun showQuotedMessage(message: Message) {
        edit_message.requestFocus()
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(edit_message, InputMethodManager.SHOW_IMPLICIT)

        textQuotedMessage.text = message.body
        val height = textQuotedMessage.getActualHeight()
        val startHeight = mainActivityViewModel.currentMessageHeight

        if (height != startHeight) {

            if (reply_layout.visibility == View.GONE)
                Handler().postDelayed({
                    reply_layout.visibility = View.VISIBLE
                }, 50)

            val targetHeight = height - startHeight

            val resizeAnim =
                ResizeAnim(
                    reply_layout,
                    startHeight,
                    targetHeight
                )

            resizeAnim.duration = ANIMATION_DURATION
            reply_layout.startAnimation(resizeAnim)

            mainActivityViewModel.currentMessageHeight = height

        }

    }

    private fun TextView.getActualHeight(): Int {
        textQuotedMessage.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        return measuredHeight
    }

    override fun onQuoteClick(position: Int) {
        recyclerView.smoothScrollToPosition(position - 1)
        (recyclerView.adapter as MessageAdapter).blinkItem(position)
    }
}



