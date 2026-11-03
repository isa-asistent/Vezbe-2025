package rs.ac.uns.isa.transactions.dto;

public class Create {
    public record Request(String name){}
    public record Response(Long id, String name){}
}
