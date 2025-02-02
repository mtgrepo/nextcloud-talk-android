/*
 * Nextcloud Talk application
 *
 * @author Andy Scherzinger
 * @author Mario Danic
 * Copyright (C) 2022 Andy Scherzinger <info@andy-scherzinger.de>
 * Copyright (C) 2017-2020 Mario Danic <mario@lovelyhq.com>
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

package com.zeuscloud.talk.data.source.local.converters

import androidx.room.TypeConverter
import com.bluelinelabs.logansquare.LoganSquare
import com.zeuscloud.talk.models.json.signaling.settings.SignalingSettings

class SignalingSettingsConverter {

    @TypeConverter
    fun fromSignalingSettingsToString(signalingSettings: SignalingSettings?): String {
        return if (signalingSettings == null) {
            ""
        } else {
            LoganSquare.serialize(signalingSettings)
        }
    }

    @TypeConverter
    fun fromStringToSignalingSettings(value: String): SignalingSettings? {
        return if (value.isBlank()) {
            null
        } else {
            return LoganSquare.parse(value, SignalingSettings::class.java)
        }
    }
}
