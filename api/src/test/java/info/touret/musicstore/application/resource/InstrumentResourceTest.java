package info.touret.musicstore.application.resource;

import info.touret.musicstore.application.data.InstrumentDto;
import info.touret.musicstore.application.data.InstrumentTypeDto;
import info.touret.musicstore.application.data.UserDto;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.vertx.core.json.JsonObject;
import org.hamcrest.core.Is;
import org.hamcrest.text.MatchesPattern;
import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@QuarkusTest
class InstrumentResourceTest {

    private InstrumentDto instrumentCreation;
    private Header userHeader;

    @BeforeEach
    void setUp() {
        instrumentCreation = new InstrumentDto(null, "Gibson Les Paul", "gibson-les-paul", "Gibson", 2000D, "Jimmy Page's guitar", InstrumentTypeDto.GUITAR);
        userHeader = new Header("User", JsonObject.mapFrom(new UserDto("John", "Doe", "john.doe@gmail.com", "France")).encode());
    }

    @Order(1)
    @Test
    void should_get_a_list_of_instruments() {
        RestAssured.given()
                .header(userHeader)
                .get("/instruments")
                .then()
                .statusCode(200)
                .assertThat().body("isEmpty()", Is.is(false));
    }

    @Test
    void should_get_a_list_of_instruments_without_header_failed() {
        RestAssured.given()
                .get("/instruments")
                .then()
                .statusCode(400);
    }

    @Order(2)
    @Test
    void should_create_an_instrument_successfully() {

        RestAssured.given()
                .header("Content-Type", "application/json")
                .header(userHeader)
                .and()
                .body(instrumentCreation)
                .when()
                .post("/instruments")
                .then()
                .statusCode(201)
                .assertThat().body("instrumentId", MatchesPattern.matchesPattern("[0-9a-zA-Z-]*"));
    }

    @Order(3)
    @Test
    void should_update_an_instrument_successfully() {
        var instrumentToBeUpdated = new InstrumentDto(100L, "Stratocaster", "FEN-STR-01", "Fender", 1200.0, "Classic Stratocaster", InstrumentTypeDto.GUITAR);
        RestAssured.given()
                .header("Content-Type", "application/json")
                .header(userHeader)
                .and()
                .body(instrumentToBeUpdated)
                .when()
                .put("/instruments/100")
                .then()
                .statusCode(200);
    }

    @Order(4)
    @Test
    void should_delete_an_instrument_successfully() {
        RestAssured.given()
                .header(userHeader)
                .when()
                .delete("/instruments/100")
                .then()
                .statusCode(204);
    }

    @Order(5)
    @Test
    void should_fail_updating_an_invalid_instrument() {
        var instrumentToBeUpdated = new InstrumentDto(100L, "Stratocaster", "FEN-STR-01", "Fender", 1200.0, "Classic Stratocaster", InstrumentTypeDto.GUITAR);
        RestAssured.given()
                .header("Content-Type", "application/json")
                .header(userHeader)
                .and()
                .body(instrumentToBeUpdated)
                .when()
                .put("/instruments/110")
                .then()
                .statusCode(400);
    }

    @Order(6)
    @Test
    void should_fail_updating_a_missing_instrument() {
        var instrumentToBeUpdated = new InstrumentDto(300L, "Stratocaster", "FEN-STR-01", "Fender", 1200.0, "Classic Stratocaster", InstrumentTypeDto.GUITAR);
        RestAssured.given()
                .header("Content-Type", "application/json")
                .header(userHeader)
                .and()
                .body(instrumentToBeUpdated)
                .when()
                .put("/instruments/300")
                .then()
                .statusCode(400);
    }


    @Order(7)
    @Test
    void should_fail_deleting_a_missing_instrument() {
        RestAssured.given()
                .header("Content-Type", "application/json")
                .header(userHeader)
                .when()
                .delete("/instruments/300")
                .then()
                .statusCode(404);
    }


    @Test
    void should_search_successfully() {
        RestAssured.given()
                .header(userHeader)
                .get("/instruments/search?q=Fender")
                .then()
                .statusCode(200)
                .assertThat().body("isEmpty()", Is.is(false));
    }

    @Test
    void should_fail_searching() {
        RestAssured.given()
                .header(userHeader)
                .get("/instruments/search?q=")
                .then()
                .statusCode(400)
                .assertThat().body("isEmpty()", Is.is(false));
    }
}
