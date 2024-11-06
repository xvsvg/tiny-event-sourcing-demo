package ru.quipy.api

import ru.quipy.api.aggregates.ProjectAggregate
import ru.quipy.api.aggregates.TaskAggregate
import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import ru.quipy.logic.task.TaskStatusEntity
import java.util.*

const val TASK_CREATED_EVENT = "TASK_CREATED_EVENT"
const val TASK_NAME_UPDATED_EVENT = "TASK_NAME_UPDATED_EVENT"
const val EXECUTOR_ADDED_EVENT = "EXECUTOR_ADDED_EVENT"

@DomainEvent(name = TASK_CREATED_EVENT)
data class TaskCreatedEvent(
    val taskId: UUID,
    val taskName: String,
    val description: String,
    val status: TaskStatusEntity?,
    val projectId: UUID
) : Event<ProjectAggregate>(
    name = TASK_CREATED_EVENT,
    createdAt = System.currentTimeMillis()
)

@DomainEvent(name = TASK_NAME_UPDATED_EVENT)
data class TaskStatusChangedEvent(
    val status: TaskStatusEntity
) : Event<TaskAggregate>(
    name = TASK_NAME_UPDATED_EVENT,
    createdAt = System.currentTimeMillis()
)

@DomainEvent(name = EXECUTOR_ADDED_EVENT)
data class ExecutorAddedEvent(
    val taskId: UUID,
    val executors: Collection<UUID>
) : Event<TaskAggregate>(
    name = EXECUTOR_ADDED_EVENT,
    createdAt = System.currentTimeMillis()
)