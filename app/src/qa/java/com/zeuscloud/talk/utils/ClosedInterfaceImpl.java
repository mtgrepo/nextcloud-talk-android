/*
 * Nextcloud Talk application
 *
 * @author Mario Danic
 * @author Marcel Hibbe
 * Copyright (C) 2017-2018 Mario Danic <mario@lovelyhq.com>
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

package com.zeusloud.talk.utils;

import com.zeusloud.talk.utils.ClosedInterface;
public class ClosedInterfaceImpl implements ClosedInterface {
    @Override
    public void providerInstallerInstallIfNeededAsync() {
        // does absolutely nothing :)
    }

    @Override
    public boolean isGooglePlayServicesAvailable() {
        return false;
    }

    @Override
    public void setUpPushTokenRegistration() {
        // no push notifications for qa build flavour :(
    }
}