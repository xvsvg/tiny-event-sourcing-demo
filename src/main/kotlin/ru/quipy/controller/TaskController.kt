package ru.quipy.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.quipy.api.ExecutorAddedEvent
import ru.quipy.api.TaskCreatedEvent
import ru.quipy.api.TaskStatusChangedEvent
import ru.quipy.api.TaskStatusCreatedEvent
import ru.quipy.api.aggregates.ProjectAggregate
import ru.quipy.api.aggregates.TaskAggregate
import ru.quipy.api.aggregates.TaskStatusAggregate
import ru.quipy.controller.models.ChangeStatusModel
import ru.quipy.controller.models.CreateStatusModel
import ru.quipy.controller.models.CreateTaskModel
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.project.Project
import ru.quipy.logic.task.Task
import ru.quipy.logic.task.TaskStatus
import java.util.*

@RestController
@RequestMapping("/tasks")
class TaskController(
    val taskStatusEsService: EventSourcingService<UUID, TaskStatusAggregate, TaskStatus>,
    val projectEsService: EventSourcingService<UUID, ProjectAggregate, Project>,
    val taskEsService: EventSourcingService<UUID, TaskAggregate, Task>,
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
    fun createTask(@RequestBody task : CreateTaskModel): TaskCreatedEvent {
        var project = projectEsService.getState(task.projectId)
            ?: ResponseEntity("project not found", HttpStatus.NOT_FOUND)

        val p = project as Project
        return projectEsService.update(p.projectId) {
            it.createTask(task.taskName, task.taskDescription, task.projectId, p.defaultStatus)
        }
    }

    @PutMapping
    fun changeStatus(@RequestBody changeStatusModel : ChangeStatusModel): TaskStatusChangedEvent {
        val foundTask = taskEsService.getState(changeStatusModel.taskId)
            ?: ResponseEntity("task not found", HttpStatus.NOT_FOUND)

        val task = foundTask as Task

        val foundProject = projectEsService.getState(task.projectId)!!

        if (!(foundProject.taskStatuses.containsKey(changeStatusModel.statusId)))
            ResponseEntity("requested status does not belong to tasks' project", HttpStatus.BAD_REQUEST)

        return taskEsService.update(task.taskId){
            it.changeStatus(foundProject.taskStatuses[changeStatusModel.statusId]!!)
        }
    }
}