package info.touret.musicstore.domain.model;

import java.util.Objects;
import java.util.function.Function;

/**
 * Encapsulates the outcome of a domain operation. 
 * A result can be either successful with a generic value or represent a failure with a {@link DomainError}.
 *
 * @param <T> The type of the value held on a successful operation
 * @param value The value indicating success, null if the operation failed
 * @param error The domain error detailing the failure, null if the operation succeeded
 */
public record Result<T>(T value, DomainError error) {
    
    /**
     * Creates a Result representing a successful execution.
     * 
     * @param value The success value to be wrapped, must not be null
     * @return A Result containing the success value
     * @param <T> The type of the value
     */
    public static <T> Result<T> success(T value) {
        return new Result<>(Objects.requireNonNull(value), null);
    }

    /**
     * Creates a Result representing a failed execution.
     * 
     * @param error The domain error representing the failure reason, must not be null
     * @return A Result indicating failure
     * @param <T> The type context
     */
    public static <T> Result<T> failure(DomainError error) {
        return new Result<>(null, Objects.requireNonNull(error));
    }

    /**
     * @return True if the operation was successful and holds a value.
     */
    public boolean isSuccess() {
        return error == null;
    }

    /**
     * @return True if the operation failed and holds a domain error.
     */
    public boolean isFailure() {
        return error != null;
    }

    /**
     * Applies a mapping function to the successful value of this Result,
     * maintaining failure states unharmed.
     * 
     * @param mapper The function to transform the current successful value into a new value type
     * @return A Result instance holding the transformed value, or the original failure state
     * @param <U> The resulting type of the transformation
     */
    public <U> Result<U> map(Function<? super T, ? extends U> mapper) {
        if (isSuccess()) {
            return Result.success(mapper.apply(value));
        }
        return Result.failure(error);
    }
}
