package info.touret.musicstore.infrastructure.database.adapter;

import info.touret.musicstore.domain.exception.DataNotFoundException;
import info.touret.musicstore.domain.model.Address;
import info.touret.musicstore.domain.model.Customer;
import info.touret.musicstore.domain.model.Order;
import info.touret.musicstore.domain.model.OrderStatus;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@QuarkusTest
class OrderAdapterTest {

    @Inject
    OrderAdapter orderAdapter;

    private Order order;

    @BeforeEach
    void setUp() {
        Customer customer = new Customer(null, "John", "Doe", "john.doe@example.com",
                new Address("123", "Main St", "Paris", "75000", "France"));
        order = new Order(new ArrayList<>(), null, UUID.randomUUID(), ZonedDateTime.now(), customer, OrderStatus.CREATED);
    }

    @org.junit.jupiter.api.Order(1)
    @Test
    void should_find_all_successfully() {
        assertNotEquals(0, orderAdapter.findAll().size());
    }

    @org.junit.jupiter.api.Order(2)
    @Test
    void should_create_successfully() {
        assertNotNull(orderAdapter.create(order).id());
    }

    @org.junit.jupiter.api.Order(3)
    @Test
    void should_update_successfully() {
        var orderInserted = orderAdapter.create(order);
        var updatedOrder = new Order(orderInserted.instruments(), orderInserted.id(), orderInserted.reference(),
                ZonedDateTime.now().plusDays(1), orderInserted.customer(), OrderStatus.CREATED);

        var result = orderAdapter.update(updatedOrder);
        assertEquals(updatedOrder.orderDate().toEpochSecond(), result.orderDate().toEpochSecond());
    }

    @org.junit.jupiter.api.Order(6)
    @Test
    void should_update_with_incorrect_values_failed() {
        var orderInserted = orderAdapter.create(order);
        var incorrectOrder = new Order(null, orderInserted.id(), null, null, null, OrderStatus.CREATED);
        assertThrows(ConstraintViolationException.class, () -> orderAdapter.update(incorrectOrder));
    }

    @Test
    void should_create_with_invalid_values_failed() {
        var incorrectOrder = new Order(null, null, null, null, null, OrderStatus.CREATED);
        assertThrows(ConstraintViolationException.class, () -> orderAdapter.create(incorrectOrder));
    }

    @org.junit.jupiter.api.Order(4)
    @Test
    void should_delete_successfully() {
        var orderInserted = orderAdapter.create(order);
        var count = orderAdapter.findAll().size();
        assertTrue(orderAdapter.delete(orderInserted));
        assertEquals(count - 1, orderAdapter.findAll().size());
    }

    @org.junit.jupiter.api.Order(5)
    @Test
    void should_delete_failed() {
        var orderToBeDeleted = new Order(new ArrayList<>(), 400L, UUID.randomUUID(), ZonedDateTime.now(), null, OrderStatus.CREATED);
        assertFalse(orderAdapter.delete(orderToBeDeleted));
    }

    @Test
    void should_findById_failed() {
        assertThrows(DataNotFoundException.class, () -> orderAdapter.findById(600L));
    }

    @Test
    void should_search_failed_with_null_query() {
        assertThrows(NullPointerException.class, () -> orderAdapter.search(null));
    }
}
