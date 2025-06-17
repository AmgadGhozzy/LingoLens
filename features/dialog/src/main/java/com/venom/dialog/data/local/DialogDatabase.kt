package com.venom.dialog.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.venom.dialog.data.local.dao.DialogMessageDao
import com.venom.dialog.data.local.model.DialogMessage

@Database(entities = [DialogMessage::class], version = 1, exportSchema = false)
abstract class DialogDatabase : RoomDatabase() {
    abstract fun dialogMessageDao(): DialogMessageDao
}
