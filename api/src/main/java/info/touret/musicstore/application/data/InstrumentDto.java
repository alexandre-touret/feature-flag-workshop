package info.touret.musicstore.application.data;

import com.fasterxml.jackson.annotation.JsonRootName;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@JsonRootName("Instrument")
public record InstrumentDto(@Min(0) Long id, @NotEmpty String name, @NotEmpty String reference,
                            @NotEmpty String manufacturer, @Min(0) Double price, @NotEmpty String description,
                            @NotNull InstrumentTypeDto type) {
}
