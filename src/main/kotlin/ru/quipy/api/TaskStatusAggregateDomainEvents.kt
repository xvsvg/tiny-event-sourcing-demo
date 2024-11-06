package ru.quipy.api

import ru.quipy.api.aggregates.TaskStatusAggregate
import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

const val TASK_STATUS_CREATED_EVENT = "TASK_STATUS_CREATED_EVENT"

@DomainEvent(name = TASK_STATUS_CREATED_EVENT)
data class TaskStatusCreatedEvent(
    val taskStatusId: UUID,
    val taskStatusName: String,
    val projectId: UUID,
    val rColor : Int,
    val bColor : Int,
    val gColor : Int
) : Event<TaskStatusAggregate>(
    name = TASK_STATUS_CREATED_EVENT,
    createdAt = System.currentTimeMillis()
)