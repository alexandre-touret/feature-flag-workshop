package info.touret.musicstore.infrastructure.database.adapter;

import info.touret.musicstore.application.resource.OrderResource;
import info.touret.musicstore.domain.exception.DataNotFoundException;
import info.touret.musicstore.domain.model.*;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static info.touret.musicstore.domain.model.OrderStatus.CREATED;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@QuarkusTest
class OrderAdapterTest {

    @Inject
    OrderAdapter orderAdapter;
    @Inject
    OrderResource orderResource;

    private Order order;
    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer(null, "John", "Doe", "john.doe@example.com",
                new Address("123", "Main St", "Paris", "75000", "France"));
        order = new Order(new ArrayList<>(), null, UUID.randomUUID(), ZonedDateTime.now(), customer, CREATED);
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
                ZonedDateTime.now().plusDays(1), orderInserted.customer(), CREATED);

        var result = orderAdapter.update(updatedOrder);
        assertEquals(updatedOrder.orderDate().toEpochSecond(), result.orderDate().toEpochSecond());
        assertEquals(updatedOrder.customer().id(), result.customer().id());
    }

    @org.junit.jupiter.api.Order(6)
    @Test
    void should_update_with_incorrect_values_failed() {
        var orderInserted = orderAdapter.create(order);
        var incorrectOrder = new Order(null, orderInserted.id(), null, null, null, CREATED);
        assertThrows(ConstraintViolationException.class, () -> orderAdapter.update(incorrectOrder));
    }

    @Test
    void should_create_with_invalid_values_failed() {
        var incorrectOrder = new Order(null, null, null, null, null, CREATED);
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
        var orderToBeDeleted = new Order(new ArrayList<>(), 400L, UUID.randomUUID(), ZonedDateTime.now(), null, CREATED);
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

    @Test
    void should_create_with_existing_customer_successfully() {
        var orderCreated = orderAdapter.create(order);
        var createdCustomer = orderCreated.customer();
        var id = createdCustomer.id();
        assertNotNull(id);
        var newOrder = new Order(new ArrayList<>(), null, UUID.randomUUID(), ZonedDateTime.now(), createdCustomer, CREATED);
        newOrder = orderAdapter.create(newOrder);
        assertNotNull(newOrder.id());
        assertEquals(id, newOrder.customer().id());
    }

    @Test
    void should_update_with_existing_instruments_successfully() {
        var orderWithInstruments = new Order(List.of(new Instrument(null, "Kawai K3", "Kawai K3", "Kawai", 4500D, "Acoustic Upright Piano", InstrumentType.PIANO)), null, UUID.randomUUID(), ZonedDateTime.now(), customer, CREATED);
        var orderCreated = orderAdapter.create(orderWithInstruments);
        var createdCustomer = orderCreated.customer();
        var id = createdCustomer.id();
        assertNotNull(id);
        var instruments = orderCreated.instruments();
        var createdInstrumentId = instruments.get(0).id();
        instruments.add(new Instrument(null, "Nord Piano 5", "Nord Piano 5", "Nord", 3500D, "Electric Piano", InstrumentType.PIANO));
        var newOrder = new Order(instruments, orderCreated.id(), orderCreated.reference(), orderCreated.orderDate(), createdCustomer, OrderStatus.PAID);
        newOrder = orderAdapter.update(newOrder);
        assertNotNull(newOrder.id());
        assertEquals(id, newOrder.customer().id());
        assertEquals(2, newOrder.instruments().size());
        assertEquals(createdInstrumentId, newOrder.instruments().get(0).id());
        assertTrue(newOrder.instruments().stream().allMatch(Objects::nonNull));
        assertTrue(newOrder.instruments().stream().anyMatch(i -> i.id().equals(createdInstrumentId)));
    }


    @Test
    void should_update_with_updated_instruments_successfully() {
        var orderWithInstruments = new Order(List.of(new Instrument(null, "Kawai K3", "Kawai K3", "Kawai", 4500D, "Acoustic Upright Piano", InstrumentType.PIANO)), null, UUID.randomUUID(), ZonedDateTime.now(), customer, CREATED);
        var orderCreated = orderAdapter.create(orderWithInstruments);
        var createdCustomer = orderCreated.customer();
        var id = createdCustomer.id();
        assertNotNull(id);
        var instruments = orderCreated.instruments();
        var createdInstrumentId = instruments.get(0).id();
        instruments=List.of(new Instrument(createdInstrumentId, "Kawai K3", "Kawai K3-0004", "Kawai", 4500D, "Acoustic Upright Piano", InstrumentType.PIANO),new Instrument(null, "Nord Piano 5", "Nord Piano 5", "Nord", 3500D, "Electric Piano", InstrumentType.PIANO));
        var newOrder = new Order(instruments, orderCreated.id(), orderCreated.reference(), orderCreated.orderDate(), createdCustomer, OrderStatus.PAID);
        newOrder = orderAdapter.update(newOrder);
        assertNotNull(newOrder.id());
        assertEquals(id, newOrder.customer().id());
        assertEquals(2, newOrder.instruments().size());
        assertEquals(createdInstrumentId, newOrder.instruments().get(0).id());
        assertTrue(newOrder.instruments().stream().allMatch(Objects::nonNull));
        assertTrue(newOrder.instruments().stream().anyMatch(i -> i.id().equals(createdInstrumentId)));
    }
}
