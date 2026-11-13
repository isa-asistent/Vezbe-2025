package rs.ac.uns.ftn.ci.isa.locust.products.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.ci.isa.locust.products.dto.Get;
import rs.ac.uns.ftn.ci.isa.locust.products.service.ProductService;

import java.util.List;

@CrossOrigin(value = "*")
@RestController
@RequestMapping("api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService service;

    @GetMapping("byName")
    public ResponseEntity<List<Get.Response>> getByName(
            @RequestParam(name = "name", defaultValue = "") String name
    ) {
        var response = service.getByName(name);
        return ResponseEntity.ok(response);
    }

    @GetMapping("byPriceRange")
    public ResponseEntity<List<Get.Response>> getByPriceRange(
            @RequestParam(name = "lower", defaultValue = "") Double lower,
            @RequestParam(name = "upper", defaultValue = "") Double upper

    ) {
        var response = service.getByPriceRange(lower, upper);
        return ResponseEntity.ok(response);
    }
}
