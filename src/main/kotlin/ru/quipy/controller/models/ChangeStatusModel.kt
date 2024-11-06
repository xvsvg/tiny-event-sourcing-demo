package ru.quipy.controller.models

import java.util.*

data class ChangeStatusModel(
    val taskId : UUID,
    val statusId : UUID
)