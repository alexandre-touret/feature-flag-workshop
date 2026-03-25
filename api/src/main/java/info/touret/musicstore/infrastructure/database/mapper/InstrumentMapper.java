package info.touret.musicstore.infrastructure.database.mapper;

import info.touret.musicstore.domain.model.Instrument;
import info.touret.musicstore.infrastructure.database.entity.InstrumentEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface InstrumentMapper {
    List<Instrument> toInstruments(List<InstrumentEntity> instrumentEntities );

    InstrumentEntity toInstrumentEntity(Instrument instrument);
    Instrument toinstrument(InstrumentEntity instrumentEntity);
}
