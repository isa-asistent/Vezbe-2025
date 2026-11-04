package rs.ac.uns.ftn.informatika.cache.service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import rs.ac.uns.ftn.informatika.cache.domain.Product;

public interface ProductService {

    List<Product> findAll();

    void saveProduct(Product product);

    /*
     * Anotacijom @Cacheable i nazivom "product"
     * naznaceno je da se objekti tipa Product koji se dobave
     * metodom findOne smestaju u kes kolekciju "product"
     * kao i u ehcache.xml konfiguraciji
     */
    @Cacheable(value ="product", key = "#id")
    Product findOne(long id);
    /**
     * Kada zelimo da obirsemo iz cache memorije neki zapis, koristimo anotaciju
     * {@link  CacheEvict}. U ovom konkretnom primeru prilikom poziva ove metode
     * brisu se svi zapisi u cache memoriji product (specificirano putem allEntries parametra)
     */
    @CacheEvict(cacheNames = {"product"}, key = "#id")
    void delete(long id);


    List<Product> findByName(String name);

    List<Product> findByPrice(long price);

    List<Product> findByPriceRange(long price1, long price2);
    /*
    Koriscenjem #name u SpEl (Spring Expression Language) definisemo kljuc pod kojim ce vrednost rezultata metode
    biti sacuvana u cache.
     */
    @Cacheable(
            value = "byNameMatch",
            key = "#name"
    )
    List<Product> findByNameMatch(String name);
    @Cacheable(
            value = "byNamedParam",
            key = "#name+':' + #origin + ':' + #price"
    )
    List<Product> findByNamedParam(String name, String origin, long price);

    /**
     * Kada zelimo da obirsemo iz cache memorije neki zapis, koristimo anotaciju
     * {@link  CacheEvict}. U ovom konkretnom primeru prilikom poziva ove metode
     * brisu se svi zapisi u cache memoriji product (specificirano putem allEntries parametra)
     */
    @CacheEvict(cacheNames = {"product"}, allEntries = true)
    void removeFromCache();

    /**
     * Ukoliko zelimo da dodamo nesto u cache memoriju mozemo koristiti anotaciju
     * {@link CachePut}. Po zavrsetku ove metode, nova vrednost entiteta ce biti sacuvana
     * u cache memoriju "product", pod kljucem koji odogovara {@code id}.
     * @param id identifikator proizvoda ciji se zapis zapisuje u kes memroiju
     */
    @CachePut(cacheNames = {"product"}, key = "#id")
    Product updateProduct(Long id, Product newProduct);
}
