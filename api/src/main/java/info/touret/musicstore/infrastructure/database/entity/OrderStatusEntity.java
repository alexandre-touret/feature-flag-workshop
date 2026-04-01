package info.touret.musicstore.infrastructure.database.entity;

/**
 * JPA Enum representing an OrderStatus in the database.
 */
public enum OrderStatusEntity {
    CREATED, CANCELED, PAID, SHIPPED, IN_TRANSIT, DELIVERED;
}
