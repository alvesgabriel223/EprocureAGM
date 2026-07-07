package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String, // can be email or uuid
    val name: String,
    val phone: String,
    val email: String,
    val profilePhoto: String? = null,
    val joinedAt: Long = System.currentTimeMillis()
)
