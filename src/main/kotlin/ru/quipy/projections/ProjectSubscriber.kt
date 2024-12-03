package ru.quipy.projections

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.quipy.api.TaskCreatedEvent
import ru.quipy.api.TaskStatusChangedEvent
import ru.quipy.api.aggregates.ProjectAggregate
import ru.quipy.api.aggregates.TaskAggregate
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.project.Project
import ru.quipy.streams.AggregateSubscriptionsManager
import java.util.*
import javax.annotation.PostConstruct

@Service
class ProjectSubscriber(val projectEsService: EventSourcingService<UUID, ProjectAggregate, Project>) {

    val logger: Logger = LoggerFactory.getLogger(ProjectSubscriber::class.java)

    @Autowired
    lateinit var subscriptionsManager: AggregateSubscriptionsManager

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(TaskAggregate::class, "project-subscriber") {
            `when`(TaskCreatedEvent::class) { event ->
                projectEsService.update(event.projectId) {
                    it.addTask(event.taskId, event.taskName, event.description, event.projectId, event.status)
                }
            }

            `when`(TaskStatusChangedEvent::class) { event ->
                projectEsService.update(event.projectId) {
                    it.updateTaskStatus(event.taskId, event.status)
                }
            }
        }
    }
}