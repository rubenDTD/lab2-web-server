package es.unizar.webeng.lab2

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest
@AutoConfigureMockMvc
class TimeTest @Autowired constructor(
    val mockMvc: MockMvc,
    val timeService: TimeService
) {

    @Test
    fun `return time service`() {
        val time = timeService.now()
        assert(time != null)
    }

    @Test
    fun `time endpoint`() {
        mockMvc.get("/time") {
            contentType = MediaType.APPLICATION_JSON
        }
            .andDo { print() }
            .andExpect { status { isOk() } }
    }
}
