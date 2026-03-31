package info.touret.musicstore.domain.service;

import info.touret.musicstore.domain.model.Instrument;
import info.touret.musicstore.domain.model.InstrumentType;
import info.touret.musicstore.domain.model.Result;
import info.touret.musicstore.domain.port.InstrumentPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InstrumentServiceTest {

    private InstrumentService instrumentService;
    private InstrumentPort instrumentPort;
    private Instrument gibsones335;

    @BeforeEach
    void setUp() {
        instrumentPort = mock(InstrumentPort.class);
        instrumentService = new InstrumentService(instrumentPort);
        gibsones335 = new Instrument(1L, "Gibson ES 335", "ES 355", "Gibson", 2444D, "Chuck Berry's Guitar", InstrumentType.GUITAR);
    }

    @Test
    void should_find_successfully() {

        when(instrumentPort.findAll()).thenReturn(Result.success(List.of(gibsones335)));
        assertEquals(1L, instrumentService.findInstruments().value().getFirst().id());
    }

    @Test
    void should_create_successfully() {
        // Pour la création, l'ID doit être null au début dans l'objet passé
        var instrumentToCreate = new Instrument(null, "Gibson ES 335", "ES 355", "Gibson", 2444D, "Chuck Berry's Guitar", InstrumentType.GUITAR);
        when(instrumentPort.create(instrumentToCreate)).thenReturn(Result.success(gibsones335));
        var result = instrumentService.createInstrument(instrumentToCreate);
        assertTrue(result.isSuccess());
        assertEquals(1L, result.value().id());
    }

    @Test
    void should_update_successfully() {
        var gibsones335Modified = new Instrument(1L, "Gibson ES 335", "ES 355", "Gibson", 2000D, "Chuck Berry's Guitar", InstrumentType.GUITAR);
        when(instrumentPort.update(gibsones335)).thenReturn(Result.success(gibsones335Modified));
        var result = instrumentService.updateInstrument(gibsones335);
        assertTrue(result.isSuccess());
        assertEquals(2000D, result.value().price());
    }

    @Test
    void should_delete_successfully() {
        when(instrumentPort.delete(gibsones335)).thenReturn(Result.success(true));
        assertTrue(instrumentService.deleteInstrument(gibsones335).isSuccess());
    }
}
