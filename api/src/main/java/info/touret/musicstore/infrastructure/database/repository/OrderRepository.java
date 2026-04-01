package info.touret.musicstore.infrastructure.database.repository;

import info.touret.musicstore.infrastructure.database.entity.OrderEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

/**
 * Hibernate with Panache repository for {@link OrderEntity}.
 * Provides data access operations for order records in the database.
 */
@ApplicationScoped
public class OrderRepository implements PanacheRepository<OrderEntity> {
    
    /**
     * Executes a named query to search for orders matching the given text query.
     * 
     * @param query the search term
     * @return a list of matching order entities
     */
    public List<OrderEntity> search(String query) {
        return list("#OrderEntity.search", query);
    }
}
