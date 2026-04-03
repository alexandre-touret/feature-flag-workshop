package info.touret.musicstore.application.data;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@JsonRootName("User")
@Schema(name = "User")
public record UserDto(@NotEmpty String firstName, @NotEmpty String lastName, @Email String email,
                      @NotEmpty String country) {

    @SuppressWarnings("unused")
    public static UserDto fromString(String headerValue) {
        if (headerValue == null || headerValue.isBlank()) {
            return null;
        }

        try {
            return new ObjectMapper().readValue(headerValue, UserDto.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(String.format("Invalid user header %s", headerValue), e);
        }

    }

    @Override
    public String toString() {
        return "UserDto{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
