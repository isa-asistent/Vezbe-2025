package tx.optimistic.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tx.optimistic.demo.model.Product;

public interface ProductRepository extends JpaRepository<Product,Long> {

}
