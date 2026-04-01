package info.touret.musicstore.domain.model;

/**
 * Represents the lifecycle statuses of an Order.
 */
public enum OrderStatus {
    /** The order has been newly created but no further action has taken place yet. */
    CREATED,
    
    /** The order has been canceled by the customer or the store. */
    CANCELED,
    
    /** The payment for the order has been successfully processed. */
    PAID,
    
    /** The order has been packed and handed over to a delivery service. */
    SHIPPED,
    
    /** The order is currently on its way to the delivery address. */
    IN_TRANSIT,
    
    /** The order has successfully reached the customer. */
    DELIVERED;
}
