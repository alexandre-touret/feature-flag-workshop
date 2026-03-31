package info.touret.musicstore.domain.service;

import info.touret.musicstore.domain.model.DomainError;
import info.touret.musicstore.domain.model.Order;
import info.touret.musicstore.domain.model.Result;
import info.touret.musicstore.domain.port.OrderPort;

import java.util.List;

public class OrderService {

    private final OrderPort orderPort;

    public OrderService(OrderPort orderPort) {
        this.orderPort = orderPort;
    }

    public Result<List<Order>> findOrders() {
        return orderPort.findAll();
    }

    public Result<Order> createOrder(Order order) {
        if (order.id() != null) {
            return Result.failure(new DomainError.InvalidData("Order id must be null for creation"));
        }
        return orderPort.create(order);
    }

    public Result<Order> updateOrder(Order order) {
        if (order.id() == null) {
            return Result.failure(new DomainError.InvalidData("Order id must not be null for update"));
        }
        return orderPort.update(order);
    }

    public void deleteOrder(Order order) {
        orderPort.delete(order);
    }

    public Result<List<Order>> search(String query) {
        return orderPort.search(query);
    }

    public Result<Order> findById(Long id) {
        return orderPort.findById(id);
    }
}
