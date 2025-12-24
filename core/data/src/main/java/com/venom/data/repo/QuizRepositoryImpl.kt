package com.venom.data.repo

import com.venom.data.local.database.QuizDatabase
import com.venom.data.mapper.toDomainModel
import com.venom.domain.model.Question
import com.venom.domain.model.TestCategory
import com.venom.domain.model.TestClass
import com.venom.domain.model.TestList
import com.venom.domain.repo.IQuizRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class QuizRepositoryImpl @Inject constructor(
    private val database: QuizDatabase
) : IQuizRepository {

    override fun getAllTestClasses(): Flow<List<TestClass>> {
        return database.testClassDao().getAllTestClasses().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getCategoriesForClass(classId: Int): Flow<List<TestCategory>> {
        return database.testCategoryDao().getCategoriesForClass(classId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getTestsForCategory(categoryId: Int): Flow<List<TestList>> {
        return database.testListDao().getTestsForCategory(categoryId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getQuestionsForTest(testId: Int): Flow<List<Question>> {
        return database.questionDao().getQuestionsForTest(testId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun getTestClassById(classId: Int): TestClass? {
        return database.testClassDao().getTestClassById(classId)?.toDomainModel()
    }

    override suspend fun getCategoryById(categoryId: Int): TestCategory? {
        return database.testCategoryDao().getCategoryById(categoryId)?.toDomainModel()
    }

    override suspend fun getTestById(testId: Int): TestList? {
        return database.testListDao().getTestById(testId)?.toDomainModel()
    }
}