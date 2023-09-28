/*
 * Nextcloud Talk application
 *
 * @author Mario Danic
 * @author Andy Scherzinger
 * Copyright (C) 2022 Andy Scherzinger <info@andy-scherzinger.de>
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
package com.zeuscloud.talk.models.json.signaling

import android.os.Parcelable
import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonObject
data class NCMessagePayload(
    @JsonField(name = ["type"])
    var type: String? = null,
    @JsonField(name = ["sdp"])
    var sdp: String? = null,
    @JsonField(name = ["nick"])
    var nick: String? = null,
    @JsonField(name = ["candidate"])
    var iceCandidate: NCIceCandidate? = null,
    @JsonField(name = ["name"])
    var name: String? = null,
    @JsonField(name = ["state"])
    var state: Boolean? = null,
    @JsonField(name = ["timestamp"])
    var timestamp: Long? = null,
    @JsonField(name = ["reaction"])
    var reaction: String? = null
) : Parcelable {
    // This constructor is added to work with the 'com.bluelinelabs.logansquare.annotation.JsonObject'
    constructor() : this(null, null, null, null, null, null, null, null)
}
