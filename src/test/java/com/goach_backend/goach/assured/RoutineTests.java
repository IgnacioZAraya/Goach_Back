package com.goach_backend.goach.assured;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RoutineTests {

    @LocalServerPort
    private int port;

    private String trainerToken;
    private String traineeToken;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;

        String trainerloginBody = """
                {
                	"email": "izeladaa@ucenfotec.ac.cr",
                	"password": "chitou123"
                }
                """;

        trainerToken = given()
                .contentType(ContentType.JSON)
                .body(trainerloginBody)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .path("token");

        String traineeLoginBody = """
                {
                    "email": "meganacho080@gmail.com",
                    "password": "Pokemon.2012"
                }
                """;
        traineeToken = given()
                .contentType(ContentType.JSON)
                .body(traineeLoginBody)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }

    // ---------------------------------------------------------------
    // Test 1: Simula a un entrenador (Trainer) enviando datos válidos.
    // Verifica que el sistema responda con un código 201 Created,
    // confirmando que la rutina se guardó correctamente.
    // ---------------------------------------------------------------
    @Test
    public void createRoutine_WhenValidData_ShouldReturn201() {
        String realTrainerId = "70125a08-9693-4873-89ec-1ecd7b0f595e";

        String requestBody = """
                {
                    "name": "Rutina Cardio",
                    "trainer": {"id": "%s"}
                }
                """.formatted(realTrainerId);

        given()
                .header("Authorization", "Bearer " + trainerToken)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/routine")
                .then()
                .statusCode(201);
    }

    // ---------------------------------------------------------------
    // Test 2: Se envía el ID de un entrenador generado aleatoriamente (que no existe en BD).
    // Verifica que la API rechace la petición con un 404 Not Found y un mensaje de error claro,
    // evitando inconsistencias en los datos.
    // ---------------------------------------------------------------
    @Test
    public void createRoutine_WhenTrainerDoesNotExist_ShouldReturn404() {
        String fakeUserId = UUID.randomUUID().toString();

        String requestBody = """
                {
                    "name": "Rutina",
                    "trainer": {"id": "%s"}
                }
                """.formatted(fakeUserId);

        given()
                .header("Authorization", "Bearer " + trainerToken)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/routine")
                .then()
                .statusCode(404)
                .body("error", equalTo("User not found or it is not a trainer"));
    }

    // ---------------------------------------------------------------
    // Test 3: Se intenta asignar una rutina a un ID que pertenece a un usuario regular (Trainee).
    // Verifica que el sistema valide el rol a nivel de negocio y rechace la operación.
    // --------------------------------------------------------------
    @Test
    public void createRoutine_WhenUserIsNotTrainer_ShouldReturn404() {
        String realTraineeId = "5f7c0674-0327-47f4-b15e-b0b56faf46a9";

        String requestBody = """
                {
                    "name": "Rutina",
                    "trainer": {"id": "%s"}
                }
                """.formatted(realTraineeId);

        given()
                .header("Authorization", "Bearer " + trainerToken)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/routine")
                .then()
                .statusCode(404)
                .body("error", equalTo("User not found or it is not a trainer"));
    }

    // ---------------------------------------------------------------
    // Test 4: Se ejecuta la petición utilizando el token JWT de un usuario con rol Trainee.
    // Valida que Spring Security bloquee la solicitud antes de procesarla,
    // devolviendo un 403 Forbidden.
    // ---------------------------------------------------------------
    @Test
    public void createRoutine_WhenUserLacksPermissions_ShouldReturn403() {
        String someTrainerId = "5991fe04-162e-4d68-914a-e6e8fe5bdcff";

        String requestBody = """
                {
                    "name": "Mi propia rutina",
                    "trainer": {"id": "%s"}
                }
                """.formatted(someTrainerId);

        given()
                .header("Authorization", "Bearer " + traineeToken)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/routine")
                .then()
                .statusCode(403);
    }
}
