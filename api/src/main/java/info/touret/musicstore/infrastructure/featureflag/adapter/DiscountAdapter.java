package info.touret.musicstore.infrastructure.featureflag.adapter;

import dev.openfeature.sdk.MutableContext;
import dev.openfeature.sdk.OpenFeatureAPI;
import info.touret.musicstore.domain.model.Instrument;
import info.touret.musicstore.domain.model.Result;
import info.touret.musicstore.domain.model.User;
import info.touret.musicstore.domain.port.DiscountPort;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class DiscountAdapter implements DiscountPort {

    private final OpenFeatureAPI openFeatureAPI;
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscountAdapter.class);

    public DiscountAdapter(OpenFeatureAPI openFeatureAPI) {
        this.openFeatureAPI = openFeatureAPI;
    }

    @Inject
    Tracer tracer;

    @WithSpan
    @Override
    public Result<Instrument> applyDiscount(Instrument instrument, User user) {
        // TODO: Chapter 3 - Implement this with OpenFeature
        // For now, let's keep it simple (manually toggle for testing if needed)
//        boolean manualDiscount = false; // Toggle to true to test UI
//        if (manualDiscount) {
//            double originalPrice = instrument.price();
//            double discountedPrice = originalPrice * 0.9; // 10% discount
//            return Result.success(instrument.withDiscount(discountedPrice, originalPrice));
//        }

        Span discountEnabledspan = tracer.spanBuilder("openfeature-discount-enabled").startSpan();

        var openFeatureAPIClient = this.openFeatureAPI.getClient();
        openFeatureAPIClient.setEvaluationContext(new MutableContext()
                .add("clientCountry", user.country())
                .add("targetingKey", user.email())
                .add("clientEmail", user.email()));
        try (Scope ignored = discountEnabledspan.makeCurrent()) {// New attribute for segment targeting
            var evaluationDetails = openFeatureAPIClient.getBooleanDetails("discount-enabled", false);

            LOGGER.info(evaluationDetails.toString());
            boolean isDiscountEnabled = evaluationDetails.getValue();
            if (isDiscountEnabled) {
                double originalPrice = instrument.price();
                double discountAmount = openFeatureAPIClient.getDoubleValue("discount-amount", 0.1);
                double discountedPrice = originalPrice * (1.0 - discountAmount);
                return Result.success(instrument.withDiscount(discountedPrice, originalPrice));
            }
            return Result.success(instrument);
        } finally {
            discountEnabledspan.end();
        }
    }
}
