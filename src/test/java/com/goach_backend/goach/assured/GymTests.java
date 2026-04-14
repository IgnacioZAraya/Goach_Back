package com.goach_backend.goach.assured;

import static io.restassured.RestAssured.*;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.server.LocalServerPort;


import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GymTests {

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
    // Test 1: Envía un request a la direccion del id del ejercicio.
    // Valida el código 200 y que la respuesta contenga el gimnasio pedido.
    // ---------------------------------------------------------------
    @Test
    public void testGetGym_Successful() {
        String gymId = "368ca9f8-13f1-4b6e-9c48-4252c00a22b0";

        String body = given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/gym/{gymId}", gymId)
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();
        System.out.println(body);
    }

    // ---------------------------------------------------------------
    // Test 2: Envía un request a la direccion del id de un gimnasio que no existe.
    // Valida el código con un error 500 debido a que es invalido.
    // ---------------------------------------------------------------
    @Test
    public void testGetGym_InvalidGym() {
        String gymId = "...";

        String body = given().log().all()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/gym/{gymId}", gymId)
                .then().log().all()
                .statusCode(500)
                .extract()
                .body()
                .asString();
        System.out.println(body);
    }

    // ---------------------------------------------------------------
    // Test 3: Envía un request a la direccion global de gimasios
    // Valida el código 200 y la respuesta contendra todos los gimnasios existentes.
    // ---------------------------------------------------------------
    @Test
    public void testGetAllGyms_Successful() {
        String body = given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/gym")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();
        System.out.println(body);
    }

    // ---------------------------------------------------------------
    // Test 4: Envía un JSON con ALGUNOS datos obligatorios (nombre del gimnasio).
    // Valida el código con un error 400 ya que falta el id del Owner.
    // ---------------------------------------------------------------
    @Test
    public void testCreateGym_OwnerNotSpecified() {
        String requestBody = """
                {
                    "name":"WaluiGyms"
                }
                """;
        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/gym")
                .then()
                .statusCode(400);
    }

    // ---------------------------------------------------------------
    // Test 5: Envía un JSON con los datos obligatorios de un gimnasio (nombre del gimnasio, id del owner).
    // Valida el código 201 Created y que la respuesta contenga los valores enviados.
    // ---------------------------------------------------------------
    @Test
    public void testCreateGym_Successful() {
        String requestBody = """
                {
                    "name":"WaluiGym",
                    "owner": {"id": "70125a08-9693-4873-89ec-1ecd7b0f595e"}
                }
                """;
        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/gym")
                .then()
                .statusCode(201)
                .body("name", equalTo("WaluiGym"));
    }

}
