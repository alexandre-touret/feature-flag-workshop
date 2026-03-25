package info.touret.musicstore.infrastructure.database.entity;

import info.touret.musicstore.domain.model.InstrumentType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "instruments")
@NamedQueries({
        @NamedQuery(name = "InstrumentEntity.search", query = "from InstrumentEntity where lower(name) LIKE lower(concat('%',?1,'%')) OR lower(reference) LIKE lower(concat('%',?1,'%')) or lower(manufacturer) LIKE lower(concat('%',?1,'%'))")})
public class InstrumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    private String name;
    @NotEmpty
    private String reference;
    @NotEmpty
    private String manufacturer;
    @Min(0)
    private Double price;
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    private InstrumentType type;

    public InstrumentEntity() {
    }

    public InstrumentEntity(Long id, String name, String reference, String manufacturer, Double price, String description, InstrumentType type) {
        this.id = id;
        this.name = name;
        this.reference = reference;
        this.manufacturer = manufacturer;
        this.price = price;
        this.description = description;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public InstrumentType getType() {
        return type;
    }

    public void setType(InstrumentType type) {
        this.type = type;
    }
}
