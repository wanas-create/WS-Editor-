package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects WHERE isDeleted = 0 ORDER BY updatedAt DESC")
    fun getRecentProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE isDeleted = 0 AND isDraft = 1 ORDER BY updatedAt DESC")
    fun getDraftProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE isDeleted = 1 ORDER BY updatedAt DESC")
    fun getDeletedProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :id LIMIT 1")
    suspend fun getProjectById(id: Long): ProjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity): Long

    @Update
    suspend fun updateProject(project: ProjectEntity)

    @Query("UPDATE projects SET isDeleted = 1, updatedAt = :timestamp WHERE id = :id")
    suspend fun softDelete(id: Long, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE projects SET isDeleted = 0, updatedAt = :timestamp WHERE id = :id")
    suspend fun restoreProject(id: Long, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM projects WHERE id = :id")
    suspend fun permanentlyDelete(id: Long)

    @Query("UPDATE projects SET title = :newTitle, updatedAt = :timestamp WHERE id = :id")
    suspend fun renameProject(id: Long, newTitle: String, timestamp: Long = System.currentTimeMillis())
}
