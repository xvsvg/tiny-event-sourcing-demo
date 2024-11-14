package ru.quipy

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.*
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
    val status: StatusDataS? = null,
    val projectId: String,
    val createdAt: Long,
    val id: String,
    val name: String? = null,
    val sagaContext: String? = null,
    val version: Int,
    val executors: List<UserData>? = null
)

data class StatusDataS(
    val id: String,
    val name: String,
    val projectId: String,
    val rcolor: Int,
    val gcolor: Int,
    val bcolor: Int
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
        val status = mapOf(
            "name" to "status1", "projectId" to createdProject.projectId,
            "gcolor" to 0, "rcolor" to 0, "bcolor" to 0
        )

        val createdStatus = mockMvc.post("/tasks/status") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(status)
        }.andExpect {
            status { isOk() }
        }.andReturn().response.contentAsString
        val getTask: StatusData = mapper.readValue(createdStatus)
        assertEquals(getTask.taskStatusName, "status1")
    }

    @Test
    fun `try to delete status with tasks`() {
        // Создание проекта
        val res = mockMvc.post("/projects/MyPerfectProject") {
        }.andExpect {
            status { isOk() }
        }.andReturn().response.contentAsString
        val mapper = jacksonObjectMapper()
        val createdProject: ProjectData = mapper.readValue(res)

        // Создание статуса
        val status = mapOf(
            "name" to "status1", "projectId" to createdProject.projectId,
            "gcolor" to 0, "rcolor" to 0, "bcolor" to 0
        )

        val createdStatus = mockMvc.post("/tasks/status") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(status)
        }.andExpect {
            status { isOk() }
        }.andReturn().response.contentAsString
        val getStatus: StatusData = mapper.readValue(createdStatus)
        assertEquals(getStatus.taskStatusName, "status1")
        mockMvc.put("/projects/${createdProject.projectId}/status/${getStatus.taskStatusId}") {}

        // Создание задачи
        val task = mapOf("taskName" to "task1", "taskDescription" to "", "projectId" to createdProject.projectId)

        val createdTask = mockMvc.post("/tasks") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(task)
        }.andExpect {
            status { isOk() }
        }.andReturn().response.contentAsString
        val getTask: TaskData = mapper.readValue(createdTask)

        // Проверка что выставлен статус по умолчанию
        assertEquals(getTask.status?.id, getStatus.taskStatusId)

        // Пробуем удалить статус
        mockMvc.delete("/projects/${createdProject.projectId}/status/${getStatus.taskStatusId}") {}

        // Проверяем что задача и статус остались на месте
        val curTask = mockMvc.get("/tasks/${getTask.taskId}") {}.andExpect {
            status { isOk() }
        }.andReturn().response.contentAsString
        val curGetTask: TaskData = mapper.readValue(curTask)
        assertEquals(curGetTask.status?.id, getStatus.taskStatusId)
        assertEquals(curGetTask.taskId, getTask.taskId)
    }

    @Test
    fun `try to add status from other project`() {
        // Создание проектов
        val res1 = mockMvc.post("/projects/MyPerfectProject") {
        }.andExpect {
            status { isOk() }
        }.andReturn().response.contentAsString
        val mapper = jacksonObjectMapper()
        val createdProject1: ProjectData = mapper.readValue(res1)

        val res2 = mockMvc.post("/projects/MyPerfectProject") {
        }.andExpect {
            status { isOk() }
        }.andReturn().response.contentAsString
        val createdProject2: ProjectData = mapper.readValue(res2)

        // Создание задачи
        val task = mapOf("taskName" to "task1", "taskDescription" to "", "projectId" to createdProject1.projectId)

        val createdTask = mockMvc.post("/tasks") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(task)
        }.andExpect {
            status { isOk() }
        }.andReturn().response.contentAsString
        val getTask: TaskData = mapper.readValue(createdTask)
        assertEquals(getTask.projectId, createdProject1.projectId)

        // Создание статуса
        val status = mapOf(
            "name" to "status1", "projectId" to createdProject2.projectId,
            "gcolor" to 0, "rcolor" to 0, "bcolor" to 0
        )

        val createdStatus = mockMvc.post("/tasks/status") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(status)
        }.andExpect {
            status { isOk() }
        }.andReturn().response.contentAsString
        val getStatus: StatusData = mapper.readValue(createdStatus)
        assertEquals(getStatus.taskStatusName, "status1")
        mockMvc.put("/projects/${createdProject2.projectId}/status/${getStatus.taskStatusId}") {}

        // Попытка поменять статус
        assertThrows<Exception> {
            mockMvc.put("/tasks") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    objectMapper.writeValueAsString(
                        mapOf(
                            "taskId" to getTask.taskId,
                            "statusId" to getStatus.taskStatusId
                        )
                    )
            }
        }
        // Проверка что статус не присвоился
        val curTask = mockMvc.get("/tasks/${getTask.taskId}") {}.andExpect {
            status { isOk() }
        }.andReturn().response.contentAsString
        val curGetTask: TaskData = mapper.readValue(curTask)

        assertNotEquals(curGetTask.status?.id, getStatus.taskStatusId)
    }

    @Test
    fun `try to add user as executor in other project's task`() {
        // Создание проекта
        val res1 = mockMvc.post("/projects/MyPerfectProject") {
        }.andExpect {
            status { isOk() }
        }.andReturn().response.contentAsString
        val mapper = jacksonObjectMapper()
        val createdProject1: ProjectData = mapper.readValue(res1)

        // Создание задачи
        val task = mapOf("taskName" to "task1", "taskDescription" to "", "projectId" to createdProject1.projectId)

        val createdTask = mockMvc.post("/tasks") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(task)
        }.andExpect {
            status { isOk() }
        }.andReturn().response.contentAsString
        val getTask: TaskData = mapper.readValue(createdTask)
        assertEquals(getTask.projectId, createdProject1.projectId)

        // Создание юзера
        val user = mapOf("fullname" to "Eugeny Pakhalyuk", "nickname" to "123", "password" to "secret")

        val res = mockMvc.post("/users") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(user)
        }.andExpect {
            status { isOk() }
        }.andReturn().response.contentAsString
        val createdUser: UserData = mapper.readValue(res)
        assertEquals(createdUser.fullname, "Eugeny Pakhalyuk")

        // Пытаемся сделать юзера исполнителем в задаче не его проекта
        assertThrows<Exception> {
            mockMvc.post("/tasks/${getTask.taskId}/add-executors") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(mapOf("executors" to arrayOf(createdUser.userId)))
            }
        }

        // Проверяем, что юзера нет в исполнителях задачи
        val curTask = mockMvc.get("/tasks/${getTask.taskId}") {}.andExpect {
            status { isOk() }
        }.andReturn().response.contentAsString
        val curGetTask: TaskData = mapper.readValue(curTask)
        assertTrue(curGetTask.executors?.isEmpty()!!)
    }
}
