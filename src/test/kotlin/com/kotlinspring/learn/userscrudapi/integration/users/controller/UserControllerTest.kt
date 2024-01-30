package com.kotlinspring.learn.userscrudapi.integration.users.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.jayway.jsonpath.JsonPath
import com.kotlinspring.learn.userscrudapi.mock.UserMock
import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.web.servlet.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    private val userMock: UserMock = UserMock()
    private val uri: String = "users"

    @AfterEach
    fun tearDown() {
        jdbcTemplate.execute("DELETE FROM user_stack")
        jdbcTemplate.execute("DELETE FROM users")
    }

    @Nested
    inner class CreateUser {
        @Test
        @DisplayName("GIVEN: valid complete user payload; WHEN: Post /users; THEN: return status 201 and User with ID")
        fun validCompletePayloadTest() {
            val payload = userMock.createUserPayload()

            mockMvc.perform(
                post("/$uri")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(payload))
                )
                .andExpect(status().isCreated)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNotEmpty)
                .andExpect(jsonPath("$.name").value(payload.name))
                .andExpect(jsonPath("$.nick").value(payload.nick))
                .andExpect(jsonPath("$.birth_date").value(payload.birth_date))
                .andExpect(jsonPath("$.stack").value(payload.stack))
        }

        @Test
        @DisplayName("GIVEN: valid (null optionals) user payload; WHEN: Post /users; THEN: return status 201 and User with ID")
        fun validNullOptionalsPayloadTest() {
            val payload = userMock.createUserPayload().copy(nick = null, stack = null)

            mockMvc.perform(
                post("/$uri")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(payload))
            )
                .andExpect(status().isCreated)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNotEmpty)
                .andExpect(jsonPath("$.name").value(payload.name))
                .andExpect(jsonPath("$.nick").value(null))
                .andExpect(jsonPath("$.birth_date").value(payload.birth_date))
                .andExpect(jsonPath("$.stack").value(null))
        }

        @ParameterizedTest
        @ValueSource(strings = [
            "",
            "That'sStringContainMoreThan32Characters"
        ])
        @DisplayName("GIVEN: invalid nick; WHEN: Post /users; THEN: return status 400 and error_message Invalid Payload")
        fun invalidNickPayloadTest(nick: String) {
            val payload = userMock.createUserPayload().copy(nick = nick)

            mockMvc.perform(
                post("/$uri")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(payload))
            )
                .andExpect(status().isBadRequest)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error_message").value("Invalid Payload"))
        }

        @ParameterizedTest
        @ValueSource(strings = [
            "",
            " ",
            "This_Sentence_Written_in_the_English_Language_Was_Designed_and_Written_to_Test_a_String_Type_Input_with_"+
                    "a_Maximum_Allowed_Size_of_255_Characters_Therefore,_To_Meet_the_Premise_of_the_Test,_The_Total_"+
                    "Number_of_Letters_in_This_Sentence_Exceeds_the_Previously_Quoted_Value"
        ])
        @DisplayName("GIVEN: invalid name; WHEN: Post /users; THEN: return status 400 and error_message Invalid Payload")
        fun invalidNamePayloadTest(name: String) {
            val payload = userMock.createUserPayload().copy(name = name)

            mockMvc.perform(
                post("/$uri")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(payload))
            )
                .andExpect(status().isBadRequest)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error_message").value("Invalid Payload"))
        }

        @Test
        @DisplayName("GIVEN: existing name; WHEN: Post /users; THEN: return status 400 and error_message Exclusive Data Restriction")
        fun existingNamePayloadTest() {
            val payload = userMock.createUserPayload()

            mockMvc.perform(
                post("/$uri")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(payload))
            )

            mockMvc.perform(
                post("/$uri")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(payload))
            )
                .andExpect(status().isBadRequest)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error_message").value("Exclusive Data Restriction"))
        }

        @ParameterizedTest
        @ValueSource(strings = [
            "",
            " ",
            "Some Text",
            "17/10/1981 09:40:01",
            "17/10/1981",
            "09:40:01"
        ])
        @DisplayName("GIVEN: invalid birth_date; WHEN: Post /users; THEN: return status 400 and error_message Http Message Not Readable")
        fun invalidBirthDatePayloadTest(birthDate: String) {
            val payload = userMock.createUserPayload().copy(birth_date = birthDate)

            mockMvc.perform(
                post("/$uri")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(payload))
            )
                .andExpect(status().isBadRequest)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error_message").value("Http Message Not Readable"))
        }

        @ParameterizedTest
        @ValueSource(strings = [
            "",
            " ",
            "That'sStringContainMoreThan32Characters"
        ])
        @DisplayName("GIVEN: invalid stack item; WHEN: Post /users; THEN: return status 400 and error_message Invalid Payload")
        fun invalidStackItemPayloadTest(stackItem: String) {
            val stack: List<String> = listOf(stackItem)
            val payload = userMock.createUserPayload().copy(stack = stack)

            mockMvc.perform(
                post("/$uri")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(payload))
            )
                .andExpect(status().isBadRequest)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error_message").value("Invalid Payload"))
        }
    }

    @Nested
    inner class UpdateUser {
        @Test
        @DisplayName("GIVEN: valid ID and update payload; WHEN: Put /users/{id}; THEN: return status 200 and new data")
        fun validPayloadTest() {
            val existingUser = userMock.createUserPayload()
            val createResponse = mockMvc.perform(
                post("/$uri")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(existingUser))
            ).andReturn().response.contentAsString
            existingUser.id = JsonPath.parse(createResponse).read("$.id")

            val updatePayload = userMock.createUserPayload()

            mockMvc.put("/$uri/{id}", existingUser.id) {
                content = objectMapper.writeValueAsString(
                    existingUser.copy(
                        nick = updatePayload.nick,
                        birth_date = updatePayload.birth_date,
                        stack = updatePayload.stack
                    )
                )
                contentType = MediaType.APPLICATION_JSON
            }.andExpectAll {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.id") { value(existingUser.id) }
                jsonPath("$.name") { value(existingUser.name) }
                jsonPath("$.nick") { value(updatePayload.nick) }
                jsonPath("$.birth_date") { value(updatePayload.birth_date) }
                jsonPath("$.stack") { value(updatePayload.stack) }
            }
        }

        @Test
        @DisplayName("GIVEN: invalid ID; WHEN: Put /users{id}; THEN: return status 404 and message error User Not Found")
        fun invalidIDPayloadTest() {
            val updatePayload = userMock.createUserPayload()
            val id: String = UUID.randomUUID().toString()

            mockMvc.put("/$uri/{id}", id)
            {
                content = objectMapper.writeValueAsString(updatePayload)
                contentType = MediaType.APPLICATION_JSON
            }.
            andExpectAll {
                status { isNotFound() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.error_message") { value("User Not Found") }
            }

        }

        @ParameterizedTest
        @ValueSource(strings = [
            "",
            "That'sStringContainMoreThan32Characters"
        ])
        @DisplayName("GIVEN: invalid nick; WHEN: Put /users/{id}; THEN: return status 400 and error_message Invalid Payload")
        fun invalidNickPayloadTest(nick: String) {
            val updatePayload = userMock.createUserPayload().copy(nick = nick)
            val id: String = UUID.randomUUID().toString()

            mockMvc.put("/$uri/{id}", id)
            {
                content = objectMapper.writeValueAsString(updatePayload)
                contentType = MediaType.APPLICATION_JSON
            }.
            andExpectAll {
                status { isBadRequest() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.error_message") { value("Invalid Payload") }
            }
        }

        @ParameterizedTest
        @ValueSource(strings = [
            "",
            " ",
            "This_Sentence_Written_in_the_English_Language_Was_Designed_and_Written_to_Test_a_String_Type_Input_with_"+
                    "a_Maximum_Allowed_Size_of_255_Characters_Therefore,_To_Meet_the_Premise_of_the_Test,_The_Total_"+
                    "Number_of_Letters_in_This_Sentence_Exceeds_the_Previously_Quoted_Value"
        ])
        @DisplayName("GIVEN: invalid name; WHEN: Put /users/{id}; THEN: return status 400 and error_message Invalid Payload")
        fun invalidNamePayloadTest(name: String) {
            val updatePayload = userMock.createUserPayload().copy(name = name)
            val id: String = UUID.randomUUID().toString()

            mockMvc.put("/$uri/{id}", id)
            {
                content = objectMapper.writeValueAsString(updatePayload)
                contentType = MediaType.APPLICATION_JSON
            }.
            andExpectAll {
                status { isBadRequest() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.error_message") { value("Invalid Payload") }
            }
        }

        @Test
        @DisplayName("GIVEN: existing name; WHEN: Put /users/{id}; THEN: return status 400 and error_message Exclusive Data Restriction")
        fun existingNamePayloadTest() {
            val payloadUserOne = userMock.createUserPayload()
            mockMvc.perform(
                post("/$uri")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(payloadUserOne))
                )

            val payloadUserTwo = userMock.createUserPayload()
            val consult = mockMvc.perform(
                post("/$uri")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(payloadUserTwo))
                ).andReturn().response.contentAsString

            payloadUserTwo.id = JsonPath.parse(consult).read("$.id")

            mockMvc.put("/$uri/{id}", payloadUserTwo.id)
            {
                content = objectMapper.writeValueAsString(payloadUserTwo.copy(name = payloadUserOne.name))
                contentType = MediaType.APPLICATION_JSON
            }.
            andExpectAll {
                status { isBadRequest() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.error_message") { value("Exclusive Data Restriction") }
            }
        }

        @ParameterizedTest
        @ValueSource(strings = [
            "",
            " ",
            "Some Text",
            "17/10/1981 09:40:01",
            "17/10/1981",
            "09:40:01",
            "1981-10-17 09:40:01",
        ])
        @DisplayName("GIVEN: invalid birth_date; WHEN: Put /users/{id}; THEN: return status 400 and error_message Http Message Not Readable")
        fun invalidBirthDatePayloadTest(birthDate: String) {
            val updatePayload = userMock.createUserPayload().copy(birth_date = birthDate)
            val id: String = UUID.randomUUID().toString()

            mockMvc.put("/$uri/{id}", id)
            {
                content = objectMapper.writeValueAsString(updatePayload)
                contentType = MediaType.APPLICATION_JSON
            }.
            andExpectAll {
                status { isBadRequest() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.error_message") { value("Http Message Not Readable") }
            }
        }

        @ParameterizedTest
        @ValueSource(strings = [
            "",
            " ",
            "That'sStringContainMoreThan32Characters"
        ])
        @DisplayName("GIVEN: invalid stack item; WHEN: Put /users/{id}; THEN: return status 400 and error_message Invalid Payload")
        fun invalidStackItemPayloadTest(stackItem: String) {
            val stack: List<String> = listOf(stackItem)
            val updatePayload = userMock.createUserPayload().copy(stack = stack)
            val id: String = UUID.randomUUID().toString()

            mockMvc.put("/$uri/{id}", id)
            {
                content = objectMapper.writeValueAsString(updatePayload)
                contentType = MediaType.APPLICATION_JSON
            }.
            andExpectAll {
                status { isBadRequest() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.error_message") { value("Invalid Payload") }
            }
        }
    }

    @Nested
    inner class FindUserById {
        @Test
        @DisplayName("GIVEN: existing User and valid ID; WHEN: Get /users/{id}; THEN: return status 200 and User data")
        fun validUserTest() {
            val payload = userMock.createUserPayload()
            val consult = mockMvc.perform(
                post("/$uri")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(payload))
            ).andReturn().response.contentAsString
            payload.id = JsonPath.parse(consult).read("$.id")

            mockMvc.get("/$uri/{id}", payload.id).
            andExpectAll {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.id") { value(payload.id) }
                jsonPath("$.name") {value(payload.name)}
                jsonPath("$.nick") {value(payload.nick)}
                jsonPath("$.birth_date") {value(payload.birth_date)}
                jsonPath("$.stack") {value(payload.stack)}
            }

        }

        @Test
        @DisplayName("GIVEN: valid ID (non existent user); WHEN: Get /users/{id}; THEN: return status 404 and message error User Not Found")
        fun invalidUserTest() {
            val id: UUID = UUID.randomUUID()

            mockMvc.get("/$uri/{id}", id.toString()) { }
                .andExpectAll {
                    status { isNotFound() }
                    jsonPath("$.error_message") { value("User Not Found") }
                }
        }

        @Test
        @DisplayName("GIVEN: invalid ID (empty); WHEN: Get /users/{id}; THEN: return status 404 and error_message No Resource Found")
        fun invalidEmptyIdTest() {
            mockMvc.get("/$uri/{id}", "").
            andExpectAll {
                status { isNotFound() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.error_message") { value("No Resource Found") }
            }
        }

        @Test
        @DisplayName("GIVEN: invalid ID (blank); WHEN: Get /users/{id}; THEN: return status 400 and error_message Missing Path Variable")
        fun invalidBlackIdTest() {
            mockMvc.get("/$uri/{id}", " ").
            andExpectAll {
                status { isBadRequest() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.error_message") { value("Missing Path Variable") }
            }
        }

        @ParameterizedTest
        @ValueSource(strings = [
            "123",
            "abc",
            "9ba51452-be13-44f7-a6cb"
        ])
        @DisplayName("GIVEN: invalid ID; WHEN: Get /users/{id}; THEN: return status 400 and error_message Method Argument Type Mismatch")
        fun invalidIdTest(id: String) {
            mockMvc.get("/$uri/{id}", id).
            andExpectAll {
                status { isBadRequest() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.error_message") { value("Method Argument Type Mismatch") }
            }
        }
    }

    @Nested
    inner class FindUsers {
        @Test
        @DisplayName("GIVEN: db with 55 users; WHEN: Get /users; THEN: return status 200, page_number=0, page_size=50, total_elements=55 and total_pages=2")
        fun validDefaultConsultTest() {
            for (i in 1..55) {
                val payload = userMock.createUserPayload()
                mockMvc.perform(
                    post("/$uri")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload))
                )
            }

            mockMvc.get("/$uri") {}
                .andExpectAll {
                    status { isOk() }
                    jsonPath("$.pageable.page_number") {value( "0")}
                    jsonPath("$.pageable.page_size") {value( "50")}
                    jsonPath("$.total_elements") {value( "55")}
                    jsonPath("$.total_pages") {value( "2")}
                }
        }

        @Test
        @DisplayName("GIVEN: db with 10 users; WHEN: Get /users?page={0..9}&size=1; THEN: return status 200 and page with 1 Item (10x)")
        fun validPaginatedConsultTest() {
            val pageSize = 1

            for (i in 0.. 9) {
                val payload = userMock.createUserPayload()

                mockMvc.perform(
                    post("/$uri")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload))
                )

                mockMvc.get("/$uri?page=$i&size=$pageSize") {}
                    .andExpectAll {
                        status { isOk() }
                        jsonPath("$.pageable.page_number") {value( "$i")}
                        jsonPath("$.pageable.page_size") {value( "$pageSize")}
                        jsonPath("$.pageable.offset") {value( "${i * pageSize}")}
                        jsonPath("$.total_elements") {value( "${i + 1}")}
                        jsonPath("$.total_pages") {value( "${(i + 1) / pageSize}")}
                        jsonPath("$.last") {value( "true")}
                    }
            }
        }

    }

    @Nested
    inner class DeleteUser {

        @Test
        @DisplayName("GIVEN: existing User and valid ID; WHEN: Delete /users/{id}; THEN: return status 204 and remove user")
        fun validUserTest() {
            val payload = userMock.createUserPayload()
            val consult = mockMvc.perform(
                post("/$uri")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(payload))
            ).andReturn().response.contentAsString
            payload.id = JsonPath.parse(consult).read("$.id")

            mockMvc.delete("/$uri/{id}", payload.id).
            andExpectAll {
                status { isNoContent() }
            }

            mockMvc.get("/$uri/{id}", payload.id).
            andExpectAll {
                status { isNotFound() }
            }

        }

        @Test
        @DisplayName("GIVEN: valid ID (non existent user); WHEN: Delete /users/{id}; THEN: return status 404 and message error User Not Found")
        fun invalidUserTest() {
            val id: UUID = UUID.randomUUID()

            mockMvc.delete("/$uri/{id}", id.toString()) { }
                .andExpectAll {
                    status { isNotFound() }
                    jsonPath("$.error_message") { value("User Not Found") }
                }
        }

        @Test
        @DisplayName("GIVEN: invalid ID (empty); WHEN: Delete /users/{id}; THEN: return status 404 and error_message No Resource Found")
        fun invalidEmptyIdTest() {
            mockMvc.get("/$uri/{id}", "").
            andExpectAll {
                status { isNotFound() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.error_message") { value("No Resource Found") }
            }
        }

        @Test
        @DisplayName("GIVEN: invalid ID (blank); WHEN: Delete /users/{id}; THEN: return status 400 and error_message Missing Path Variable")
        fun invalidBlackIdTest() {
            mockMvc.get("/$uri/{id}", " ").
            andExpectAll {
                status { isBadRequest() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.error_message") { value("Missing Path Variable") }
            }
        }

        @ParameterizedTest
        @ValueSource(strings = [
            "123",
            "abc",
            "9ba51452-be13-44f7-a6cb"
        ])
        @DisplayName("GIVEN: invalid ID; WHEN: Delete /users/{id}; THEN: return status 400 and error_message Method Argument Type Mismatch")
        fun invalidIdTest(id: String) {
            mockMvc.get("/$uri/{id}", id).
            andExpectAll {
                status { isBadRequest() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.error_message") { value("Method Argument Type Mismatch") }
            }
        }
    }

}