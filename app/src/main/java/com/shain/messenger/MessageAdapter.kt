package com.shain.messenger

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.shain.messenger.model.Message
import com.shain.messenger.model.MessageType
import kotlinx.android.synthetic.main.send_message_quoted_row.view.*
import kotlinx.android.synthetic.main.send_message_row.view.txtBody
import java.util.*

/**
 * Created by Shain Singh on 2/2/19.
 */
class MessageAdapter(private val context: Context) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    private var messageList = ArrayList<Message>()

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
}