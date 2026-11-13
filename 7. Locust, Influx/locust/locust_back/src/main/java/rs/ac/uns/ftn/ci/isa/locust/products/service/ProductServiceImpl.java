package rs.ac.uns.ftn.ci.isa.locust.products.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.ci.isa.locust.products.dto.Get;
import rs.ac.uns.ftn.ci.isa.locust.products.repository.ProductRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository repository;

    @Override
    public List<Get.Response> getByName(String name) {
        var response = repository.getAllByName(name);
        return response.stream()
                .map(p -> new Get.Response(
                        p.getId(),
                        p.getName(),
                        p.getPrice()
                )).toList();
    }

    @Override
    public List<Get.Response> getByPriceRange(Double lower, Double upper) {
        var response = repository.getAllByPriceRange(lower, upper);
        return response.stream()
                .map(p -> new Get.Response(
                        p.getId(),
                        p.getName(),
                        p.getPrice()
                )).toList();
    }
}
