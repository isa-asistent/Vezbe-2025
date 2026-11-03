package rs.ac.uns.isa.transactions.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rs.ac.uns.isa.transactions.model.Product;
import rs.ac.uns.isa.transactions.repository.projections.GetProjection;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // kroz JPQL ili native query se moze iz baze odraditi projekcija direktno u DTO objekat
    @Query("select p.id as id, p.name as name from Product p where p.id = :id")
    Optional<GetProjection.DTO> findProjectedDtoById(@Param("id") Long id);

    // kroz JPQL ili native query se moze iz baze odraditi projekcija direktno u interfejs
    @Query("select p.id as id, p.name as name from Product p where p.id = :id")
    Optional<GetProjection.Interface> findProjectedInterfaceById(@Param("id") Long id);
}
