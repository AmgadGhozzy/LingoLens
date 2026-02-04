package com.venom.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.venom.data.local.dao.UserActivityDao
import com.venom.data.local.dao.UserIdentityDao
import com.venom.data.local.dao.UserWordProgressDao
import com.venom.data.local.dao.WordMasterDao
import com.venom.data.local.entity.UserActivityEntity
import com.venom.data.local.entity.UserIdentityEntity
import com.venom.data.local.entity.UserWordProgressEntity
import com.venom.data.local.entity.WordMasterEntity

/**
 * Unified database for all learning-related entities.
 */
@Database(
    entities = [
        WordMasterEntity::class,
        UserIdentityEntity::class,
        UserWordProgressEntity::class,
        UserActivityEntity::class
    ],
    version = 1,
    exportSchema = false
)

abstract class LearningDatabase : RoomDatabase() {
    abstract fun wordMasterDao(): WordMasterDao
    abstract fun userIdentityDao(): UserIdentityDao
    abstract fun userWordProgressDao(): UserWordProgressDao
    abstract fun userActivityDao(): UserActivityDao
}
