package info.touret.musicstore.domain.model;

/**
 * Represents a business or domain-level error that occurred during application execution.
 * These errors abstract away technical exceptions and represent meaningful domain issues.
 */
public sealed interface DomainError {
    /**
     * An error indicating that the requested data could not be found.
     * 
     * @param message A descriptive message about the missing data
     */
    record DataNotFound(String message) implements DomainError {}
    
    /**
     * An error indicating that the provided data is invalid and violates a business rule.
     * 
     * @param message A descriptive message about why the data is invalid
     */
    record InvalidData(String message) implements DomainError {}
    
    /**
     * An error indicating a conflict with the current state of the domain,
     * such as attempting to create a resource that already exists.
     * 
     * @param message A descriptive message about the nature of the conflict
     */
    record Conflict(String message) implements DomainError {}
}
