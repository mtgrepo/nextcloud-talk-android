/*
 * Nextcloud Talk application
 *
 * @author Álvaro Brey
 * @author Andy Scherzinger
 * Copyright (C) 2022 Álvaro Brey
 * Copyright (C) 2022 Andy Scherzinger <info@andy-scherzinger.de>
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

package com.zeuscloud.talk.ui.theme

import com.nextcloud.android.common.ui.color.ColorUtil
import com.nextcloud.android.common.ui.theme.MaterialSchemes
import com.zeuscloud.talk.data.user.model.User
import com.zeuscloud.talk.models.json.capabilities.Capabilities
import com.zeuscloud.talk.utils.database.user.CurrentUserProviderNew
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

internal class MaterialSchemesProviderImpl @Inject constructor(
    private val userProvider: CurrentUserProviderNew,
    private val colorUtil: ColorUtil
) : MaterialSchemesProvider {

    private val themeCache: ConcurrentHashMap<String, MaterialSchemes> = ConcurrentHashMap()

    override fun getMaterialSchemesForUser(user: User?): MaterialSchemes {
        val url: String = if (user?.baseUrl != null) {
            user.baseUrl!!
        } else {
            FALLBACK_URL
        }

        if (!themeCache.containsKey(url)) {
            themeCache[url] = getMaterialSchemesForCapabilities(user?.capabilities)
        }

        return themeCache[url]!!
    }

    override fun getMaterialSchemesForCurrentUser(): MaterialSchemes {
        return getMaterialSchemesForUser(userProvider.currentUser.blockingGet())
    }

    override fun getMaterialSchemesForCapabilities(capabilities: Capabilities?): MaterialSchemes {
        val serverTheme = ServerThemeImpl(capabilities?.themingCapability, colorUtil)
        return MaterialSchemes.fromServerTheme(serverTheme)
    }

    companion object {
        const val FALLBACK_URL = "NULL"
    }
}
