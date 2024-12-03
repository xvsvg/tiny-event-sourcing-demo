package ru.quipy.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.quipy.api.UserCreatedEvent
import ru.quipy.api.aggregates.UserAggregate
import ru.quipy.controller.models.CreateUserModel
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.user.User
import java.util.*

@RestController
@RequestMapping("/users")
class UserController(
    val userEsService: EventSourcingService<UUID, UserAggregate, User>
) {

    @PostMapping
    fun create(@RequestBody user: CreateUserModel): UserCreatedEvent {
        return userEsService.create { it.create(user.fullname, user.nickname, user.password) }
    }
}