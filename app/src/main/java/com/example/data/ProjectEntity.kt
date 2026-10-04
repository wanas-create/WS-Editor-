package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ProjectType {
    VIDEO,
    PHOTO
}

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val type: String, // "VIDEO" or "PHOTO"
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val durationMs: Long = 0L,
    val thumbnailUri: String? = null,
    val aspectRatio: String = "16:9",
    val resolution: String = "1080p",
    val fps: Int = 30,
    val isDraft: Boolean = true,
    val isDeleted: Boolean = false, // for Trash / Restore
    val mediaUrisJson: String = "[]",
    val projectDataJson: String = "{}"
)
