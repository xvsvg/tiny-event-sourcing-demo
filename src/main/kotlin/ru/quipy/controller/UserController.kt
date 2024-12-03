package ru.quipy.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.quipy.api.UserCreatedEvent
import ru.quipy.api.aggregates.UserAggregate
import ru.quipy.controller.models.CreateUserModel
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.user.User
import ru.quipy.projections.UserProjection
import ru.quipy.projections.UserProjectionRepository
import java.util.*

@RestController
@RequestMapping("/users")
class UserController(
    val userEsService: EventSourcingService<UUID, UserAggregate, User>,
    val userProjectionRepository: UserProjectionRepository
) {

    @PostMapping
    fun create(@RequestBody user: CreateUserModel): UserCreatedEvent {
        return userEsService.create { it.create(user.fullname, user.nickname, user.password) }
    }

    @GetMapping("/{userId}")
    fun getUserById(@PathVariable userId: UUID): ResponseEntity<UserProjection> {
        val user = userProjectionRepository.findById(userId)
        return if (user.isNotEmpty()) {
            ResponseEntity.ok(user.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping
    fun getAllUsers(): ResponseEntity<List<UserProjection>> {
        val users = userProjectionRepository.findAll()
        return if (users.isNotEmpty()) {
            ResponseEntity.ok(users)
        } else {
            ResponseEntity.noContent().build()
        }
    }
}