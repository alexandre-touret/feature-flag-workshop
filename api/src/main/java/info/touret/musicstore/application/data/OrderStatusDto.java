package info.touret.musicstore.application.data;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("OrderStatus")
public enum OrderStatusDto {
    CREATED, CANCELED, PAID, SHIPPED, IN_TRANSIT, DELIVERED;
}
