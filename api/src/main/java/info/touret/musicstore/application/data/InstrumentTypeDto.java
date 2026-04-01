package info.touret.musicstore.application.data;

import com.fasterxml.jackson.annotation.JsonRootName;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Data Transfer Object representing an InstrumentType in the API.
 */
@JsonRootName("InstrumentType")
@Schema(name = "InstrumentType", description = "Instrument type data")
public enum InstrumentTypeDto {
    GUITAR, PIANO
}
