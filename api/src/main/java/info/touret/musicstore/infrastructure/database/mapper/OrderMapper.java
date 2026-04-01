package info.touret.musicstore.infrastructure.database.mapper;

import info.touret.musicstore.domain.model.Order;
import info.touret.musicstore.infrastructure.database.entity.OrderEntity;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Mapper for converting between domain Order records and database OrderEntity objects.
 */
@Mapper
public interface OrderMapper {

    /**
     * Converts a single OrderEntity object to a domain Order record.
     * 
     * @param entity the entity to convert
     * @return the domain order
     */
    Order toOrder(OrderEntity entity);

    /**
     * Converts a list of OrderEntity objects to domain Order records.
     * 
     * @param entities the list of entities to convert
     * @return a list of domain orders
     */
    List<Order> toOrders(List<OrderEntity> entities);

    /**
     * Converts a domain Order record to an OrderEntity object for persistence.
     * 
     * @param order the domain order to convert
     * @return the database entity
     */
    OrderEntity toOrderEntity(Order order);


}
