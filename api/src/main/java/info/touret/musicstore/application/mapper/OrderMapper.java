package info.touret.musicstore.application.mapper;

import info.touret.musicstore.application.data.OrderDto;
import info.touret.musicstore.domain.model.Order;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Mapper for converting between domain Order records and OrderDto data transfer objects.
 * Uses MapStruct to generate efficient mapping code at compile time.
 * This mapper bridges the domain layer with the REST API layer, ensuring
 * domain entities are never directly exposed via HTTP endpoints.
 *
 **/
@Mapper
public interface OrderMapper {

    List<OrderDto> toOrderDtos(List<Order> orders);

    OrderDto toOrderDto(Order order);

    Order toOrder(OrderDto orderDto);

}
