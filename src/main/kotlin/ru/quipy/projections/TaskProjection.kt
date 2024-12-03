package ru.quipy.projections

import javax.persistence.Entity
import javax.persistence.Id
import java.util.UUID

@Entity
data class TaskProjection(
    @Id
    val taskId: UUID = UUID.randomUUID(),
    val taskName: String = "",
    val description: String = "",
    var status: String? = null,
    val projectId: UUID = UUID.randomUUID()
) {
    constructor() : this(UUID.randomUUID(), "", "", null, UUID.randomUUID())
} 