package info.touret.musicstore.infrastructure.featureflag.openfeature;

import dev.openfeature.sdk.FeatureProvider;
import dev.openfeature.sdk.OpenFeatureAPI;
import dev.openfeature.sdk.providers.memory.Flag;
import dev.openfeature.sdk.providers.memory.InMemoryProvider;
import io.quarkus.arc.Arc;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Produces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * OpenFeature Factory CLass
 */
@ApplicationScoped
public class OpenFeatureFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenFeatureFactory.class);

    /**
     * Creates the provider
     *
     * @return The {@link FeatureProvider} instance
     * @see dev.openfeature.sdk.providers.memory.InMemoryProvider
     */
    private FeatureProvider createProvider() {
        Map<String, Flag<?>> flags = Map.of(
                // Creates a flag to enable discounts
                "discount-enabled", Flag.builder()
                        .variant("on", true)
                        .variant("off", false)
                        .defaultVariant("on")
                        .build(),
                // Creates a flag to show the welcome message
                "welcome-message", Flag.builder()
                        .variant("greeting", "Bienvenue sur notre boutique !")
                        .variant("empty", "")
                        .defaultVariant("greeting")
                        .build()
        );
        return new InMemoryProvider(flags);
    }

    /**
     * Creates the {@link OpenFeatureAPI} instance
     *
     * @return The {@link OpenFeatureAPI} instance
     */
    @ApplicationScoped
    @Produces
    public OpenFeatureAPI getOpenFeatureAPIInstance() {
        var openFeatureAPI = OpenFeatureAPI.getInstance();
        openFeatureAPI.setProvider(createProvider());
        return openFeatureAPI;
    }

    /**
     * Destroys the OpenFeature instance and flushes pending events
     */
    @PreDestroy
    private void shutdownProvider() {
        try {
            var openFeatureAPI = Arc.container().select(OpenFeatureAPI.class).get();
            if (openFeatureAPI != null) {
                openFeatureAPI.shutdown();
            }
        } catch (Exception e) {
            LOGGER.warn("Failing to shutdown the provider");
        }
    }
}
