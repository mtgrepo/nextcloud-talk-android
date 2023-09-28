/*
 * Nextcloud Talk application
 *
 * @author Marcel Hibbe
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

package com.zeuscloud.talk.polls.adapters

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import androidx.recyclerview.widget.RecyclerView
import com.zeuscloud.talk.R
import com.zeuscloud.talk.databinding.PollCreateOptionsItemBinding
import com.zeuscloud.talk.ui.theme.ViewThemeUtils
import com.zeuscloud.talk.utils.EmojiTextInputEditText

class PollCreateOptionViewHolder(
    private val binding: PollCreateOptionsItemBinding,
    private val viewThemeUtils: ViewThemeUtils
) : RecyclerView.ViewHolder(binding.root) {

    lateinit var optionText: EmojiTextInputEditText
    private var textListener: TextWatcher? = null

    @SuppressLint("SetTextI18n")
    fun bind(
        pollCreateOptionItem: PollCreateOptionItem,
        itemsListener: PollCreateOptionsItemListener,
        position: Int,
        focus: Boolean
    ) {
        textListener?.let {
            binding.pollOptionTextEdit.removeTextChangedListener(it)
        }

        binding.pollOptionTextEdit.setText(pollCreateOptionItem.pollOption)
        viewThemeUtils.material.colorTextInputLayout(binding.pollOptionTextInputLayout)

        if (focus) {
            itemsListener.requestFocus(binding.pollOptionTextEdit)
        }

        binding.pollOptionDelete.setOnClickListener {
            itemsListener.onRemoveOptionsItemClick(pollCreateOptionItem, position)
        }

        textListener = getTextWatcher(pollCreateOptionItem, itemsListener)
        binding.pollOptionTextEdit.addTextChangedListener(textListener)
        binding.pollOptionTextInputLayout.hint = String.format(
            binding.pollOptionTextInputLayout.resources.getString(R.string.polls_option_hint),
            position + 1
        )

        binding.pollOptionDelete.contentDescription = String.format(
            binding.pollOptionTextInputLayout.resources.getString(R.string.polls_option_delete),
            position + 1
        )
    }

    private fun getTextWatcher(
        pollCreateOptionItem: PollCreateOptionItem,
        itemsListener: PollCreateOptionsItemListener
    ) =
        object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                // unused atm
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // unused atm
            }

            override fun onTextChanged(option: CharSequence, start: Int, before: Int, count: Int) {
                pollCreateOptionItem.pollOption = option.toString()

                itemsListener.onOptionsItemTextChanged(pollCreateOptionItem)
            }
        }
}
