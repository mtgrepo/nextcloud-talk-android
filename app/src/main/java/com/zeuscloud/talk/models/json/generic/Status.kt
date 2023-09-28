/*
 * Nextcloud Talk application
 *
 * @author Mario Danic
 * @author Andy Scherzinger
 * Copyright (C) 2022 Andy Scherzinger <info@andy-scherzinger.de>
 * Copyright (C) 2017 Mario Danic (mario@lovelyhq.com)
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
package com.zeuscloud.talk.models.json.generic

import android.os.Parcelable
import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonObject
data class Status(
    @JsonField(name = ["installed"])
    var installed: Boolean = false,

    @JsonField(name = ["maintenance"])
    var maintenance: Boolean = false,

    @JsonField(name = ["upgrade"])
    var needsUpgrade: Boolean = false,

    @JsonField(name = ["version"])
    var version: String? = null,

    @JsonField(name = ["versionstring"])
    var versionString: String? = null,

    @JsonField(name = ["edition"])
    var edition: String? = null,

    @JsonField(name = ["productname"])
    var productName: String? = null
) : Parcelable {
    // This constructor is added to work with the 'com.bluelinelabs.logansquare.annotation.JsonObject'
    constructor() : this(false, false, false, null, null, null, null)
}
