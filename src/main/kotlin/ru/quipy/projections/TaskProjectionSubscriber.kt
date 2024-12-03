package ru.quipy.projections

import org.springframework.stereotype.Service
import ru.quipy.api.TaskCreatedEvent
import ru.quipy.api.TaskStatusChangedEvent
import ru.quipy.api.aggregates.TaskAggregate
import ru.quipy.streams.annotation.AggregateSubscriber
import ru.quipy.streams.annotation.SubscribeEvent
import ru.quipy.streams.AggregateSubscriptionsManager
import javax.annotation.PostConstruct

@Service
@AggregateSubscriber(aggregateClass = TaskAggregate::class, subscriberName = "task-projection-subscriber")
class TaskProjectionSubscriber(
    private val taskProjectionRepository: TaskProjectionRepository,
    private val subscriptionsManager: AggregateSubscriptionsManager
) {

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(TaskAggregate::class, "task-projection-subscriber") {
            `when`(TaskCreatedEvent::class, ::handleTaskCreatedEvent)
            `when`(TaskStatusChangedEvent::class, ::handleTaskStatusChangedEvent)
        }
    }

    @SubscribeEvent
    fun handleTaskCreatedEvent(event: TaskCreatedEvent) {
        val projection = TaskProjection(
            taskId = event.taskId,
            taskName = event.taskName,
            description = event.description,
            status = event.status?.name,
            projectId = event.projectId
        )
        taskProjectionRepository.save(projection)
    }

    @SubscribeEvent
    fun handleTaskStatusChangedEvent(event: TaskStatusChangedEvent) {
        val task = taskProjectionRepository.findById(event.taskId).orElse(null)
        task?.let {
            it.status = event.status.name
            taskProjectionRepository.save(it)
        }
    }
} 