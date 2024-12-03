package ru.quipy.projections

import javax.persistence.Entity
import javax.persistence.Id
import java.util.UUID

@Entity
data class UserProjection(
    @Id
    val userId: UUID = UUID.randomUUID(),
    val fullname: String = "",
    val nickname: String = ""
) {
    constructor() : this(UUID.randomUUID(), "", "")
} 