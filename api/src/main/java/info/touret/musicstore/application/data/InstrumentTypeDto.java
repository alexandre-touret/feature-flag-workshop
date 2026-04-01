package info.touret.musicstore.application.data;

import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * Data Transfer Object representing an InstrumentType in the API.
 */
@JsonRootName("InstrumentType")
public enum InstrumentTypeDto {
    GUITAR,PIANO;
}
