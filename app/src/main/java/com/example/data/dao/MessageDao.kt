package com.example.data.dao

import androidx.room.*
import com.example.data.entity.MessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE chatRoomId = :chatRoomId ORDER BY timestamp ASC")
    fun getMessagesForChatRoom(chatRoomId: String): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity): Long
}
