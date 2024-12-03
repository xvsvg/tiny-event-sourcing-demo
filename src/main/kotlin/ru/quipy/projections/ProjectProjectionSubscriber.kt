package ru.quipy.projections

import org.springframework.stereotype.Service
import ru.quipy.api.ProjectCreatedEvent
import ru.quipy.api.aggregates.ProjectAggregate
import ru.quipy.streams.annotation.AggregateSubscriber
import ru.quipy.streams.annotation.SubscribeEvent
import ru.quipy.streams.AggregateSubscriptionsManager
import javax.annotation.PostConstruct

@Service
@AggregateSubscriber(aggregateClass = ProjectAggregate::class, subscriberName = "project-projection-subscriber")
class ProjectProjectionSubscriber(
    private val projectProjectionRepository: ProjectProjectionRepository,
    private val subscriptionsManager: AggregateSubscriptionsManager
) {

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(ProjectAggregate::class, "project-projection-subscriber") {
            `when`(ProjectCreatedEvent::class, ::handleProjectCreatedEvent)
        }
    }

    @SubscribeEvent
    fun handleProjectCreatedEvent(event: ProjectCreatedEvent) {
        val projection = ProjectProjection(
            projectId = event.projectId,
            title = event.title,
            createdAt = event.createdAt
        )
        projectProjectionRepository.save(projection)
    }
} 