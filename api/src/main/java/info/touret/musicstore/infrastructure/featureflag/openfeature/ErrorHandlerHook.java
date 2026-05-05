package info.touret.musicstore.infrastructure.featureflag.openfeature;

import dev.openfeature.sdk.BooleanHook;
import dev.openfeature.sdk.EvaluationContext;
import dev.openfeature.sdk.HookContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

public class ErrorHandlerHook implements BooleanHook {
    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorHandlerHook.class);

    @Override
    public Optional<EvaluationContext> before(HookContext<Boolean> ctx, Map<String, Object> hints) {
        LOGGER.info(">>> Before evaluating boolean flag: {}", ctx.getFlagKey());
        return BooleanHook.super.before(ctx, hints);
    }

    @Override
    public void error(HookContext<Boolean> ctx, Exception error, Map<String, Object> hints) {
        LOGGER.error(">>> Unable to process this flag [{}]: {}", ctx.getFlagKey(), error.getMessage());
        BooleanHook.super.error(ctx, error, hints);
    }
}
