package info.touret.musicstore.domain.service;

import info.touret.musicstore.domain.model.DomainError;
import info.touret.musicstore.domain.model.Instrument;
import info.touret.musicstore.domain.model.Result;
import info.touret.musicstore.domain.port.InstrumentPort;

import java.util.List;

public class InstrumentService {
    private final InstrumentPort instrumentPort;

    public InstrumentService(InstrumentPort instrumentPort) {
        this.instrumentPort = instrumentPort;
    }

    public Result<List<Instrument>> findInstruments() {
        return instrumentPort.findAll();
    }

    public Result<Instrument> createInstrument(Instrument instrument) {
        if (instrument.id() != null) {
            return Result.failure(new DomainError.InvalidData("Instrument id must be null for creation"));
        }
        return instrumentPort.create(instrument);
    }

    public Result<Instrument> updateInstrument(Instrument instrument) {
        if (instrument.id() == null) {
            return Result.failure(new DomainError.InvalidData("Instrument id must not be null for update"));
        }
        return instrumentPort.update(instrument);
    }

    public Result<Boolean> deleteInstrument(Instrument instrument) {
        return instrumentPort.delete(instrument);
    }

    public Result<List<Instrument>> search(String query) {
        return instrumentPort.search(query);
    }

    public Result<Instrument> findById(Long id) {
        return instrumentPort.findById(id);
    }
}
