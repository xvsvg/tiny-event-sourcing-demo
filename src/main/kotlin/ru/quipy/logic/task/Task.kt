package ru.quipy.logic.task

import ru.quipy.api.ExecutorAddedEvent
import ru.quipy.api.TaskCreatedEvent
import ru.quipy.api.TaskStatusChangedEvent
import ru.quipy.api.aggregates.TaskAggregate
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*
import kotlin.collections.HashSet

class Task : AggregateState<UUID, TaskAggregate> {
    lateinit var taskId: UUID
    lateinit var name: String
    lateinit var description: String
    var status: TaskStatusEntity? = null
    lateinit var projectId: UUID
    var executors: List<UUID> = emptyList()

    override fun getId() = taskId

    fun changeStatus(taskId: UUID, projectId : UUID, status : TaskStatusEntity): TaskStatusChangedEvent {
        return TaskStatusChangedEvent(taskId, projectId, status)
    }

    fun createTask(name: String, description: String, status: TaskStatusEntity?, projectId : UUID): TaskCreatedEvent {
        return TaskCreatedEvent(UUID.randomUUID(), name, description, status, projectId);
    }

    fun assignExecutors(executors: Collection<UUID>): ExecutorAddedEvent {
        return ExecutorAddedEvent(executors)
    }

    @StateTransitionFunc
    fun taskCreatedApply(event: TaskCreatedEvent) {
        taskId = event.taskId
        name = event.taskName
        description = event.description
        status = event.status
        projectId = event.projectId
    }

    @StateTransitionFunc
    fun taskStatusChangedApply(event: TaskStatusChangedEvent) {
        this.status = event.status
    }

    @StateTransitionFunc
    fun assignExecutorsApply(event: ExecutorAddedEvent) {
        val uniqueExecutors = HashSet<UUID>()
        uniqueExecutors.addAll(event.executors)
        uniqueExecutors.addAll(executors)

        this.executors = uniqueExecutors.toList()
    }
}

data class TaskEntity(
    var id: UUID,
    var name: String,
    var description: String,
    var status: TaskStatusEntity?,
    var projectId: UUID
)