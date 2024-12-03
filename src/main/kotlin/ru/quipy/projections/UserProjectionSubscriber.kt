package ru.quipy.projections

import org.springframework.stereotype.Service
import ru.quipy.api.UserCreatedEvent
import ru.quipy.api.aggregates.UserAggregate
import ru.quipy.streams.annotation.AggregateSubscriber
import ru.quipy.streams.annotation.SubscribeEvent
import ru.quipy.streams.AggregateSubscriptionsManager
import javax.annotation.PostConstruct

@Service
@AggregateSubscriber(aggregateClass = UserAggregate::class, subscriberName = "user-projection-subscriber")
class UserProjectionSubscriber(
    private val userProjectionRepository: UserProjectionRepository,
    private val subscriptionsManager: AggregateSubscriptionsManager
) {

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(UserAggregate::class, "user-projection-subscriber") {
            `when`(UserCreatedEvent::class, ::handleUserCreatedEvent)
        }
    }

    @SubscribeEvent
    fun handleUserCreatedEvent(event: UserCreatedEvent) {
        val projection = UserProjection(
            userId = event.userId,
            fullname = event.fullname,
            nickname = event.nickname
        )
        userProjectionRepository.save(projection)
    }
} 