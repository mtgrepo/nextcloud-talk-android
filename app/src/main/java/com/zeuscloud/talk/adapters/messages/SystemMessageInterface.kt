package com.zeuscloud.talk.adapters.messages

import com.zeuscloud.talk.models.json.chat.ChatMessage

interface SystemMessageInterface {
    fun expandSystemMessage(chatMessage: ChatMessage)
    fun collapseSystemMessages()
}
