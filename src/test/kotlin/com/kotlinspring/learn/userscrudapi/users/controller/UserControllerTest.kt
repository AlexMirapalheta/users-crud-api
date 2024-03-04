package com.kotlinspring.learn.userscrudapi.users.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.jayway.jsonpath.JsonPath
import com.kotlinspring.learn.userscrudapi.mock.UserMock
import com.kotlinspring.learn.userscrudapi.users.dto.StackRequest
import com.kotlinspring.learn.userscrudapi.users.repository.UserRepository
import org.hamcrest.Matchers.containsInAnyOrder
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
import kotlin.collections.ArrayList

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var userRepository: UserRepository

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
        @DisplayName("GIVEN: valid complete user request; WHEN: Post /users; THEN: return status 201 and User with ID")
        fun validCompletePayloadTest() {
            val payload = userMock.createUserRequest()

            /*
            * TODO Solução Preguiçosa! Procurar uma forma melhor de validar os elementos da Stack            *
            */
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
                .andExpect(jsonPath("$.birth_date").value(payload.birthDate.toString()))
                .andExpect(jsonPath("$.stack[*].stack").value(
                    containsInAnyOrder(
                        payload.stack!!.elementAt(0).stack,
                        payload.stack!!.elementAt(1).stack)
                ))

        }

        @Test
        @DisplayName("GIVEN: valid (null optionals) user request; WHEN: Post /users; THEN: return status 201 and User with ID")
        fun validNullOptionalsPayloadTest() {
            val payload = userMock.createUserRequest().copy(nick = null, stack = null)

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
                .andExpect(jsonPath("$.birth_date").value(payload.birthDate.toString()))
                .andExpect(jsonPath("$.stack").isArray)
        }

        @ParameterizedTest
        @ValueSource(strings = [
            "",
            "That'sStringContainMoreThan32Characters"
        ])
        @DisplayName("GIVEN: invalid nick; WHEN: Post /users; THEN: return status 400 and code Invalid Payload")
        fun invalidNickPayloadTest(nick: String) {
            val payload = userMock.createUserRequest().copy(nick = nick)

            mockMvc.perform(
                post("/$uri")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(payload))
            )
                .andExpect(status().isBadRequest)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error_messages[0].code").value("Invalid Payload"))
        }

        @ParameterizedTest
        @ValueSource(strings = [
            "",
            " ",
            "This_Sentence_Written_in_the_English_Language_Was_Designed_and_Written_to_Test_a_String_Type_Input_with_"+
                    "a_Maximum_Allowed_Size_of_255_Characters_Therefore,_To_Meet_the_Premise_of_the_Test,_The_Total_"+
                    "Number_of_Letters_in_This_Sentence_Exceeds_the_Previously_Quoted_Value"
        ])
        @DisplayName("GIVEN: invalid name; WHEN: Post /users; THEN: return status 400 and code Invalid Payload")
        fun invalidNamePayloadTest(name: String) {
            val payload = userMock.createUserRequest().copy(name = name)

            mockMvc.perform(
                post("/$uri")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(payload))
            )
                .andExpect(status().isBadRequest)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error_messages[0].code").value("Invalid Payload"))
        }

        @Test
        @DisplayName("GIVEN: existing name; WHEN: Post /users; THEN: return status 400 and code Exclusive Data Restriction")
        fun existingNamePayloadTest() {
            val payload = userMock.createUserRequest()

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
                .andExpect(jsonPath("$.error_messages[0].code").value("Data Integrity Violation"))
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
            val payload = userMock.createUserPayload()
                .copy(birthDate = birthDate)

            mockMvc.perform(
                post("/$uri")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(payload))
            )
                .andExpect(status().isBadRequest)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error_messages[0].code").value("Http Message Not Readable"))
        }

        @ParameterizedTest
        @ValueSource(strings = [
            "",
            " ",
            "That'sStringContainMoreThan32Characters"
        ])
        @DisplayName("GIVEN: invalid stack item; WHEN: Post /users; THEN: return status 400 and code Invalid Payload")
        fun invalidStackItemPayloadTest(stackItem: String) {
            val stack: MutableSet<StackRequest> = mutableSetOf(
                StackRequest(
                    stack = stackItem,
                    score = userMock.getRandomInt(userMock.maxLevel)
                )
            )
            val payload = userMock.createUserRequest().copy(stack = stack)

            mockMvc.perform(
                post("/$uri")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(payload))
            )
                .andExpect(status().isBadRequest)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error_messages[0].code").value("Invalid Payload"))
        }
    }

    @Nested
    inner class UpdateUser {
        @Test
        @DisplayName("GIVEN: valid ID and update payload; WHEN: Put /users/{id}; THEN: return status 200 and new data")
        fun validPayloadTest() {
            /*
            * TODO Solução Preguiçosa. Melhorar Isso!
            */

            val newUser = userMock.createUserRequest()
            val createdUserAsString = mockMvc.perform(
                post("/$uri")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newUser))
            ).andReturn().response.contentAsString
            val newUserId = JsonPath.parse(createdUserAsString).read<String>("$.id")

            val updatePayload = userMock.createUserRequest()

            mockMvc.put("/$uri/{id}", newUserId) {
                content = objectMapper.writeValueAsString(
                    newUser.copy(
                        nick = updatePayload.nick,
                        birthDate = updatePayload.birthDate,
                        stack = updatePayload.stack
                    )
                )
                contentType = MediaType.APPLICATION_JSON
            }.andExpectAll {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.id") { value(newUserId) }
                jsonPath("$.name") { value(newUser.name) }
                jsonPath("$.nick") { value(updatePayload.nick) }
                jsonPath("$.birth_date") { value(updatePayload.birthDate.toString()) }
                jsonPath("$.stack[*].stack") { value(
                    containsInAnyOrder(
                        updatePayload.stack!!.elementAt(0).stack,
                        updatePayload.stack!!.elementAt(1).stack
                    )
                )}
            }
        }

        @Test
        @DisplayName("GIVEN: invalid ID; WHEN: Put /users{id}; THEN: return status 404 and message error User Not Found")
        fun invalidIDPayloadTest() {
            val updatePayload = userMock.createUserRequest()
            val id: String = UUID.randomUUID().toString()

            mockMvc.put("/$uri/{id}", id)
            {
                content = objectMapper.writeValueAsString(updatePayload)
                contentType = MediaType.APPLICATION_JSON
            }.
            andExpectAll {
                status { isNotFound() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.error_messages[0].code") { value("User Not Found") }
            }

        }

        @ParameterizedTest
        @ValueSource(strings = [
            "",
            "That'sStringContainMoreThan32Characters"
        ])
        @DisplayName("GIVEN: invalid nick; WHEN: Put /users/{id}; THEN: return status 400 and error_message Invalid Payload")
        fun invalidNickPayloadTest(nick: String) {
            val updatePayload = userMock.createUserRequest().copy(nick = nick)
            val id: String = UUID.randomUUID().toString()

            mockMvc.put("/$uri/{id}", id)
            {
                content = objectMapper.writeValueAsString(updatePayload)
                contentType = MediaType.APPLICATION_JSON
            }.
            andExpectAll {
                status { isBadRequest() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.error_messages[0].code") { value("Invalid Payload") }
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
            val updatePayload = userMock.createUserRequest().copy(name = name)
            val id: String = UUID.randomUUID().toString()

            mockMvc.put("/$uri/{id}", id)
            {
                content = objectMapper.writeValueAsString(updatePayload)
                contentType = MediaType.APPLICATION_JSON
            }.
            andExpectAll {
                status { isBadRequest() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.error_messages[0].code") { value("Invalid Payload") }
            }
        }

        @Test
        @DisplayName("GIVEN: existing name; WHEN: Put /users/{id}; THEN: return status 400 and error message code Exclusive Data Restriction")
        fun existingNamePayloadTest() {
            val payloadUserOne = userMock.createUserRequest()
            mockMvc.perform(
                post("/$uri")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(payloadUserOne))
                )

            val payloadUserTwo = userMock.createUserRequest()
            val consult = mockMvc.perform(
                post("/$uri")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(payloadUserTwo))
                ).andReturn().response.contentAsString

            val userTwoId = JsonPath.parse(consult).read<String>("$.id")

            mockMvc.put("/$uri/{id}", userTwoId)
            {
                content = objectMapper.writeValueAsString(payloadUserTwo.copy(name = payloadUserOne.name))
                contentType = MediaType.APPLICATION_JSON
            }.
            andExpectAll {
                status { isBadRequest() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.error_messages[0].code") { value("Data Integrity Violation") }
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
        @DisplayName("GIVEN: invalid birthDate; WHEN: Put /users/{id}; THEN: return status 400 and error message code Http Message Not Readable")
        fun invalidBirthDatePayloadTest(birthDate: String) {
            val updatePayload = userMock.createUserPayload().copy(birthDate = birthDate)
            val id: String = UUID.randomUUID().toString()

            mockMvc.put("/$uri/{id}", id)
            {
                content = objectMapper.writeValueAsString(updatePayload)
                contentType = MediaType.APPLICATION_JSON
            }.
            andExpectAll {
                status { isBadRequest() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.error_messages[0].code") { value("Http Message Not Readable") }
            }
        }

        @ParameterizedTest
        @ValueSource(strings = [
            "",
            " ",
            "That'sStringContainMoreThan32Characters"
        ])
        @DisplayName("GIVEN: invalid stack item; WHEN: Put /users/{id}; THEN: return status 400 and error message code Invalid Payload")
        fun invalidStackItemPayloadTest(stackItem: String) {
            val stacks: MutableSet<StackRequest> = mutableSetOf(
                StackRequest(
                    stack = stackItem,
                    score = userMock.getRandomInt(userMock.maxLevel)
                )
            )
            val updatePayload = userMock.createUserRequest().copy(stack = stacks)
            val id: String = UUID.randomUUID().toString()

            mockMvc.put("/$uri/{id}", id)
            {
                content = objectMapper.writeValueAsString(updatePayload)
                contentType = MediaType.APPLICATION_JSON
            }.
            andExpectAll {
                status { isBadRequest() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.error_messages[0].code") { value("Invalid Payload") }
            }
        }
    }

    @Nested
    inner class FindUserById {
        @Test
        @DisplayName("GIVEN: existing User and valid ID; WHEN: Get /users/{id}; THEN: return status 200 and User data")
        fun validUserTest() {
            val payload = userMock.createUserRequest()
            val consult = mockMvc.perform(
                post("/$uri")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(payload))
            ).andReturn().response.contentAsString
            val payloadId = JsonPath.parse(consult).read<String>("$.id")

            /*
            * TODO Solução Preguiçosa. Melhorar Isso!
            */
            mockMvc.get("/$uri/{id}", payloadId).
            andExpectAll {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.id") { value(payloadId) }
                jsonPath("$.name") {value(payload.name)}
                jsonPath("$.nick") {value(payload.nick)}
                jsonPath("$.birth_date") {value(payload.birthDate.toString())}
                jsonPath("$.stack[*].stack") {value(
                    containsInAnyOrder(
                        payload.stack!!.elementAt(0).stack,
                        payload.stack!!.elementAt(1).stack
                    )
                )}
            }

        }

        @Test
        @DisplayName("GIVEN: valid ID (non existent user); WHEN: Get /users/{id}; THEN: return status 404 and message error User Not Found")
        fun invalidUserTest() {
            val id: UUID = UUID.randomUUID()

            mockMvc.get("/$uri/{id}", id.toString()) { }
                .andExpectAll {
                    status { isNotFound() }
                    jsonPath("$.error_messages[0].code") { value("User Not Found") }
                }
        }

        @Test
        @DisplayName("GIVEN: invalid ID (empty); WHEN: Get /users/{id}; THEN: return status 404 and error_message No Resource Found")
        fun invalidEmptyIdTest() {
            mockMvc.get("/$uri/{id}", "").
            andExpectAll {
                status { isNotFound() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.error_messages[0].code") { value("No Resource Found") }
            }
        }

        @Test
        @DisplayName("GIVEN: invalid ID (blank); WHEN: Get /users/{id}; THEN: return status 400 and error_message Missing Path Variable")
        fun invalidBlackIdTest() {
            mockMvc.get("/$uri/{id}", " ").
            andExpectAll {
                status { isBadRequest() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.error_messages[0].code") { value("Missing Path Variable") }
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
                jsonPath("$.error_messages[0].code") { value("Method Argument Type Mismatch") }
            }
        }
    }

    @Nested
    inner class FindUsers {
        @Test
        @DisplayName("GIVEN: db with 16 users; WHEN: Get /users; THEN: return status 1st 206, page=0, page_size=15, total=16")
        fun validDefaultConsultTest() {
            for (i in 1..16) {
                val newUser = userMock.createUserEntity()
                userRepository.save(newUser)
            }

            mockMvc.get("/$uri") {}
                .andExpectAll {
                    status { isPartialContent() }
                    jsonPath("$.page") {value( "0")}
                    jsonPath("$.page_size") {value( "15")}
                    jsonPath("$.total") {value( "16")}
                }
        }

        @Test
        @DisplayName("GIVEN: db with 31 users; WHEN: Get /users?page=1; THEN: return status 1st 206, page=1, page_size=15, total=31")
        fun validPartialConsultTest() {
            for (i in 1..31) {
                val newUser = userMock.createUserEntity()
                userRepository.save(newUser)
            }

            mockMvc.get("/$uri") {
                param("page", "1")
            }
                .andExpectAll {
                    status { isPartialContent() }
                    jsonPath("$.page") {value( "1")}
                    jsonPath("$.page_size") {value( "15")}
                    jsonPath("$.total") {value( "31")}
                }
        }

        @Test
        @DisplayName("GIVEN: db with 10 users; WHEN: Get /users?page=1&page_size=5; THEN: return status 200 and last page")
        fun validLastPageConsultTest() {
            for (i in 0.. 9) {
                val newUser = userMock.createUserEntity()
                userRepository.save(newUser)
            }

            mockMvc.get("/$uri") {
                param("page", "1")
                param("page_size", "5")
            }
                .andExpectAll {
                    status { isOk() }
                    jsonPath("$.page") {value( "1")}
                    jsonPath("$.page_size") {value( "5")}
                    jsonPath("$.total") {value( "10")}
                }
        }

        @Test
        @DisplayName("GIVEN: db with 10 users; WHEN: Get /users?sort=name; THEN: return status 200 and ASC Sorted Items by name")
        fun validAscSortConsultTest() {
            var names: List<String> = ArrayList()

            for (i in 0.. 9) {
                val newUser = userMock.createUserEntity()
                names.addLast(newUser.name)
                userRepository.save(newUser)
            }

            /*
            * TODO Solução Preguiçosa. Melhorar Isso!
            */
            names = names.sorted()

            mockMvc.get("/$uri") {
                param("sort", "name")
            }
                .andExpectAll {
                    status { isOk() }
                    jsonPath("$.records[0].name") {value(names[0])}
                    jsonPath("$.records[1].name") {value(names[1])}
                    jsonPath("$.records[2].name") {value(names[2])}
                    jsonPath("$.records[3].name") {value(names[3])}
                    jsonPath("$.records[4].name") {value(names[4])}
                    jsonPath("$.records[5].name") {value(names[5])}
                    jsonPath("$.records[6].name") {value(names[6])}
                    jsonPath("$.records[7].name") {value(names[7])}
                    jsonPath("$.records[8].name") {value(names[8])}
                    jsonPath("$.records[9].name") {value(names[9])}
                }
        }

        @Test
        @DisplayName("GIVEN: db with 10 users; WHEN: Get /users?sort=-name; THEN: return status 200 and DESC Sorted Items by name")
        fun validDescSortConsultTest() {
            var names: List<String> = ArrayList()

            for (i in 0.. 9) {
                val newUser = userMock.createUserEntity()
                names.addLast(newUser.name)
                userRepository.save(newUser)
            }

            names = names.sortedDescending()

            mockMvc.get("/$uri") {
                param("sort", "-name")
            }
                .andExpectAll {
                    status { isOk() }
                    jsonPath("$.records[0].name") {value(names[0])}
                    jsonPath("$.records[1].name") {value(names[1])}
                    jsonPath("$.records[2].name") {value(names[2])}
                    jsonPath("$.records[3].name") {value(names[3])}
                    jsonPath("$.records[4].name") {value(names[4])}
                    jsonPath("$.records[5].name") {value(names[5])}
                    jsonPath("$.records[6].name") {value(names[6])}
                    jsonPath("$.records[7].name") {value(names[7])}
                    jsonPath("$.records[8].name") {value(names[8])}
                    jsonPath("$.records[9].name") {value(names[9])}
                }
        }

    }

    @Nested
    inner class DeleteUser {

        @Test
        @DisplayName("GIVEN: existing User and valid ID; WHEN: Delete /users/{id}; THEN: return status 204 and remove user")
        fun validUserTest() {
            val payload = userMock.createUserRequest()
            val consult = mockMvc.perform(
                post("/$uri")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(payload))
            ).andReturn().response.contentAsString
            val payloadId = JsonPath.parse(consult).read<String>("$.id")

            mockMvc.delete("/$uri/{id}", payloadId).
            andExpectAll {
                status { isNoContent() }
            }

            mockMvc.get("/$uri/{id}", payloadId).
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
                    jsonPath("$.error_messages[0].code") { value("User Not Found") }
                }
        }

        @Test
        @DisplayName("GIVEN: invalid ID (empty); WHEN: Delete /users/{id}; THEN: return status 404 and error_message No Resource Found")
        fun invalidEmptyIdTest() {
            mockMvc.get("/$uri/{id}", "").
            andExpectAll {
                status { isNotFound() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.error_messages[0].code") { value("No Resource Found") }
            }
        }

        @Test
        @DisplayName("GIVEN: invalid ID (blank); WHEN: Delete /users/{id}; THEN: return status 400 and error_message Missing Path Variable")
        fun invalidBlackIdTest() {
            mockMvc.get("/$uri/{id}", " ").
            andExpectAll {
                status { isBadRequest() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.error_messages[0].code") { value("Missing Path Variable") }
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
                jsonPath("$.error_messages[0].code") { value("Method Argument Type Mismatch") }
            }
        }
    }

}