package info.touret.musicstore.infrastructure.database.repository;

import info.touret.musicstore.infrastructure.database.entity.InstrumentEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class InstrumentRepository implements PanacheRepository<InstrumentEntity> {

}
