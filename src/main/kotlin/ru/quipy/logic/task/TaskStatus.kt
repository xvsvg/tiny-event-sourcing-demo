package ru.quipy.logic.task

import ru.quipy.api.TaskStatusCreatedEvent
import ru.quipy.api.aggregates.TaskAggregate
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

class TaskStatus(
    private var id: UUID,
    private var name: String,
    private var projectId: UUID,
    private var rColor: Int,
    private var gColor: Int,
    private var bColor: Int
) : AggregateState<UUID, TaskAggregate> {

    override fun getId() = id

    fun changeColor(rColor : Int, gColor : Int, bColor : Int) {
        if (!(rColor >= 0 && gColor >= 0 && bColor >= 0)) {
            throw IllegalArgumentException("Color value cannot be negative")
        }

        this.rColor = rColor
        this.gColor = gColor
        this.bColor = bColor
    }

    fun create(name: String, rColor: Int = 0, gColor: Int = 0, bColor: Int = 0): TaskStatusCreatedEvent {
        if (name.isBlank()) {
            throw IllegalArgumentException("Status name should not be blank")
        }

        return TaskStatusCreatedEvent(UUID.randomUUID(), name, getId(), rColor, gColor, bColor)
    }
}