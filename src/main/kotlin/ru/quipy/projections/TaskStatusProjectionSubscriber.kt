package ru.quipy.projections

import org.springframework.stereotype.Service
import ru.quipy.api.TaskStatusCreatedEvent
import ru.quipy.api.aggregates.TaskStatusAggregate
import ru.quipy.streams.annotation.AggregateSubscriber
import ru.quipy.streams.annotation.SubscribeEvent
import ru.quipy.streams.AggregateSubscriptionsManager
import javax.annotation.PostConstruct

@Service
@AggregateSubscriber(aggregateClass = TaskStatusAggregate::class, subscriberName = "task-status-projection-subscriber")
class TaskStatusProjectionSubscriber(
    private val taskStatusProjectionRepository: TaskStatusProjectionRepository,
    private val subscriptionsManager: AggregateSubscriptionsManager
) {

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(TaskStatusAggregate::class, "task-status-projection-subscriber") {
            `when`(TaskStatusCreatedEvent::class, ::handleTaskStatusCreatedEvent)
        }
    }

    @SubscribeEvent
    fun handleTaskStatusCreatedEvent(event: TaskStatusCreatedEvent) {
        val projection = TaskStatusProjection(
            taskStatusId = event.taskStatusId,
            name = event.taskStatusName,
            projectId = event.projectId,
            rColor = event.rColor,
            gColor = event.gColor,
            bColor = event.bColor
        )
        taskStatusProjectionRepository.save(projection)
    }
} 