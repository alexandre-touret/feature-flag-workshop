package info.touret.musicstore.application.data;

import com.fasterxml.jackson.annotation.JsonRootName;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Data Transfer Object representing an Order in the API.
 * Used for transferring order data between clients and the server over REST endpoints.
 */
@JsonRootName("Order")
public record OrderDto(@NotNull List<InstrumentDto> instruments, @Min(0L) Long id,
                       @Schema(implementation = String.class, format = "uuid") @NotNull UUID reference,
                       @Schema(implementation = String.class, format = "date-time") @NotNull ZonedDateTime orderDate,
                       @NotNull CustomerDto customer, OrderStatusDto orderStatus) {
}
