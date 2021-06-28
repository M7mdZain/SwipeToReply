package com.shain.messenger.model

/**
 * Created by Shain Singh on 2/2/19.
 */
data class Message(var body: String, var time: Long, var type: Int) {

    var quote: String = ""
    var quotePos: Int = -1

    constructor(
        body: String,
        time: Long,
        type: Int,
        quote: String,
        quotePos: Int
    ) : this(body, time, type) {
        this.quote = quote
        this.quotePos = quotePos
    }

}

object MessageType {
    const val SEND = 1
    const val RECEIVED = 2
}