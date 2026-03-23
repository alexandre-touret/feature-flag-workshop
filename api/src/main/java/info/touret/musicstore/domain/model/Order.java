package info.touret.musicstore.domain.model;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public record Order(List<Instrument> instruments, Long orderId, UUID reference, ZonedDateTime orderDate,
                    Customer customer) {
}
