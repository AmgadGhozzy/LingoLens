package com.venom.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "Classes")
data class TestClassEntity(
    @PrimaryKey val id: Int?,
    val title: String,
    val totalTest: Int,
    val totalQuestion: Int,
    val totalCategory: Int
)

@Entity(
    tableName = "TestCategories",
    foreignKeys = [ForeignKey(
        entity = TestClassEntity::class,
        parentColumns = ["id"],
        childColumns = ["classId"],
        onDelete = ForeignKey.NO_ACTION,
        onUpdate = ForeignKey.NO_ACTION
    )]
)
data class TestCategoryEntity(
    @PrimaryKey val id: Int?,
    @ColumnInfo(index = true) val classId: Int,
    val title: String,
    val totalTest: Int,
    val totalQuestion: Int
)

@Entity(
    tableName = "TestListe",
    foreignKeys = [ForeignKey(
        entity = TestCategoryEntity::class,
        parentColumns = ["id"],
        childColumns = ["categoryId"],
        onDelete = ForeignKey.NO_ACTION,
        onUpdate = ForeignKey.NO_ACTION
    )]
)
data class TestListEntity(
    @PrimaryKey val id: Int?,
    @ColumnInfo(index = true) val categoryId: Int,
    val title: String,
    val questionNum: Int
)

@Entity(
    tableName = "Questions",
    foreignKeys = [ForeignKey(
        entity = TestListEntity::class,
        parentColumns = ["id"],
        childColumns = ["testId"],
        onDelete = ForeignKey.NO_ACTION,
        onUpdate = ForeignKey.NO_ACTION
    )]
)
data class QuestionEntity(
    @PrimaryKey val id: Int?,
    @ColumnInfo(index = true) val testId: Int,
    val question: String,
    val choice1: String,
    val choice2: String,
    val choice3: String?,
    val choice4: String?,
    val choice5: String?,
    val answer: String
)