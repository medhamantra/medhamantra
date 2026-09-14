package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity for persisting raw JSON payloads received from GitHub.
 * Guarantees offline availability, instant app launch, and zero schema crash
 * when remote JSON models are extended with new fields in GitHub.
 */
@Entity(tableName = "cached_data")
data class CachedDataEntity(
    @PrimaryKey
    val key: String,
    val jsonData: String,
    val lastUpdated: Long = System.currentTimeMillis()
)
