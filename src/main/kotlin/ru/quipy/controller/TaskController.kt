﻿package ru.quipy.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.quipy.api.*
import ru.quipy.api.aggregates.ProjectAggregate
import ru.quipy.api.aggregates.TaskAggregate
import ru.quipy.api.aggregates.TaskStatusAggregate
import ru.quipy.api.aggregates.UserAggregate
import ru.quipy.controller.models.AddExecutorsModel
import ru.quipy.controller.models.ChangeStatusModel
import ru.quipy.controller.models.CreateStatusModel
import ru.quipy.controller.models.CreateTaskModel
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.project.Project
import ru.quipy.logic.task.Task
import ru.quipy.logic.task.TaskStatus
import ru.quipy.logic.user.User
import ru.quipy.projections.TaskProjection
import ru.quipy.projections.TaskProjectionRepository
import java.util.*

@RestController
@RequestMapping("/tasks")
class TaskController(
    val taskProjectionRepository: TaskProjectionRepository,
    val taskStatusEsService: EventSourcingService<UUID, TaskStatusAggregate, TaskStatus>,
    val projectEsService: EventSourcingService<UUID, ProjectAggregate, Project>,
    val taskEsService: EventSourcingService<UUID, TaskAggregate, Task>,
    val userEsService: EventSourcingService<UUID, UserAggregate, User>
) {

    @PostMapping("/status")
    fun createStatus(@RequestBody status: CreateStatusModel): TaskStatusCreatedEvent {
        var project = projectEsService.getState(status.projectId)
            ?: ResponseEntity("project not found", HttpStatus.NOT_FOUND)

        return taskStatusEsService.create {
            it.create(
                status.name,
                status.projectId,
                status.rColor,
                status.gColor,
                status.bColor
            )
        }
    }

    @PostMapping
    fun createTask(@RequestBody task: CreateTaskModel): TaskCreatedEvent {
        var project = projectEsService.getState(task.projectId)
            ?: ResponseEntity("project not found", HttpStatus.NOT_FOUND)

        val p = project as Project
        val defaultStatus = p.defaultStatus

        return taskEsService.create {
            it.createTask(task.taskName, task.taskDescription, defaultStatus, task.projectId)
        }
    }

    @PutMapping
    fun changeStatus(@RequestBody changeStatusModel: ChangeStatusModel): TaskStatusChangedEvent {
        val foundTask = taskEsService.getState(changeStatusModel.taskId)
            ?: ResponseEntity("task not found", HttpStatus.NOT_FOUND)

        val task = foundTask as Task

        val foundProject = projectEsService.getState(task.projectId)!!

        if (!(foundProject.taskStatuses.containsKey(changeStatusModel.statusId)))
            ResponseEntity("requested status does not belong to tasks' project", HttpStatus.BAD_REQUEST)

        return taskEsService.update(task.taskId) {
            it.changeStatus(task.taskId, foundTask.projectId, foundProject.taskStatuses[changeStatusModel.statusId]!!)
        }
    }

    @GetMapping("/{taskId}")
    fun getTask(@PathVariable taskId: UUID): Task? {
        return taskEsService.getState(taskId)
    }

    @PostMapping("/{taskId}/add-executors")
    fun addExecutors(@PathVariable taskId : UUID, @RequestBody model : AddExecutorsModel): ExecutorAddedEvent {
        val user = userEsService.getState(model.executors[0])!!
        val userProject = projectEsService.getState(user.userId)!!
        val taskProject = projectEsService.getState(taskId)!!
        if (taskProject.projectId != userProject.projectId)
            ResponseEntity("requested status does not belong to tasks' project", HttpStatus.BAD_REQUEST)
        return taskEsService.update(taskId){
            it.assignExecutors(model.executors)
        }
    }

    @GetMapping("/project/{projectId}")
    fun getTasksByProjectId(@PathVariable projectId: UUID): ResponseEntity<List<TaskProjection>> {
        val tasks = taskProjectionRepository.findByProjectId(projectId)
        return if (tasks.isNotEmpty()) {
            ResponseEntity.ok(tasks)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping
    fun getTaskByName(@RequestParam taskName: String): ResponseEntity<List<TaskProjection>> {
        val tasks = taskProjectionRepository.findByTaskName(taskName)
        return if (tasks.isNotEmpty()) {
            ResponseEntity.ok(tasks)
        } else {
            ResponseEntity.notFound().build()
        }
    }
}