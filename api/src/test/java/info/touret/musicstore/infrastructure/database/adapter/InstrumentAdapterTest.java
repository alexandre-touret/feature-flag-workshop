package info.touret.musicstore.infrastructure.database.adapter;

import info.touret.musicstore.domain.model.Instrument;
import info.touret.musicstore.domain.model.InstrumentType;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class InstrumentAdapterTest {

    @Inject
    InstrumentAdapter instrumentAdapter;
    private Instrument nord;

    @BeforeEach
    void setUp() {
        nord = new Instrument(null, "Nord Piano 88", "nord-piano-88", "Nord", 2500D, "What Else?", InstrumentType.PIANO);
    }

    @Test
    void findAll() {
        assertNotEquals(0, instrumentAdapter.findAll().size());
    }

    @Test
    void create() {
        assertNotEquals(null, instrumentAdapter.create(nord).id());
    }

    @Test
    void update() {
        var nordInserted = instrumentAdapter.create(nord);
        var nordUpdated = new Instrument(nordInserted.id(), "Nord Piano 88", "nord-piano-88", "Nord", 3000D, "What Else?", InstrumentType.PIANO);
        instrumentAdapter.update(nordUpdated);
        assertEquals(3000D, instrumentAdapter.update(nordUpdated).price());
    }

    @Test
    void delete() {
        var nordInserted = instrumentAdapter.create(nord);
        var count = instrumentAdapter.findAll().size();
        assertDoesNotThrow(() -> instrumentAdapter.delete(nordInserted));
        assertEquals(count - 1, instrumentAdapter.findAll().size());
    }

    @Test
    void search() {
    }
}
