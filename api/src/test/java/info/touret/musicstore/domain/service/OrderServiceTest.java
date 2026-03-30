package info.touret.musicstore.domain.service;

import info.touret.musicstore.domain.model.*;
import info.touret.musicstore.domain.port.OrderPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static info.touret.musicstore.domain.model.InstrumentType.GUITAR;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OrderServiceTest {

    private OrderService orderService;
    private OrderPort orderPort;
    private Order order;

    @BeforeEach
    void setUp() {
        orderPort = mock(OrderPort.class);
        orderService = new OrderService(orderPort);
        order = new Order(List.of(new Instrument(1L, "Gibson ES 335", "ES 355", "Gibson", 2444D, "Chuck Berry's Guitar", GUITAR)), 1L, UUID.randomUUID(), ZonedDateTime.now(),
                new Customer(1L, "John", "Doe", "john@doe.net", new Address("1", "Beale Street", "Memphis", "01010", "USA")), OrderStatus.CREATED);
    }

    @Test
    void should_find_orders_successfully() {
        when(orderPort.findAll()).thenReturn(List.of(order));
        assertEquals(1, orderService.findOrders().size());
    }

    @Test
    void should_create_successfully() {
        when(orderPort.create(order)).thenReturn(order);
        assertEquals(1L, orderService.createOrder(order).id());
    }

    @Test
    void should_update_successfully() {
        var orderModified = new Order(List.of(new Instrument(1L, "Gibson ES 335", "ES 355", "Gibson", 2000D, "Chuck Berry's Guitar", GUITAR)), 1L, UUID.randomUUID(), ZonedDateTime.now(),
                new Customer(1L, "John", "Doe", "john@doe.net", new Address("1", "Beale Street", "Memphis", "01010", "USA")), OrderStatus.CREATED);
        when(orderPort.update(order)).thenReturn(orderModified);
        assertEquals(2000D, orderService.updateOrder(order).instruments().get(0).price());
    }

    @Test
    void should_delete_successfully() {
        when(orderPort.delete(order)).thenReturn(true);
        assertDoesNotThrow(() -> orderService.deleteOrder(order));
    }
}
