package rs.ac.uns.isa.transactions.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.ac.uns.isa.transactions.dto.Get;
import rs.ac.uns.isa.transactions.repository.ProductRepository;
import rs.ac.uns.isa.transactions.repository.projections.GetProjection;
import rs.ac.uns.isa.transactions.utils.TransactionInspector;

@Service
@RequiredArgsConstructor
public class ProductQueryServiceImpl implements ProductQueryService {
    private final ProductRepository repository;
    private final TransactionInspector txInspector;

    // Ovde zelimo da je transkacija READ ONLY. Ne zelimo da zahtevamo da Hibernate prati promene po entitetima, kako ih samo dobavljamo iz baze.
    // Ovo radimo u slucaju da vracamo entitet kao rezultat upita u bazu.
    @Override
    @Transactional(readOnly = true)
    public Get.Response get(Long id) {
        var product = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found!"));
        var response = new Get.Response(
                product.getId(),
                product.getName()
        );
        return response;
    }

    // Kako iz repozitorijuma vracamo projekcije, u ovom slucaju DTO, nije neophodna @Transactional anotacija
    // iz razloga sto ne vracamo entitete kojima Hibernate upravlja.
    //  @Transactional(readOnly = true)
    @Override
    public Get.Response getWithDto(Long id) {
        GetProjection.DTO projection = repository
                .findProjectedDtoById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Product not found!")
                );

        return new Get.Response(
                projection.id(),
                projection.name()
        );
    }

    // Kako iz repozitorijuma vracamo projekcije, u ovom slucaju interfejs, nije neophodna @Transactional anotacija
    // iz razloga sto ne vracamo entitete kojima Hibernate upravlja.
    @Override
    public Get.Response getWithInterface(Long id) {
        GetProjection.Interface projection = repository
                .findProjectedInterfaceById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Product not found!")
                );
        return new Get.Response(
                projection.getId(),
                projection.getName()
        );
    }

}