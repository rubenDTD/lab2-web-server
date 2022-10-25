package es.unizar.webeng.lab2.error

import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.NoHandlerFoundException
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/*
 * https://www.baeldung.com/kotlin/spring-rest-error-handling -> source
 *
 */

/*
 * An error interface to show error messages
 *
 */
class ErrorMessageModel(
    var status: Int? = null,
    var message: String? = null,
)

/*
 * Override default configuration of DefaultServlet to disable the files' search like error.html
 *
 */
@EnableWebMvc
@Configuration
class WebConfig : WebMvcConfigurer {
    override fun configureDefaultServletHandling(configurer: DefaultServletHandlerConfigurer) {
        // Do nothing instead of configurer.enable();
    }
}

/*
 * Here we can define or custom error exceptions
 *
 */
@RestControllerAdvice
class ExceptionControllerAdvice {
    @ExceptionHandler(NoHandlerFoundException::class)
    fun handleIllegalStateException(ex: NoHandlerFoundException): ResponseEntity<ErrorMessageModel> {
        val errorMessage = ErrorMessageModel(
            HttpStatus.NOT_FOUND.value(),
            ex.message
        )
        return ResponseEntity(errorMessage, HttpStatus.NOT_FOUND)
    }
}
