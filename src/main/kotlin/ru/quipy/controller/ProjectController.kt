package ru.quipy.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.quipy.api.ProjectCreatedEvent
import ru.quipy.api.ProjectNameChangedEvent
import ru.quipy.api.aggregates.ProjectAggregate
import ru.quipy.api.aggregates.TaskStatusAggregate
import ru.quipy.api.aggregates.UserAggregate
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.project.Project
import ru.quipy.logic.task.TaskStatus
import ru.quipy.logic.user.User
import java.util.*

@RestController
@RequestMapping("/projects")
class ProjectController(
    val projectEsService: EventSourcingService<UUID, ProjectAggregate, Project>,
    val userEsService: EventSourcingService<UUID, UserAggregate, User>,
    val taskStatusEsService: EventSourcingService<UUID, TaskStatusAggregate, TaskStatus>
) {

    @PostMapping("/{projectTitle}")
    fun createProject(@PathVariable projectTitle: String): ProjectCreatedEvent {
        return projectEsService.create { it.create(projectTitle) }
    }

    @PostMapping("/{projectId}/members/{userId}")
    fun addMember(@PathVariable projectId: UUID, @PathVariable userId : UUID): Any {
        var user = userEsService.getState(userId)
            ?: return ResponseEntity("user not found", HttpStatus.NOT_FOUND)

        return projectEsService.update(projectId){
            it.addMember(userId, user.fullname, user.nickname)
        }
    }

    @GetMapping("/{projectId}")
    fun getProject(@PathVariable projectId: UUID): Project? {
        return projectEsService.getState(projectId)
    }

    @PutMapping("/{projectId}/status/{statusId}")
    fun setDefaultStatus(@PathVariable projectId: UUID, @PathVariable statusId: UUID): Any {
        var status = taskStatusEsService.getState(statusId)
            ?: return ResponseEntity("status not found", HttpStatus.NOT_FOUND)

        return projectEsService.update(projectId){
            it.changeDefaultStatus(status)
        }
    }

    @DeleteMapping("/{projectId}/status/{statusId}")
    fun deleteStatus(@PathVariable projectId: UUID, @PathVariable statusId: UUID): Any {
        var status = taskStatusEsService.getState(statusId)
            ?: return ResponseEntity("status not found", HttpStatus.NOT_FOUND)

        return projectEsService.update(projectId){
            it.deleteDefaultStatus(status)
        }
    }

    @PutMapping("/{projectId}/{name}")
    fun changeProjectName(@PathVariable projectId: UUID, @PathVariable name: String): ProjectNameChangedEvent {
        return projectEsService.update(projectId){
            it.changeProjectName(name)
        }
    }
}