package info.touret.musicstore.domain.service;

import info.touret.musicstore.domain.model.Order;
import info.touret.musicstore.domain.port.OrderPort;

import java.util.List;

public class OrderService {

    private OrderPort orderPort;

    public OrderService(OrderPort orderPort) {
        this.orderPort = orderPort;
    }

    public List<Order> findOrders() {
        return orderPort.findAll();
    }

    public Order createOrder(Order order) {
        return orderPort.create(order);
    }

    public Order updateOrder(Order order) {
        return orderPort.update(order);
    }

    public void deleteOrder(Order order) {
        orderPort.delete(order);
    }

    public List<Order> search(String query) {
        return orderPort.search(query);
    }
}
