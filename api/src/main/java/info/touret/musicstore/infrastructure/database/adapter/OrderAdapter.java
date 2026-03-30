package info.touret.musicstore.infrastructure.database.adapter;

import info.touret.musicstore.domain.exception.DataNotFoundException;
import info.touret.musicstore.domain.exception.InvalidDataException;
import info.touret.musicstore.domain.model.Order;
import info.touret.musicstore.domain.port.OrderPort;
import info.touret.musicstore.infrastructure.database.entity.OrderEntity;
import info.touret.musicstore.infrastructure.database.mapper.OrderMapper;
import info.touret.musicstore.infrastructure.database.repository.OrderRepository;
import io.quarkus.arc.ArcUndeclaredThrowableException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@ApplicationScoped
public class OrderAdapter implements OrderPort {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Inject
    public OrderAdapter(OrderRepository orderRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }

    @Override
    public List<Order> findAll() {
        return orderMapper.toOrders(orderRepository.listAll());
    }

    @Transactional
    @Override
    public Order create(Order order) {
        Objects.requireNonNull(order);
        OrderEntity orderToBeCreated = orderMapper.toOrderEntity(order);
        orderRepository.persist(orderToBeCreated);
        orderRepository.flush();
        return orderMapper.toOrder(orderToBeCreated);
    }

    @Transactional
    @Override
    public Order update(Order order) {
        try {
            OrderEntity merged = orderRepository.getEntityManager().merge(orderMapper.toOrderEntity(order));
            orderRepository.flush();
            return orderMapper.toOrder(merged);
        } catch (ConstraintViolationException e) {
            throw e;
        } catch (ArcUndeclaredThrowableException e) {
            if (e.getCause() instanceof ConstraintViolationException cve) {
                throw cve;
            }
            throw new InvalidDataException(e);
        } catch (Exception e) {
            throw new InvalidDataException(e);
        }
    }

    @Transactional
    @Override
    public List<Order> search(String query) {
        Objects.requireNonNull(query);
        return orderMapper.toOrders(orderRepository.search(query));
    }

    @Override
    public Order findById(Long id) {
        Objects.requireNonNull(id, String.format("Order {} not found", id));
        return Optional.ofNullable(orderMapper.toOrder(orderRepository.findById(id))).orElseThrow(() -> new DataNotFoundException(String.format("Order {} not found", id)));
    }

    @Transactional
    @Override
    public boolean delete(Order order) {
        Objects.requireNonNull(order.id());
        return orderRepository.deleteById(order.id());
    }
}
