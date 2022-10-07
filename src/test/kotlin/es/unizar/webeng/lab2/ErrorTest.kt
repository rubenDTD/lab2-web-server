package es.unizar.webeng.lab2

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

/*
 * source:
 * https://medium.com/backyard-programmers/kotlin-spring-boot-unit-testing-integration-testing-with-junit5-and-mockk-a2977bbe5711
 *
 *
 */

@SpringBootTest
@AutoConfigureMockMvc
class ErrorTest @Autowired constructor(
    val mockMvc: MockMvc
) {

    @Test
    fun `check bad request error`() {
        mockMvc.get("/randomURI") {
            contentType = MediaType.APPLICATION_JSON
        }
            .andDo { print() }
            .andExpect { status { isBadRequest() } }
    }
}
