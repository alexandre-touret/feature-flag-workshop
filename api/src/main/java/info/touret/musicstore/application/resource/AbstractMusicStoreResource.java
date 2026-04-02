package info.touret.musicstore.application.resource;

import info.touret.musicstore.domain.model.DomainError;
import info.touret.musicstore.domain.model.Result;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Common methods for API Resources
 */
public abstract class AbstractMusicStoreResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractMusicStoreResource.class);

    /**
     * Transform errors to HTTP responses
     *
     * @param error Domain error to handle
     * @return The correspondir Response
     * @see DomainError
     */
    protected Response toErrorResponse(DomainError error) {
        LOGGER.warn("Creating error response: {}", error);
        return switch (error) {
            case DomainError.DataNotFound e -> Response.status(404).entity(e.message()).build();
            case DomainError.InvalidData e -> Response.status(400).entity(e.message()).build();
            case DomainError.Conflict e -> Response.status(409).entity(e.message()).build();
        };
    }

    protected abstract Response handleResult(Result<?> result, int successStatus);
}
