# No more static

The primary task consists on replace de `error.html` file with a custom error message with `@ControllerAdvice`.
Also, along with the new functionality, a JUnit test it's going to be implemented.

## ErrorComponent

We're going to create a new file `ErrorComponent.kt` and inside define the class `ErrorMessageModel`:

```kotlin
class ErrorMessageModel(
    var status: Int? = null,
    var message: String? = null,
)
```
This is going to be our custom error message. I've only defined just the error HTTP status and a string message,
but we can define all the attributes we need.

Next we create the class `ExceptionControllerAdvice` where we're going to define our error exceptions:

```kotlin
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
```
`RestControllerAdvice` allow us to define a global handler for multiple Controllers. That means, you can create
any exception you need to catch depending on your application context. In this case, we're going to capture
a `NoHandlerFoundException` when client tries to use a non defined endpoint.

Next, we're going to indicate Spring to throw a `NoHandlerFoundException` when
no handler is found, adding the next line to `application.properties`:

```properties
spring.mvc.throw-exception-if-no-handler-found=true
```

And disable the default configuration of the `DefaultServlet`, which searches files such as `error.html`
to handle exceptions. We do that like this:

```kotlin
@EnableWebMvc
@Configuration
class WebConfig : WebMvcConfigurer {
    override fun configureDefaultServletHandling(configurer: DefaultServletHandlerConfigurer) {
        // Do nothing instead of configurer.enable();
    }
}
```

## Manual Testing

We start the application as usual:

```bash
./gradlew bootRun
```

Now we try with any endpoint, `/random` for instance, and you'll se something like this:

```bash
ruben@ruben-VM:~/2022_2023/IW/lab2-web-server$ curl -k -LH "Accept: text/html,*/*;q=0.9" -i https://127.0.0.1:8443/random
HTTP/2 404 
content-type: application/json
date: Tue, 25 Oct 2022 16:22:46 GMT

{"status":404,"message":"No handler found for GET /random"}ruben@ruben-VM:~/2022_2023/IW/lab2-web-server$
```

As we can see, it works!

## JUnit + Mockk Testing

Now we're going to implement a simple test with JUnit and Mockk. I have decided to use Mockk
instead Mockito because I already have worked with Mockito before, and also, from what I've read,
Mockk was specifically done for Kotlin.

First we need a new file, `ErrorTest.kt`, to implement the test, with the next content:

```kotlin
@SpringBootTest
@AutoConfigureMockMvc
class ErrorTest @Autowired constructor(
    val mockMvc: MockMvc
) {

    @Test
    fun `check not found error`() {
        mockMvc.get("/randomURI") {
            contentType = MediaType.APPLICATION_JSON
        }
            .andDo { print() }
            .andExpect {
                status { isNotFound() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json(ERROR_RESPONSE_BODY(404, "No handler found for GET /randomURI"))
                }
            }
    }
}
```
`@SpringBootTest` indicates that this is a Spring boot based test.


`@AutoConfigureMockMvc` enables autoconfiguration to `mockMvc`, which it's going to be used
to fake a `GET` request.

Function `check not found error` use `mockMvc` to execute a fake request to `/randomURI`.
We're telling that we want to print the request's result, that we expect `404 NOT FOUND` and
JSON content must be the HTTP error code and the custom message. I have defined an `ERROR_RESPONSE_BODY`
for this:

```kotlin
private val ERROR_RESPONSE_BODY = { status: Int, message: String ->
    """
    {"status":$status,"message":"$message"}
    """
}
```
But the next line do the same:

```kotlin
json("{status: 404, message: \"No handler found for GET /randomURI\"}")
```

We get the next response when running the test:

```bash
MockHttpServletResponse:
           Status = 404
    Error message = null
          Headers = [Content-Type:"application/json"]
     Content type = application/json
             Body = {"status":404,"message":"No handler found for GET /randomURI"}
    Forwarded URL = null
   Redirected URL = null
          Cookies = []
BUILD SUCCESSFUL in 5s
```



## Reference Documentation

- [ControllerAdvice guide](https://www.baeldung.com/kotlin/spring-rest-error-handling)
- [JUnit + Mockk guide](https://medium.com/backyard-programmers/kotlin-spring-boot-unit-testing-integration-testing-with-junit5-and-mockk-a2977bbe5711)
- [Mockito vs Mockk](https://blog.logrocket.com/unit-testing-kotlin-projects-with-mockk-vs-mockito/)