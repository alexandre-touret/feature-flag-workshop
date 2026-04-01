package info.touret.musicstore.application.data;

import com.fasterxml.jackson.annotation.JsonRootName;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Data Transfer Object representing an Instrument in the API.
 * Carries data from the client to the server and vice versa over REST endpoints.
 */
@JsonRootName("Instrument")
@Schema(name = "Instrument", description = "Instrument data")
public record InstrumentDto(@Min(0) Long id, @NotEmpty String name, @NotEmpty String reference,
                            @NotEmpty String manufacturer, @Min(0) Double price, @NotEmpty String description,
                            @NotNull InstrumentTypeDto type) {
}
