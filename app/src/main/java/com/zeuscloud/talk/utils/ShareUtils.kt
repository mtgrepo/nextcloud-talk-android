/*
 * Nextcloud Talk application
 *
 * @author Mario Danic
 * Copyright (C) 2017 Mario Danic <mario@lovelyhq.com>
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
package com.zeuscloud.talk.utils

import android.content.Context
import com.zeuscloud.talk.R
import com.zeuscloud.talk.data.user.model.User
import com.zeuscloud.talk.models.json.conversations.Conversation

object ShareUtils {
    fun getStringForIntent(
        context: Context,
        user: User,
        conversation: Conversation?
    ): String {
        return String.format(
            context.resources.getString(R.string.nc_share_text),
            user.baseUrl,
            conversation?.token
        )
    }
}
