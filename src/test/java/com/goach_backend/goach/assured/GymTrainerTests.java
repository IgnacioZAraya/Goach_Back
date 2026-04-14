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
public class GymTrainerTests {

    @LocalServerPort
    private int port;

    private String token;

    private static final String GYM_ID = "368Ca9f8-13f1-4b6e-9c48-4252c00a22b0";
    private static final String TRAINER_ID = "70125a08-9693-4873-89ec-1ecd7b0f595e";

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
    // Test 1: Envía gymId y trainerId válidos con token de autenticación.
    // Valida el código 200 OK y que la respuesta contenga los datos del entrenador asociado.
    // ---------------------------------------------------------------
    @Test
    public void getGymTrainer_WhenBothIdsAreValid_ShouldReturn200() {
        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .when()
                .get("/gyms/{gymId}/trainers/{trainerId}", GYM_ID, TRAINER_ID)
                .then()
                .statusCode(200)
                .body("associateStatus", notNullValue())
                .body("gymPaymentStatus", notNullValue())
                .body("trainer", notNullValue())
                .body("trainer.id", equalToIgnoringCase(TRAINER_ID));
    }

    // ---------------------------------------------------------------
    // Test 2: Envía un trainerId inexistente junto con un gymId válido.
    // Valida que el sistema retorne un código de error al no encontrar el entrenador.
    // ---------------------------------------------------------------
    @Test
    public void getGymTrainer_WhenTrainerIdDoesNotExist_ShouldReturnError() {
        String fakeTrainerId = UUID.randomUUID().toString();

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .when()
                .get("/gyms/{gymId}/trainers/{trainerId}", GYM_ID, fakeTrainerId)
                .then()
                .statusCode(500);
    }

    // ---------------------------------------------------------------
    // Test 3: Envía un gymId inexistente junto con un trainerId válido.
    // Valida que el sistema retorne un código de error al no encontrar el gimnasio.
    // ---------------------------------------------------------------
    @Test
    public void getGymTrainer_WhenGymIdDoesNotExist_ShouldReturnError() {
        String fakeGymId = UUID.randomUUID().toString();

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .when()
                .get("/gyms/{gymId}/trainers/{trainerId}", fakeGymId, TRAINER_ID)
                .then()
                .statusCode(500);
    }

    // ---------------------------------------------------------------
    // Test 4: Envía un gymId con formato inválido junto con un trainerId válido.
    // Valida que el sistema retorne un código de error al recibir un UUID incorrecto.
    // ---------------------------------------------------------------
    @Test
    public void getGymTrainer_WhenTrainerIdHasInvalidFormat_ShouldReturnError() {
        String invalidTrainerId = "esto-no-es-un-uuid-valido";

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .when()
                .get("/gyms/{gymId}/trainers/{trainerId}", GYM_ID, invalidTrainerId)
                .then()
                .statusCode(anyOf(equalTo(400), equalTo(500)));
    }

    // ---------------------------------------------------------------
    // Test 5: Envía gymId y trainerId válidos y verifica la estructura del JSON.
    // Valida que la respuesta contenga todos los campos importantes del GymTrainer.
    // ---------------------------------------------------------------
    @Test
    public void getGymTrainer_WhenValid_ShouldReturnAllExpectedFields() {
        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .when()
                .get("/gyms/{gymId}/trainers/{trainerId}", GYM_ID, TRAINER_ID)
                .then()
                .statusCode(200)
                .body("associateStatus", notNullValue())
                .body("gymPaymentStatus", notNullValue())
                .body("gymPaymentPrice", notNullValue())
                .body("gymPaymentDate", notNullValue())
                .body("createdAt", notNullValue())
                .body("updatedAt", notNullValue());
    }
}