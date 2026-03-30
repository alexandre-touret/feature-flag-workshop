package info.touret.musicstore.application.data;

import com.fasterxml.jackson.annotation.JsonRootName;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@JsonRootName("Order")
public record OrderDto(@NotNull List<InstrumentDto> instruments, @Min(0L) Long id,
                       @org.hibernate.validator.constraints.UUID UUID reference, @NotNull ZonedDateTime orderDate,
                       @NotNull CustomerDto customer, OrderStatusDto orderStatus) {
}
