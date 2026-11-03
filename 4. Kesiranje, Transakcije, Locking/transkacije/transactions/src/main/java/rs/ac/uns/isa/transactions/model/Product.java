package rs.ac.uns.isa.transactions.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "products")
public class Product {
    @Id
    @Column(name = "id",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Ovaj konstruktor se koristisamo za kreiranje
     */
    private Product(String name) {
        this.id = null;
        this.name = name;
    }

    public static Product Create(String name) {
        return new Product(name);
    }
}
