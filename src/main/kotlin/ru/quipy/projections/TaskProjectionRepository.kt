package ru.quipy.projections

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface TaskProjectionRepository : JpaRepository<TaskProjection, UUID> {
    fun findByProjectId(projectId: UUID): List<TaskProjection>
    fun findByTaskName(taskName: String): List<TaskProjection>
} 