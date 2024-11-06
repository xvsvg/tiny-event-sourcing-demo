package ru.quipy.api.aggregates

import ru.quipy.core.annotations.AggregateType
import ru.quipy.domain.Aggregate

@AggregateType(aggregateEventsTableName = "task_statuses")
class TaskStatusAggregate : Aggregate