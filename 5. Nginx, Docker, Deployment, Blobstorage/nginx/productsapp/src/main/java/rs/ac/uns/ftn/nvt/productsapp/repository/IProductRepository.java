package rs.ac.uns.ftn.nvt.productsapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.ac.uns.ftn.nvt.productsapp.model.Product;

public interface IProductRepository extends JpaRepository<Product, Long> {
}
