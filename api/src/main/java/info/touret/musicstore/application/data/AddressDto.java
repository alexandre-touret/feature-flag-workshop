package info.touret.musicstore.application.data;

import com.fasterxml.jackson.annotation.JsonRootName;
import jakarta.validation.constraints.NotEmpty;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Data Transfer Object representing an Address in the API.
 */
@JsonRootName("Address")
@Schema(name = "Address", description = "Address data")
public record AddressDto(@NotEmpty String number, @NotEmpty String street, @NotEmpty String city,
                         @NotEmpty String zipCode, @NotEmpty String country) {
}
