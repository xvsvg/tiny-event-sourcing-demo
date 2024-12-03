package ru.quipy.projections

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserProjectionRepository : JpaRepository<UserProjection, UUID> {
    fun findByNickname(nickname: String): List<UserProjection>
} 