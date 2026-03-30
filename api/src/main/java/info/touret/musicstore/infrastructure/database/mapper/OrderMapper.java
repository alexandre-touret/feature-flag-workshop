package info.touret.musicstore.infrastructure.database.mapper;

import info.touret.musicstore.domain.model.Order;
import info.touret.musicstore.infrastructure.database.entity.OrderEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface OrderMapper {

    Order toOrder(OrderEntity entity);

    List<Order> toOrders(List<OrderEntity> entities);

    OrderEntity toOrderEntity(Order order);


}
