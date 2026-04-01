package info.touret.musicstore.infrastructure.database.repository;

import info.touret.musicstore.infrastructure.database.entity.CustomerEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Hibernate with Panache repository for {@link CustomerEntity}.
 * Provides data access operations for customer records in the database.
 */
@ApplicationScoped
public class CustomerRepository implements PanacheRepository<CustomerEntity> {
}
