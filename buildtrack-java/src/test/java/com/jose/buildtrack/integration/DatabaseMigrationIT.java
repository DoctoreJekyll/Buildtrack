package com.jose.buildtrack.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

/**
 * Prueba de integración de la infraestructura de base de datos.
 *
 * Esta prueba no utiliza H2.
 * Arranca la aplicación contra PostgreSQL y comprueba que:
 *
 * - Flyway ejecuta las migraciones.
 * - Hibernate valida correctamente el esquema.
 * - La aplicación puede cargar el contexto completo.
 */
@SpringBootTest
@ActiveProfiles("integration")
class DatabaseMigrationIT {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldApplyAllFlywayMigrations() {

        /*
         * Consultamos cuál es la última migración aplicada correctamente.
         *
         * Si Flyway ejecutó V1 y V2, la versión más reciente debe ser 2.
         */
        String latestVersion = jdbcTemplate.queryForObject(
                """
                SELECT version
                FROM flyway_schema_history
                WHERE success = true
                  AND version IS NOT NULL
                ORDER BY installed_rank DESC
                LIMIT 1
                """,
                String.class
        );

        assertEquals("3", latestVersion);
    }

    @Test
    void shouldCreateApplicationTables() {

        /*
         * Comprobamos que existen las cuatro tablas de negocio
         * creadas mediante las migraciones.
         */
        Integer tableCount = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM information_schema.tables
                WHERE table_schema = 'public'
                  AND table_name IN (
                      'builds',
                      'issues',
                      'releases',
                      'release_builds'
                  )
                """,
                Integer.class
        );

        assertEquals(4, tableCount);
    }
}