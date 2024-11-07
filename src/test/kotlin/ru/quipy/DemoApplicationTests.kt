package ru.quipy

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.put
import java.util.UUID

data class UserData(
    val userId: String,
    val fullname: String,
    val password: String,
    val nickname: String,
    val id: String,
    val name: String,
    val sagaContext: String? = null,
    val version: Int,
    val createdAt: Long
)

data class ProjectData(
    val projectId: String,
    val title: String,
    val createdAt: Long,
    val id: String,
    val name: String,
    val sagaContext: String? = null,
    val version: Int,
)

data class TaskData(
    val taskId: String,
    val taskName: String,
    val description: String,
    val status: String? = null,
    val projectId: String,
    val createdAt: Long,
    val id: String,
    val name: String,
    val sagaContext: String? = null,
    val version: Int,
)

data class StatusData(
    val taskStatusId: String,
    val taskStatusName: String,
    val rcolor: Int,
    val gcolor: Int,
    val bcolor: Int,
    val status: String? = null,
    val projectId: String,
    val createdAt: Long,
    val id: String,
    val name: String,
    val sagaContext: String? = null,
    val version: Int,
)

@SpringBootTest
@AutoConfigureMockMvc
class ApiControllerTests @Autowired constructor(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper
) {

    @Test
    fun `should create user`() {
        val user = mapOf("fullname" to "Eugeny Pakhalyuk", "nickname" to "123", "password" to "secret")

        val res = mockMvc.post("/users") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(user)
        }.andExpect {
            status { isOk() }
        }.andReturn().response.contentAsString
        val mapper = jacksonObjectMapper()
        val createdUser: UserData = mapper.readValue(res)
        assertEquals(createdUser.fullname, "Eugeny Pakhalyuk")
    }

    @Test
    fun `should create project`() {
        // Создание
        val res = mockMvc.post("/projects/MyPerfectProject") {
        }.andExpect {
            status { isOk() }
        }.andReturn().response.contentAsString
        val mapper = jacksonObjectMapper()
        val createdProject: ProjectData = mapper.readValue(res)
        assertEquals(createdProject.title, "MyPerfectProject")

        // Чтение
        val project = mockMvc.get("/projects/${createdProject.projectId}") {
        }.andExpect {
            status { isOk() }
        }.andReturn().response.contentAsString

        val readProject: ProjectData = mapper.readValue(res)
        assertEquals(createdProject, readProject)
    }

    @Test
    fun `create task in project`() {
        // Создание проекта
        val res = mockMvc.post("/projects/MyPerfectProject") {
        }.andExpect {
            status { isOk() }
        }.andReturn().response.contentAsString
        val mapper = jacksonObjectMapper()
        val createdProject: ProjectData = mapper.readValue(res)

        // Создание задачи
        val task = mapOf("taskName" to "task1", "taskDescription" to "", "projectId" to createdProject.projectId)

        val createdTask = mockMvc.post("/tasks") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(task)
        }.andExpect {
            status { isOk() }
        }.andReturn().response.contentAsString
        val getTask: TaskData = mapper.readValue(createdTask)
        assertEquals(getTask.taskName, "task1")
    }

    @Test
    fun `create status in project`() {
        // Создание проекта
        val res = mockMvc.post("/projects/MyPerfectProject") {
        }.andExpect {
            status { isOk() }
        }.andReturn().response.contentAsString
        val mapper = jacksonObjectMapper()
        val createdProject: ProjectData = mapper.readValue(res)

        // Создание статуса
        val status = mapOf("name" to "status1", "projectId" to createdProject.projectId,
            "gcolor" to 0, "rcolor" to 0, "bcolor" to 0)

        val createdStatus = mockMvc.post("/tasks/status") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(status)
        }.andExpect {
            status { isOk() }
        }.andReturn().response.contentAsString
        val getTask: StatusData = mapper.readValue(createdStatus)
        assertEquals(getTask.taskStatusName, "status1")
    }
}
