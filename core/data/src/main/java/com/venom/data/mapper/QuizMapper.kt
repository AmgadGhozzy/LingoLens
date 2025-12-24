package com.venom.data.mapper

import com.venom.data.local.entity.QuestionEntity
import com.venom.data.local.entity.TestCategoryEntity
import com.venom.data.local.entity.TestClassEntity
import com.venom.data.local.entity.TestListEntity
import com.venom.domain.model.Question
import com.venom.domain.model.TestCategory
import com.venom.domain.model.TestClass
import com.venom.domain.model.TestList

fun TestClassEntity.toDomainModel(): TestClass {
    return TestClass(
        id = id ?: 0,
        title = title,
        totalTest = totalTest,
        totalQuestion = totalQuestion,
        totalCategory = totalCategory,
        progress = 0f
    )
}

fun TestCategoryEntity.toDomainModel(): TestCategory {
    return TestCategory(
        id = id ?: 0,
        classId = classId,
        title = title,
        totalTest = totalTest,
        totalQuestion = totalQuestion,
        progress = 0f
    )
}

fun TestListEntity.toDomainModel(): TestList {
    return TestList(
        id = id ?: 0,
        categoryId = categoryId,
        title = title,
        questionNum = questionNum,
        isCompleted = false
    )
}

fun QuestionEntity.toDomainModel(): Question {
    return Question(
        id = id ?: 0,
        testId = testId,
        question = question,
        choices = listOfNotNull(choice1, choice2, choice3, choice4, choice5),
        answer = answer
    )
}