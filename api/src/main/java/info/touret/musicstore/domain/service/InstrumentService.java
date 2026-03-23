package info.touret.musicstore.domain.service;

import info.touret.musicstore.domain.model.Instrument;
import info.touret.musicstore.domain.port.InstrumentPort;

import java.util.List;

public class InstrumentService {
    private InstrumentPort instrumentPort;

    public InstrumentService(InstrumentPort instrumentPort) {
        this.instrumentPort = instrumentPort;
    }

    public List<Instrument> findInstruments() {
        return instrumentPort.findAll();
    }

    public Instrument createInstrument(Instrument instrument) {
        return instrumentPort.create(instrument);
    }

    public Instrument updateInstrument(Instrument instrument) {
        return instrumentPort.update(instrument);
    }

    public void deleteInstrument(Instrument instrument) {
        instrumentPort.delete(instrument);
    }

    public List<Instrument> search(String query){
        return instrumentPort.search(query);
    }
}
