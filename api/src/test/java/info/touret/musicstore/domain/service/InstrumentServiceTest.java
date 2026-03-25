package info.touret.musicstore.domain.service;

import info.touret.musicstore.domain.model.Instrument;
import info.touret.musicstore.domain.model.InstrumentType;
import info.touret.musicstore.domain.port.InstrumentPort;
import org.junit.jupiter.api.AfterEach;
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

    @AfterEach
    void tearDown() {
    }

    @Test
    void should_find_successfully() {
        when(instrumentPort.findAll()).thenReturn(List.of(gibsones335));
        assertEquals(1L, instrumentService.findInstruments().get(0).id());
    }

    @Test
    void should_create_successfully() {
        when(instrumentPort.create(gibsones335)).thenReturn(gibsones335);
        assertEquals(1L, instrumentService.createInstrument(gibsones335).id());
    }

    @Test
    void should_update_successfully() {
        var gibsones335Modified = new Instrument(1L, "Gibson ES 335", "ES 355", "Gibson", 2000D, "Chuck Berry's Guitar", InstrumentType.GUITAR);
        when(instrumentPort.update(gibsones335)).thenReturn(gibsones335Modified);
        assertEquals(2000D, instrumentService.updateInstrument(gibsones335).price());
    }

    @Test
    void should_delete_successfully() {
        when(instrumentPort.delete(gibsones335)).thenReturn(true);
        assertTrue(instrumentService.deleteInstrument(gibsones335));
    }
}
