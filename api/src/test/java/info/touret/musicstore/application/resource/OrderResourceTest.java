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

    /// Test 10: Modify an existing instrument in an order.
    /// Changes the name and price of an instrument within order 10.
    @Order(10)
    @Test
    void should_modify_instrument_in_order_successfully() {
        var modifiedInstrument = new InstrumentDto(10L, "Firebird Modified", "GIB-FIR-01", "Gibson", 2500.0, "Modified description", InstrumentTypeDto.GUITAR);
        var originalInstrument60 = new InstrumentDto(60L, "Rhoads RRX24", "JAC-RHO-01", "Jackson", 800.0, "Rhoads Electric", InstrumentTypeDto.GUITAR);

        var orderToUpdate = new OrderDto(
                List.of(modifiedInstrument, originalInstrument60),
                10L,
                UUID.fromString("a0000000-0000-0000-0000-000000000010"),
                ZonedDateTime.now(),
                new CustomerDto(10L, "Judy", "Martinez", "judy@test.com", addressDto),
                OrderStatusDto.CREATED
        );

        RestAssured.given()
                .header("Content-Type", "application/json")
                .body(orderToUpdate)
                .when()
                .put("/orders/10")
                .then()
                .statusCode(200)
                .assertThat().body("instruments[0].name", Is.is("Firebird Modified"))
                .assertThat().body("instruments[0].price", Is.is(2500.0f));
    }

    /// Test 11: Add a new instrument to an existing order.
    /// Adds instrument 1 to order 10's instrument list.
    @Order(11)
    @Test
    void should_add_instrument_to_order_successfully() {
        var inst1 = new InstrumentDto(1L, "Stratocaster", "FEN-STR-01", "Fender", 1200.0, "Classic Stratocaster", InstrumentTypeDto.GUITAR);
        var inst10 = new InstrumentDto(10L, "Firebird Modified", "GIB-FIR-01", "Gibson", 2500.0, "Modified description", InstrumentTypeDto.GUITAR);
        var inst60 = new InstrumentDto(60L, "Rhoads RRX24", "JAC-RHO-01", "Jackson", 800.0, "Rhoads Electric", InstrumentTypeDto.GUITAR);

        var orderToUpdate = new OrderDto(
                List.of(inst1, inst10, inst60),
                10L,
                UUID.fromString("a0000000-0000-0000-0000-000000000010"),
                ZonedDateTime.now(),
                new CustomerDto(10L, "Judy", "Martinez", "judy@test.com", addressDto),
                OrderStatusDto.CREATED
        );

        RestAssured.given()
                .header("Content-Type", "application/json")
                .body(orderToUpdate)
                .when()
                .put("/orders/10")
                .then()
                .statusCode(200)
                .assertThat().body("instruments.size()", Is.is(3));
    }

    /// Test 12: Modify customer details in an order.
    /// Updates the customer's lastname and email for order 10.
    @Order(12)
    @Test
    void should_modify_customer_in_order_successfully() {
        var modifiedCustomer = new CustomerDto(10L, "Judy", "Updated", "judy.updated@test.com", addressDto);
        var inst1 = new InstrumentDto(1L, "Stratocaster", "FEN-STR-01", "Fender", 1200.0, "Classic Stratocaster", InstrumentTypeDto.GUITAR);

        var orderToUpdate = new OrderDto(
                List.of(inst1),
                10L,
                UUID.fromString("a0000000-0000-0000-0000-000000000010"),
                ZonedDateTime.now(),
                modifiedCustomer,
                OrderStatusDto.CREATED
        );

        RestAssured.given()
                .header("Content-Type", "application/json")
                .body(orderToUpdate)
                .when()
                .put("/orders/10")
                .then()
                .statusCode(200)
                .assertThat().body("customer.lastname", Is.is("Updated"))
                .assertThat().body("customer.email", Is.is("judy.updated@test.com"));
    }

    /// Test 13: Fail when order data is invalid (empty or null fields).
    /// Expects 400 Bad Request due to validation constraints.
    @Order(13)
    @Test
    void should_fail_when_order_data_is_invalid() {
        // Case 1: Empty customer firstname
        var invalidCustomer = new CustomerDto(10L, "", "Martinez", "judy@test.com", addressDto);
        var inst1 = new InstrumentDto(1L, "Stratocaster", "FEN-STR-01", "Fender", 1200.0, "Classic Stratocaster", InstrumentTypeDto.GUITAR);

        var invalidOrder = new OrderDto(
                List.of(inst1),
                10L,
                UUID.fromString("a0000000-0000-0000-0000-000000000010"),
                ZonedDateTime.now(),
                invalidCustomer,
                OrderStatusDto.CREATED
        );

        RestAssured.given()
                .header("Content-Type", "application/json")
                .body(invalidOrder)
                .when()
                .put("/orders/10")
                .then()
                .statusCode(400);

        // Case 2: Null customer
        var nullCustomerOrder = new OrderDto(
                List.of(inst1),
                10L,
                UUID.fromString("a0000000-0000-0000-0000-000000000010"),
                ZonedDateTime.now(),
                null,
                OrderStatusDto.CREATED
        );

        RestAssured.given()
                .header("Content-Type", "application/json")
                .body(nullCustomerOrder)
                .when()
                .put("/orders/10")
                .then()
                .statusCode(400);
    }
}
