package com.venom.dialog.data.repo

import com.venom.dialog.data.local.dao.DialogMessageDao
import com.venom.dialog.data.local.model.DialogMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DialogRepository @Inject constructor(
    private val dialogMessageDao: DialogMessageDao
) {

    fun getAllMessages(): Flow<List<DialogMessage>> = dialogMessageDao.getAllMessages()

    suspend fun insertMessage(message: DialogMessage) = dialogMessageDao.insertMessage(message)

    suspend fun deleteMessage(message: DialogMessage) = dialogMessageDao.deleteMessage(message)

    suspend fun deleteAllMessages() = dialogMessageDao.deleteAllMessages()

}