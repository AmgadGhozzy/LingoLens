package com.venom.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.venom.data.local.dao.OcrDao
import com.venom.data.local.typeconverter.OcrResponseConverter
import com.venom.data.model.OcrEntry

@Database(entities = [OcrEntry::class], version = 1, exportSchema = false)
@TypeConverters(OcrResponseConverter::class)
abstract class OcrDatabase : RoomDatabase() {
    abstract fun ocrDao(): OcrDao
}