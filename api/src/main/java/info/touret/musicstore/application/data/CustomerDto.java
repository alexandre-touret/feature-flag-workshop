package info.touret.musicstore.application.data;

import com.fasterxml.jackson.annotation.JsonRootName;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object representing a Customer in the API.
 */
@JsonRootName("Customer")
public record CustomerDto(@Min(0) Long id, @NotEmpty String firstname, @NotEmpty String lastname, @NotEmpty String email,
                          @NotNull AddressDto address) {
}
