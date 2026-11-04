package rs.ac.uns.isa.transactions.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import rs.ac.uns.isa.transactions.dto.Create;
import rs.ac.uns.isa.transactions.dto.Update;
import rs.ac.uns.isa.transactions.model.Product;
import rs.ac.uns.isa.transactions.repository.ProductRepository;
import rs.ac.uns.isa.transactions.utils.TransactionInspector;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository repository;
    private final TransactionInspector txInspector;

    // Default vrednost propagacije je Propagation.Required, nije je neophodno navesti u
    // @Transactional ako zelimo bas tu vrstu propagacije transakcije.
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Create.Response create(Create.Request request) {
        txInspector.log("Create method in service");
        var name = request.name();
        var product = Product.Create(name);
        var savedProduct = repository.save(product);
        return new Create.Response(
                savedProduct.getId(),
                savedProduct.getName()
        );
    }

    // Propagation.REQUIRES_NEW:
// 1. Ako postoji transakcija, suspenduje je i otvara potpuno NOVU, nezavisnu transakciju
//    (sa svojom konekcijom). Kada se unutrasnja zavrsi (commit ili rollback), spoljna se nastavlja.
// 2. Unutrasnja i spoljna transakcija su nezavisne: rollback spoljne NE ponistava
//    komitovane promene unutrasnje, i obrnuto.
// 3. Ako transakcija ne postoji, ponasa se isto kao REQUIRED (otvara novu).
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Update.RequiresNew.Response updateRequiresNew(Long id, Update.RequiresNew.Request request) {
        txInspector.log("update requires new in service method");

        var product = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        var newName = request.name();
        product.setName(newName);
        return new Update.RequiresNew.Response(
                product.getId(),
                product.getName()
        );
    }

    // Propagation.REQUIRED (podrazumevana vrednost):
// 1. Ako transakcija postoji, metoda joj se PRIDRUZUJE i koristi istu konekciju i isti persistence context.
//    Ne otvara se nova transakcija.
// 2. Ako transakcija ne postoji, otvara se nova, koja traje do kraja metode.
// 3. Spoljna i unutrasnja metoda dele ISTU transakciju: commit ili rollback vazi za sve zajedno.
//    Ako unutrasnja metoda baci izuzetak, transakcija se oznacava kao rollback-only, pa se cak i
//    kada spoljna metoda uhvati izuzetak, na kraju ponistava sve (UnexpectedRollbackException).
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Update.Required.Response updateRequired(Long id, Update.Required.Request request) {
        txInspector.log("update required in service method");

        var product = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        var newName = request.name();
        product.setName(newName);
        return new Update.Required.Response(
                product.getId(),
                product.getName()
        );
    }
    // Propagation.SUPPORTS:
// 1. Ako transakcija postoji, metoda joj se PRIDRUZUJE (ponasa se kao REQUIRED).
// 2. Ako transakcija ne postoji, metoda se izvrsava BEZ transakcije (ne baca izuzetak, za razliku od MANDATORY).
//    findById tada radi u sopstvenoj kratkoj transakciji i vraca detached entitet, pa setName
//    nece biti sacuvan u bazi (isto kao kod NEVER).
// Razlika u odnosu na NEVER: SUPPORTS prihvata oba slucaja, NEVER baca izuzetak ako transakcija postoji.
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Update.Supports.Response updateSupports(Long id, Update.Supports.Request request) {
        txInspector.log("update supports in service method");

        var product = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        product.setName(request.name());
        return new Update.Supports.Response(product.getId(), product.getName());
    }

    // Propagation.MANDATORY, zahteva da postoji postojeca transakcija prilikom pozovia ovemetode,
    // Ukoliko transakcija ne postoji, baca se IllegalTransactionStateException izuzetak
    // Ukoliko transakcija postoji, prikljucuje se postojecoj
    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Update.Mandatory.Response updateMandatory(Long id, Update.Mandatory.Request request) {
        txInspector.log("update mandatory in service method");

        var product = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        var newName = request.name();
        product.setName(newName);
        return new Update.Mandatory.Response(
                product.getId(),
                product.getName()
        );
    }

    // Propagation.NOT_SUPPORTED:
// 1. Ako transakcija postoji, SUSPENDUJE je i izvrsava metodu bez transakcije. Po zavrsetku
//    metode, spoljna transakcija se nastavlja.
// 2. Ako transakcija ne postoji, metoda se izvrsava bez transakcije.
// Razlika u odnosu na NEVER: NOT_SUPPORTED ne baca izuzetak kada transakcija postoji, vec je zaobilazi.
// Izmene nisu sacuvane (detached entitet), osim ako se eksplicitno pozove repository.save(product).
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @Override
    public Update.NotSupported.Response updateNotSupported(Long id, Update.NotSupported.Request request) {
        txInspector.log("update not supported in service method");

        var product = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        product.setName(request.name());
        return new Update.NotSupported.Response(product.getId(), product.getName());
    }

    // Propagation.NESTED:
// 1. Ako transakcija postoji, otvara se SAVEPOINT unutar nje (ista konekcija, ista transakcija).
//    Ako unutrasnja metoda baci izuzetak, rollback ide samo do savepoint-a, a spoljna transakcija
//    moze da nastavi i da komituje.
// 2. Ako transakcija ne postoji, ponasa se kao REQUIRED (otvara novu).
// Razlika u odnosu na REQUIRES_NEW: nema nove konekcije, a ako spoljna transakcija ucini rollback,
// ponistava se i sve sto je unutrasnja vec "komitovala" (savepoint se oslobadja tek sa spoljnim commit-om).
    @Transactional(propagation = Propagation.NESTED)
    @Override
    public void createNestedAndFail(String name) {
        txInspector.log("create nested in service method");
        repository.save(Product.Create(name));
        throw new IllegalStateException("nested failed");
    }

    // Propagation.NEVER: zahteva da NE postoji transakcija.
// - Ako postoji, baca se IllegalTransactionStateException.
// - Ako ne postoji, metoda se izvrsava bez transakcije. findById se izvrsava u sopstvenoj
//   kratkoj transakciji i vraca detached entitet, pa setName menja objekat koji niko ne flush-uje.
//   Zato izmena NIJE sacuvana u bazi, iako odgovor prikazuje novo ime.
    @Override
    @Transactional(propagation = Propagation.NEVER)
    public Update.Never.Response updateNever(Long id, Update.Never.Request request) {
        txInspector.log("update never in service method");

        var product = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        var newName = request.name();
        product.setName(newName);

        // Ovo se mora pozvati ako zelimo da sacuvamo promene,  Propagation.NEVER ce izvristi metod
        // u netransakcionom rezimu, te se promene nece sacuvati, jer ih ORM ne prati.
        // repository.save(product);
        return new Update.Never.Response(
                product.getId(),
                product.getName()
        );
    }

}
