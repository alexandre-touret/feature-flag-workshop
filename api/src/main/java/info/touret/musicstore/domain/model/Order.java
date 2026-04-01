package info.touret.musicstore.domain.model;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Represents a customer's order in the music store.
 * An order contains a list of instruments, a customer, and tracks the order status over time.
 * 
 * @param instruments The list of instruments included in this order
 * @param id The technical identifier of the order
 * @param reference The unique business reference of the order
 * @param orderDate The date and time when the order was placed
 * @param customer The customer who placed the order
 * @param orderStatus The current status of the order
 */
public record Order(List<Instrument> instruments, Long id, UUID reference, ZonedDateTime orderDate,
                    Customer customer, OrderStatus orderStatus) {
}
