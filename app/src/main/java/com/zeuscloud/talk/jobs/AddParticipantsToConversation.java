/*
 * Nextcloud Talk application
 *
 * @author Mario Danic
 * Copyright (C) 2017-2019 Mario Danic <mario@lovelyhq.com>
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

package com.zeuscloud.talk.jobs;

import android.content.Context;

import com.zeuscloud.talk.api.NcApi;
import com.zeuscloud.talk.application.NextcloudTalkApplication;
import com.zeuscloud.talk.data.user.model.User;
import com.zeuscloud.talk.events.EventStatus;
import com.zeuscloud.talk.models.RetrofitBucket;
import com.zeuscloud.talk.users.UserManager;
import com.zeuscloud.talk.utils.ApiUtils;
import com.zeuscloud.talk.utils.UserIdUtils;
import com.zeuscloud.talk.utils.bundle.BundleKeys;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import autodagger.AutoInjector;
import io.reactivex.schedulers.Schedulers;

@AutoInjector(NextcloudTalkApplication.class)
public class AddParticipantsToConversation extends Worker {
    @Inject
    NcApi ncApi;

    @Inject
    UserManager userManager;

    @Inject
    EventBus eventBus;

    public AddParticipantsToConversation(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        NextcloudTalkApplication.Companion.getSharedApplication().getComponentApplication().inject(this);
    }

    @NonNull
    @Override
    public Result doWork() {
        Data data = getInputData();
        String[] selectedUserIds = data.getStringArray(BundleKeys.KEY_SELECTED_USERS);
        String[] selectedGroupIds = data.getStringArray(BundleKeys.KEY_SELECTED_GROUPS);
        String[] selectedCircleIds = data.getStringArray(BundleKeys.KEY_SELECTED_CIRCLES);
        String[] selectedEmails = data.getStringArray(BundleKeys.KEY_SELECTED_EMAILS);
        User user =
            userManager.getUserWithInternalId(
                data.getLong(BundleKeys.KEY_INTERNAL_USER_ID, -1))
                .blockingGet();

        int apiVersion = ApiUtils.getConversationApiVersion(user, new int[] {ApiUtils.APIv4, 1});

        String conversationToken = data.getString(BundleKeys.KEY_TOKEN);
        String credentials = ApiUtils.getCredentials(user.getUsername(), user.getToken());

        RetrofitBucket retrofitBucket;
        if (selectedUserIds != null) {
            for (String userId : selectedUserIds) {
                retrofitBucket = ApiUtils.getRetrofitBucketForAddParticipant(apiVersion, user.getBaseUrl(),
                                                                             conversationToken,
                                                                             userId);

                ncApi.addParticipant(credentials, retrofitBucket.getUrl(), retrofitBucket.getQueryMap())
                        .subscribeOn(Schedulers.io())
                        .blockingSubscribe();
            }
        }

        if (selectedGroupIds != null) {
            for (String groupId : selectedGroupIds) {
                retrofitBucket = ApiUtils.getRetrofitBucketForAddParticipantWithSource(
                        apiVersion,
                        user.getBaseUrl(),
                        conversationToken,
                        "groups",
                        groupId
                                                                                      );

                ncApi.addParticipant(credentials, retrofitBucket.getUrl(), retrofitBucket.getQueryMap())
                        .subscribeOn(Schedulers.io())
                        .blockingSubscribe();
            }
        }

        if (selectedCircleIds != null) {
            for (String circleId : selectedCircleIds) {
                retrofitBucket = ApiUtils.getRetrofitBucketForAddParticipantWithSource(
                        apiVersion,
                        user.getBaseUrl(),
                        conversationToken,
                        "circles",
                        circleId
                                                                                      );

                ncApi.addParticipant(credentials, retrofitBucket.getUrl(), retrofitBucket.getQueryMap())
                        .subscribeOn(Schedulers.io())
                        .blockingSubscribe();
            }
        }

        if (selectedEmails != null) {
            for (String email : selectedEmails) {
                retrofitBucket = ApiUtils.getRetrofitBucketForAddParticipantWithSource(
                        apiVersion,
                        user.getBaseUrl(),
                        conversationToken,
                        "emails",
                        email
                                                                                      );

                ncApi.addParticipant(credentials, retrofitBucket.getUrl(), retrofitBucket.getQueryMap())
                        .subscribeOn(Schedulers.io())
                        .blockingSubscribe();
            }
        }

        eventBus.post(new EventStatus(UserIdUtils.INSTANCE.getIdForUser(user),
                                      EventStatus.EventType.PARTICIPANTS_UPDATE,
                                      true));
        return Result.success();
    }
}