package com.venom.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.venom.data.local.entity.QuestionEntity
import com.venom.data.local.entity.TestCategoryEntity
import com.venom.data.local.entity.TestClassEntity
import com.venom.data.local.entity.TestListEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TestClassDao {
    @Query("SELECT * FROM Classes ORDER BY id ASC")
    fun getAllTestClasses(): Flow<List<TestClassEntity>>

    @Query("SELECT * FROM Classes WHERE id = :classId")
    suspend fun getTestClassById(classId: Int): TestClassEntity?
}

@Dao
interface TestCategoryDao {
    @Query("SELECT * FROM TestCategories WHERE classId = :classId ORDER BY id ASC")
    fun getCategoriesForClass(classId: Int): Flow<List<TestCategoryEntity>>

    @Query("SELECT * FROM TestCategories WHERE id = :categoryId")
    suspend fun getCategoryById(categoryId: Int): TestCategoryEntity?
}

@Dao
interface TestListDao {
    @Query("SELECT * FROM TestListe WHERE categoryId = :categoryId ORDER BY id ASC")
    fun getTestsForCategory(categoryId: Int): Flow<List<TestListEntity>>

    @Query("SELECT * FROM TestListe WHERE id = :testId")
    suspend fun getTestById(testId: Int): TestListEntity?
}

@Dao
interface QuestionDao {
    @Query("SELECT * FROM Questions WHERE testId = :testId ORDER BY id ASC")
    fun getQuestionsForTest(testId: Int): Flow<List<QuestionEntity>>

    @Query("SELECT COUNT(*) FROM Questions WHERE testId = :testId")
    suspend fun getQuestionCount(testId: Int): Int
}