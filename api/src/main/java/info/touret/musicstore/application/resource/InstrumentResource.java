package info.touret.musicstore.application.resource;

import info.touret.musicstore.application.data.InstrumentDto;
import info.touret.musicstore.application.mapper.InstrumentMapper;
import info.touret.musicstore.domain.service.InstrumentService;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.jboss.resteasy.reactive.ResponseStatus;
import org.jboss.resteasy.reactive.RestPath;

import java.util.List;
import java.util.Map;

@Path("/instruments")
public class InstrumentResource {

    private final InstrumentMapper instrumentMapper;
    private final InstrumentService instrumentService;

    @Inject
    public InstrumentResource(InstrumentMapper instrumentMapper, InstrumentService instrumentService) {
        this.instrumentMapper = instrumentMapper;
        this.instrumentService = instrumentService;
    }

    @Operation(summary = "Retrieve all instruments", description = "Retrieve all instruments")
    @APIResponse(responseCode = "200", description = "Instruments retrieved successfully")
    @APIResponse(responseCode = "500", description = "Internal server error")
    @GET
    public List<InstrumentDto> retrieveInstruments() {
        return instrumentMapper.toInstrumentDtos(instrumentService.findInstruments());
    }


    @Operation
    @APIResponse(responseCode = "200", description = "Instrument created successfully")
    @APIResponse(responseCode = "500", description = "Internal server error")
    @POST
    @ResponseStatus(201)
    public Map<String, String> createInstrument(@NotNull InstrumentDto instrumentDto) {
        if(instrumentDto.id() != null){
            throw new IllegalArgumentException("Instrument id must be null for creation");
        }
        return Map.of("instrumentId", instrumentService.createInstrument(instrumentMapper.toInstrument(instrumentDto)).reference());
    }

    @Operation
    @APIResponse(responseCode = "200", description = "Instrument updated successfully")
    @PUT
    @Path("/{instrumentId}")
    public InstrumentDto updateInstrument(@NotNull @RestPath("instrumentId") Long instrumentId,
                                          @NotNull @RequestBody(required = true,
                                                  content = @Content(mediaType = MediaType.APPLICATION_JSON,
                                                          schema = @Schema(implementation = InstrumentDto.class))) InstrumentDto instrumentDto) {
        if (!instrumentDto.id().equals(instrumentId)) {
            throw new IllegalArgumentException(String.format("Instrument id %s does not match %s", instrumentId, instrumentDto.id()));
        }
        return instrumentMapper.toInstrumentDto(instrumentService.updateInstrument(instrumentMapper.toInstrument(instrumentDto)));
    }

    @Operation
    @APIResponse(responseCode = "200", description = "Instrument deleted successfully")
    @DELETE
    @Path("/{instrumentId}")
    public void deleteInstrument(@NotNull @RestPath("instrumentId") String instrumentId) {
        instrumentService.deleteInstrument(instrumentService.findById(Long.valueOf(instrumentId)));
    }
}
