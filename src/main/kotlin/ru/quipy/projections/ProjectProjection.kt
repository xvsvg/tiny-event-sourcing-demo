package ru.quipy.projections

import javax.persistence.Entity
import javax.persistence.Id
import java.util.UUID

@Entity
data class ProjectProjection(
    @Id
    val projectId: UUID = UUID.randomUUID(),
    val title: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    constructor() : this(UUID.randomUUID(), "", System.currentTimeMillis())
} 