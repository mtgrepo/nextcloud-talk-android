/*
 * Nextcloud Talk application
 *
 * @author Marcel Hibbe
 * Copyright (C) 2017-2018 Mario Danic <mario@lovelyhq.com>
 * Copyright (C) 2022 Marcel Hibbe <dev@mhibbe.de>
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

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import autodagger.AutoInjector
import coil.load
import com.nextcloud.android.common.ui.theme.utils.ColorRole
import com.zeuscloud.talk.R
import com.zeuscloud.talk.api.NcApi
import com.zeuscloud.talk.application.NextcloudTalkApplication
import com.zeuscloud.talk.application.NextcloudTalkApplication.Companion.sharedApplication
import com.zeuscloud.talk.chat.ChatActivity
import com.zeuscloud.talk.databinding.ItemCustomOutcomingPollMessageBinding
import com.zeuscloud.talk.models.json.chat.ChatMessage
import com.zeuscloud.talk.models.json.chat.ReadStatus
import com.zeuscloud.talk.polls.ui.PollMainDialogFragment
import com.zeuscloud.talk.ui.theme.ViewThemeUtils
import com.zeuscloud.talk.utils.ApiUtils
import com.zeuscloud.talk.utils.DateUtils
import com.zeuscloud.talk.utils.message.MessageUtils
import com.zeuscloud.talk.utils.preferences.AppPreferences
import com.stfalcon.chatkit.messages.MessageHolders
import javax.inject.Inject

@AutoInjector(NextcloudTalkApplication::class)
class OutcomingPollMessageViewHolder(outcomingView: View, payload: Any) :
    MessageHolders.OutcomingTextMessageViewHolder<ChatMessage>(outcomingView, payload) {

    private val binding: ItemCustomOutcomingPollMessageBinding = ItemCustomOutcomingPollMessageBinding.bind(itemView)

    @Inject
    lateinit var context: Context

    @Inject
    lateinit var viewThemeUtils: ViewThemeUtils

    @Inject
    lateinit var messageUtils: MessageUtils

    @Inject
    lateinit var dateUtils: DateUtils

    @Inject
    lateinit var appPreferences: AppPreferences

    @Inject
    lateinit var ncApi: NcApi

    lateinit var message: ChatMessage

    lateinit var commonMessageInterface: CommonMessageInterface

    @SuppressLint("SetTextI18n")
    override fun onBind(message: ChatMessage) {
        super.onBind(message)
        this.message = message
        sharedApplication!!.componentApplication.inject(this)
        viewThemeUtils.platform.colorTextView(binding.messageTime, ColorRole.ON_SURFACE_VARIANT)
        binding.messageTime.text = dateUtils.getLocalTimeStringFromTimestamp(message.timestamp)

        colorizeMessageBubble(message)

        itemView.isSelected = false

        // parent message handling
        setParentMessageDataOnMessageItem(message)

        val readStatusDrawableInt = when (message.readStatus) {
            ReadStatus.READ -> R.drawable.ic_check_all
            ReadStatus.SENT -> R.drawable.ic_check
            else -> null
        }

        val readStatusContentDescriptionString = when (message.readStatus) {
            ReadStatus.READ -> context.resources?.getString(R.string.nc_message_read)
            ReadStatus.SENT -> context.resources?.getString(R.string.nc_message_sent)
            else -> null
        }

        readStatusDrawableInt?.let { drawableInt ->
            AppCompatResources.getDrawable(context, drawableInt)?.let {
                binding.checkMark.setImageDrawable(it)
                viewThemeUtils.talk.themeMessageCheckMark(binding.checkMark)
            }
        }

        binding.checkMark.contentDescription = readStatusContentDescriptionString

        setPollPreview(message)

        Reaction().showReactions(
            message,
            ::clickOnReaction,
            ::longClickOnReaction,
            binding.reactions,
            binding.messageTime.context,
            true,
            viewThemeUtils
        )
    }

    private fun longClickOnReaction(chatMessage: ChatMessage) {
        commonMessageInterface.onLongClickReactions(chatMessage)
    }

    private fun clickOnReaction(chatMessage: ChatMessage, emoji: String) {
        commonMessageInterface.onClickReaction(chatMessage, emoji)
    }

    private fun setPollPreview(message: ChatMessage) {
        var pollId: String? = null
        var pollName: String? = null

        if (message.messageParameters != null && message.messageParameters!!.size > 0) {
            for (key in message.messageParameters!!.keys) {
                val individualHashMap: Map<String?, String?> = message.messageParameters!![key]!!
                if (individualHashMap["type"] == "talk-poll") {
                    pollId = individualHashMap["id"]
                    pollName = individualHashMap["name"].toString()
                }
            }
        }

        if (pollId != null && pollName != null) {
            binding.messagePollTitle.text = pollName

            val roomToken = (payload as? MessagePayload)!!.roomToken
            val isOwnerOrModerator = (payload as? MessagePayload)!!.isOwnerOrModerator ?: false

            binding.bubble.setOnClickListener {
                val pollVoteDialog = PollMainDialogFragment.newInstance(
                    message.activeUser!!,
                    roomToken,
                    isOwnerOrModerator,
                    pollId,
                    pollName
                )
                pollVoteDialog.show(
                    (binding.messagePollIcon.context as ChatActivity).supportFragmentManager,
                    TAG
                )
            }
        }
    }

    private fun setParentMessageDataOnMessageItem(message: ChatMessage) {
        if (!message.isDeleted && message.parentMessage != null) {
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
            binding.messageQuote.quotedMessageAuthor.text = parentChatMessage.actorDisplayName
                ?: context.getText(R.string.nc_nick_guest)
            binding.messageQuote.quotedMessage.text = messageUtils
                .enrichChatReplyMessageText(
                    binding.messageQuote.quotedMessage.context,
                    parentChatMessage,
                    false,
                    viewThemeUtils
                )
            viewThemeUtils.talk.colorOutgoingQuoteText(binding.messageQuote.quotedMessage)
            viewThemeUtils.talk.colorOutgoingQuoteAuthorText(binding.messageQuote.quotedMessageAuthor)
            viewThemeUtils.talk.colorOutgoingQuoteBackground(binding.messageQuote.quoteColoredView)

            binding.messageQuote.quotedChatMessageView.visibility = View.VISIBLE
        } else {
            binding.messageQuote.quotedChatMessageView.visibility = View.GONE
        }
    }

    private fun colorizeMessageBubble(message: ChatMessage) {
        viewThemeUtils.talk.themeOutgoingMessageBubble(bubble, message.isGrouped, message.isDeleted)
    }

    fun assignCommonMessageInterface(commonMessageInterface: CommonMessageInterface) {
        this.commonMessageInterface = commonMessageInterface
    }

    companion object {
        private val TAG = NextcloudTalkApplication::class.java.simpleName
    }
}
