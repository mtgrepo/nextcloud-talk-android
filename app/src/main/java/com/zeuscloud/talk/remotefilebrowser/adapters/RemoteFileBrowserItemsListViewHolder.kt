/*
 * Nextcloud Talk application
 *
 * @author Andy Scherzinger
 * Copyright (C) 2022 Andy Scherzinger <info@andy-scherzinger.de>
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

package com.zeuscloud.talk.remotefilebrowser.adapters

import android.text.format.Formatter
import android.view.View
import android.widget.ImageView
import com.zeuscloud.talk.R
import com.zeuscloud.talk.data.user.model.User
import com.zeuscloud.talk.databinding.RvItemBrowserFileBinding
import com.zeuscloud.talk.extensions.loadImage
import com.zeuscloud.talk.remotefilebrowser.SelectionInterface
import com.zeuscloud.talk.remotefilebrowser.model.RemoteFileBrowserItem
import com.zeuscloud.talk.ui.theme.ViewThemeUtils
import com.zeuscloud.talk.utils.ApiUtils
import com.zeuscloud.talk.utils.DateUtils
import com.zeuscloud.talk.utils.Mimetype.FOLDER

class RemoteFileBrowserItemsListViewHolder(
    override val binding: RvItemBrowserFileBinding,
    mimeTypeSelectionFilter: String?,
    currentUser: User,
    selectionInterface: SelectionInterface,
    private val viewThemeUtils: ViewThemeUtils,
    private val dateUtils: DateUtils,
    onItemClicked: (Int) -> Unit
) : RemoteFileBrowserItemsViewHolder(binding, mimeTypeSelectionFilter, currentUser, selectionInterface) {

    override val fileIcon: ImageView
        get() = binding.fileIcon

    private var selectable: Boolean = true
    private var clickable: Boolean = true

    init {
        itemView.setOnClickListener {
            if (clickable) {
                onItemClicked(bindingAdapterPosition)
                if (selectable) {
                    binding.selectFileCheckbox.toggle()
                }
            }
        }
    }

    override fun onBind(item: RemoteFileBrowserItem) {
        super.onBind(item)

        if (!item.isAllowedToReShare || item.isEncrypted) {
            binding.root.isEnabled = false
            binding.root.alpha = DISABLED_ALPHA
        } else {
            binding.root.isEnabled = true
            binding.root.alpha = ENABLED_ALPHA
        }

        binding.fileEncryptedImageView.visibility =
            if (item.isEncrypted) {
                View.VISIBLE
            } else {
                View.GONE
            }

        binding.fileFavoriteImageView.visibility =
            if (item.isFavorite) {
                View.VISIBLE
            } else {
                View.GONE
            }

        calculateSelectability(item)
        calculateClickability(item, selectable)
        setSelectability()

        val placeholder = viewThemeUtils.talk.getPlaceholderImage(binding.root.context, item.mimeType)

        if (item.hasPreview) {
            val path = ApiUtils.getUrlForFilePreviewWithRemotePath(
                currentUser.baseUrl,
                item.path,
                fileIcon.context.resources.getDimensionPixelSize(R.dimen.small_item_height)
            )
            if (path.isNotEmpty()) {
                fileIcon.loadImage(path, currentUser, placeholder)
            }
        } else {
            fileIcon.setImageDrawable(placeholder)
        }

        binding.filenameTextView.text = item.displayName
        binding.fileModifiedInfo.text = String.format(
            binding.fileModifiedInfo.context.getString(R.string.nc_last_modified),
            Formatter.formatShortFileSize(binding.fileModifiedInfo.context, item.size),
            dateUtils.getLocalDateTimeStringFromTimestamp(item.modifiedTimestamp)
        )

        binding.selectFileCheckbox.isChecked = selectionInterface.isPathSelected(item.path!!)
    }

    private fun setSelectability() {
        if (selectable) {
            binding.selectFileCheckbox.visibility = View.VISIBLE
            viewThemeUtils.platform.themeCheckbox(binding.selectFileCheckbox)
        } else {
            binding.selectFileCheckbox.visibility = View.GONE
        }
    }

    private fun calculateSelectability(item: RemoteFileBrowserItem) {
        selectable = item.isFile &&
            (mimeTypeSelectionFilter == null || item.mimeType?.startsWith(mimeTypeSelectionFilter) == true) &&
            (item.isAllowedToReShare && !item.isEncrypted)
    }

    private fun calculateClickability(item: RemoteFileBrowserItem, selectableItem: Boolean) {
        clickable = selectableItem || FOLDER == item.mimeType
    }

    companion object {
        private const val DISABLED_ALPHA: Float = 0.38f
        private const val ENABLED_ALPHA: Float = 1.0f
    }
}
