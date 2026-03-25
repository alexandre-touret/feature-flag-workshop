package info.touret.musicstore.domain.port;

import info.touret.musicstore.domain.model.Instrument;

import java.util.List;

public interface InstrumentPort {

    List<Instrument> findAll();

    Instrument create(Instrument instrument);

    Instrument update(Instrument instrument);

    boolean delete(Instrument instrument);

    List<Instrument> search(String query);

    Instrument findById(Long id);
}
