package ru.quipy.controller.models

import java.util.*

data class CreateTaskModel(
    val taskName: String,
    val taskDescription: String,
    val projectId : UUID
)