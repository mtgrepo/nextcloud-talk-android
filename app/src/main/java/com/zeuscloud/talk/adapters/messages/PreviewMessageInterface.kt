package com.zeuscloud.talk.adapters.messages

import com.zeuscloud.talk.models.json.chat.ChatMessage

interface PreviewMessageInterface {
    fun onPreviewMessageLongClick(chatMessage: ChatMessage)
}
