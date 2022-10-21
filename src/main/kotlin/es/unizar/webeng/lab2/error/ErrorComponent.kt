package es.unizar.webeng.lab2.error

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

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

class NotFoundException(message: String) : RuntimeException(message)

/*
 * Here we can define or custom error exceptions
 *
 */
@ControllerAdvice
class ExceptionControllerAdvice {

    @ExceptionHandler
    fun handleIllegalStateException(ex: NotFoundException): ResponseEntity<ErrorMessageModel> {

        val errorMessage = ErrorMessageModel(
            HttpStatus.NOT_FOUND.value(),
            ex.message
        )

        return ResponseEntity(errorMessage, HttpStatus.NOT_FOUND)
    }
}

/*
 * Simple ErrorController to map any unregistered endpoint
 *
 */
@RestController
class ErrorController {
    @GetMapping("*")
    fun defaultError() {
        throw NotFoundException("Looks like you got a 404 error...again :D")
    }
}
