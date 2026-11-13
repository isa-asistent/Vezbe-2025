package rs.ac.uns.isa.transactions.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.ac.uns.isa.transactions.dto.Create;
import rs.ac.uns.isa.transactions.dto.Update;
import rs.ac.uns.isa.transactions.service.ProductService;

@Service
@RequiredArgsConstructor
public class DemoService {
    private final ProductService products;

    @Transactional
    public void supportsInsideTx(Long id, Update.Supports.Request r) { products.updateSupports(id, r); }

    @Transactional
    public void notSupportedInsideTx(Long id, Update.NotSupported.Request r) { products.updateNotSupported(id, r); }

    @Transactional
    public void nestedCaught() {
        products.create(new Create.Request("outer"));
        try { products.createNestedAndFail("inner"); } catch (IllegalStateException ignored) {}
    }
}