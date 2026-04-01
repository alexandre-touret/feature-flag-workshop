package info.touret.musicstore.application.data;

import com.fasterxml.jackson.annotation.JsonRootName;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Data Transfer Object representing an Order in the API.
 * Used for transferring order data between clients and the server over REST endpoints.
 */
@JsonRootName("Order")
public record OrderDto(@NotNull List<InstrumentDto> instruments, @Min(0L) Long id,
                       UUID reference, @NotNull ZonedDateTime orderDate,
                       @NotNull CustomerDto customer, OrderStatusDto orderStatus) {
}
