package info.touret.musicstore.domain.port;

import info.touret.musicstore.domain.model.Order;

import java.util.List;

public interface OrderPort {

    List<Order> findAll();

    Order create(Order order);

    Order update(Order order);

    boolean delete(Order order);

    List<Order> search(String query);

    Order findById(Long id);

}
