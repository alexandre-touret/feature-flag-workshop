package info.touret.musicstore.application.mapper;

import info.touret.musicstore.application.data.InstrumentDto;
import info.touret.musicstore.domain.model.Instrument;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Mapper for converting between domain Instrument records and InstrumentDto data transfer objects.
 * Uses MapStruct to generate efficient mapping code at compile time.
 * This mapper bridges the domain layer with the REST API layer, ensuring
 * domain entities are never directly exposed via HTTP endpoints.
 */
@Mapper
public interface InstrumentMapper {

    List<InstrumentDto> toInstrumentDtos(List<Instrument> instruments);

    Instrument toInstrument(InstrumentDto instrumentDto);

    InstrumentDto toInstrumentDto(Instrument instrument);

}
