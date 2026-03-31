package info.touret.musicstore.application.resource;

import info.touret.musicstore.application.data.*;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.hamcrest.core.Is;
import org.hamcrest.text.MatchesPattern;
import org.junit.jupiter.api.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/// Integration tests for the OrderResource REST API endpoints.
/// Tests verify all CRUD operations and error handling using RestAssured.
/// Tests follow the @Order pattern for sequential execution and data dependencies.
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@QuarkusTest
class OrderResourceTest {

    private OrderDto orderDto;
    private OrderDto orderCreation;
    private CustomerDto customerDto;
    private AddressDto addressDto;
    private InstrumentDto instrumentDto;

    @BeforeEach
    void setUp() {
        // Setup test data matching the import.sql test data
        addressDto = new AddressDto("10", "Rue de Paris", "Paris", "75001", "France");
        customerDto = new CustomerDto(null, "Alice", "Smith", "alice@test.com", addressDto);
        instrumentDto = new InstrumentDto(null, "Stratocaster", "FEN-STR-01", "Fender", 1200D, "Classic Stratocaster", InstrumentTypeDto.GUITAR);
        
        orderDto = new OrderDto(
            List.of(instrumentDto),
            null, // Use existing order from test data
            UUID.fromString("a0000000-0000-0000-0000-000000000001"),
            ZonedDateTime.now(),
            customerDto,
            OrderStatusDto.CREATED
        );
        
        orderCreation = new OrderDto(
            List.of(instrumentDto),
            null, // No ID for creation
            UUID.randomUUID(),
            ZonedDateTime.now(),
            customerDto,
            OrderStatusDto.CREATED
        );
    }

    /// Test 1: Retrieve all orders successfully.
    /// Expects a 200 response with a non-empty list.
    @Order(1)
    @Test
    void should_get_a_list_of_orders() {
        RestAssured.given()
            .get("/orders")
            .then()
            .statusCode(200)
            .assertThat().body("isEmpty()", Is.is(false));
    }

    /// Test 2: Retrieve a specific order by ID.
    /// Expects a 200 response with valid order data for order ID 1.
    @Order(2)
    @Test
    void should_get_an_order_by_id() {
        RestAssured.given()
            .when()
            .get("/orders/1")
            .then()
            .statusCode(200)
            .assertThat().body("id", Is.is(1));
    }

    /// Test 3: Create a new order successfully.
    /// Expects a 201 response with the generated order reference (UUID format).
    @Order(3)
    @Test
    void should_create_an_order_successfully() {
        RestAssured.given()
            .header("Content-Type", "application/json")
            .and()
            .body(orderCreation)
            .when()
            .post("/orders")
            .then()
            .statusCode(201)
            .assertThat().body("id", MatchesPattern.matchesPattern("[0-9a-f-]*"));
    }

    /// Test 4: Update an existing order successfully.
    /// Expects a 200 response with the updated order data.
    @Order(4)
    @Test
    void should_update_an_order_successfully() {
        var orderToUpdate = new OrderDto(
            List.of(instrumentDto),
            2L,
            UUID.fromString("a0000000-0000-0000-0000-000000000002"),
            ZonedDateTime.now(),
            customerDto,
            OrderStatusDto.PAID
        );
        
        RestAssured.given()
            .header("Content-Type", "application/json")
            .and()
            .body(orderToUpdate)
            .when()
            .put("/orders/2")
            .then()
            .statusCode(200);
    }

    /// Test 5: Delete an existing order successfully.
    /// Expects a 204 No Content response.
    @Order(5)
    @Test
    void should_delete_an_order_successfully() {
        RestAssured.given()
            .when()
            .delete("/orders/2")
            .then()
            .statusCode(204);
    }

    /// Test 6: Fail updating an order with mismatched IDs.
    /// Path ID doesn't match body ID - expects 400 Bad Request.
    @Order(6)
    @Test
    void should_fail_updating_an_order_with_mismatched_ids() {
        var orderToUpdate = new OrderDto(
            List.of(instrumentDto),
            3L,
            UUID.fromString("a0000000-0000-0000-0000-000000000003"),
            ZonedDateTime.now(),
            customerDto,
            OrderStatusDto.PAID
        );
        
        RestAssured.given()
            .header("Content-Type", "application/json")
            .and()
            .body(orderToUpdate)
            .when()
            .put("/orders/999") // ID mismatch: path has 999, body has 3
            .then()
            .statusCode(400);
    }

    /// Test 7: Fail deleting a non-existent order.
    /// Expects 404 Not Found response.
    @Order(7)
    @Test
    void should_fail_deleting_a_missing_order() {
        RestAssured.given()
            .when()
            .delete("/orders/9999")
            .then()
            .statusCode(404);
    }

    /// Test 8: Search for orders by customer first name.
    /// Expects a 200 response with matching orders.
    @Test
    void should_search_orders_successfully() {
        RestAssured.given()
            .get("/orders/search?q=Alice")
            .then()
            .statusCode(200)
            .assertThat().body("isEmpty()", Is.is(false));
    }

    /// Test 9: Fail searching with empty query.
    /// Expects 400 Bad Request response.
    @Test
    void should_fail_searching_with_empty_query() {
        RestAssured.given()
            .get("/orders/search?q=")
            .then()
            .statusCode(400);
    }

}
