package info.touret.musicstore.application.data;

import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * Data Transfer Object representing an OrderStatus in the API.
 */
@JsonRootName("OrderStatus")
public enum OrderStatusDto {
    CREATED, CANCELED, PAID, SHIPPED, IN_TRANSIT, DELIVERED;
}
