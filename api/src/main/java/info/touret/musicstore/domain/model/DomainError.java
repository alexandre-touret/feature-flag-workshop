package info.touret.musicstore.domain.model;

public sealed interface DomainError {
    record DataNotFound(String message) implements DomainError {}
    record InvalidData(String message) implements DomainError {}
    record Conflict(String message) implements DomainError {}
}
