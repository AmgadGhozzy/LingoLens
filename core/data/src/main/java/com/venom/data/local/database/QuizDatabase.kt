package com.venom.data.local.database


import androidx.room.Database
import androidx.room.RoomDatabase
import com.venom.data.local.dao.QuestionDao
import com.venom.data.local.dao.TestCategoryDao
import com.venom.data.local.dao.TestClassDao
import com.venom.data.local.dao.TestListDao
import com.venom.data.local.entity.QuestionEntity
import com.venom.data.local.entity.TestCategoryEntity
import com.venom.data.local.entity.TestClassEntity
import com.venom.data.local.entity.TestListEntity

@Database(
    entities = [
        QuestionEntity::class,
        TestClassEntity::class,
        TestCategoryEntity::class,
        TestListEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class QuizDatabase : RoomDatabase() {
    abstract fun testClassDao(): TestClassDao
    abstract fun testCategoryDao(): TestCategoryDao
    abstract fun testListDao(): TestListDao
    abstract fun questionDao(): QuestionDao
}