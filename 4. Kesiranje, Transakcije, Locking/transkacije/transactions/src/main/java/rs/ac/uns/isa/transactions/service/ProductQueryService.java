package rs.ac.uns.isa.transactions.service;

import rs.ac.uns.isa.transactions.dto.Get;

/**
 * Ovaj servis demonstrira nacina citanja entiteta iz baze podataka.
 * Da li se to odvijalo putem READ ONLY traskacija ili dto/interfejs projekacija
 */
public interface ProductQueryService{
    Get.Response get(Long id);
    Get.Response getWithDto(Long id);
    Get.Response getWithInterface(Long id);

}
