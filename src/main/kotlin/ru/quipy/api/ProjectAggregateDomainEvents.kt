package ru.quipy.api

import ru.quipy.api.aggregates.ProjectAggregate
import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

const val PROJECT_CREATED_EVENT = "PROJECT_CREATED_EVENT"
const val USER_APPENDED_EVENT = "USER_APPENDED_EVENT"
const val PROJECT_UPDATED_EVENT = "PROJECT_UPDATED_EVENT"

// API
@DomainEvent(name = PROJECT_CREATED_EVENT)
class ProjectCreatedEvent(
    val projectId: UUID,
    val title: String,
    val creatorId: String,
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

@DomainEvent(name = USER_APPENDED_EVENT)
data class ProjectUpdatedEvent(
    val projectId: UUID,
    val title: String,
) : Event<ProjectAggregate>(
    name = PROJECT_UPDATED_EVENT,
    createdAt = System.currentTimeMillis()
)