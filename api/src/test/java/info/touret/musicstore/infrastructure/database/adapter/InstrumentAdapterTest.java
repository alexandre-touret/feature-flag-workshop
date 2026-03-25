package info.touret.musicstore.infrastructure.database.adapter;

import info.touret.musicstore.domain.exception.DataNotFoundException;
import info.touret.musicstore.domain.model.Instrument;
import info.touret.musicstore.domain.model.InstrumentType;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@QuarkusTest
class InstrumentAdapterTest {

    @Inject
    InstrumentAdapter instrumentAdapter;
    private Instrument nord;

    @BeforeEach
    void setUp() {
        nord = new Instrument(null, "Nord Piano 88", "nord-piano-88", "Nord", 2500D, "What Else?", InstrumentType.PIANO);
    }

    @Order(1)
    @Test
    void should_find_all_successfully() {
        assertNotEquals(0, instrumentAdapter.findAll().size());
    }

    @Order(2)
    @Test
    void should_create_successfully() {
        assertNotEquals(null, instrumentAdapter.create(nord).id());
    }

    @Order(3)
    @Test
    void should_update_successfully() {
        var nordInserted = instrumentAdapter.create(nord);
        var nordUpdated = new Instrument(nordInserted.id(), "Nord Piano 88", "nord-piano-88", "Nord", 3000D, "What Else?", InstrumentType.PIANO);
        instrumentAdapter.update(nordUpdated);
        assertEquals(3000D, instrumentAdapter.update(nordUpdated).price());
    }

    @Order(6)
    @Test
    void should_update_with_incorrect_values_failed() {
        var nordInserted = instrumentAdapter.create(nord);
        var nordUpdated = new Instrument(nordInserted.id(), null, null, "Nord", 3000D, "What Else?", InstrumentType.PIANO);
        assertThrows(ConstraintViolationException.class, () -> instrumentAdapter.update(nordUpdated));
    }

    @Test
    void should_create_with_invalid_values_failed() {
        var nordIncorrect = new Instrument(null, null, null, "Nord", 3000D, "What Else?", InstrumentType.PIANO);
        assertThrows(ConstraintViolationException.class, () -> instrumentAdapter.create(nordIncorrect));
    }

    @Order(4)
    @Test
    void should_delete_successfully() {
        var nordInserted = instrumentAdapter.create(nord);
        var count = instrumentAdapter.findAll().size();
        assertTrue(instrumentAdapter.delete(nordInserted));
        assertEquals(count - 1, instrumentAdapter.findAll().size());
    }

    @Order(5)
    @Test
    void should_delete_failed() {
        var nordToBeDeleted = new Instrument(400L, "Nord Piano 88", "nord-piano-88", "Nord", 3000D, "What Else?", InstrumentType.PIANO);
        assertFalse(instrumentAdapter.delete(nordToBeDeleted));
    }

    @Test
    void should_findById_failed() {
        assertThrows(DataNotFoundException.class, () -> instrumentAdapter.findById(600L));
    }

    @Test
    void search() {
    }
}
