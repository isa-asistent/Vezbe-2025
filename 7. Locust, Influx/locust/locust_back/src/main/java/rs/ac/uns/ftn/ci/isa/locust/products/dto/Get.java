package rs.ac.uns.ftn.ci.isa.locust.products.dto;

public class Get {
    public record Response(Long id, String name, Double price) {
    }
}
