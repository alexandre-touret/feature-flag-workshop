package info.touret.musicstore.domain.model;

/**
 * Represents a physical address for a customer or an order delivery.
 * 
 * @param number The street number
 * @param street The name of the street
 * @param city The city name
 * @param zipCode The postal or ZIP code
 * @param country The country name
 */
public record Address(String number, String street, String city, String zipCode, String country) {
    @Override
    public String toString() {
        return "Address{" +
                "number='" + number + '\'' +
                ", street='" + street + '\'' +
                ", city='" + city + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
