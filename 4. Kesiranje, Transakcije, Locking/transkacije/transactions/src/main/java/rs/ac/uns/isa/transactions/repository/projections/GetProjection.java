package rs.ac.uns.isa.transactions.repository.projections;


// Kako bi smo izbegli da Hibernate proverava da li su nasi entiteti "dirani" kada ih dobavimo iz baze,
// mozemo koristiti projekcije.
// Projekcije se mogu realizovati kao DTO ili kao Interfejsi. Ovime zaobilazimo Hibernate mehanizam provere stanja entiteta.
// Ovo je pozeljno u slucaju da nas use-case zahteva samo dobavlanje entiteta iz baze i njihovo prezentovanje.
public class GetProjection {
    public interface Interface{
        Long getId();

        String getName();
    }
    public record DTO(Long id, String name){

    }
}
