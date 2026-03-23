package info.touret.musicstore.domain.model;

public record Customer (Long id, String firstname, String lastname, String email, Address address){
}
