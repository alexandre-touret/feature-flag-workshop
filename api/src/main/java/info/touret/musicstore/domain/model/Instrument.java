package info.touret.musicstore.domain.model;

public record Instrument(Long id, String name, String reference, String manufacturer, Double price, String description, InstrumentType type) {
}
