package com.example.txpessimistic.controller;

import com.example.txpessimistic.model.Product;
import com.example.txpessimistic.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping(value = "/products/",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Product addProduct(@RequestBody Product product) {
        productService.save(product);
        return product;
    }

    @PutMapping(value = "/products/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Product> updateProduct(@RequestBody Product product) throws PessimisticLockingFailureException {
        Product updatedProduct = productService.update(product);
        return new ResponseEntity<Product>(updatedProduct, HttpStatus.OK);
    }

    @DeleteMapping(value = "/products/{id}")
    public void deleteProduct(@PathVariable int id) {
        productService.delete((long) id);
    }

    @GetMapping(value = "/products/")
    public List<Product> getProducts() {
        return productService.findAll();
    }

    @GetMapping(value = "/products/{id}")
    public Product getProduct(@PathVariable int id) throws Exception {
        return productService.findById((long) id);
    }
}
