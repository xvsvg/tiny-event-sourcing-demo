package ru.quipy.logic.task

import ru.quipy.api.TaskStatusCreatedEvent
import ru.quipy.api.aggregates.TaskStatusAggregate
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

class TaskStatus : AggregateState<UUID, TaskStatusAggregate> {

    lateinit var taskStatusId: UUID
    lateinit var name: String
    lateinit var projectId: UUID
    var rColor: Int = 0
    var gColor: Int = 0
    var bColor: Int = 0
    var priority: Int = 0

    override fun getId() = taskStatusId

    fun create(name: String, projectId: UUID, rColor: Int = 0, gColor: Int = 0, bColor: Int = 0, priority: Int = 0): TaskStatusCreatedEvent {
        if (name.isBlank()) {
            throw IllegalArgumentException("Status name should not be blank")
        }

        return TaskStatusCreatedEvent(UUID.randomUUID(), name, projectId, rColor, gColor, bColor, priority)
    }

    @StateTransitionFunc
    fun taskStatusCreatedApply(event: TaskStatusCreatedEvent) {
        taskStatusId = event.taskStatusId
        name = event.taskStatusName
        projectId = event.projectId
        rColor = event.rColor
        gColor = event.gColor
        bColor = event.bColor
        priority = event.priority
    }
}

data class TaskStatusEntity(
    var id: UUID,
    var name: String,
    var projectId: UUID,
    var rColor: Int = 0,
    var gColor: Int = 0,
    var bColor: Int = 0,
    var priority: Int = 0
)
