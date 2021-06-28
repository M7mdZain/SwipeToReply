package com.shain.messenger

import android.R.attr.button
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import com.shain.messenger.model.Message
import com.shain.messenger.model.MessageType
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.send_message_quoted_row.view.*
import kotlinx.android.synthetic.main.send_message_row.view.txtBody
import java.util.*
import java.util.logging.Handler


/**
 * Created by Shain Singh on 2/2/19.
 */
class MessageAdapter(private val context: Context) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    private var messageList = ArrayList<Message>()
    private var blinkItem = NO_POSITION

    interface QuoteClickListener {
        fun onQuoteClick(position: Int)
    }

    private var mQuoteClickListener: QuoteClickListener? = null

    fun setQuoteClickListener(listener: QuoteClickListener) {
        mQuoteClickListener = listener
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MessageViewHolder {

        val messageViewHolder =
            MessageViewHolder(LayoutInflater.from(context).inflate(viewType, viewGroup, false))

        messageViewHolder.quoteLayout?.setOnClickListener {
            mQuoteClickListener?.onQuoteClick(messageList[messageViewHolder.adapterPosition].quotePos)
        }

        return messageViewHolder


    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messageList[position]
        holder.txtSendMsg.text = message.body
        holder.txtQuotedMsg?.text = message.quote

        if (blinkItem == position) {
            val anim: Animation = AlphaAnimation(0.0f, 0.5f)
            android.os.Handler().postDelayed({
                anim.duration = 200
                holder.itemView.startAnimation(anim)
                anim.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {
                    }

                    override fun onAnimationEnd(animation: Animation?) {
                        blinkItem = NO_POSITION
                    }

                    override fun onAnimationRepeat(animation: Animation?) {
                    }
                })
            }, 100)

        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItemViewType(messageList[position])
    }

    private fun getItemViewType(message: Message): Int {
        return if (message.type == MessageType.SEND)
            if (message.quotePos == -1) R.layout.send_message_row
            else R.layout.send_message_quoted_row
        else
            if (message.quotePos == -1) R.layout.received_message_row
            else R.layout.received_message_quoted_row
    }

    fun setMessages(albumList: List<Message>) {
        this.messageList = albumList as ArrayList<Message>
        notifyDataSetChanged()
    }

    class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtSendMsg = view.txtBody!!
        val txtQuotedMsg: TextView? = view.textQuote
        val quoteLayout: ConstraintLayout? = view.reply
    }


    fun blinkItem(position: Int) {
        blinkItem = position
        notifyItemChanged(position)
    }
}