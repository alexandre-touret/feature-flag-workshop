package info.touret.musicstore.application;

import info.touret.musicstore.application.resource.AbstractMusicStoreResource;
import jakarta.ws.rs.container.ContainerRequestContext;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Filters {
    private static final Logger LOGGER = LoggerFactory.getLogger(Filters.class);

    @ServerRequestFilter(preMatching = true)
    public void preMatchingFilter(ContainerRequestContext requestContext) {
        LOGGER.debug(requestContext.getHeaderString(AbstractMusicStoreResource.USER));
    }
}
