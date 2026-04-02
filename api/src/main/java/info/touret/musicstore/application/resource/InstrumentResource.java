package info.touret.musicstore.application.resource;

import info.touret.musicstore.application.data.InstrumentDto;
import info.touret.musicstore.application.mapper.InstrumentMapper;
import info.touret.musicstore.domain.model.Instrument;
import info.touret.musicstore.domain.model.Result;
import info.touret.musicstore.domain.service.InstrumentService;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.jboss.resteasy.reactive.RestPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * REST API Resource for managing instruments.
 * Exposes inbound HTTP endpoints and delegates business logic to the InstrumentService.
 */
@Path("/instruments")
public class InstrumentResource extends AbstractMusicStoreResource{
    private static final Logger LOGGER = LoggerFactory.getLogger(InstrumentResource.class);
    private final InstrumentMapper instrumentMapper;
    private final InstrumentService instrumentService;

    @Inject
    public InstrumentResource(InstrumentMapper instrumentMapper, InstrumentService instrumentService) {
        this.instrumentMapper = instrumentMapper;
        this.instrumentService = instrumentService;
    }

    @Operation(summary = "Retrieve all instruments", description = "Retrieve all instruments")
    @APIResponse(responseCode = "200", description = "Instruments retrieved successfully",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = InstrumentDto.class, type = SchemaType.ARRAY)))
    @APIResponse(responseCode = "500", description = "Internal server error")
    @GET
    @RunOnVirtualThread
    public Response retrieveInstruments() {
        var result = instrumentService.findInstruments();
        return handleResult(result, 200);
    }

    @Operation(summary = "Retrieve an instrument by ID", description = "Retrieve a specific instrument using its unique identifier")
    @APIResponse(responseCode = "200", description = "Instrument retrieved successfully",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = InstrumentDto.class)))
    @APIResponse(responseCode = "404", description = "Instrument not found",
            content = @Content(mediaType = MediaType.TEXT_PLAIN, schema = @Schema(implementation = String.class)))
    @APIResponse(responseCode = "500", description = "Internal server error")
    @GET
    @Path("/{instrumentId}")
    @RunOnVirtualThread
    public Response retrieveInstrument(@NotNull @RestPath("instrumentId") Long instrumentId) {
        var result = instrumentService.findById(instrumentId);
        return handleResult(result, 200);
    }


    @Operation
    @APIResponse(responseCode = "201", description = "Instrument created successfully",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Map.class)))
    @APIResponse(responseCode = "400", description = "Invalid data",
            content = @Content(mediaType = MediaType.TEXT_PLAIN, schema = @Schema(implementation = String.class)))
    @APIResponse(responseCode = "500", description = "Internal server error")
    @POST
    @RunOnVirtualThread
    public Response createInstrument(@NotNull InstrumentDto instrumentDto) {
        var result = instrumentService.createInstrument(instrumentMapper.toInstrument(instrumentDto));
        return handleResult(result, 201);
    }

    @Operation
    @APIResponse(responseCode = "200", description = "Instrument updated successfully",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = InstrumentDto.class)))
    @APIResponse(responseCode = "404", description = "Instrument not found",
            content = @Content(mediaType = MediaType.TEXT_PLAIN, schema = @Schema(implementation = String.class)))
    @PUT
    @Path("/{instrumentId}")
    @RunOnVirtualThread
    public Response updateInstrument(@NotNull @RestPath("instrumentId") Long instrumentId,
                                     @NotNull @RequestBody(
                                             content = @Content(mediaType = MediaType.APPLICATION_JSON,
                                                     schema = @Schema(implementation = InstrumentDto.class))) InstrumentDto instrumentDto) {
        if (!instrumentId.equals(instrumentDto.id())) {
            LOGGER.error("Instrument id does not match");
            return Response.status(400).entity("Instrument id does not match").build();
        }
        var result = instrumentService.updateInstrument(instrumentMapper.toInstrument(instrumentDto));
        return handleResult(result, 200);
    }

    @Operation
    @APIResponse(responseCode = "204", description = "Instrument deleted successfully")
    @DELETE
    @Path("/{instrumentId}")
    @RunOnVirtualThread
    public Response deleteInstrument(@NotNull @RestPath("instrumentId") String instrumentId) {
        var result = instrumentService.findById(Long.valueOf(instrumentId));
        if (result.isFailure()) {
            LOGGER.error("Instrument not found");
            return toErrorResponse(result.error());
        }
        instrumentService.deleteInstrument(result.value());
        return Response.noContent().build();
    }

    @GET
    @Path("/search")
    @Operation
    @APIResponse(responseCode = "200", description = "Instruments searched successfully",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = InstrumentDto.class, type = SchemaType.ARRAY)))
    @RunOnVirtualThread
    public Response search(@NotNull @QueryParam("q") String query) {
        return handleResult(Result.success(instrumentService.search(query).value()), 200);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Response handleResult(Result<?> result, int successStatus) {
        if (result.isSuccess()) {
            Object responseBody = result.value();
            return switch (responseBody) {
                case Instrument instrument when successStatus == 201 ->
                        Response.status(201).entity(Map.of("instrumentId", instrument.reference())).build();
                case Instrument instrument ->
                        Response.status(successStatus).entity(instrumentMapper.toInstrumentDto(instrument)).build();
                case List<?> list ->
                        Response.status(successStatus).entity(instrumentMapper.toInstrumentDtos((List<Instrument>) list)).build();
                default -> Response.status(successStatus).entity(responseBody).build();
            };
        }
        return toErrorResponse(result.error());
    }
}
