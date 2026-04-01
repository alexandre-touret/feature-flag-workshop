package info.touret.musicstore.infrastructure.database.adapter;

import info.touret.musicstore.domain.model.DomainError;
import info.touret.musicstore.domain.model.Order;
import info.touret.musicstore.domain.model.Result;
import info.touret.musicstore.domain.port.OrderPort;
import info.touret.musicstore.infrastructure.database.entity.OrderEntity;
import info.touret.musicstore.infrastructure.database.mapper.OrderMapper;
import info.touret.musicstore.infrastructure.database.repository.CustomerRepository;
import info.touret.musicstore.infrastructure.database.repository.OrderRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Adapter implementation of the {@link OrderPort}.
 * Connects the domain layer to the underlying relational database using Hibernate ORM via Panache repositories.
 */
@ApplicationScoped
public class OrderAdapter implements OrderPort {
    private final static Logger LOGGER = org.slf4j.LoggerFactory.getLogger(OrderAdapter.class);
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final CustomerRepository customerRepository;

    @Inject
    public OrderAdapter(OrderRepository orderRepository, OrderMapper orderMapper, CustomerRepository customerRepository) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.customerRepository = customerRepository;
    }

    @Override
    public Result<List<Order>> findAll() {
        return Result.success(orderMapper.toOrders(orderRepository.listAll()));
    }

    @Transactional
    @Override
    public Result<Order> create(Order order) {
        try {
            Objects.requireNonNull(order);
            OrderEntity orderToBeCreated = orderMapper.toOrderEntity(order);
            if (orderToBeCreated.getCustomer() != null && orderToBeCreated.getCustomer().getId() != null && orderToBeCreated.getCustomer().getId() > 0L) {
                orderToBeCreated.setCustomer(customerRepository.findById(orderToBeCreated.getCustomer().getId()));
            }
            LOGGER.info("Creating order: {}" , order);
            orderRepository.persistAndFlush(orderToBeCreated);
            return Result.success(orderMapper.toOrder(orderToBeCreated));
        } catch (PersistenceException | ConstraintViolationException e) {
            LOGGER.error("Order creation failed: {}" , order, e);
            return Result.failure(new DomainError.InvalidData(e.getMessage()));
        }
    }

    @Transactional
    @Override
    public Result<Order> update(Order order) {
        try {
            LOGGER.info("Updating order: {}" , order);
            OrderEntity merged = orderRepository.getEntityManager().merge(orderMapper.toOrderEntity(order));
            orderRepository.flush();
            return Result.success(orderMapper.toOrder(merged));
        } catch (PersistenceException | ConstraintViolationException e) {
            LOGGER.error("Order update failed: {}" , order, e);
            return Result.failure(new DomainError.InvalidData(e.getMessage()));
        }
    }

    @Transactional
    @Override
    public Result<List<Order>> search(String query) {
        Objects.requireNonNull(query);
        return Result.success(orderMapper.toOrders(orderRepository.search(query)));
    }

    @Override
    public Result<Order> findById(Long id) {
        return Optional.ofNullable(orderMapper.toOrder(orderRepository.findById(id)))
                .map(Result::success)
                .orElseGet(() -> Result.failure(new DomainError.DataNotFound(String.format("Order %d not found", id))));
    }

    @Transactional
    @Override
    public Result<Boolean> delete(Order order) {
        try {
            Objects.requireNonNull(order.id());
            var isDeleted = orderRepository.deleteById(order.id());
            if (isDeleted) {
                return Result.success(isDeleted);
            } else {
                LOGGER.error("Order deletion failed: {}" , order);
                return Result.failure(new DomainError.DataNotFound(String.format("Order %d not found", order.id())));
            }
        } catch (NullPointerException | PersistenceException e) {
            LOGGER.error("Order update failed: {}" , order, e);
            return Result.failure(new DomainError.InvalidData(e.getMessage()));
        }
    }
}
