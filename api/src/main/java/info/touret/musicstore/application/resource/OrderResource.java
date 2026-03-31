package info.touret.musicstore.application.resource;

import info.touret.musicstore.application.data.OrderDto;
import info.touret.musicstore.application.mapper.OrderMapper;
import info.touret.musicstore.domain.model.DomainError;
import info.touret.musicstore.domain.model.Order;
import info.touret.musicstore.domain.model.Result;
import info.touret.musicstore.domain.service.OrderService;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.jboss.resteasy.reactive.RestPath;

import java.util.List;
import java.util.Map;

@Path("/orders")
public class OrderResource {

    private final OrderMapper orderMapper;
    private final OrderService orderService;

    @Inject
    public OrderResource(OrderMapper orderMapper, OrderService orderService) {
        this.orderMapper = orderMapper;
        this.orderService = orderService;
    }

    @Operation(summary = "Retrieve all orders", description = "Retrieve all orders from the music store")
    @APIResponse(responseCode = "200", description = "Orders retrieved successfully")
    @APIResponse(responseCode = "500", description = "Internal server error")
    @GET
    @RunOnVirtualThread
    public Response retrieveOrders() {
        return handleResult(orderService.findOrders(), 200);
    }

    @Operation(summary = "Retrieve an order by ID", description = "Retrieve a specific order using its unique identifier")
    @APIResponse(responseCode = "200", description = "Order retrieved successfully")
    @APIResponse(responseCode = "404", description = "Order not found")
    @APIResponse(responseCode = "500", description = "Internal server error")
    @GET
    @Path("/{id}")
    @RunOnVirtualThread
    public Response retrieveOrder(@NotNull @RestPath("id") Long id) {
        var result = orderService.findById(id);
        return handleResult(result, 200);
    }

    @Operation(summary = "Create a new order", description = "Create a new order in the music store")
    @APIResponse(responseCode = "201", description = "Order created successfully")
    @APIResponse(responseCode = "400", description = "Invalid request body")
    @APIResponse(responseCode = "500", description = "Internal server error")
    @POST
    @RunOnVirtualThread
    public Response createOrder(@NotNull @Valid OrderDto orderDto) {
        var result = orderService.createOrder(orderMapper.toOrder(orderDto));
        return handleResult(result, 201);
    }

    @Operation(summary = "Update an order", description = "Update an existing order using its unique identifier")
    @APIResponse(responseCode = "200", description = "Order updated successfully")
    @APIResponse(responseCode = "400", description = "Order ID mismatch")
    @APIResponse(responseCode = "404", description = "Order not found")
    @APIResponse(responseCode = "500", description = "Internal server error")
    @PUT
    @Path("/{id}")
    @RunOnVirtualThread
    public Response updateOrder(@NotNull @RestPath("id") Long id,
                                @NotNull @Valid @RequestBody(required = true,
                                        content = @Content(mediaType = MediaType.APPLICATION_JSON,
                                                schema = @Schema(implementation = OrderDto.class))) OrderDto orderDto) {
        if (!id.equals(orderDto.id())) {
            return Response.status(400).entity("Order id mismatch").build();
        }
        var result = orderService.updateOrder(orderMapper.toOrder(orderDto));
        return handleResult(result, 200);
    }

    @Operation(summary = "Delete an order", description = "Delete an order from the music store")
    @APIResponse(responseCode = "204", description = "Order deleted successfully")
    @APIResponse(responseCode = "404", description = "Order not found")
    @APIResponse(responseCode = "500", description = "Internal server error")
    @DELETE
    @Path("/{id}")
    @RunOnVirtualThread
    public Response deleteOrder(@NotNull @RestPath("id") String orderId) {
        var result = orderService.findById(Long.valueOf(orderId));
        if (result.isFailure()) {
            return toErrorResponse(result.error());
        }
        orderService.deleteOrder(result.value());
        return Response.noContent().build();
    }

    @Operation(summary = "Search orders", description = "Search for orders using a query string")
    @APIResponse(responseCode = "200", description = "Orders searched successfully")
    @APIResponse(responseCode = "400", description = "Invalid or empty query")
    @APIResponse(responseCode = "500", description = "Internal server error")
    @GET
    @Path("/search")
    @RunOnVirtualThread
    public Response search(@NotNull @QueryParam("q") String query) {
        var result = orderService.search(query);
        return handleResult(result, 200);

    }

    @SuppressWarnings("unchecked")
    private Response handleResult(Result<?> result, int successStatus) {
        if (result.isSuccess()) {
            Object responseBody = result.value();
            if (responseBody instanceof info.touret.musicstore.domain.model.Order order && successStatus == 201) {
                return Response.status(201).entity(Map.of("id", order.reference().toString())).build();
            }
            if (responseBody instanceof info.touret.musicstore.domain.model.Order order) {
                return Response.status(successStatus).entity(orderMapper.toOrderDto(order)).build();
            }
            if (responseBody instanceof List<?> list) {
                return Response.status(successStatus).entity(orderMapper.toOrderDtos((List<Order>) list)).build();
            }
            return Response.status(successStatus).entity(responseBody).build();
        }
        return toErrorResponse(result.error());
    }

    private Response toErrorResponse(DomainError error) {
        return switch (error) {
            case DomainError.DataNotFound e -> Response.status(404).entity(e.message()).build();
            case DomainError.InvalidData e -> Response.status(400).entity(e.message()).build();
            case DomainError.Conflict e -> Response.status(409).entity(e.message()).build();
        };
    }
}
