package info.touret.musicstore.domain.service;

import info.touret.musicstore.domain.model.DomainError;
import info.touret.musicstore.domain.model.Instrument;
import info.touret.musicstore.domain.model.Result;
import info.touret.musicstore.domain.port.InstrumentPort;

import java.util.List;

/**
 * Manages instruments in the music store application.
 * Contains domain logic and validations associated with instrument lifecycle such as creation, updates, and searches.
 */
public class InstrumentService {
    private final InstrumentPort instrumentPort;

    public InstrumentService(InstrumentPort instrumentPort) {
        this.instrumentPort = instrumentPort;
    }

    /**
     * Retrieves all instruments currently available in the system.
     * 
     * @return A Result containing a list of all instruments
     */
    public Result<List<Instrument>> findInstruments() {
        return instrumentPort.findAll();
    }

    /**
     * Creates a new instrument. Validates that the instrument does not already have an ID.
     * 
     * @param instrument The instrument to create
     * @return A Result wrapping the created instrument, or a DomainError on validation failure
     */
    public Result<Instrument> createInstrument(Instrument instrument) {
        if (instrument.id() != null) {
            return Result.failure(new DomainError.InvalidData("Instrument id must be null for creation"));
        }
        return instrumentPort.create(instrument);
    }

    /**
     * Updates an existing instrument's information. Validates that the instrument has an ID.
     * 
     * @param instrument The updated instrument model
     * @return A Result wrapping the updated instrument, or a DomainError on validation failure
     */
    public Result<Instrument> updateInstrument(Instrument instrument) {
        if (instrument.id() == null) {
            return Result.failure(new DomainError.InvalidData("Instrument id must not be null for update"));
        }
        return instrumentPort.update(instrument);
    }

    /**
     * Deletes a given instrument from the system.
     * 
     * @param instrument The instrument to delete
     * @return A Result wrapping a boolean indicating successful deletion, or a DomainError
     */
    public Result<Boolean> deleteInstrument(Instrument instrument) {
        return instrumentPort.delete(instrument);
    }

    /**
     * Searches for instruments matching a specific query string.
     * 
     * @param query The search term
     * @return A Result containing a list of matching instruments
     */
    public Result<List<Instrument>> search(String query) {
        return instrumentPort.search(query);
    }

    /**
     * Retrieves an instrument by its unique identifier.
     * 
     * @param id The technical identifier of the instrument
     * @return A Result wrapping the found instrument, or a DomainError if not found
     */
    public Result<Instrument> findById(Long id) {
        return instrumentPort.findById(id);
    }
}
