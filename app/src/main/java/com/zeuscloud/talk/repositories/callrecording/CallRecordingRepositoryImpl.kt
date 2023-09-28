/*
 * Nextcloud Talk application
 *
 * @author Marcel Hibbe
 * Copyright (C) 2022 Marcel Hibbe <dev@mhibbe.de>
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

package com.zeuscloud.talk.repositories.callrecording

import com.zeuscloud.talk.api.NcApi
import com.zeuscloud.talk.data.user.model.User
import com.zeuscloud.talk.models.domain.StartCallRecordingModel
import com.zeuscloud.talk.models.domain.StopCallRecordingModel
import com.zeuscloud.talk.models.json.generic.GenericMeta
import com.zeuscloud.talk.utils.ApiUtils
import com.zeuscloud.talk.utils.database.user.CurrentUserProviderNew
import io.reactivex.Observable

class CallRecordingRepositoryImpl(private val ncApi: NcApi, currentUserProvider: CurrentUserProviderNew) :
    CallRecordingRepository {

    val currentUser: User = currentUserProvider.currentUser.blockingGet()
    val credentials: String = ApiUtils.getCredentials(currentUser.username, currentUser.token)

    var apiVersion = 1

    override fun startRecording(
        roomToken: String
    ): Observable<StartCallRecordingModel> {
        return ncApi.startRecording(
            credentials,
            ApiUtils.getUrlForRecording(
                apiVersion,
                currentUser.baseUrl,
                roomToken
            ),
            1
        ).map { mapToStartCallRecordingModel(it.ocs?.meta!!) }
    }

    override fun stopRecording(
        roomToken: String
    ): Observable<StopCallRecordingModel> {
        return ncApi.stopRecording(
            credentials,
            ApiUtils.getUrlForRecording(
                apiVersion,
                currentUser.baseUrl,
                roomToken
            )
        ).map { mapToStopCallRecordingModel(it.ocs?.meta!!) }
    }

    private fun mapToStartCallRecordingModel(
        response: GenericMeta
    ): StartCallRecordingModel {
        val success = response.statusCode == HTTP_OK
        return StartCallRecordingModel(
            success
        )
    }

    private fun mapToStopCallRecordingModel(
        response: GenericMeta
    ): StopCallRecordingModel {
        val success = response.statusCode == HTTP_OK
        return StopCallRecordingModel(
            success
        )
    }

    companion object {
        private const val HTTP_OK: Int = 200
    }
}
