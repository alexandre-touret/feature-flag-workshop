package info.touret.musicstore.infrastructure.database.adapter;

import info.touret.musicstore.domain.model.Instrument;
import info.touret.musicstore.domain.model.InstrumentType;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
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
        assertNotEquals(0, instrumentAdapter.findAll().value().size());
    }

    @Order(2)
    @Test
    void should_create_successfully() {
        var result = instrumentAdapter.create(nord);
        assertTrue(result.isSuccess());
        assertNotNull(result.value().id());
    }

    @Order(3)
    @Test
    void should_update_successfully() {
        var nordInserted = instrumentAdapter.create(nord).value();
        var nordUpdated = new Instrument(nordInserted.id(), "Nord Piano 88", "nord-piano-88", "Nord", 3000D, "What Else?", InstrumentType.PIANO);
        var result = instrumentAdapter.update(nordUpdated);
        assertTrue(result.isSuccess());
        assertEquals(3000D, result.value().price());
    }

    @Order(6)
    @Test
    void should_update_with_incorrect_values_failed() {
        var nordInserted = instrumentAdapter.create(nord).value();
        var nordUpdated = new Instrument(nordInserted.id(), null, null, "Nord", 3000D, "What Else?", InstrumentType.PIANO);
        var result = instrumentAdapter.update(nordUpdated);
        assertTrue(result.isFailure());
    }

    @Test
    void should_create_with_invalid_values_failed() {
        var nordIncorrect = new Instrument(null, null, null, "Nord", 3000D, "What Else?", InstrumentType.PIANO);
        var result = instrumentAdapter.create(nordIncorrect);
        assertTrue(result.isFailure());
    }

    @Order(4)
    @Test
    void should_delete_successfully() {
        var nordInserted = instrumentAdapter.create(nord).value();
        var count = instrumentAdapter.findAll().value().size();
        assertTrue(instrumentAdapter.delete(nordInserted).isSuccess());
        assertEquals(count - 1, instrumentAdapter.findAll().value().size());
    }

    @Order(5)
    @Test
    void should_delete_failed() {
        var nordToBeDeleted = new Instrument(400L, "Nord Piano 88", "nord-piano-88", "Nord", 3000D, "What Else?", InstrumentType.PIANO);
        assertTrue(instrumentAdapter.delete(nordToBeDeleted).isFailure());
    }

    @Test
    void should_findById_failed() {
        var result = instrumentAdapter.findById(600L);
        assertTrue(result.isFailure());
    }

    @Test
    void should_search_succesfully() {
        assertTrue(instrumentAdapter.search("Nord").value().size() > 0);
    }

    @Test
    void should_search_failed_with_null_query() {
        assertThrows(NullPointerException.class, () -> instrumentAdapter.search(null));
    }

    @Test
    void should_search_ignore_case() {
        assertEquals(instrumentAdapter.search("Fender").value().size(), instrumentAdapter.search("fender").value().size());
    }
}
