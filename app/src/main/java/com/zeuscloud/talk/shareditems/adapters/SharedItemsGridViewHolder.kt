/*
 * Nextcloud Talk application
 *
 * @author Tim Krüger
 * @author Álvaro Brey
 * Copyright (C) 2022 Álvaro Brey
 * Copyright (C) 2022 Tim Krüger <t@timkrueger.me>
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

package com.zeuscloud.talk.shareditems.adapters

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import com.zeuscloud.talk.data.user.model.User
import com.zeuscloud.talk.databinding.SharedItemGridBinding
import com.zeuscloud.talk.ui.theme.ViewThemeUtils

class SharedItemsGridViewHolder(
    override val binding: SharedItemGridBinding,
    user: User,
    viewThemeUtils: ViewThemeUtils
) : SharedItemsViewHolder(binding, user, viewThemeUtils) {

    override val image: ImageView
        get() = binding.image
    override val clickTarget: View
        get() = binding.image
    override val progressBar: ProgressBar
        get() = binding.progressBar
}
