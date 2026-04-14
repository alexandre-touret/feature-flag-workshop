package info.touret.musicstore.infrastructure.featureflag.adapter;

import info.touret.musicstore.domain.model.Instrument;
import info.touret.musicstore.domain.model.User;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static info.touret.musicstore.domain.model.InstrumentType.PIANO;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class DiscountAdapterTest {
    @Inject
    DiscountAdapter discountAdapter;
    private Instrument instrument;
    private User user;

    @BeforeEach
    void setUp() {
        instrument = new Instrument(null, "Kawai K3", "Kawai K3", "Kawai", 4500D, "Acoustic Upright Piano", PIANO);
        user = new User("John", "Doe", "john.doe@example.com", "FRANCE");
    }

    @Test
    void should_return_the_instrument_with_a_discount_successfully() {
        assertEquals(instrument.price() * 0.9, discountAdapter.applyDiscount(instrument, user).value().price());
    }


    @Test
    void should_return_the_instrument_with_no_discount_successfully() {
        var userGB = new User("John", "Doe", "john.doe@example.com", "FRANCE");
        assertEquals(instrument.price() * 0.9, discountAdapter.applyDiscount(instrument, userGB).value().price());
    }
}
