package info.touret.musicstore.domain.service;

import info.touret.musicstore.domain.model.DomainError;
import info.touret.musicstore.domain.model.Order;
import info.touret.musicstore.domain.model.Result;
import info.touret.musicstore.domain.port.OrderPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Manages customer orders within the music store application.
 * Contains domain logic and validations associated with the order lifecycle.
 */
public class OrderService {

    private final OrderPort orderPort;
    private final static Logger LOGGER = LoggerFactory.getLogger(OrderService.class);
    public OrderService(OrderPort orderPort) {
        this.orderPort = orderPort;
    }

    /**
     * Retrieves all orders currently in the system.
     * 
     * @return A Result containing a list of all orders
     */
    public Result<List<Order>> findOrders() {
        return orderPort.findAll();
    }

    /**
     * Creates a new customer order. Validates that the order does not already have an ID.
     * 
     * @param order The order to create
     * @return A Result wrapping the created order, or a DomainError on validation failure
     */
    public Result<Order> createOrder(Order order) {
        if (order.id() != null) {
            LOGGER.error("order ID null for creation. Returning an failure");
            return Result.failure(new DomainError.InvalidData("Order id must be null for creation"));
        }
        return orderPort.create(order);
    }

    /**
     * Updates an existing order's information. Validates that the order already has an ID.
     * 
     * @param order The updated order model
     * @return A Result wrapping the updated order, or a DomainError on validation failure
     */
    public Result<Order> updateOrder(Order order) {
        if (order.id() == null) {
            return Result.failure(new DomainError.InvalidData("Order id must not be null for update"));
        }
        return orderPort.update(order);
    }

    /**
     * Deletes a given order from the system.
     * 
     * @param order The order to delete
     */
    public void deleteOrder(Order order) {
        orderPort.delete(order);
    }

    /**
     * Searches for orders matching a specific query string.
     * 
     * @param query The search term
     * @return A Result containing a list of matching orders
     */
    public Result<List<Order>> search(String query) {
        return orderPort.search(query);
    }

    /**
     * Retrieves an order by its unique identifier.
     * 
     * @param id The technical identifier of the order
     * @return A Result wrapping the found order, or a DomainError if not found
     */
    public Result<Order> findById(Long id) {
        return orderPort.findById(id);
    }
}
