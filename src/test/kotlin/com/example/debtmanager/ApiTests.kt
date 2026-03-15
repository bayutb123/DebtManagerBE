package com.example.debtmanager

import com.example.debtmanager.infrastructure.security.GoogleTokenVerifier
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.get
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

@SpringBootTest
@AutoConfigureMockMvc
class ApiTests {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockBean
    lateinit var googleTokenVerifier: GoogleTokenVerifier

    private lateinit var jwt: String

    @BeforeEach
    fun setup() {
        val payload = GoogleIdToken.Payload()
        payload.email = "test@example.com"
        payload["name"] = "Test User"
        payload["picture"] = "http://example.com/pic.png"
        whenever(googleTokenVerifier.verify("dummy"))
            .thenReturn(payload)

        val result = mockMvc.post("/auth/google") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(mapOf("idToken" to "dummy"))
        }.andExpect {
            status { isOk() }
        }.andReturn()

        val json = objectMapper.readTree(result.response.contentAsString)
        println("Auth setup response: ${result.response.contentAsString}")
        jwt = json.get("token").asText()
        assertNotNull(jwt)
    }

    @Test
    fun `authentication returns jwt and user`() {
        val payload = GoogleIdToken.Payload()
        payload.email = "another@example.com"
        payload["name"] = "Another User"
        payload["picture"] = "http://example.com/pic.png"
        whenever(googleTokenVerifier.verify("dummy2"))
            .thenReturn(payload)

        val result = mockMvc.post("/auth/google") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(mapOf("idToken" to "dummy2"))
        }.andExpect {
            status { isOk() }
        }.andReturn()

        val json = objectMapper.readTree(result.response.contentAsString)
        assertNotNull(json.get("token").asText())
        assertEquals("another@example.com", json.get("user").get("email").asText())
    }

    @Test
    fun `debt creation and payment updates status`() {
        // create contact
        val contactResponse = mockMvc.post("/contacts") {
            contentType = MediaType.APPLICATION_JSON
            header("Authorization", "Bearer $jwt")
            content = objectMapper.writeValueAsString(mapOf(
                "name" to "Alice",
                "phone" to "123",
                "notes" to "friend"
            ))
        }.andExpect { status { isCreated() } }.andReturn()
        val contactJson = objectMapper.readTree(contactResponse.response.contentAsString)
        val contactId = UUID.fromString(contactJson.get("id").asText())

        // create debt
        val debtResponse = mockMvc.post("/debts") {
            contentType = MediaType.APPLICATION_JSON
            header("Authorization", "Bearer $jwt")
            content = objectMapper.writeValueAsString(mapOf(
                "contactId" to contactId,
                "title" to "Loan",
                "amount" to BigDecimal(100).toPlainString(),
                "dueDate" to LocalDate.now().plusDays(7).toString(),
                "notes" to "test"
            ))
        }.andExpect { status { isCreated() } }.andReturn()
        val debtJson = objectMapper.readTree(debtResponse.response.contentAsString)
        val debtId = UUID.fromString(debtJson.get("id").asText())

        // pay debt
        val payResponse = mockMvc.post("/debts/$debtId/payment") {
            contentType = MediaType.APPLICATION_JSON
            header("Authorization", "Bearer $jwt")
            content = objectMapper.writeValueAsString(mapOf(
                "amount" to BigDecimal(100).toPlainString(),
                "date" to LocalDate.now().toString(),
                "note" to "paid"
            ))
        }.andExpect { status { isOk() } }.andReturn()

        val updatedDebt = objectMapper.readTree(payResponse.response.contentAsString)
        assertEquals("PAID", updatedDebt.get("status").asText())
    }
}
