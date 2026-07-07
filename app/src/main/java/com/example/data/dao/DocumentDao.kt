package com.example.data.dao

import androidx.room.*
import com.example.data.entity.DocumentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentDao {
    @Query("SELECT * FROM documents ORDER BY timestamp DESC")
    fun getAllDocuments(): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents WHERE status = :status ORDER BY timestamp DESC")
    fun getDocumentsByStatus(status: String): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents WHERE id = :id LIMIT 1")
    suspend fun getDocumentById(id: Int): DocumentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: DocumentEntity): Long

    @Query("UPDATE documents SET isDelivered = :isDelivered WHERE id = :id")
    suspend fun updateDeliveryStatus(id: Int, isDelivered: Boolean)

    @Delete
    suspend fun deleteDocument(document: DocumentEntity)
}
