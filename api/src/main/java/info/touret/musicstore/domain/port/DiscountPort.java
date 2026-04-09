package info.touret.musicstore.domain.port;

import info.touret.musicstore.domain.model.Instrument;
import info.touret.musicstore.domain.model.Result;
import info.touret.musicstore.domain.model.User;

public interface DiscountPort {

    Result<Instrument> applyDiscount(Instrument instrument, User user);
}
