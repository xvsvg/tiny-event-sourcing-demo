package ru.quipy.projections

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface TaskStatusProjectionRepository : JpaRepository<TaskStatusProjection, UUID> 