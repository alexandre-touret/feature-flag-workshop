package info.touret.musicstore.application.mapper;

import info.touret.musicstore.application.data.OrderDto;
import info.touret.musicstore.domain.model.Order;
import org.mapstruct.Mapper;

import java.util.List;

/// Mapper for converting between domain Order records and OrderDto data transfer objects.
/// Uses MapStruct to generate efficient mapping code at compile time.
/// This mapper bridges the domain layer with the REST API layer, ensuring
/// domain entities are never directly exposed via HTTP endpoints.
@Mapper
public interface OrderMapper {

    /// Converts a list of domain Order records to OrderDto transfer objects.
    /// @param orders the list of domain orders to convert
    /// @return a list of data transfer objects ready for REST API serialization
    List<OrderDto> toOrderDtos(List<Order> orders);

    /// Converts a single domain Order record to an OrderDto transfer object.
    /// @param order the domain order to convert
    /// @return the data transfer object
    OrderDto toOrderDto(Order order);

    /// Converts an OrderDto transfer object to a domain Order record.
    /// @param orderDto the data transfer object
    /// @return a domain order record
    Order toOrder(OrderDto orderDto);

}
