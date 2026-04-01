package info.touret.musicstore;

import info.touret.musicstore.domain.port.InstrumentPort;
import info.touret.musicstore.domain.port.OrderPort;
import info.touret.musicstore.domain.service.InstrumentService;
import info.touret.musicstore.domain.service.OrderService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Produces;

/**
 * Factory class responsible for instantiating domain services.
 * Registers domain services as CDI beans so they can be injected into the application layer.
 * Keeps the domain layer agnostic of the dependency injection framework.
 */
@ApplicationScoped
public class DomainFactory {

    /**
     * Produces an instance of InstrumentService for CDI injection.
     * 
     * @param instrumentPort the port to inject into the service
     * @return a fully constructed InstrumentService
     */
    @ApplicationScoped
    @Produces
    public InstrumentService createInstrumentService(InstrumentPort instrumentPort) {
        return new InstrumentService(instrumentPort);
    }

    /**
     * Produces an instance of OrderService for CDI injection.
     * 
     * @param orderPort the port to inject into the service
     * @return a fully constructed OrderService
     */
    @ApplicationScoped
    @Produces
    public OrderService createOrderService(OrderPort orderPort) {
        return new OrderService(orderPort);
    }
}
