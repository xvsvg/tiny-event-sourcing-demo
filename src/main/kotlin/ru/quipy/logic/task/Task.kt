package ru.quipy.logic.task

import ru.quipy.api.ExecutorAddedEvent
import ru.quipy.api.TaskCreatedEvent
import ru.quipy.api.TaskNameUpdatedEvent
import ru.quipy.api.aggregates.TaskAggregate
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*
import kotlin.collections.HashSet

class Task(
    private  var id : UUID,
    private  var name : String,
    private  var description : String,
    private  var status : TaskStatus,
    private var projectId : UUID
) : AggregateState<UUID, TaskAggregate> {
    private var executors : List<UUID> = emptyList()

    override fun getId() = id

    fun changeName(name : String): TaskNameUpdatedEvent {
        return TaskNameUpdatedEvent(getId(), name)
    }

    fun assignExecutors(executors : Collection<UUID>): ExecutorAddedEvent {
        val uniqueExecutors = HashSet<UUID>()
        uniqueExecutors.addAll(executors)
        uniqueExecutors.addAll(this.executors)

        return ExecutorAddedEvent(getId(), uniqueExecutors)
    }

    @StateTransitionFunc
    fun taskCreatedApply(event : TaskCreatedEvent) {
        id = event.taskId
        name = event.taskName
        description = event.description
        status = event.status
        projectId = event.projectId
    }

    @StateTransitionFunc
    fun taskNameUpdatedApply(event : TaskNameUpdatedEvent) {
        this.name = event.name
    }

    @StateTransitionFunc
    fun assignExecutorsApply(event: ExecutorAddedEvent) {
        this.executors = event.executors.toList()
    }
}