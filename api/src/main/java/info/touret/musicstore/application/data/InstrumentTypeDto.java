package info.touret.musicstore.application.data;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("InstrumentType")
public enum InstrumentTypeDto {
    GUITAR,PIANO;
}
