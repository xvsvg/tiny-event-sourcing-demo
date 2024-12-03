package ru.quipy.api

import ru.quipy.api.aggregates.UserAggregate
import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

const val USER_CREATED_EVENT = "USER_CREATED_EVENT"

@DomainEvent(USER_CREATED_EVENT)
data class UserCreatedEvent(
    val userId : UUID,
    val fullname: String,
    val password : String,
    val nickname : String
) : Event<UserAggregate>(
    name = USER_CREATED_EVENT,
    createdAt = System.currentTimeMillis(),
)