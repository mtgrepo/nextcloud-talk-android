/*
 * Nextcloud Talk application
 *
 * @author Marcel Hibbe
 * Copyright (C) 2023 Marcel Hibbe <dev@mhibbe.de>
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

package com.zeuscloud.talk.chat.data

import com.zeuscloud.talk.data.user.model.User
import com.zeuscloud.talk.models.domain.ConversationModel
import com.zeuscloud.talk.models.json.generic.GenericOverall
import com.zeuscloud.talk.models.json.reminder.Reminder
import io.reactivex.Observable

interface ChatRepository {
    fun getRoom(user: User, roomToken: String): Observable<ConversationModel>
    fun joinRoom(user: User, roomToken: String, roomPassword: String): Observable<ConversationModel>
    fun setReminder(user: User, roomToken: String, messageId: String, timeStamp: Int): Observable<Reminder>
    fun getReminder(user: User, roomToken: String, messageId: String): Observable<Reminder>
    fun deleteReminder(user: User, roomToken: String, messageId: String): Observable<GenericOverall>
}
