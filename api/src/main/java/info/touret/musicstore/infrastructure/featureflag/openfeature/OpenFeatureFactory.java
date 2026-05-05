package info.touret.musicstore.infrastructure.featureflag.openfeature;

import dev.openfeature.contrib.providers.gofeatureflag.GoFeatureFlagProvider;
import dev.openfeature.contrib.providers.gofeatureflag.GoFeatureFlagProviderOptions;
import dev.openfeature.contrib.providers.gofeatureflag.exception.InvalidOptions;
import dev.openfeature.sdk.FeatureProvider;
import dev.openfeature.sdk.OpenFeatureAPI;
import io.quarkus.arc.Arc;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Produces;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OpenFeature Factory CLass
 */
@ApplicationScoped
public class OpenFeatureFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenFeatureFactory.class);

    @ConfigProperty(name = "go-feature-flag.url")
    String goFeatureFlagUrl;

    @ConfigProperty(name = "go-feature-flag.polling-interval")
    long goFeatureFLagPollingInterval;

    /**
     * Creates the provider
     *
     * @return The {@link FeatureProvider} instance
     * @see GoFeatureFlagProvider
     */
    private FeatureProvider createProvider() {
/* Solution using Flagd
return new FlagdProvider(
                FlagdOptions.builder()
                        .resolverType(Config.Resolver.FILE)
                        .offlineFlagSourcePath(Thread.currentThread().getContextClassLoader().getResource("/flags.flagd.json").getPath())
                        .build());*/

        try {
            return new GoFeatureFlagProvider(GoFeatureFlagProviderOptions.builder()
                    .endpoint(goFeatureFlagUrl)
                    .flagChangePollingIntervalMs(goFeatureFLagPollingInterval)
                    .build());
        } catch (InvalidOptions e) {
            LOGGER.error("Unable to create the OpenFeature provider instance with this URL : [{}]", goFeatureFlagUrl);
            throw new RuntimeException(e);
        }
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
        openFeatureAPI.addHooks(new ErrorHandlerHook());
        openFeatureAPI.setProviderAndWait(createProvider());
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
