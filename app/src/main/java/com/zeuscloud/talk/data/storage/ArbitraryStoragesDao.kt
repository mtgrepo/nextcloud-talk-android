/*
 * Nextcloud Talk application
 *
 * @author Andy Scherzinger
 * Copyright (C) 2022 Andy Scherzinger <infoi@andy-scherzinger.de>
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

package com.zeuscloud.talk.data.storage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zeuscloud.talk.data.storage.model.ArbitraryStorageEntity
import io.reactivex.Maybe

@Dao
abstract class ArbitraryStoragesDao {
    @Query(
        "SELECT * FROM ArbitraryStorage WHERE " +
            "accountIdentifier = :accountIdentifier AND " +
            "\"key\" = :key AND " +
            "object = :objectString"
    )
    abstract fun getStorageSetting(
        accountIdentifier: Long,
        key: String,
        objectString: String
    ): Maybe<ArbitraryStorageEntity>

    @Query(
        "SELECT * FROM ArbitraryStorage"
    )
    abstract fun getAll(): Maybe<List<ArbitraryStorageEntity>>

    @Query("DELETE FROM ArbitraryStorage WHERE accountIdentifier = :accountIdentifier")
    abstract fun deleteArbitraryStorage(accountIdentifier: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun saveArbitraryStorage(arbitraryStorage: ArbitraryStorageEntity): Long
}
