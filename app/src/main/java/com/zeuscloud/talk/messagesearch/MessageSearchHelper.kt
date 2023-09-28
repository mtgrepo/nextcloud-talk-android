/*
 * Nextcloud Talk application
 *
 * @author Álvaro Brey
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

package com.zeuscloud.talk.messagesearch

import android.util.Log
import com.zeuscloud.talk.models.domain.SearchMessageEntry
import com.zeuscloud.talk.repositories.unifiedsearch.UnifiedSearchRepository
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

class MessageSearchHelper @JvmOverloads constructor(
    private val unifiedSearchRepository: UnifiedSearchRepository,
    private val fromRoom: String? = null
) {

    data class MessageSearchResults(val messages: List<SearchMessageEntry>, val hasMore: Boolean)

    private var unifiedSearchDisposable: Disposable? = null
    private var previousSearch: String? = null
    private var previousCursor: Int = 0
    private var previousResults: List<SearchMessageEntry> = emptyList()

    fun startMessageSearch(search: String): Observable<MessageSearchResults> {
        resetCachedData()
        return doSearch(search)
    }

    fun loadMore(): Observable<MessageSearchResults>? {
        previousSearch?.let {
            return doSearch(it, previousCursor)
        }
        return null
    }

    fun cancelSearch() {
        disposeIfPossible()
    }

    private fun doSearch(search: String, cursor: Int = 0): Observable<MessageSearchResults> {
        disposeIfPossible()
        return searchCall(search, cursor)
            .map { results ->
                previousSearch = search
                previousCursor = results.cursor
                previousResults = previousResults + results.entries
                MessageSearchResults(previousResults, results.hasMore)
            }
            .doOnSubscribe {
                unifiedSearchDisposable = it
            }
            .doOnError { throwable ->
                Log.e(TAG, "message search - ERROR", throwable)
                resetCachedData()
                disposeIfPossible()
            }
            .doOnComplete(this::disposeIfPossible)
    }

    private fun searchCall(
        search: String,
        cursor: Int
    ): Observable<UnifiedSearchRepository.UnifiedSearchResults<SearchMessageEntry>> {
        return when {
            fromRoom != null -> {
                unifiedSearchRepository.searchInRoom(
                    roomToken = fromRoom,
                    searchTerm = search,
                    cursor = cursor
                )
            }
            else -> {
                unifiedSearchRepository.searchMessages(
                    searchTerm = search,
                    cursor = cursor
                )
            }
        }
    }

    private fun resetCachedData() {
        previousSearch = null
        previousCursor = 0
        previousResults = emptyList()
    }

    private fun disposeIfPossible() {
        unifiedSearchDisposable?.dispose()
        unifiedSearchDisposable = null
    }

    companion object {
        private val TAG = MessageSearchHelper::class.simpleName
    }
}
