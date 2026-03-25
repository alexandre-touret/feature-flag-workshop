package info.touret.musicstore.infrastructure.database.adapter;

import info.touret.musicstore.domain.exception.DataNotFoundException;
import info.touret.musicstore.domain.exception.InvalidDataException;
import info.touret.musicstore.domain.model.Instrument;
import info.touret.musicstore.domain.port.InstrumentPort;
import info.touret.musicstore.infrastructure.database.entity.InstrumentEntity;
import info.touret.musicstore.infrastructure.database.mapper.InstrumentMapper;
import info.touret.musicstore.infrastructure.database.repository.InstrumentRepository;
import io.quarkus.arc.ArcUndeclaredThrowableException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@ApplicationScoped
public class InstrumentAdapter implements InstrumentPort {

    private final InstrumentRepository instrumentRepository;
    private final InstrumentMapper instrumentMapper;

    @Inject
    public InstrumentAdapter(InstrumentRepository instrumentRepository, InstrumentMapper instrumentMapper) {
        this.instrumentRepository = instrumentRepository;
        this.instrumentMapper = instrumentMapper;
    }


    @Override
    public List<Instrument> findAll() {
        return instrumentMapper.toInstruments(instrumentRepository.listAll());
    }

    @Transactional
    @Override
    public Instrument create(Instrument instrument) {
        Objects.requireNonNull(instrument);
        InstrumentEntity instrumentToBeCreated = instrumentMapper.toInstrumentEntity(instrument);
        instrumentRepository.persist(instrumentToBeCreated);
        instrumentRepository.flush();
        return instrumentMapper.toinstrument(instrumentToBeCreated);
    }

    @Transactional
    @Override
    public Instrument update(Instrument instrument) {
        Objects.requireNonNull(instrument);
        try {
            InstrumentEntity merged = instrumentRepository.getEntityManager().merge(instrumentMapper.toInstrumentEntity(instrument));
            instrumentRepository.flush();
            return instrumentMapper.toinstrument(merged);
        } catch (ConstraintViolationException e) {
            throw e;
        } catch (ArcUndeclaredThrowableException e) {
            if (e.getCause() instanceof ConstraintViolationException cve) {
                throw cve;
            }
            throw new InvalidDataException(e);
        } catch (Exception e) {
            throw new InvalidDataException(e);
        }
    }

    @Transactional
    @Override
    public boolean delete(Instrument instrument) {
        Objects.requireNonNull(instrument.id());
        return instrumentRepository.deleteById(instrument.id());
    }

    @Override
    public List<Instrument> search(String query) {
        return List.of();
    }

    @Override
    public Instrument findById(Long id) {
        Objects.requireNonNull(id, String.format("Instrument {} not found", id));
        return Optional.ofNullable(instrumentMapper.toinstrument(instrumentRepository.findById(id))).orElseThrow(() -> new DataNotFoundException(String.format("Instrument {} not found", id)));
    }
}
