package info.touret.musicstore.infrastructure.database.repository;

import info.touret.musicstore.infrastructure.database.entity.InstrumentEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@ApplicationScoped
public class InstrumentRepository implements PanacheRepository<InstrumentEntity> {

    public List<InstrumentEntity> search(@NotEmpty String query) {
        return list("#InstrumentEntity.search", query);
    }
}
