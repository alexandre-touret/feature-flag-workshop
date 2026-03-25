package info.touret.musicstore.application.resource;

import info.touret.musicstore.application.data.InstrumentDto;
import info.touret.musicstore.application.data.InstrumentTypeDto;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.hamcrest.core.Is;
import org.hamcrest.text.MatchesPattern;
import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@QuarkusTest
class InstrumentResourceTest {

    private InstrumentDto instrumentDto;
    private InstrumentDto instrumentCreation;

    @BeforeEach
    void setUp() {
        instrumentDto = new InstrumentDto(2000L, "Gibson Les Paul", "gibson-les-paul", "Gibson", 2000D, "Jimmy Page's guitar", InstrumentTypeDto.GUITAR);
        instrumentCreation = new InstrumentDto(null, "Gibson Les Paul", "gibson-les-paul", "Gibson", 2000D, "Jimmy Page's guitar", InstrumentTypeDto.GUITAR);
    }

    @Order(1)
    @Test
    void should_get_a_list_of_instruments() {
        RestAssured.given().get("/instruments")
                .then()
                .statusCode(200)
                .assertThat().body("isEmpty()", Is.is(false));
    }


    @Order(2)
    @Test
    void should_create_an_instrument_successfully() {

        RestAssured.given()
                .header("Content-Type", "application/json")
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
        var instrumentToBeDeleted = new InstrumentDto(100L, "Stratocaster", "FEN-STR-01", "Fender", 1200.0, "Classic Stratocaster", InstrumentTypeDto.GUITAR);
        RestAssured.given()
                .header("Content-Type", "application/json")
                .and()
                .body(instrumentToBeDeleted)
                .when()
                .put("/instruments/100")
                .then()
                .statusCode(200);
    }

    @Order(4)
    @Test
    void should_delete_an_instrument_successfully() {
        RestAssured.given()
                .when()
                .delete("/instruments/100")
                .then()
                .statusCode(204);
    }

    @Order(5)
    @Test
    void should_fail_updating_an_invalid_instrument(){
        var instrumentToBeDeleted = new InstrumentDto(100L, "Stratocaster", "FEN-STR-01", "Fender", 1200.0, "Classic Stratocaster", InstrumentTypeDto.GUITAR);
        RestAssured.given()
                .header("Content-Type", "application/json")
                .and()
                .body(instrumentToBeDeleted)
                .when()
                .put("/instruments/110")
                .then()
                .statusCode(400);
    }
    @Order(6)
    @Test
    void should_fail_updating_a_missing_instrument(){
        var instrumentToBeDeleted = new InstrumentDto(300L, "Stratocaster", "FEN-STR-01", "Fender", 1200.0, "Classic Stratocaster", InstrumentTypeDto.GUITAR);
        RestAssured.given()
                .header("Content-Type", "application/json")
                .and()
                .body(instrumentToBeDeleted)
                .when()
                .put("/instruments/300")
                .then()
                .statusCode(400);
    }
}


