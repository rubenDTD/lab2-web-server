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
 *
 */

class ErrorMessageModel(
    var status: Int? = null,
    var message: String? = null,

)

@ControllerAdvice
class ExceptionControllerAdvice {

    @ExceptionHandler
    fun handleIllegalStateException(ex: IllegalStateException): ResponseEntity<ErrorMessageModel> {

        val errorMessage = ErrorMessageModel(
            HttpStatus.NOT_FOUND.value(),
            ex.message
        )

        return ResponseEntity(errorMessage, HttpStatus.BAD_REQUEST)
    }
}

@RestController
class ErrorController {
    @GetMapping("*")
    fun defaultError() {
        error("Looks like you got a 404 error...again :D")
    }
}
