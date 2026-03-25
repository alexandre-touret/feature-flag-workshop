package info.touret.musicstore.infrastructure.database.entity;

import jakarta.persistence.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID reference;
    @Column(name = "order_date")
    private ZonedDateTime orderDate;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "customer_id")
    private CustomerEntity customer;

    @ManyToMany
    @JoinTable(
            name = "order_instruments",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "instrument_id")
    )
    private List<InstrumentEntity> instruments;

    @Enumerated(EnumType.STRING)
    private OrderStatusEntity orderStatusEntity;

    public OrderEntity() {
    }

    public OrderEntity(Long id, UUID reference, ZonedDateTime orderDate, CustomerEntity customer, List<InstrumentEntity> instruments, OrderStatusEntity orderStatusEntity) {
        this.id = id;
        this.reference = reference;
        this.orderDate = orderDate;
        this.customer = customer;
        this.instruments = instruments;
        this.orderStatusEntity = orderStatusEntity;
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

    public OrderStatusEntity getOrderStatusEntity() {
        return orderStatusEntity;
    }

    public void setOrderStatusEntity(OrderStatusEntity orderStatusEntity) {
        this.orderStatusEntity = orderStatusEntity;
    }
}
