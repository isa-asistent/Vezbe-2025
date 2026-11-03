package rs.ac.uns.ftn.nvt.productsapp.service;

import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.nvt.productsapp.model.Product;
import rs.ac.uns.ftn.nvt.productsapp.repository.IProductRepository;

import java.util.List;

@Service
public class ProductService {

    private final IProductRepository productRepository;

    public ProductService(IProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAll() {
        return productRepository.findAll();
    }
}
