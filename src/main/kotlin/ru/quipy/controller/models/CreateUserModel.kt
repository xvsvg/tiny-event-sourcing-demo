package ru.quipy.controller.models

import javax.annotation.meta.TypeQualifierNickname

data class CreateUserModel(
    val fullname: String,
    val nickname: String,
    val password: String
) {
}