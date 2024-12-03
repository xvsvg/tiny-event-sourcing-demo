package ru.quipy.projections

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ProjectProjectionRepository : JpaRepository<ProjectProjection, UUID> {
    fun findByUserId(userId: UUID): List<ProjectProjection>
}