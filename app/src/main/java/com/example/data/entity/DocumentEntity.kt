package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "documents")
data class DocumentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String,
    val ownerName: String,
    val status: String, // "LOST" or "FOUND"
    val location: String,
    val date: String,
    val description: String,
    val contactPhone: String,
    val contactEmail: String,
    val registeredByUserId: String,
    val photoUri: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isDelivered: Boolean = false,
    val lat: Double = 0.0,
    val lng: Double = 0.0
)
