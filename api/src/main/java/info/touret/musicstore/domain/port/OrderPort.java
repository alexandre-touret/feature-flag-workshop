package info.touret.musicstore.domain.port;

import info.touret.musicstore.domain.model.Order;
import info.touret.musicstore.domain.model.Result;

import java.util.List;

/**
 * Defines the outbound port for order-related data persistence and retrieval operations.
 * Isolates the domain logic from specific implementations like a relational database or REST APIs.
 */
public interface OrderPort {

    /**
     * Retrieves all existing orders from the data store.
     * 
     * @return A Result containing a list of all orders
     */
    Result<List<Order>> findAll();

    /**
     * Persists a newly placed order in the data store.
     * 
     * @param order The order data to save
     * @return A Result containing the persisted order, potentially with generated identifiers
     */
    Result<Order> create(Order order);

    /**
     * Updates an existing order in the data store.
     * 
     * @param order The order data with updated fields
     * @return A Result containing the updated order
     */
    Result<Order> update(Order order);

    /**
     * Removes an order from the data store.
     * 
     * @param order The order to delete
     * @return A Result wrapping a boolean indicating if the deletion was successful
     */
    Result<Boolean> delete(Order order);

    /**
     * Searches for orders matching the provided text query in the data store.
     * 
     * @param query The search string
     * @return A Result containing a list of matching orders
     */
    Result<List<Order>> search(String query);

    /**
     * Retrieves a single order by its unique identifier.
     * 
     * @param id The ID of the order to fetch
     * @return A Result containing the found order, or a DomainError if not found
     */
    Result<Order> findById(Long id);

}
