package tx.optimistic.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tx.optimistic.demo.model.Product;
import tx.optimistic.demo.repository.ProductRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ProductRepository productRepository;
    /*
     * Anotacija na nivou metode je specificnija od one na nivou klase, tako da ce se
     * pri izvrsavanju metode save aktivirati specificnija podesavanja. U ovom slucaju
     * readOnly parametar je postavljen na false jer metoda menja podatak (ne cita ga).
     */
    @Transactional(readOnly = false)
    public Product save(Product product) {
        logger.info("> create");
        Product savedProduct = productRepository.save(product);
        logger.info("< create");
        return savedProduct;
    }

    /*
     * Takodje se moze eksplicitno promeniti propagacija koja je podrazumevano REQUIRED.
     * Granice transakcije su sama metoda delete.
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void delete(long id) {

        logger.info("> delete");
        productRepository.deleteById(id);
        logger.info("< delete");
    }

    public Product findById(long id) {

        logger.info("> findById id:{}", id);
        Product product = productRepository.findById(id).get();
        logger.info("< findById id:{}", id);
        return product;
    }

    public List<Product> findAll() {

        logger.info("> findAll");
        List<Product> products = productRepository.findAll();
        logger.info("< findAll");
        return products;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public Product update(Product product) {
        logger.info("> update id:{}", product.getId());
        Product productToUpdate = productRepository.findById(product.getId()).orElseThrow();

        productToUpdate.setName(product.getName());
        productToUpdate.setOrigin(product.getOrigin());
        productToUpdate.setPrice(product.getPrice());

        logger.info("Product before update: {}", productToUpdate);
        productToUpdate = productRepository.save(productToUpdate);
        logger.info("< update finished for id:{}", product.getId());

        return productToUpdate;
    }

}
