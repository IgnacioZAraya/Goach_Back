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
public class ExerciseTests {
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
    // Test 1: Envía un JSON con los datos obligatorios de un ejercicio (nombre del ejercicio, grupo muscular, y su descripcion) a la direccion del ejercicio a editar.
    // Valida el código 201 Updated y que la respuesta contenga los valores enviados.
    // ---------------------------------------------------------------
    @Test
    public void testUpdateExercise_Successful() {
        String exerciseId = "317790ce-c598-4d19-b8f3-27ac85aa8f0a";

        String requestBody = """
                {
                    "name":"SUPERMAN RETURNS",
                    "muscleGroup": "FRONT_DELTOIDS",
                    "description": "Destroy your upper body like the 1930's Superman destroyed cars!"
                }
                """;
        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("/exercise/{exerciseId}", exerciseId)
                .then()
                .statusCode(201)
                .body("name", equalTo("SUPERMAN RETURNS"))
                .body("muscleGroup", equalTo("FRONT_DELTOIDS"))
                .body("description", equalTo("Destroy your upper body like the 1930's Superman destroyed cars!"));
    }

    // ---------------------------------------------------------------
    // Test 2: Envía un JSON con los datos obligatorios de un ejercicio (nombre del ejercicio, grupo muscular, y su descripcion) a la direccion del ejercicio a editar, mas el grupo muscular es inexistente.
    // Valida el código con un error 500 ya que el grupo muscular es invalido.
    // ---------------------------------------------------------------
    @Test
    public void testUpdateExercise_InvalidMuscleGroup() {
        String exerciseId = "317790ce-c598-4d19-b8f3-27ac85aa8f0a";

        String requestBody = """
                {
                    "name":"SUPERMAN RETURNS",
                    "muscleGroup": "WHATEVER_YOU_WANT, LITERALLY",
                    "description": "Destroy your upper body like the 1930's Superman destroyed cars!"
                }
                """;
        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("/exercise/{exerciseId}", exerciseId)
                .then()
                .statusCode(500);
    }

    // ---------------------------------------------------------------
    // Test 3: Envía un request a la direccion del id de un ejercicio que no existe.
    // Valida el código con un error 500 debido a que es invalido.
    // ---------------------------------------------------------------
    @Test
    public void testUpdateExercise_InvalidExercise() {
        String exerciseId = UUID.randomUUID().toString();

        String requestBody = """
                {
                    "name":"SUPERMAN RETURNS",
                    "muscleGroup": "FRONT_DELTOIDS",
                    "description": "Destroy your upper body like the 1930's Superman destroyed cars!"
                }
                """;
        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("/exercise/{exerciseId}", exerciseId)
                .then()
                .statusCode(500);
    }
}
