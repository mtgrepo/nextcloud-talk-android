package com.zeuscloud.talk.adapters.messages

import com.zeuscloud.talk.models.json.chat.ChatMessage

interface CommonMessageInterface {
    fun onLongClickReactions(chatMessage: ChatMessage)
    fun onClickReaction(chatMessage: ChatMessage, emoji: String)
    fun onOpenMessageActionsDialog(chatMessage: ChatMessage)
}
