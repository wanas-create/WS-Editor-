package com.example.data

import kotlinx.coroutines.flow.Flow

class ProjectRepository(private val projectDao: ProjectDao) {
    val recentProjects: Flow<List<ProjectEntity>> = projectDao.getRecentProjects()
    val draftProjects: Flow<List<ProjectEntity>> = projectDao.getDraftProjects()
    val deletedProjects: Flow<List<ProjectEntity>> = projectDao.getDeletedProjects()

    suspend fun getProject(id: Long): ProjectEntity? = projectDao.getProjectById(id)

    suspend fun saveProject(project: ProjectEntity): Long = projectDao.insertProject(project)

    suspend fun updateProject(project: ProjectEntity) = projectDao.updateProject(project)

    suspend fun softDelete(id: Long) = projectDao.softDelete(id)

    suspend fun restore(id: Long) = projectDao.restoreProject(id)

    suspend fun permanentlyDelete(id: Long) = projectDao.permanentlyDelete(id)

    suspend fun rename(id: Long, newTitle: String) = projectDao.renameProject(id, newTitle)

    suspend fun duplicateProject(id: Long): Long {
        val original = projectDao.getProjectById(id) ?: return -1L
        val copy = original.copy(
            id = 0,
            title = "${original.title} (Copy)",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        return projectDao.insertProject(copy)
    }
}
