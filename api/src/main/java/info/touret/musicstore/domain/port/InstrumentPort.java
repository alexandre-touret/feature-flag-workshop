package info.touret.musicstore.domain.port;

import info.touret.musicstore.domain.model.Instrument;
import info.touret.musicstore.domain.model.Result;

import java.util.List;

/**
 * Defines the outbound port for instrument-related data persistence and retrieval operations.
 * Allows the domain layer to interact with a storage mechanism without being tied to a specific technology.
 */
public interface InstrumentPort {

    /**
     * Retrieves all available instruments from the data store.
     * 
     * @return A Result containing a list of all instruments
     */
    Result<List<Instrument>> findAll();

    /**
     * Persists a newly created instrument in the data store.
     * 
     * @param instrument The instrument data to save
     * @return A Result containing the persisted instrument, potentially with generated identifiers
     */
    Result<Instrument> create(Instrument instrument);

    /**
     * Updates an existing instrument in the data store.
     * 
     * @param instrument The instrument data with updated fields
     * @return A Result containing the updated instrument
     */
    Result<Instrument> update(Instrument instrument);

    /**
     * Removes an instrument from the data store.
     * 
     * @param instrument The instrument to delete
     * @return A Result wrapping a boolean indicating if the deletion was successful
     */
    Result<Boolean> delete(Instrument instrument);

    /**
     * Searches for instruments matching the provided text query in the data store.
     * 
     * @param query The search string
     * @return A Result containing a list of matching instruments
     */
    Result<List<Instrument>> search(String query);

    /**
     * Retrieves a single instrument by its unique identifier.
     * 
     * @param id The ID of the instrument to fetch
     * @return A Result containing the found instrument, or a DomainError if not found
     */
    Result<Instrument> findById(Long id);
}
