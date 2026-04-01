package info.touret.musicstore.domain.model;

/**
 * Represents a customer of the music store.
 * A customer can place orders and contains contact information and an address.
 * 
 * @param id The technical identifier of the customer
 * @param firstname The first name of the customer
 * @param lastname The last name of the customer
 * @param email The contact email address of the customer
 * @param address The physical address of the customer
 */
public record Customer (Long id, String firstname, String lastname, String email, Address address){
}
