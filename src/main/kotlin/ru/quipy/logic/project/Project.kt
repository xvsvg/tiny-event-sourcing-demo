package ru.quipy.logic.project

import ru.quipy.api.*
import ru.quipy.api.aggregates.ProjectAggregate
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import ru.quipy.logic.task.TaskEntity
import ru.quipy.logic.task.TaskStatus
import ru.quipy.logic.task.TaskStatusEntity
import ru.quipy.logic.user.UserEntity
import java.util.*

// Service's business logic
class Project : AggregateState<UUID, ProjectAggregate> {
    lateinit var projectId: UUID
    var tasks = mutableMapOf<UUID, TaskEntity>()
    var taskStatuses = mutableMapOf<UUID, TaskStatusEntity>()
    var participants = mutableMapOf<UUID, UserEntity>()
    var defaultStatus: TaskStatusEntity? = null
    lateinit var projectTitle: String

    override fun getId() = projectId

    fun addMember(id: UUID, fullname: String, nickname: String): UserAppendedEvent {
        return UserAppendedEvent(id, fullname, nickname)
    }

    fun changeDefaultStatus(status: TaskStatus): ProjectUpdatedEvent {
        return ProjectUpdatedEvent(
            status.taskStatusId,
            status.projectId,
            status.name,
            status.rColor,
            status.gColor,
            status.gColor
        )
    }

    fun changeProjectName(name: String): ProjectNameChangedEvent {
        return ProjectNameChangedEvent(name)
    }

    fun deleteDefaultStatus(status: TaskStatus): StatusDeletedEvent {
        return StatusDeletedEvent(status.taskStatusId)
    }

    fun createTask(name: String, description: String, projectId: UUID, status: TaskStatusEntity?): TaskCreatedEvent {
        return TaskCreatedEvent(UUID.randomUUID(), name, description, status, projectId)
    }

    fun create(title: String): ProjectCreatedEvent {
        return ProjectCreatedEvent(
            projectId = UUID.randomUUID(),
            title = title
        )
    }

    @StateTransitionFunc
    fun userAppendedApply(event: UserAppendedEvent) {
        participants[event.userId] = UserEntity(event.userId, event.fullname, event.nickname)
    }

    @StateTransitionFunc
    fun projectUpdatedApply(event: ProjectUpdatedEvent) {
        defaultStatus = TaskStatusEntity(
            event.statusId,
            event.statusName,
            event.projectId,
            event.rColor,
            event.gColor,
            event.bColor
        )
        taskStatuses[event.statusId] = TaskStatusEntity(
            event.statusId,
            event.statusName,
            event.projectId,
            event.rColor,
            event.gColor,
            event.bColor
        )
    }

    @StateTransitionFunc
    fun statusDeletedApply(event: StatusDeletedEvent) {
        var usedStatuses =  tasks.values.filter { it.status != null }.groupBy { it.status!!.id }

        if (usedStatuses.containsKey(event.statusId))
            return

        taskStatuses.remove(event.statusId)
    }

    @StateTransitionFunc
    fun projectCreatedApply(event: ProjectCreatedEvent) {
        projectId = event.projectId
        projectTitle = event.title
    }

    @StateTransitionFunc
    fun taskCreatedApply(event: TaskCreatedEvent) {
        tasks[event.taskId] = TaskEntity(event.taskId, event.taskName, event.description, event.status, event.projectId)
    }

    @StateTransitionFunc
    fun taskStatusCreatedApply(event: TaskStatusCreatedEvent) {
        taskStatuses[event.id] = TaskStatusEntity(
            event.taskStatusId,
            event.taskStatusName,
            event.projectId,
            event.rColor,
            event.gColor,
            event.bColor
        )
    }

    @StateTransitionFunc
    fun projectNameChangeApply(event: ProjectNameChangedEvent) {
        projectTitle = event.projectName
    }
}