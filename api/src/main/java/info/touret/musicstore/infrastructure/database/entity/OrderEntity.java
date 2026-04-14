package info.touret.musicstore.infrastructure.database.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static jakarta.persistence.CascadeType.*;

/**
 * JPA Entity representing an Order record in the database.
 */
@Entity
@Table(name = "orders")
@NamedQuery(name = "OrderEntity.search", query = "from OrderEntity where cast(reference as String) LIKE concat('%',?1,'%') OR lower(customer.firstName) LIKE lower(concat('%',?1,'%')) OR lower(customer.lastName) LIKE lower(concat('%',?1,'%'))")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private UUID reference;
    @Column(name = "order_date")
    private ZonedDateTime orderDate;

    @ManyToOne(cascade = {DETACH, MERGE, PERSIST, REFRESH})
    @JoinColumn(name = "customer_id")
    private CustomerEntity customer;

    @ManyToMany(cascade = ALL)
    @JoinTable(
            name = "order_instruments",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "instrument_id")
    )
    private List<InstrumentEntity> instruments;

    @NotNull
    @Enumerated(EnumType.STRING)
    private OrderStatusEntity orderStatus;

    public OrderEntity() {
    }

    public OrderEntity(Long id, UUID reference, ZonedDateTime orderDate, CustomerEntity customer, List<InstrumentEntity> instruments, OrderStatusEntity orderStatus) {
        this.id = id;
        this.reference = reference;
        this.orderDate = orderDate;
        this.customer = customer;
        this.instruments = instruments;
        this.orderStatus = orderStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getReference() {
        return reference;
    }

    public void setReference(UUID reference) {
        this.reference = reference;
    }

    public ZonedDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(ZonedDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }

    public List<InstrumentEntity> getInstruments() {
        return instruments;
    }

    public void setInstruments(List<InstrumentEntity> instruments) {
        this.instruments = instruments;
    }

    public OrderStatusEntity getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatusEntity orderStatusEntity) {
        this.orderStatus = orderStatusEntity;
    }
}
