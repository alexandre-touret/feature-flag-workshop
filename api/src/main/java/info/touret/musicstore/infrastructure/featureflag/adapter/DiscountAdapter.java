package info.touret.musicstore.infrastructure.featureflag.adapter;

import info.touret.musicstore.domain.model.Instrument;
import info.touret.musicstore.domain.model.Result;
import info.touret.musicstore.domain.model.User;
import info.touret.musicstore.domain.port.DiscountPort;
import jakarta.enterprise.context.ApplicationScoped;

// TODO: Chapter 3 - Uncomment to use OpenFeature
// import dev.openfeature.sdk.Client;
// import dev.openfeature.sdk.MutableContext;
// import dev.openfeature.sdk.OpenFeatureAPI;

@ApplicationScoped
public class DiscountAdapter implements DiscountPort {

    @Override
    public Result<Instrument> applyDiscount(Instrument instrument, User user) {
        // TODO: Chapter 3 - Implement this with OpenFeature
        // For now, let's keep it simple (manually toggle for testing if needed)
        boolean manualDiscount = false; // Toggle to true to test UI
        if (manualDiscount) {
            double originalPrice = instrument.price();
            double discountedPrice = originalPrice * 0.9; // 10% discount
            return Result.success(instrument.withDiscount(discountedPrice, originalPrice));
        }

        return Result.success(instrument);
    }
}
