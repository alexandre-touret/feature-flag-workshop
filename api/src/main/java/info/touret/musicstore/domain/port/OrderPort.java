package info.touret.musicstore.domain.port;

import info.touret.musicstore.domain.model.Order;
import info.touret.musicstore.domain.model.Result;

import java.util.List;

public interface OrderPort {

    Result<List<Order>> findAll();

    Result<Order> create(Order order);

    Result<Order> update(Order order);

    Result<Boolean> delete(Order order);

    Result<List<Order>> search(String query);

    Result<Order> findById(Long id);

}
