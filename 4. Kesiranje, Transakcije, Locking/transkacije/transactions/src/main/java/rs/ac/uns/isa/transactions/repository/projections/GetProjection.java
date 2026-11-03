package rs.ac.uns.isa.transactions.repository.projections;

public class GetProjection {
    public interface Interface{
        Long getId();

        String getName();
    }
    public record DTO(Long id, String name){

    }
}
