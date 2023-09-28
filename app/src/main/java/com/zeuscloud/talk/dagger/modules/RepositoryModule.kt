/*
 * Nextcloud Talk application
 *
 * @author Álvaro Brey
 * @author Andy Scherzinger
 * @author Marcel Hibbe
 * Copyright (C) 2022 Marcel Hibbe <dev@mhibbe.de>
 * Copyright (C) 2022 Andy Scherzinger <info@andy-scherzinger.de>
 * Copyright (C) 2022 Álvaro Brey
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

package com.zeuscloud.talk.dagger.modules

import com.zeuscloud.talk.api.NcApi
import com.zeuscloud.talk.chat.data.ChatRepository
import com.zeuscloud.talk.chat.data.ChatRepositoryImpl
import com.zeuscloud.talk.conversationinfoedit.data.ConversationInfoEditRepository
import com.zeuscloud.talk.conversationinfoedit.data.ConversationInfoEditRepositoryImpl
import com.zeuscloud.talk.data.source.local.TalkDatabase
import com.zeuscloud.talk.data.storage.ArbitraryStoragesRepository
import com.zeuscloud.talk.data.storage.ArbitraryStoragesRepositoryImpl
import com.zeuscloud.talk.data.user.UsersRepository
import com.zeuscloud.talk.data.user.UsersRepositoryImpl
import com.zeuscloud.talk.openconversations.data.OpenConversationsRepository
import com.zeuscloud.talk.openconversations.data.OpenConversationsRepositoryImpl
import com.zeuscloud.talk.polls.repositories.PollRepository
import com.zeuscloud.talk.polls.repositories.PollRepositoryImpl
import com.zeuscloud.talk.raisehand.RequestAssistanceRepository
import com.zeuscloud.talk.raisehand.RequestAssistanceRepositoryImpl
import com.zeuscloud.talk.remotefilebrowser.repositories.RemoteFileBrowserItemsRepository
import com.zeuscloud.talk.remotefilebrowser.repositories.RemoteFileBrowserItemsRepositoryImpl
import com.zeuscloud.talk.repositories.callrecording.CallRecordingRepository
import com.zeuscloud.talk.repositories.callrecording.CallRecordingRepositoryImpl
import com.zeuscloud.talk.repositories.conversations.ConversationsRepository
import com.zeuscloud.talk.repositories.conversations.ConversationsRepositoryImpl
import com.zeuscloud.talk.repositories.reactions.ReactionsRepository
import com.zeuscloud.talk.repositories.reactions.ReactionsRepositoryImpl
import com.zeuscloud.talk.repositories.unifiedsearch.UnifiedSearchRepository
import com.zeuscloud.talk.repositories.unifiedsearch.UnifiedSearchRepositoryImpl
import com.zeuscloud.talk.shareditems.repositories.SharedItemsRepository
import com.zeuscloud.talk.shareditems.repositories.SharedItemsRepositoryImpl
import com.zeuscloud.talk.translate.repositories.TranslateRepository
import com.zeuscloud.talk.translate.repositories.TranslateRepositoryImpl
import com.zeuscloud.talk.utils.DateUtils
import com.zeuscloud.talk.utils.database.user.CurrentUserProviderNew
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient

@Module
class RepositoryModule {

    @Provides
    fun provideConversationsRepository(ncApi: NcApi, userProvider: CurrentUserProviderNew): ConversationsRepository {
        return ConversationsRepositoryImpl(ncApi, userProvider)
    }

    @Provides
    fun provideSharedItemsRepository(ncApi: NcApi, dateUtils: DateUtils): SharedItemsRepository {
        return SharedItemsRepositoryImpl(ncApi, dateUtils)
    }

    @Provides
    fun provideUnifiedSearchRepository(ncApi: NcApi, userProvider: CurrentUserProviderNew): UnifiedSearchRepository {
        return UnifiedSearchRepositoryImpl(ncApi, userProvider)
    }

    @Provides
    fun provideDialogPollRepository(ncApi: NcApi, userProvider: CurrentUserProviderNew): PollRepository {
        return PollRepositoryImpl(ncApi, userProvider)
    }

    @Provides
    fun provideRemoteFileBrowserItemsRepository(okHttpClient: OkHttpClient, userProvider: CurrentUserProviderNew):
        RemoteFileBrowserItemsRepository {
        return RemoteFileBrowserItemsRepositoryImpl(okHttpClient, userProvider)
    }

    @Provides
    fun provideUsersRepository(database: TalkDatabase): UsersRepository {
        return UsersRepositoryImpl(database.usersDao())
    }

    @Provides
    fun provideArbitraryStoragesRepository(database: TalkDatabase): ArbitraryStoragesRepository {
        return ArbitraryStoragesRepositoryImpl(database.arbitraryStoragesDao())
    }

    @Provides
    fun provideReactionsRepository(ncApi: NcApi, userProvider: CurrentUserProviderNew): ReactionsRepository {
        return ReactionsRepositoryImpl(ncApi, userProvider)
    }

    @Provides
    fun provideCallRecordingRepository(ncApi: NcApi, userProvider: CurrentUserProviderNew): CallRecordingRepository {
        return CallRecordingRepositoryImpl(ncApi, userProvider)
    }

    @Provides
    fun provideRequestAssistanceRepository(ncApi: NcApi, userProvider: CurrentUserProviderNew):
        RequestAssistanceRepository {
        return RequestAssistanceRepositoryImpl(ncApi, userProvider)
    }

    @Provides
    fun provideOpenConversationsRepository(ncApi: NcApi, userProvider: CurrentUserProviderNew):
        OpenConversationsRepository {
        return OpenConversationsRepositoryImpl(ncApi, userProvider)
    }

    @Provides
    fun translateRepository(ncApi: NcApi):
        TranslateRepository {
        return TranslateRepositoryImpl(ncApi)
    }

    @Provides
    fun provideChatRepository(ncApi: NcApi):
        ChatRepository {
        return ChatRepositoryImpl(ncApi)
    }

    @Provides
    fun provideConversationInfoEditRepository(ncApi: NcApi, userProvider: CurrentUserProviderNew):
        ConversationInfoEditRepository {
        return ConversationInfoEditRepositoryImpl(ncApi, userProvider)
    }
}
