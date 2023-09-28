/*
 * Nextcloud Talk application
 *
 * @author Andy Scherzinger
 * @author Tim Krüger
 * Copyright (C) 2021 Tim Krüger <t@timkrueger.me>
 * Copyright (C) 2021 Andy Scherzinger <info@andy-scherzinger.de>
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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.zeuscloud.talk.adapters.messages;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.material.card.MaterialCardView;
import com.zeuscloud.talk.R;
import com.zeuscloud.talk.databinding.ItemCustomOutcomingPreviewMessageBinding;
import com.zeuscloud.talk.databinding.ReactionsInsideMessageBinding;
import com.zeuscloud.talk.models.json.chat.ChatMessage;

import androidx.core.content.ContextCompat;
import androidx.emoji2.widget.EmojiTextView;

public class OutcomingPreviewMessageViewHolder extends PreviewMessageViewHolder {

    private final ItemCustomOutcomingPreviewMessageBinding binding;

    public OutcomingPreviewMessageViewHolder(View itemView) {
        super(itemView, null);
        binding = ItemCustomOutcomingPreviewMessageBinding.bind(itemView);
    }

    @Override
    public void onBind(ChatMessage message) {
        super.onBind(message);

        binding.messageText.setTextColor(ContextCompat.getColor(binding.messageText.getContext(),
                                                                R.color.no_emphasis_text));
        binding.messageTime.setTextColor(ContextCompat.getColor(binding.messageText.getContext(),
                                                                R.color.no_emphasis_text));
    }

    @Override
    public EmojiTextView getMessageText() {
        return binding.messageText;
    }

    @Override
    public ProgressBar getProgressBar() {
        return binding.progressBar;
    }

    @Override
    public View getPreviewContainer() {
        return binding.previewContainer;
    }

    @Override
    public MaterialCardView getPreviewContactContainer() {
        return binding.contactContainer;
    }

    @Override
    public ImageView getPreviewContactPhoto() {
        return binding.contactPhoto;
    }

    @Override
    public EmojiTextView getPreviewContactName() {
        return binding.contactName;
    }

    @Override
    public ProgressBar getPreviewContactProgressBar() {
        return binding.contactProgressBar;
    }

    @Override
    public ReactionsInsideMessageBinding getReactionsBinding() { return binding.reactions; }
}
