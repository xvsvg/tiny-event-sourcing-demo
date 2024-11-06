package ru.quipy.controller.models

import java.util.*

data class CreateStatusModel(
    val name : String,
    val projectId : UUID,
    val rColor: Int = 0,
    val gColor: Int = 0,
    val bColor: Int = 0,
)