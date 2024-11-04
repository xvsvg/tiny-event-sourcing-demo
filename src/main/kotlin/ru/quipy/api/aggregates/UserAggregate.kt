﻿package ru.quipy.api.aggregates

import ru.quipy.core.annotations.AggregateType
import ru.quipy.domain.Aggregate

@AggregateType(aggregateEventsTableName = "users")
class UserAggregate : Aggregate