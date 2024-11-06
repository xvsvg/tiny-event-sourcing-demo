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

    fun changeStatus(status : TaskStatusEntity): TaskStatusChangedEvent {
        return TaskStatusChangedEvent(status)
    }

    fun assignExecutors(executors: Collection<UUID>): ExecutorAddedEvent {
        val uniqueExecutors = HashSet<UUID>()
        uniqueExecutors.addAll(executors)
        uniqueExecutors.addAll(this.executors)

        return ExecutorAddedEvent(getId(), uniqueExecutors)
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
        this.executors = event.executors.toList()
    }
}

data class TaskEntity(
    var id: UUID,
    var name: String,
    var description: String,
    var status: TaskStatusEntity?,
    var projectId: UUID
)