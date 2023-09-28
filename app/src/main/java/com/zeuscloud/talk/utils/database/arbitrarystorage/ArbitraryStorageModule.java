/*
 * Nextcloud Talk application
 *
 * @author Mario Danic
 * Copyright (C) 2017-2018 Mario Danic <mario@lovelyhq.com>
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
package com.zeuscloud.talk.utils.database.arbitrarystorage;

import com.zeuscloud.talk.application.NextcloudTalkApplication;
import com.zeuscloud.talk.arbitrarystorage.ArbitraryStorageManager;
import com.zeuscloud.talk.dagger.modules.DatabaseModule;
import com.zeuscloud.talk.data.storage.ArbitraryStoragesRepository;

import javax.inject.Inject;

import autodagger.AutoInjector;
import dagger.Module;
import dagger.Provides;

@Module(includes = DatabaseModule.class)
@AutoInjector(NextcloudTalkApplication.class)
public class ArbitraryStorageModule {

    @Inject
    public ArbitraryStorageModule() {
    }

    @Provides
    public ArbitraryStorageManager provideArbitraryStorageManager(ArbitraryStoragesRepository repository) {
        return new ArbitraryStorageManager(repository);
    }
}
