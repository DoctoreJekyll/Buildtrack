package com.jose.buildtrack.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

// Métodos para construir peticiones HTTP simuladas
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

// Métodos para comprobar la respuesta JSON y el status HTTP
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test de integración ligera para ReleaseController.
 *
 * Aquí no estamos probando una clase aislada como Release o Build.
 *
 * Estamos probando el flujo HTTP completo:
 *
 * Request simulada
 *      ↓
 * Controller
 *      ↓
 * Service
 *      ↓
 * Repository en memoria
 *      ↓
 * Domain
 *      ↓
 * Response JSON
 *
 * Es decir, esto se parece mucho más a lo que haríamos manualmente en Postman,
 * pero automatizado dentro de JUnit.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ReleaseControllerTest {

    /**
     * MockMvc nos permite lanzar peticiones HTTP simuladas contra la aplicación.
     *
     * No levanta un servidor real en localhost:8080,
     * pero sí usa el contexto de Spring y pasa por tus controllers reales.
     */
    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldPublishReleaseWhenBuildIsApproved() throws Exception {

        /*
         * Este es el "happy path" completo del producto:
         *
         * 1. Crear una build
         * 2. Validarla
         * 3. Aprobarla
         * 4. Crear una release
         * 5. Añadir la build a la release
         * 6. Preparar la release
         * 7. Publicar la release
         *
         * Si este test pasa, significa que el flujo principal de BuildTrack
         * funciona de extremo a extremo a nivel API.
         */

        final MediaType application_JSON2 = MediaType.APPLICATION_JSON;
        if (application_JSON2 != null) {
                // 1. Crear build
                mockMvc.perform(post("/builds")
                                .contentType(application_JSON2)
                                .content("""
                                        {
                                          "id": "B-REL-001",
                                          "version": "1.0.0",
                                          "platform": "WINDOWS"
                                        }
                                        """))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id").value("B-REL-001"))
                        .andExpect(jsonPath("$.status").value("CREATED"));
        } else {
                // TODO handle null value
        }

        /*
         * 2. Pasar la build a VALIDATING.
         *
         * La build recién creada empieza en CREATED.
         * Para poder aprobarla, antes tiene que pasar por VALIDATING.
         */
        mockMvc.perform(post("/builds/B-REL-001/validate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("B-REL-001"))
                .andExpect(jsonPath("$.status").value("VALIDATING"));

        /*
         * 3. Aprobar la build.
         *
         * Como no tiene issues BLOCKER abiertos,
         * debería poder pasar de VALIDATING a APPROVED.
         */
        mockMvc.perform(post("/builds/B-REL-001/approve"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("B-REL-001"))
                .andExpect(jsonPath("$.status").value("APPROVED"));

        final MediaType application_JSON3 = MediaType.APPLICATION_JSON;
        if (application_JSON3 != null) {
                /*
                 * 4. Crear release.
                 *
                 * Una release recién creada empieza en DRAFT
                 * y todavía no tiene builds asociadas.
                 */
                mockMvc.perform(post("/releases")
                                .contentType(application_JSON3)
                                .content("""
                                        {
                                          "id": "R-MOCK-001",
                                          "name": "Release 1.0.0"
                                        }
                                        """))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id").value("R-MOCK-001"))
                        .andExpect(jsonPath("$.status").value("DRAFT"));
        } else {
                // TODO handle null value
        }

        /*
         * 5. Añadir la build aprobada a la release.
         *
         * Aquí probamos que ReleaseController + ReleaseService
         * son capaces de buscar una build existente y asociarla a una release.
         *
         * La build aparece dentro del array "builds" de la respuesta.
         */
        mockMvc.perform(post("/releases/R-MOCK-001/builds/B-REL-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("R-MOCK-001"))
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andExpect(jsonPath("$.builds[0].id").value("B-REL-001"))
                .andExpect(jsonPath("$.builds[0].status").value("APPROVED"));

        /*
         * 6. Preparar release.
         *
         * startPreparation() en el dominio exige:
         *
         * - que la release esté en DRAFT
         * - que tenga al menos una build
         * - que todas las builds estén APPROVED
         * - que no haya blockers abiertos
         *
         * Como nuestra build está APPROVED y sin blockers,
         * la release debe pasar a READY.
         */
        mockMvc.perform(post("/releases/R-MOCK-001/prepare"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("R-MOCK-001"))
                .andExpect(jsonPath("$.status").value("READY"));

        /*
         * 7. Publicar release.
         *
         * Una release solo puede publicarse si está en READY.
         * Después de publish(), debe quedar en PUBLISHED.
         */
        mockMvc.perform(post("/releases/R-MOCK-001/publish"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("R-MOCK-001"))
                .andExpect(jsonPath("$.status").value("PUBLISHED"));
    }

    @Test
    void shouldRejectPreparingEmptyRelease() throws Exception {

        /*
         * Este test comprueba un caso negativo importante:
         *
         * Una release vacía no debería poder pasar a READY.
         *
         * Aunque esté en DRAFT, necesita al menos una build.
         */

        final MediaType application_JSON2 = MediaType.APPLICATION_JSON;
        if (application_JSON2 != null) {
                // Creamos una release sin builds
                mockMvc.perform(post("/releases")
                                .contentType(application_JSON2)
                                .content("""
                                        {
                                          "id": "R-MOCK-EMPTY",
                                          "name": "Empty Release"
                                        }
                                        """))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id").value("R-MOCK-EMPTY"))
                        .andExpect(jsonPath("$.status").value("DRAFT"));
        } else {
                // TODO handle null value
        }

        /*
         * Intentamos prepararla.
         *
         * El dominio debería lanzar IllegalStateException.
         * Nuestro GlobalExceptionHandler debería convertir eso en:
         *
         * 400 Bad Request
         */
        mockMvc.perform(post("/releases/R-MOCK-EMPTY/prepare"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Release must contain at least one build"));
    }

    @Test
    void shouldRejectAddingUnknownBuildToRelease() throws Exception {

        /*
         * Este test comprueba que no podemos añadir a una release
         * una build que no existe.
         *
         * Es importante porque ReleaseService conecta Release con BuildService.
         */

        final MediaType application_JSON2 = MediaType.APPLICATION_JSON;
        if (application_JSON2 != null) {
                // Creamos una release válida
                mockMvc.perform(post("/releases")
                                .contentType(application_JSON2)
                                .content("""
                                        {
                                          "id": "R-MOCK-UNKNOWN-BUILD",
                                          "name": "Release With Unknown Build"
                                        }
                                        """))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id").value("R-MOCK-UNKNOWN-BUILD"))
                        .andExpect(jsonPath("$.status").value("DRAFT"));
        } else {
                // TODO handle null value
        }

        /*
         * Intentamos añadir una build inexistente.
         *
         * Como B-UNKNOWN no existe, BuildService debería lanzar
         * BuildNotFoundException.
         *
         * GlobalExceptionHandler debería convertirlo en:
         *
         * 404 Not Found
         */
        mockMvc.perform(post("/releases/R-MOCK-UNKNOWN-BUILD/builds/B-UNKNOWN"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Build with ID B-UNKNOWN not found."));
    }
}