package rs.ac.uns.isa.transactions.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.ac.uns.isa.transactions.dto.Get;
import rs.ac.uns.isa.transactions.service.ProductQueryService;
import rs.ac.uns.isa.transactions.utils.TransactionInspector;

/**
 * Ova klasa demonstrira koriscenje metoda cije su transkacije u READ ONLY rezimu, kao i
 * projekcije koje se povlace iz baze podataka.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("api/products")
public class ProductQueryController {
    private final ProductQueryService queryService;
    private final TransactionInspector txInspector;

    @GetMapping("{id}")
    public ResponseEntity<Get.Response> get(
            @PathVariable("id") Long id
    ) {
        txInspector.log("get in controller method");
        var response = queryService.get(id);
        return ResponseEntity
                .ok(response);
    }
    @GetMapping("projection/dto/{id}")
    public ResponseEntity<Get.Response> getWithDto(
            @PathVariable("id") Long id
    ) {
        txInspector.log("get dto projection in controller method");
        var response = queryService.getWithDto(id);
        return ResponseEntity
                .ok(response);
    }
    @GetMapping("projection/interface/{id}")
    public ResponseEntity<Get.Response> getWithInterface(
            @PathVariable("id") Long id
    ) {
        txInspector.log("get interface projection in controller method");
        var response = queryService.getWithInterface(id);
        return ResponseEntity
                .ok(response);
    }

}
