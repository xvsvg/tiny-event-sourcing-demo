package ru.quipy.logic.user

import ru.quipy.api.UserCreatedEvent
import ru.quipy.api.aggregates.UserAggregate
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

class User : AggregateState<UUID, UserAggregate> {
    lateinit var userId: UUID
    lateinit var fullname: String
    lateinit var nickname: String
    var password: ByteArray? = null

    override fun getId() = userId

    fun create(fullname: String, nickname: String, password: String): UserCreatedEvent {
        if (fullname.isEmpty() || nickname.isEmpty() || password.isEmpty())
            throw IllegalArgumentException("Fullname, nickname and password cannot be empty")

        return UserCreatedEvent(UUID.randomUUID(), fullname, nickname, password)
    }

    @StateTransitionFunc
    fun userCreated(event: UserCreatedEvent) {
        userId = event.userId
        fullname = event.fullname
        nickname = event.nickname
        password = event.password.encodeToByteArray()
    }
}

data class UserEntity(
    var id: UUID,
    var fullname: String,
    var nickname: String
)