package info.touret.musicstore.infrastructure.database.repository;

import info.touret.musicstore.infrastructure.database.entity.InstrumentEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * Hibernate with Panache repository for {@link InstrumentEntity}.
 * Provides data access operations for instrument records in the database.
 */
@ApplicationScoped
public class InstrumentRepository implements PanacheRepository<InstrumentEntity> {

    /**
     * Executes a named query to search for instruments matching the given text query.
     * 
     * @param query the search term
     * @return a list of matching instrument entities
     */
    public List<InstrumentEntity> search(@NotEmpty String query) {
        return list("#InstrumentEntity.search", query);
    }
}
