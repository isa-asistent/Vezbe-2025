package rs.ac.uns.ftn.informatika.cache.service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import rs.ac.uns.ftn.informatika.cache.domain.Product;

public interface ProductService {

	@Cacheable("productList")
	List<Product> findAll();
	void saveProduct(Product product);
	
	/*
	 * Anotacijom @Cacheable i nazivom "product"
	 * naznaceno je da se objekti tipa Product koji se dobave
	 * metodom findOne smestaju u kes kolekciju "product"
	 * kao i u ehcache.xml konfiguraciji
	 * unless = "#result == null" - ne kesira null vrednosti
	 */
	@Cacheable(value = "product", unless = "#result == null")
	Product findOne(long id);
	
	@CacheEvict(cacheNames = {"product", "productList"}, allEntries = false)
	void delete(long id);

	List<Product> findByName(String name);
	List<Product> findByPrice(long price);
	List<Product> findByPriceRange(long price1, long price2);
	List<Product> findByNameMatch(String name);
	List<Product> findByNamedParam(String name, String origin, long price);
	
	@CacheEvict(cacheNames = {"product", "productList"}, allEntries = true)
	void removeFromCache();
}
