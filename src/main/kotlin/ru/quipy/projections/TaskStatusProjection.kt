package ru.quipy.projections

import javax.persistence.Entity
import javax.persistence.Id
import java.util.UUID

@Entity
data class TaskStatusProjection(
    @Id
    val taskStatusId: UUID = UUID.randomUUID(),
    val name: String = "",
    val projectId: UUID = UUID.randomUUID(),
    val rColor: Int = 0,
    val gColor: Int = 0,
    val bColor: Int = 0,
    val priority: Int = 0
) {
    constructor() : this(UUID.randomUUID(), "", UUID.randomUUID(), 0, 0, 0, 0)
}
