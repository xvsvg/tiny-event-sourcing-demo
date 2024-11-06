package ru.quipy.api

import ru.quipy.api.aggregates.ProjectAggregate
import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import ru.quipy.logic.task.TaskStatus
import java.util.*

const val PROJECT_CREATED_EVENT = "PROJECT_CREATED_EVENT"
const val USER_APPENDED_EVENT = "USER_APPENDED_EVENT"
const val PROJECT_UPDATED_EVENT = "PROJECT_UPDATED_EVENT"
const val STATUS_DELETED_EVENT = "STATUS_DELETED_EVENT"
const val PROJECT_NAME_CHANGED_EVENT = "PROJECT_NAME_CHANGED_EVENT"

// API
@DomainEvent(name = PROJECT_CREATED_EVENT)
class ProjectCreatedEvent(
    val projectId: UUID,
    val title: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = PROJECT_CREATED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = USER_APPENDED_EVENT)
data class UserAppendedEvent(
    val userId: UUID,
    val fullname: String,
    val nickname: String,
) : Event<ProjectAggregate>(
    name = USER_APPENDED_EVENT,
    createdAt = System.currentTimeMillis()
)

@DomainEvent(name = PROJECT_UPDATED_EVENT)
data class ProjectUpdatedEvent(
    val statusId : UUID,
    val projectId: UUID,
    val statusName: String,
    val rColor : Int = 0,
    val gColor : Int = 0,
    val bColor : Int = 0,
) : Event<ProjectAggregate>(
    name = PROJECT_UPDATED_EVENT,
    createdAt = System.currentTimeMillis()
)

@DomainEvent(name = STATUS_DELETED_EVENT)
data class StatusDeletedEvent(
    val statusId : UUID
) : Event<ProjectAggregate>(
    name = STATUS_DELETED_EVENT,
    createdAt = System.currentTimeMillis()
)

@DomainEvent(name = PROJECT_NAME_CHANGED_EVENT)
data class ProjectNameChangedEvent(
    val projectName : String
) : Event<ProjectAggregate>(
    name = PROJECT_NAME_CHANGED_EVENT,
    createdAt = System.currentTimeMillis()
)