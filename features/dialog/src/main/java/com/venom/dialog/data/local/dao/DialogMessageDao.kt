package com.venom.dialog.data.local.dao

import androidx.room.*
import com.venom.dialog.data.local.model.DialogMessage
import kotlinx.coroutines.flow.Flow

@Dao
interface DialogMessageDao {
    @Query("SELECT * FROM dialog_messages ORDER BY timestamp DESC")
    fun getAllMessages(): Flow<List<DialogMessage>>

    @Query("SELECT * FROM dialog_messages WHERE id = :messageId")
    suspend fun getMessageById(messageId: String): DialogMessage?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: DialogMessage)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<DialogMessage>)

    @Delete
    suspend fun deleteMessage(message: DialogMessage)

    @Query("DELETE FROM dialog_messages")
    suspend fun deleteAllMessages()
}