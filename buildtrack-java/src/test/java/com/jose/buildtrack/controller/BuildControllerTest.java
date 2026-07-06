package com.jose.buildtrack.controller;

// JUnit 5: nos permite marcar métodos como tests con @Test
import org.junit.jupiter.api.Test;

// Spring inyecta aquí el objeto MockMvc ya configurado
import org.springframework.beans.factory.annotation.Autowired;

// Configura automáticamente MockMvc para poder simular peticiones HTTP
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

// Levanta el contexto completo de Spring para el test:
// controllers, services, repositories, mappers, exception handlers, etc.
import org.springframework.boot.test.context.SpringBootTest;

// Nos permite indicar que el body de la petición será JSON
import org.springframework.http.MediaType;

// Herramienta principal para simular peticiones HTTP contra nuestra API
import org.springframework.test.web.servlet.MockMvc;

// Import estático para construir peticiones POST de forma más limpia
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

// Import estático para comprobar campos concretos del JSON de respuesta
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

// Import estático para comprobar el código HTTP de respuesta
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test de integración ligera para BuildController.
 *
 * Esta clase no prueba solo Java puro.
 * Prueba el recorrido HTTP real dentro de Spring:
 *
 * Request HTTP simulada
 *      ↓
 * BuildController
 *      ↓
 * BuildService
 *      ↓
 * BuildRepository en memoria
 *      ↓
 * Domain
 *      ↓
 * Response JSON
 *
 * MockMvc nos permite hacer esto sin abrir Postman
 * y sin levantar un servidor real manualmente.
 */
@SpringBootTest
@AutoConfigureMockMvc
class BuildControllerTest {

    /**
     * MockMvc es como un "Postman automático" dentro de los tests.
     *
     * Con él podemos hacer:
     * - POST /builds
     * - GET /builds/{id}
     * - enviar JSON
     * - comprobar status HTTP
     * - comprobar campos de la respuesta JSON
     */
    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCreateBuild() throws Exception {

        final MediaType application_JSON2 = MediaType.APPLICATION_JSON;
        if (application_JSON2 != null) {
            /*
             * ARRANGE + ACT
             *
             * Aquí simulamos una petición HTTP POST a /builds.
             *
             * Es equivalente a hacer en Postman:
             *
             * POST http://localhost:8080/builds
             *
             * Body:
             * {
             *   "id": "B-MOCK-001",
             *   "version": "1.0.0",
             *   "platform": "WINDOWS"
             * }
             *
             * contentType(MediaType.APPLICATION_JSON) indica que estamos enviando JSON.
             */
            mockMvc.perform(post("/builds")
                            .contentType(application_JSON2)
                            .content("""
                                    {
                                      "id": "B-MOCK-001",
                                      "version": "1.0.0",
                                      "platform": "WINDOWS"
                                    }
                                    """))

                    /*
                     * ASSERT
                     *
                     * Esperamos que la API responda 200 OK.
                     *
                     * Eso significa que:
                     * - el JSON llegó correctamente
                     * - el controller lo recibió
                     * - el DTO se pudo crear
                     * - el service creó la build
                     * - el mapper devolvió una respuesta válida
                     */
                    .andExpect(status().isOk())

                    /*
                     * jsonPath("$.id") significa:
                     *
                     * "busca el campo id en la raíz del JSON de respuesta".
                     *
                     * Si la respuesta fuera:
                     *
                     * {
                     *   "id": "B-MOCK-001",
                     *   "version": "1.0.0",
                     *   "platform": "WINDOWS",
                     *   "status": "CREATED"
                     * }
                     *
                     * entonces $.id vale "B-MOCK-001".
                     */
                    .andExpect(jsonPath("$.id").value("B-MOCK-001"))

                    /*
                     * Comprobamos que la versión devuelta por la API
                     * coincide con la que hemos enviado.
                     */
                    .andExpect(jsonPath("$.version").value("1.0.0"))

                    /*
                     * Comprobamos que la plataforma se ha convertido bien
                     * desde el JSON al enum Platform y luego de vuelta a String.
                     */
                    .andExpect(jsonPath("$.platform").value("WINDOWS"))

                    /*
                     * Una build recién creada debe empezar siempre en CREATED.
                     *
                     * Esta es una regla del dominio que estamos comprobando
                     * a través de la API.
                     */
                    .andExpect(jsonPath("$.status").value("CREATED"));
        } else {
        }
    }

    @Test
    void shouldRejectBuildCreationWhenIdIsBlank() throws Exception {

        final MediaType application_JSON2 = MediaType.APPLICATION_JSON;
        if (application_JSON2 != null) {
            /*
             * En este test simulamos un cliente enviando un request inválido.
             *
             * El id viene vacío:
             *
             * "id": ""
             *
             * Como CreateBuildRequestDTO tiene @NotBlank en el campo id,
             * Spring debe rechazar la petición antes de llegar al service.
             */
            mockMvc.perform(post("/builds")
                            .contentType(application_JSON2)
                            .content("""
                                    {
                                      "id": "",
                                      "version": "1.0.0",
                                      "platform": "WINDOWS"
                                    }
                                    """))

                    /*
                     * Esperamos 400 Bad Request.
                     *
                     * 400 significa:
                     * "el cliente ha enviado datos incorrectos".
                     *
                     * No es un error interno del servidor.
                     */
                    .andExpect(status().isBadRequest())

                    /*
                     * A partir de aquí comprobamos que nuestro GlobalExceptionHandler
                     * está devolviendo el JSON de error con el formato que definimos.
                     */
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))

                    /*
                     * Este mensaje viene de la anotación @NotBlank:
                     *
                     * @NotBlank(message = "Build ID is required")
                     *
                     * en CreateBuildRequestDTO.
                     */
                    .andExpect(jsonPath("$.message").value("Build ID is required"));
        } else {
        }
    }

    @Test
    void shouldRejectBuildCreationWhenPlatformIsInvalid() throws Exception {

        final MediaType application_JSON2 = MediaType.APPLICATION_JSON;
        if (application_JSON2 != null) {
            /*
             * Aquí probamos otro caso inválido:
             *
             * El cliente envía una plataforma que no existe en nuestro enum Platform.
             *
             * Por ejemplo:
             *
             * "PLAYSTATION"
             *
             * Si Platform solo tiene WINDOWS, LINUX, MACOS, etc.,
             * entonces PLAYSTATION debe ser rechazada.
             */
            mockMvc.perform(post("/builds")
                            .contentType(application_JSON2)
                            .content("""
                                    {
                                      "id": "B-MOCK-002",
                                      "version": "1.0.0",
                                      "platform": "PLAYSTATION"
                                    }
                                    """))

                    /*
                     * También esperamos 400 Bad Request,
                     * porque el cliente ha enviado un valor no permitido.
                     */
                    .andExpect(status().isBadRequest())

                    /*
                     * Comprobamos otra vez el formato estándar de error.
                     */
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))

                    /*
                     * Este mensaje debería venir de InvalidPlatformException,
                     * lanzada desde el mapper cuando intenta convertir:
                     *
                     * "PLAYSTATION" → Platform
                     *
                     * y no encuentra ese valor en el enum.
                     */
                    .andExpect(jsonPath("$.message").value("Invalid platform: PLAYSTATION"));
        } else {

        }
    }
}