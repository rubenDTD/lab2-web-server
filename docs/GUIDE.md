# GUIDE

This is the repository that we will use for the second assignment of the course 2022-2023. This guide is command line oriented, but you are free to use IDE like _VS Code_, _IntelliJ IDEA_ and _Eclipse_ which have full support of the tools that we are going to use. We also assume that you have installed in your box at least [Kotlin 1.7.0](https://kotlinlang.org/docs/getting-started.html#install-kotlin).

This laboratory is not a speed competition.

## Preparation

Fork this repository.
Next you will have <https://github.com/UNIZAR-30246-WebEngineering/lab2-web-server> cloned in `https://github.com/your-github-username/ab2-web-server`.

By default, GitHub Actions is disabled for your forked repository.
Go to `https://github.com/your-github-username/lab2-web-server/actions` and enable them.

Next, go to your repository and click in `Code` on the `main` button and create a branch named `work`.

Next, clone locally the repository:

```bash
git clone https://github.com/your-github-username/lab2-web-server
cd lab2-web-server
git branch -a
```

Should show `main`, `work`, `remotes/origin/main` and `remotes/origin/work`.

Then, checkout the `work` branch:

```bash
git checkout -b work
```

Make changes to the files, commit the changes to the history and push the branch up to your forked version.

```bash
git push origin work
```

If you want to test the server, just run:

```bash
./gradlew bootRun
```

## Primary tasks

- Customize whitelabel error error page
- Add a new enpoint at `/time` that returns the time in JSON
- Enable HTTP/2 and SSL support

## Warning: Detekt and Ktlint are watching you

[Detekt](https://detekt.dev/) helps you write cleaner Kotlin code so you can focus on what matters the most building amazing software.
It is configured to run before compiling the code.

- By default, the standard rule set without any ignore list is executed on sources files located in src/main/java, src/test/java, src/main/kotlin and src/test/kotlin.
- Reports are automatically generated in xml, html, txt, md, and sarif format and can be found in build/reports/detekt/detekt.[xml|html|txt|md|sarif] respectively.
- Also, the [Klint](https://ktlint.github.io/) plugin has been enabled. It will automatically format your code (if it can) respectively to the [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html#source-code-organization). If the source code is changed the build will fail.

## Steps required

### Customize whitelabel error error page

- Add a template engine in the dependencies.
  For example, `org.springframework.boot:spring-boot-starter-thymeleaf` for Thymeleaf.
- Create a custom HTML error page.
- Save the file in `resources/templates` directory with the name `error.html`

### Add a new endpoint

- Create a file `TimeComponent.kt`.
- Create inside a DTO for returning the time:

  ```kotlin
  data class TimeDTO(val time: LocalDateTime)
  ```

- Create an interface that represents the time provider:

  ```kotlin
  interface TimeProvider {
    fun now(): LocalDateTime
  }
  ```

- Create a service that implements the `TimeProvider` interface:

  ```kotlin
  @Service
  class TimeService: TimeProvider {
    override fun now() = LocalDateTime.now()
  }
  ```

- Create an extension that transform a `LocalDateTime` into a `TimeDTO` object:

  ```kotlin
  fun LocalDateTime.toDTO() = TimeDTO(time = this)
  ```

- Create a `RestController` that exposes the service:

  ```kotlin
  @RestController 
  class TimeController(val service: TimeProvider) {
    @GetMapping("/time")
    fun time() = service.now().toDTO()
  }
  ```  

### Enable HTTP/2 and SSL support

- Use the following command to generate a self-signed certificate:

  ```sh
  openssl req -x509 -out localhost.crt -keyout localhost.key \
    -newkey rsa:2048 -nodes -sha256 \
    -subj '/CN=localhost' -extensions EXT -config <( \
     printf "[dn]\nCN=localhost\n[req]\ndistinguished_name = dn\n[EXT]\nsubjectAltName=DNS:localhost\nkeyUsage=digitalSignature\nextendedKeyUsage=serverAuth")
  ```

  The command generates two files: `localhost.crt`, the public key, and `localhost.key`, the private key.
- Next use `openssl` to generate a PKCS truststore file that contains both our certificate and key. Do not forget the export password that you are requested to enter.
  
  ```sh
  openssl pkcs12 -export -in localhost.crt -inkey localhost.key -name localhost -out localhost.p12
  ```

  Move the file `localhost.p12` to `src/main/resources`.

  **Note for windows users:** Unless you use a WSL Linux Distro, you will need to use [OpenSSL for Windows](https://wiki.openssl.org/index.php/Binaries).

- Create an `application.properties` file in `src/main/resources` with the following content follows:

  ```properties
  # Enable HTTP/2
  server.http2.enabled=true
  # Change the port to 8444
  server.port=8443
  # Enable HTTPS
  server.ssl.key-store-type=PKCS12
  server.ssl.key-store=classpath:localhost.p12
  server.ssl.key-store-password=<the export password>
  server.ssl.key-alias=localhost
  ```

## Manual verification

1. Start the application as usual.

1. For the generic error file, running in the terminal:

   ```sh
   curl -k -LH "Accept: text/html,*/*;q=0.9" -i https://127.0.0.1:8443/  
   ```

  `-k` disables the certificate validation,
  `-LK "Accept: text/html,*/*;q=0.9"` adds the `Accept` header to the request,
  `-i` specify that the ouptut should include the HTTP response headers

   should output in the terminal something similar to:

   ```http
   HTTP/2 404 
   vary: Origin
   vary: Access-Control-Request-Method
   vary: Access-Control-Request-Headers
   content-type: text/html;charset=UTF-8
   content-language: es-ES
   content-length: 134
   date: Sat, 01 Oct 2022 13:59:22 GMT
   ```

   and the content must be your custom error page.

1. For the HTTP/2 and SSL support, running in the terminal:

   ```sh
   curl -k -LH "Accept: text/html,*/*;q=0.9" -i https://127.0.0.1:8443/time  
   ```

   should output in the terminal something similar to:

   ```http
   HTTP/2 200 
   content-type: application/json
   content-length: 37
   date: Sat, 01 Oct 2022 13:59:27 GMT
   ```

   In addition the entity body must have the structure below;

   ```json
   {
     "time": "2022-10-01T16:33:58.91803" 
   }
   ```

## How to submit

In your master branch update your corresponding row in README.md with the link to your work branch, the GitHub actions badge for CI and a link to a document that proof or explains how you solved this lab.

Do a pull request from your `main` branch to this repo main branch.
The only file modified in the pull request must be `README.md`
The pull request will be accepted if:

- Your `work` branch contains proofs that shows that you have fulfilled the primary tasks.
- In `README.md` you provides a link to your work branch in your repository, i.e.:

  ```md
  [your-github-user-name](https://github.com/your-github-username/lab2-web-server/tree/work)
  ```

  along with your NIA and a link to your CI workflow.

  ```md
  ![Build Status](https://github.com/your-github-username/lab2-web-server/actions/workflows/CI.yml/badge.svg?branch=work&event=push)](https://github.com/your-github-username/lab2-web-server/actions/workflows/CI.yml)  
  ```

  and the color of your CI workflow triggered by a push event in the branch named work is green.
