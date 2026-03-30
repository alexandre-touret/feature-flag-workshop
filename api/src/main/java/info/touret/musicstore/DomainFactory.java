package info.touret.musicstore;

import info.touret.musicstore.domain.port.InstrumentPort;
import info.touret.musicstore.domain.port.OrderPort;
import info.touret.musicstore.domain.service.InstrumentService;
import info.touret.musicstore.domain.service.OrderService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Produces;

@ApplicationScoped
public class DomainFactory {

    @ApplicationScoped
    @Produces
    public InstrumentService createInstrumentService(InstrumentPort instrumentPort) {
        return new InstrumentService(instrumentPort);
    }

    @ApplicationScoped
    @Produces
    public OrderService createOrderService(OrderPort orderPort) {
        return new OrderService(orderPort);
    }
}
