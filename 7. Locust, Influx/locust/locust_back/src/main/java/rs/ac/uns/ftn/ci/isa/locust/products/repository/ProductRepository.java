package rs.ac.uns.ftn.ci.isa.locust.products.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rs.ac.uns.ftn.ci.isa.locust.products.model.Product;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("select p from Product p where p.name like concat('%', :name, '%')")
    List<Product> getAllByName(@Param("name") String name);
    @Query("select p from Product p where p.price between :lower and :upper")
    List<Product> getAllByPriceRange(@Param("lower") Double lower,@Param("upper") Double upper);
}