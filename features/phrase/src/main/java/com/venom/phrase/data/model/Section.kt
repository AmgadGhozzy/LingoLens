package com.venom.phrase.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "sections",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["category_id"],
            childColumns = ["category_id"]
        )
    ]
)
data class Section(
    @PrimaryKey
    @ColumnInfo(name = "section_id")
    val sectionId: Int,

    @ColumnInfo(name = "category_id")
    val categoryId: Int,

    @ColumnInfo(name = "priority")
    val priority: Int,

    @ColumnInfo(name = "en-US", defaultValue = "''")
    val enUS: String,

    @ColumnInfo(name = "de", defaultValue = "''")
    val de: String,

    @ColumnInfo(name = "sw", defaultValue = "''")
    val sw: String,

    @ColumnInfo(name = "zh-CN", defaultValue = "''")
    val zhCN: String,

    @ColumnInfo(name = "da", defaultValue = "''")
    val da: String,

    @ColumnInfo(name = "cs", defaultValue = "''")
    val cs: String,

    @ColumnInfo(name = "en-AU", defaultValue = "''")
    val enAU: String,

    @ColumnInfo(name = "zh-TW", defaultValue = "''")
    val zhTW: String,

    @ColumnInfo(name = "ar-SA", defaultValue = "''")
    val arSA: String,

    @ColumnInfo(name = "fr-CA", defaultValue = "''")
    val frCA: String,

    @ColumnInfo(name = "es-US", defaultValue = "''")
    val esUS: String,

    @ColumnInfo(name = "fr-FR", defaultValue = "''")
    val frFR: String,

    @ColumnInfo(name = "en-UK", defaultValue = "''")
    val enUK: String,

    @ColumnInfo(name = "es-MX", defaultValue = "''")
    val esMX: String,

    @ColumnInfo(name = "es-ES", defaultValue = "''")
    val esES: String,

    @ColumnInfo(name = "hu", defaultValue = "''")
    val hu: String,

    @ColumnInfo(name = "uk", defaultValue = "''")
    val uk: String,

    @ColumnInfo(name = "ar-EG", defaultValue = "''")
    val arEG: String,

    @ColumnInfo(name = "zh-HK", defaultValue = "''")
    val zhHK: String,

    @ColumnInfo(name = "pt-PT", defaultValue = "''")
    val ptPT: String,

    @ColumnInfo(name = "ar-AE", defaultValue = "''")
    val arAE: String,

    @ColumnInfo(name = "pt-BR", defaultValue = "''")
    val ptBR: String,

    @ColumnInfo(name = "tr", defaultValue = "''")
    val tr: String,

    @ColumnInfo(name = "ja", defaultValue = "''")
    val ja: String,

    @ColumnInfo(name = "fi", defaultValue = "''")
    val fi: String,

    @ColumnInfo(name = "sk", defaultValue = "''")
    val sk: String,

    @ColumnInfo(name = "iw", defaultValue = "''")
    val iw: String,

    @ColumnInfo(name = "ms", defaultValue = "''")
    val ms: String,

    @ColumnInfo(name = "hr", defaultValue = "''")
    val hr: String,

    @ColumnInfo(name = "vi", defaultValue = "''")
    val vi: String,

    @ColumnInfo(name = "ca", defaultValue = "''")
    val ca: String,

    @ColumnInfo(name = "th", defaultValue = "''")
    val th: String,

    @ColumnInfo(name = "pl", defaultValue = "''")
    val pl: String,

    @ColumnInfo(name = "sv", defaultValue = "''")
    val sv: String,

    @ColumnInfo(name = "id", defaultValue = "''")
    val id: String,

    @ColumnInfo(name = "ro", defaultValue = "''")
    val ro: String,

    @ColumnInfo(name = "nl", defaultValue = "''")
    val nl: String,

    @ColumnInfo(name = "ko", defaultValue = "''")
    val ko: String,

    @ColumnInfo(name = "el", defaultValue = "''")
    val el: String,

    @ColumnInfo(name = "it", defaultValue = "''")
    val it: String,

    @ColumnInfo(name = "no", defaultValue = "''")
    val no: String,

    @ColumnInfo(name = "hi", defaultValue = "''")
    val hi: String,

    @ColumnInfo(name = "ru", defaultValue = "''")
    val ru: String
)
