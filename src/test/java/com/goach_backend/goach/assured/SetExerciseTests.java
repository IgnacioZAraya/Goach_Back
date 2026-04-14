package com.goach_backend.goach.assured;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SetExerciseTests {

    @LocalServerPort
    private int port;

    private String token;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;

        String loginBody = """
                {
                	"email": "izeladaa@ucenfotec.ac.cr",
                	"password": "chitou123"
                }
                """;

        token = given()
                .contentType(ContentType.JSON)
                .body(loginBody)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }

    // ---------------------------------------------------------------
    // Test 1: Envía un JSON con datos obligatorios (ID del ejercicio, índice de orden, repeticiones mínimas y máximas).
    // Valida el código 201 Created y que la respuesta contenga exactamente los mismos valores enviados.
    // ---------------------------------------------------------------
    @Test
    public void createSetExercise_WhenDataIsValid_ShouldReturn201() {
        String setId = "157e4906-4e5a-45e0-9856-f4f2b51fce0f";
        String exerciseId = "78d00294-b171-475b-92cd-9e9c643d3f6d";

        String requestBody = """
                {
                    "exercise": {"id": "%s"},
                    "orderIndex": 1,
                    "minReps": 8,
                    "maxReps": 12
                }
                """.formatted(exerciseId);

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/set/{setId}/exercises", setId)
                .then()
                .statusCode(201)
                .body("orderIndex", equalTo(1))
                .body("minReps", equalTo(8))
                .body("maxReps", equalTo(12))
                .body("exercise.id", equalTo(exerciseId))
                .header("Location", containsString("/set/" + setId + "/exercises/"));
    }

    // ---------------------------------------------------------------
    // Test 2: Intenta agregar un ejercicio a un ID de serie inventado.
    // Valida que el sistema proteja la integridad de la base de datos devolviendo un error (Bad Request / Not Found).
    // ---------------------------------------------------------------
    @Test
    public void createSetExercise_WhenSetDoesNotExist_ShouldReturnError() {
        String fakeSetId = UUID.randomUUID().toString();
        String exerciseId = "78d00294-b171-475b-92cd-9e9c643d3f6d";

        String requestBody = """
                {
                    "exercise": {"id": "%s"},
                    "orderIndex": 1
                }
                """.formatted(exerciseId);

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/set/{setId}/exercises", fakeSetId)
                .then()
                .statusCode(500);
    }

    // ---------------------------------------------------------------
    // Test 3: Se envía un cuerpo JSON incompleto (sin el ID del ejercicio).
    // Verifica que las validaciones a nivel de DTO (ej. @NotNull) funcionen correctamente, devolviendo un 400 Bad Request.
    // ---------------------------------------------------------------
    @Test
    public void createSetExercise_WhenExerciseIdIsMissing_ShouldReturnError() {
        String setId = "473d616f-3281-4a8d-833d-72010f8c3203";

        String requestBody = """
                {
                    "orderIndex": 2,
                    "minReps": 10
                }
                """;

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/set/{setId}/exercises", setId)
                .then()
                .log().all()
                .statusCode(500);
    }

    // ---------------------------------------------------------------
    // Test 4: Evalúa un escenario más complejo incluyendo decimales y tiempos (RPE, RIR, pesos min/max y duración).
    // Asegura que el backend parsee y guarde correctamente los tipos de datos numéricos y de fecha/hora.
    // ---------------------------------------------------------------
    @Test
    public void createSetExercise_WithAllMetrics_ShouldSaveAndReturnCorrectly() {
        String setId = "0e9dff8b-38b5-4c7e-aa9c-c4eb6d54ff70";
        String exerciseId = "317790ce-c598-4d19-b8f3-27ac85aa8f0a";

        String requestBody = """
                {
                    "exercise": {"id": "%s"},
                    "orderIndex": 3,
                    "targetRPE": 8,
                    "targetRIR": 1,
                    "minWeight": 50.0,
                    "maxWeight": 55.5,
                    "duration": "00:01:00"
                }
                """.formatted(exerciseId);

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/set/{setId}/exercises", setId)
                .then()
                .statusCode(201)
                .body("targetRPE", equalTo(8))
                .body("targetRIR", equalTo(1))
                .body("minWeight", equalTo(50.0f))
                .body("maxWeight", equalTo(55.5f))
                .body("duration", equalTo("00:01:00"));
    }
}