package info.touret.musicstore.domain.port;

import info.touret.musicstore.domain.model.Instrument;
import info.touret.musicstore.domain.model.Result;

import java.util.List;

public interface InstrumentPort {

    Result<List<Instrument>> findAll();

    Result<Instrument> create(Instrument instrument);

    Result<Instrument> update(Instrument instrument);

    Result<Boolean> delete(Instrument instrument);

    Result<List<Instrument>> search(String query);

    Result<Instrument> findById(Long id);
}
