package info.touret.musicstore.infrastructure.database.mapper;

import info.touret.musicstore.domain.model.Instrument;
import info.touret.musicstore.infrastructure.database.entity.InstrumentEntity;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Mapper for converting between domain Instrument records and database InstrumentEntity objects.
 */
@Mapper
public interface InstrumentMapper {
    /**
     * Converts a list of InstrumentEntity objects to domain Instrument records.
     * 
     * @param instrumentEntities the list of entities to convert
     * @return a list of domain instruments
     */
    List<Instrument> toInstruments(List<InstrumentEntity> instrumentEntities );

    /**
     * Converts a domain Instrument record to an InstrumentEntity object for persistence.
     * 
     * @param instrument the domain instrument to convert
     * @return the database entity
     */
    InstrumentEntity toInstrumentEntity(Instrument instrument);

    /**
     * Converts a single InstrumentEntity object to a domain Instrument record.
     * 
     * @param instrumentEntity the entity to convert
     * @return the domain instrument
     */
    Instrument toinstrument(InstrumentEntity instrumentEntity);
}
