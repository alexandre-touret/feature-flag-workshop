package info.touret.musicstore.application.data;

import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * Data Transfer Object representing an Address in the API.
 */
@JsonRootName("Address")
public record AddressDto(String number, String street, String city, String zipCode, String country) {
}
