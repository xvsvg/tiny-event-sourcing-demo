package ru.quipy.logic.project

import ru.quipy.api.*
import ru.quipy.api.aggregates.ProjectAggregate
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import ru.quipy.logic.task.Task
import ru.quipy.logic.task.TaskStatus
import ru.quipy.logic.user.User
import java.util.*

// Service's business logic
class Project : AggregateState<UUID, ProjectAggregate> {
    private lateinit var projectId: UUID
    private lateinit var name: String
    private var tasks = mutableMapOf<UUID, Task>()
    private var taskStatuses = mutableMapOf<UUID, TaskStatus>()
    private var participants = mutableMapOf<UUID, User>()
    private lateinit var defaultStatus: TaskStatus
    lateinit var projectTitle: String

    override fun getId() = projectId

    fun addMember(id: UUID, fullname: String, nickname: String): UserAppendedEvent {
        return UserAppendedEvent(id, fullname, nickname)
    }

    fun changeName(name: String): ProjectUpdatedEvent {
        return ProjectUpdatedEvent(getId(), name)
    }

    fun createTask(id: UUID, name: String, description: String, projectId: UUID): TaskCreatedEvent {
        return TaskCreatedEvent(id, name, description, defaultStatus, projectId)
    }

    fun create(id: UUID, title: String, creatorId: String): ProjectCreatedEvent {
        return ProjectCreatedEvent(
            projectId = id,
            title = title,
            creatorId = creatorId,
        )
    }

    @StateTransitionFunc
    fun userAppendedApply(event: UserAppendedEvent) {
        participants[event.userId] = User(event.userId, event.fullname, event.nickname, null)
    }

    @StateTransitionFunc
    fun projectUpdatedApply(event: ProjectUpdatedEvent) {
        this.projectTitle = event.title
    }

    @StateTransitionFunc
    fun projectCreatedApply(event: ProjectCreatedEvent) {
        projectId = event.projectId
        projectTitle = event.title
    }

    @StateTransitionFunc
    fun taskCreatedApply(event: TaskCreatedEvent) {
        tasks[event.taskId] = Task()
    }

    @StateTransitionFunc
    fun taskStatusCreatedApply(event: TaskStatusCreatedEvent) {
        taskStatuses[event.id] = TaskStatus(
            event.taskStatusId,
            event.taskStatusName,
            event.projectId,
            event.rColor,
            event.gColor,
            event.bColor
        )
    }
}