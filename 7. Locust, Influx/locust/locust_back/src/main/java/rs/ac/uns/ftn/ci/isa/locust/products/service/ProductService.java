package rs.ac.uns.ftn.ci.isa.locust.products.service;

import rs.ac.uns.ftn.ci.isa.locust.products.dto.Get;

import java.util.List;

public interface ProductService {
    List<Get.Response> getByName(String name);
    List<Get.Response> getByPriceRange(Double lower, Double upper);
}
