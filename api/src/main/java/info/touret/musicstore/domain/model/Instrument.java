package info.touret.musicstore.domain.model;

/**
 * Represents a musical instrument available in the store.
 * An instrument has specific physical and commercial properties and is linked to a type.
 *
 * @param id           The technical identifier of the instrument
 * @param name         The name of the instrument
 * @param reference    The unique product reference for the instrument
 * @param manufacturer The company that manufactured the instrument
 * @param price        The sale price of the instrument
 * @param description  A brief description of the instrument
 * @param type         The type category to which the instrument belongs
 * @see InstrumentType
 */
public record Instrument(Long id, String name, String reference, String manufacturer, Double price, String description,
                         InstrumentType type, Double originalPrice, Boolean hasDiscount) {

    public Instrument(Long id, String name, String reference, String manufacturer, Double price, String description, InstrumentType type) {
        this(id, name, reference, manufacturer, price, description, type, null, null);
    }

    public Instrument withDiscount(Double discountedPrice, Double originalPrice) {
        return new Instrument(id, name, reference, manufacturer, discountedPrice, description, type, originalPrice, true);
    }

    @Override
    public String toString() {
        return "Instrument{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", reference='" + reference + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", price=" + price +
                ", description='" + description + '\'' +
                ", type=" + type +
                '}';
    }
}
