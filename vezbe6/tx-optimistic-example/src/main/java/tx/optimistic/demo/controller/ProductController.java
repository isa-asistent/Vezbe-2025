package tx.optimistic.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.*;
import tx.optimistic.demo.model.Product;
import tx.optimistic.demo.service.ProductService;

import java.util.List;

@RestController
@RequestMapping(value = "/api")
public class ProductController {

    @Autowired
    private ProductService productService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @PostMapping(value = "/products/",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Product addProduct(@RequestBody Product product){
        productService.save(product);
        return product;
    }

    @PutMapping(value = "/products/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Product> updateProduct(@RequestBody Product product) throws ObjectOptimisticLockingFailureException {
        Product updatedProduct = null;
        updatedProduct = productService.update(product);
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
    public Product getProduct(@PathVariable int id) {
        return productService.findById((long) id);
    }
}

