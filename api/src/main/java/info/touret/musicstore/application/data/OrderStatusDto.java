package info.touret.musicstore.application.data;

import com.fasterxml.jackson.annotation.JsonRootName;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Data Transfer Object representing an OrderStatus in the API.
 */
@JsonRootName("OrderStatus")
@Schema(name = "OrderStatus", description = "Order status data")
public enum OrderStatusDto {
    CREATED, CANCELED, PAID, SHIPPED, IN_TRANSIT, DELIVERED;
}
