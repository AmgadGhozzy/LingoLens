package com.venom.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.venom.data.local.Entity.OcrEntity
import com.venom.data.local.converters.OcrResponseConverter
import com.venom.data.local.dao.OcrDao

@Database(entities = [OcrEntity::class], version = 1, exportSchema = false)
@TypeConverters(OcrResponseConverter::class)
abstract class OcrDatabase : RoomDatabase() {
    abstract fun ocrDao(): OcrDao
}