/*
 * Nextcloud Talk application
 *
 * @author Mario Danic
 * @author Andy Scherzinger
 * @author Tim Krüger
 * @author Marcel Hibbe
 * Copyright (C) 2022-2023 Marcel Hibbe <dev@mhibbe.de>
 * Copyright (C) 2021 Tim Krüger <t@timkrueger.me>
 * Copyright (C) 2021 Andy Scherzinger <info@andy-scherzinger.de>
 * Copyright (C) 2017-2018 Mario Danic <mario@lovelyhq.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.zeuscloud.talk.adapters.messages

import android.content.Context
import android.text.TextUtils
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import autodagger.AutoInjector
import coil.load
import com.zeuscloud.talk.R
import com.zeuscloud.talk.application.NextcloudTalkApplication
import com.zeuscloud.talk.application.NextcloudTalkApplication.Companion.sharedApplication
import com.zeuscloud.talk.chat.ChatActivity
import com.zeuscloud.talk.databinding.ItemCustomIncomingTextMessageBinding
import com.zeuscloud.talk.extensions.loadBotsAvatar
import com.zeuscloud.talk.extensions.loadChangelogBotAvatar
import com.zeuscloud.talk.models.json.chat.ChatMessage
import com.zeuscloud.talk.ui.theme.ViewThemeUtils
import com.zeuscloud.talk.utils.ApiUtils
import com.zeuscloud.talk.utils.DateUtils
import com.zeuscloud.talk.utils.TextMatchers
import com.zeuscloud.talk.utils.message.MessageUtils
import com.zeuscloud.talk.utils.preferences.AppPreferences
import com.stfalcon.chatkit.messages.MessageHolders
import javax.inject.Inject

@AutoInjector(NextcloudTalkApplication::class)
class IncomingTextMessageViewHolder(itemView: View, payload: Any) :
    MessageHolders.IncomingTextMessageViewHolder<ChatMessage>(itemView, payload) {

    private val binding: ItemCustomIncomingTextMessageBinding = ItemCustomIncomingTextMessageBinding.bind(itemView)

    @Inject
    lateinit var context: Context

    @Inject
    lateinit var viewThemeUtils: ViewThemeUtils

    @Inject
    lateinit var messageUtils: MessageUtils

    @Inject
    lateinit var appPreferences: AppPreferences

    @Inject
    lateinit var dateUtils: DateUtils

    lateinit var commonMessageInterface: CommonMessageInterface

    override fun onBind(message: ChatMessage) {
        super.onBind(message)
        sharedApplication!!.componentApplication.inject(this)

        setAvatarAndAuthorOnMessageItem(message)

        colorizeMessageBubble(message)

        itemView.isSelected = false

        var textSize = context.resources!!.getDimension(R.dimen.chat_text_size)

        var processedMessageText = messageUtils.enrichChatMessageText(
            binding.messageText.context,
            message,
            true,
            viewThemeUtils
        )

        processedMessageText = messageUtils.processMessageParameters(
            binding.messageText.context,
            viewThemeUtils,
            processedMessageText!!,
            message,
            itemView
        )

        val messageParameters = message.messageParameters
        if (
            (messageParameters == null || messageParameters.size <= 0) &&
            TextMatchers.isMessageWithSingleEmoticonOnly(message.text)
        ) {
            textSize = (textSize * TEXT_SIZE_MULTIPLIER).toFloat()
            itemView.isSelected = true
            binding.messageAuthor.visibility = View.GONE
        }

        binding.messageText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
        binding.messageText.text = processedMessageText

        binding.messageTime.text = dateUtils.getLocalTimeStringFromTimestamp(message.timestamp)

        // parent message handling
        if (!message.isDeleted && message.parentMessage != null) {
            processParentMessage(message)
            binding.messageQuote.quotedChatMessageView.visibility = View.VISIBLE
        } else {
            binding.messageQuote.quotedChatMessageView.visibility = View.GONE
        }

        itemView.setTag(R.string.replyable_message_view_tag, message.replyable)

        Reaction().showReactions(
            message,
            ::clickOnReaction,
            ::longClickOnReaction,
            binding.reactions,
            binding.messageText.context,
            false,
            viewThemeUtils
        )
    }

    private fun longClickOnReaction(chatMessage: ChatMessage) {
        commonMessageInterface.onLongClickReactions(chatMessage)
    }

    private fun clickOnReaction(chatMessage: ChatMessage, emoji: String) {
        commonMessageInterface.onClickReaction(chatMessage, emoji)
    }

    private fun setAvatarAndAuthorOnMessageItem(message: ChatMessage) {
        val author: String = message.actorDisplayName!!
        if (!TextUtils.isEmpty(author)) {
            binding.messageAuthor.visibility = View.VISIBLE
            binding.messageAuthor.text = author
            binding.messageUserAvatar.setOnClickListener {
                (payload as? MessagePayload)?.profileBottomSheet?.showFor(message.actorId!!, itemView.context)
            }
        } else {
            binding.messageAuthor.setText(R.string.nc_nick_guest)
        }

        if (!message.isGrouped && !message.isOneToOneConversation && !message.isFormerOneToOneConversation) {
            setAvatarOnMessage(message)
        } else {
            if (message.isOneToOneConversation || message.isFormerOneToOneConversation) {
                binding.messageUserAvatar.visibility = View.GONE
            } else {
                binding.messageUserAvatar.visibility = View.INVISIBLE
            }
            binding.messageAuthor.visibility = View.GONE
        }
    }

    private fun setAvatarOnMessage(message: ChatMessage) {
        binding.messageUserAvatar.visibility = View.VISIBLE
        if (message.actorType == "guests") {
            // do nothing, avatar is set
        } else if (message.actorType == "bots" && message.actorId == "changelog") {
            binding.messageUserAvatar.loadChangelogBotAvatar()
        } else if (message.actorType == "bots") {
            binding.messageUserAvatar.loadBotsAvatar()
        }
    }

    private fun colorizeMessageBubble(message: ChatMessage) {
        viewThemeUtils.talk.themeIncomingMessageBubble(bubble, message.isGrouped, message.isDeleted)
    }

    private fun processParentMessage(message: ChatMessage) {
        val parentChatMessage = message.parentMessage
        parentChatMessage!!.activeUser = message.activeUser
        parentChatMessage.imageUrl?.let {
            binding.messageQuote.quotedMessageImage.visibility = View.VISIBLE
            binding.messageQuote.quotedMessageImage.load(it) {
                addHeader(
                    "Authorization",
                    ApiUtils.getCredentials(message.activeUser!!.username, message.activeUser!!.token)
                )
            }
        } ?: run {
            binding.messageQuote.quotedMessageImage.visibility = View.GONE
        }
        binding.messageQuote.quotedMessageAuthor.text = if (parentChatMessage.actorDisplayName.isNullOrEmpty()) {
            context.getText(R.string.nc_nick_guest)
        } else {
            parentChatMessage.actorDisplayName
        }

        binding.messageQuote.quotedMessage.text = messageUtils
            .enrichChatReplyMessageText(
                binding.messageQuote.quotedMessage.context,
                parentChatMessage,
                true,
                viewThemeUtils
            )

        if (parentChatMessage.actorId?.equals(message.activeUser!!.userId) == true) {
            viewThemeUtils.platform.colorViewBackground(binding.messageQuote.quoteColoredView)
        } else {
            binding.messageQuote.quoteColoredView.setBackgroundColor(
                ContextCompat.getColor(binding.messageQuote.quoteColoredView.context, R.color.high_emphasis_text)
            )
        }

        binding.messageQuote.quotedChatMessageView.setOnClickListener {
            val chatActivity = commonMessageInterface as ChatActivity
            chatActivity.jumpToQuotedMessage(parentChatMessage)
        }
    }

    private fun showAvatarOnChatMessage(message: ChatMessage) {
        binding.messageUserAvatar.visibility = View.VISIBLE
        if (message.actorType == "guests") {
            // do nothing, avatar is set
        } else if (message.actorType == "bots" && message.actorId == "changelog") {
            binding.messageUserAvatar.loadChangelogBotAvatar()
        } else if (message.actorType == "bots") {
            binding.messageUserAvatar.loadBotsAvatar()
        }
    }

    fun assignCommonMessageInterface(commonMessageInterface: CommonMessageInterface) {
        this.commonMessageInterface = commonMessageInterface
    }

    companion object {
        const val TEXT_SIZE_MULTIPLIER = 2.5
    }
}
