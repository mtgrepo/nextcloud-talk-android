/*
 * Nextcloud Talk application
 *
 * @author Álvaro Brey
 * @author Tim Krüger
 * Copyright (C) 2022 Tim Krüger <t@timkrueger.me>
 * Copyright (C) 2022 Álvaro Brey
 * Copyright (C) 2022 Nextcloud GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.zeuscloud.talk.adapters.items

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.zeuscloud.talk.R
import com.zeuscloud.talk.data.user.model.User
import com.zeuscloud.talk.databinding.RvItemSearchMessageBinding
import com.zeuscloud.talk.extensions.loadThumbnail
import com.zeuscloud.talk.models.domain.SearchMessageEntry
import com.zeuscloud.talk.ui.theme.ViewThemeUtils
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFilterable
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.flexibleadapter.items.ISectionable
import eu.davidea.viewholders.FlexibleViewHolder

data class MessageResultItem constructor(
    private val context: Context,
    private val currentUser: User,
    val messageEntry: SearchMessageEntry,
    private val showHeader: Boolean = false,
    private val viewThemeUtils: ViewThemeUtils
) :
    AbstractFlexibleItem<MessageResultItem.ViewHolder>(),
    IFilterable<String>,
    ISectionable<MessageResultItem.ViewHolder, GenericTextHeaderItem> {

    class ViewHolder(view: View, adapter: FlexibleAdapter<*>) :
        FlexibleViewHolder(view, adapter) {
        var binding: RvItemSearchMessageBinding

        init {
            binding = RvItemSearchMessageBinding.bind(view)
        }
    }

    override fun getLayoutRes(): Int = R.layout.rv_item_search_message

    override fun createViewHolder(
        view: View,
        adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>
    ): ViewHolder = ViewHolder(view, adapter)

    override fun bindViewHolder(
        adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>,
        holder: ViewHolder,
        position: Int,
        payloads: MutableList<Any>?
    ) {
        holder.binding.conversationTitle.text = messageEntry.title
        bindMessageExcerpt(holder)
        messageEntry.thumbnailURL?.let { holder.binding.thumbnail.loadThumbnail(it, currentUser) }
    }

    private fun bindMessageExcerpt(holder: ViewHolder) {
        viewThemeUtils.platform.highlightText(
            holder.binding.messageExcerpt,
            messageEntry.messageExcerpt,
            messageEntry.searchTerm
        )
    }

    override fun filter(constraint: String?): Boolean = true

    override fun getItemViewType(): Int {
        return VIEW_TYPE
    }

    companion object {
        const val VIEW_TYPE = FlexibleItemViewType.MESSAGE_RESULT_ITEM
    }

    override fun getHeader(): GenericTextHeaderItem = MessagesTextHeaderItem(context, viewThemeUtils)
        .apply {
            isHidden = showHeader // FlexibleAdapter needs this hack for some reason
        }

    override fun setHeader(header: GenericTextHeaderItem?) {
        // nothing, header is always the same
    }
}
