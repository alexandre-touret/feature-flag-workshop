package info.touret.musicstore.application.mapper;

import info.touret.musicstore.application.data.InstrumentDto;
import info.touret.musicstore.domain.model.Instrument;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface InstrumentMapper {

    List<InstrumentDto> toInstrumentDtos(List<Instrument> instruments);

    Instrument toInstrument(InstrumentDto instrumentDto);

    InstrumentDto toInstrumentDto(Instrument instrument);

}
