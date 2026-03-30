package info.touret.musicstore.infrastructure.database.repository;

import info.touret.musicstore.infrastructure.database.entity.OrderEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class OrderRepository implements PanacheRepository<OrderEntity> {
    public List<OrderEntity> search(String query) {
        return list("#OrderEntity.search", query);
    }
}
