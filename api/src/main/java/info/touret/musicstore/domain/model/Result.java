package info.touret.musicstore.domain.model;

import java.util.Objects;
import java.util.function.Function;

public record Result<T>(T value, DomainError error) {
    public static <T> Result<T> success(T value) {
        return new Result<>(Objects.requireNonNull(value), null);
    }

    public static <T> Result<T> failure(DomainError error) {
        return new Result<>(null, Objects.requireNonNull(error));
    }

    public boolean isSuccess() {
        return error == null;
    }

    public boolean isFailure() {
        return error != null;
    }

    public <U> Result<U> map(Function<? super T, ? extends U> mapper) {
        if (isSuccess()) {
            return Result.success(mapper.apply(value));
        }
        return Result.failure(error);
    }
}
