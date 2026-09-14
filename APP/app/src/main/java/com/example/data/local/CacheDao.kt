package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CacheDao {
    @Query("SELECT * FROM cached_data WHERE `key` = :key LIMIT 1")
    suspend fun getCached(key: String): CachedDataEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(entity: CachedDataEntity)

    @Query("SELECT lastUpdated FROM cached_data ORDER BY lastUpdated DESC LIMIT 1")
    suspend fun getLatestUpdateTime(): Long?

    @Query("DELETE FROM cached_data WHERE `key` = :key")
    suspend fun delete(key: String)

    @Query("DELETE FROM cached_data")
    suspend fun clearAll()
}
