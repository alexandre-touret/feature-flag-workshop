package info.touret.musicstore.infrastructure.database.adapter;

import info.touret.musicstore.domain.model.DomainError;
import info.touret.musicstore.domain.model.Instrument;
import info.touret.musicstore.domain.model.Result;
import info.touret.musicstore.domain.port.InstrumentPort;
import info.touret.musicstore.infrastructure.database.entity.InstrumentEntity;
import info.touret.musicstore.infrastructure.database.mapper.InstrumentMapper;
import info.touret.musicstore.infrastructure.database.repository.InstrumentRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@ApplicationScoped
public class InstrumentAdapter implements InstrumentPort {
    private final static Logger LOGGER = LoggerFactory.getLogger(InstrumentAdapter.class);
    private final InstrumentRepository instrumentRepository;
    private final InstrumentMapper instrumentMapper;

    @Inject
    public InstrumentAdapter(InstrumentRepository instrumentRepository, InstrumentMapper instrumentMapper) {
        this.instrumentRepository = instrumentRepository;
        this.instrumentMapper = instrumentMapper;
    }


    @Override
    public Result<List<Instrument>> findAll() {
        return Result.success(instrumentMapper.toInstruments(instrumentRepository.listAll()));
    }

    @Transactional
    @Override
    public Result<Instrument> create(Instrument instrument) {
        try {
            Objects.requireNonNull(instrument);
            InstrumentEntity instrumentToBeCreated = instrumentMapper.toInstrumentEntity(instrument);
            LOGGER.info("Creating instrument: {}" , instrument);
            instrumentRepository.persistAndFlush(instrumentToBeCreated);
            LOGGER.info("Created instrument: {}" , instrumentToBeCreated);
            return Result.success(instrumentMapper.toinstrument(instrumentToBeCreated));
        } catch (PersistenceException | ConstraintViolationException e) {
            LOGGER.error("Instrument creation failed: {}" , instrument, e);
            return Result.failure(new DomainError.InvalidData(e.getMessage()));
        }
    }

    @Transactional
    @Override
    public Result<Instrument> update(Instrument instrument) {
        Objects.requireNonNull(instrument);
        try {
            LOGGER.info("Updating instrument: {}");
            InstrumentEntity merged = instrumentRepository.getEntityManager().merge(instrumentMapper.toInstrumentEntity(instrument));
            instrumentRepository.flush();
            return Result.success(instrumentMapper.toinstrument(merged));
        } catch (PersistenceException | ConstraintViolationException e) {
            LOGGER.error("Instrument update failed: {}" , instrument, e);
            return Result.failure(new DomainError.InvalidData(e.getMessage()));
        }
    }

    @Transactional
    @Override
    public Result<Boolean> delete(Instrument instrument) {
        try {
            Objects.requireNonNull(instrument.id());
            LOGGER.info("Deleting instrument: {}" , instrument);
            var isDeleted = instrumentRepository.deleteById(instrument.id());
            if (isDeleted) {
                LOGGER.info("Deleted instrument successfully");
                return Result.success(isDeleted);
            } else {
                LOGGER.error("Instrument deletion failed: {}" ,instrument);
                return Result.failure(new DomainError.DataNotFound(String.format("Instrument %d not found", instrument.id())));
            }
        } catch (NullPointerException | PersistenceException e) {
            LOGGER.error("Instrument deletion failed: {}",instrument,e);
            return Result.failure(new DomainError.InvalidData(e.getMessage()));
        }
    }

    @Override
    public Result<List<Instrument>> search(String query) {
        Objects.requireNonNull(query);
        return Result.success(instrumentMapper.toInstruments(instrumentRepository.search(query)));
    }

    @Override
    public Result<Instrument> findById(Long id) {
        return Optional.ofNullable(instrumentMapper.toinstrument(instrumentRepository.findById(id)))
                .map(Result::success)
                .orElseGet(() -> Result.failure(new DomainError.DataNotFound(String.format("Instrument %d not found", id))));
    }
}
