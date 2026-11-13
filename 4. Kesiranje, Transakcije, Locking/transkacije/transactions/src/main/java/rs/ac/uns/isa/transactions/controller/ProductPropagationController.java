package rs.ac.uns.isa.transactions.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.isa.transactions.dto.Create;
import rs.ac.uns.isa.transactions.dto.Update;
import rs.ac.uns.isa.transactions.service.ProductService;
import rs.ac.uns.isa.transactions.utils.TransactionInspector;

import java.net.URI;

/**
 * Ova klasa demonstrira vrste propagacija transkacija prilikom modifikujucih operacija nad entitetima.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("api/products")
public class ProductPropagationController {
    private final ProductService productService;
    private final TransactionInspector txInspector;

    @PostMapping
    public ResponseEntity<Create.Response> create(@RequestBody Create.Request request) {
        txInspector.log("create in controller method");
        var response = productService.create(request);
        return ResponseEntity
                .created(URI.create("/api/products/" + response.id()))
                .body(response);
    }

    // ---------- REQUIRED ----------
    // withoutTx: servis otvara novu transakciju
    @PutMapping("required/withoutTx/{id}")
    public ResponseEntity<Update.Required.Response> updateRequiredWithoutTx(
            @PathVariable("id") Long id,
            @RequestBody Update.Required.Request request) {
        txInspector.log("update required WITHOUT tx in controller method");
        return ResponseEntity.ok(productService.updateRequired(id, request));
    }

    // withTx: servis se pridruzuje transakciji kontrolera (isto ime transakcije u logu)
    @PutMapping("required/withTx/{id}")
    @Transactional
    public ResponseEntity<Update.Required.Response> updateRequiredWithTx(
            @PathVariable("id") Long id,
            @RequestBody Update.Required.Request request) {
        txInspector.log("update required WITH tx in controller method");
        return ResponseEntity.ok(productService.updateRequired(id, request));
    }

    // ---------- REQUIRES_NEW ----------
    // withoutTx: servis otvara novu transakciju
    @PutMapping("requiresNew/withoutTx/{id}")
    public ResponseEntity<Update.RequiresNew.Response> updateRequiresNewWithoutTx(
            @PathVariable("id") Long id,
            @RequestBody Update.RequiresNew.Request request) {
        txInspector.log("update requires new WITHOUT tx in controller method");
        return ResponseEntity.ok(productService.updateRequiresNew(id, request));
    }

    // withTx: spoljna se suspenduje, servis radi u novoj, nezavisnoj transakciji (drugo ime u logu)
    @PutMapping("requiresNew/withTx/{id}")
    @Transactional
    public ResponseEntity<Update.RequiresNew.Response> updateRequiresNewWithTx(
            @PathVariable("id") Long id,
            @RequestBody Update.RequiresNew.Request request) {
        txInspector.log("update requires new WITH tx in controller method");
        return ResponseEntity.ok(productService.updateRequiresNew(id, request));
    }

    // ---------- MANDATORY ----------
    // withoutTx: baca IllegalTransactionStateException
    @PutMapping("mandatory/withoutTx/{id}")
    public ResponseEntity<Update.Mandatory.Response> updateMandatoryWithoutTx(
            @PathVariable("id") Long id,
            @RequestBody Update.Mandatory.Request request) {
        txInspector.log("update mandatory WITHOUT tx in controller method");
        return ResponseEntity.ok(productService.updateMandatory(id, request));
    }

    // withTx: servis se pridruzuje transakciji kontrolera
    @PutMapping("mandatory/withTx/{id}")
    @Transactional
    public ResponseEntity<Update.Mandatory.Response> updateMandatoryWithTx(
            @PathVariable("id") Long id,
            @RequestBody Update.Mandatory.Request request) {
        txInspector.log("update mandatory WITH tx in controller method");
        return ResponseEntity.ok(productService.updateMandatory(id, request));
    }

    // ---------- NEVER ----------
    // withoutTx: radi bez transakcije (izmena se NE cuva u bazi)
    @PutMapping("never/withoutTx/{id}")
    public ResponseEntity<Update.Never.Response> updateNeverWithoutTx(
            @PathVariable("id") Long id,
            @RequestBody Update.Never.Request request) {
        txInspector.log("update never WITHOUT tx in controller method");
        return ResponseEntity.ok(productService.updateNever(id, request));
    }

    // withTx: baca IllegalTransactionStateException
    @PutMapping("never/withTx/{id}")
    @Transactional
    public ResponseEntity<Update.Never.Response> updateNeverWithTx(
            @PathVariable("id") Long id,
            @RequestBody Update.Never.Request request) {
        txInspector.log("update never WITH tx in controller method");
        return ResponseEntity.ok(productService.updateNever(id, request));
    }

    // ---------- SUPPORTS ----------
    // withoutTx: radi bez transakcije (izmena se NE cuva u bazi)
    @PutMapping("supports/withoutTx/{id}")
    public ResponseEntity<Update.Supports.Response> updateSupportsWithoutTx(
            @PathVariable("id") Long id,
            @RequestBody Update.Supports.Request request) {
        txInspector.log("update supports WITHOUT tx in controller method");
        return ResponseEntity.ok(productService.updateSupports(id, request));
    }

    // withTx: pridruzuje se transakciji kontrolera, izmena se cuva
    @PutMapping("supports/withTx/{id}")
    @Transactional
    public ResponseEntity<Update.Supports.Response> updateSupportsWithTx(
            @PathVariable("id") Long id,
            @RequestBody Update.Supports.Request request) {
        txInspector.log("update supports WITH tx in controller method");
        return ResponseEntity.ok(productService.updateSupports(id, request));
    }

    // ---------- NOT_SUPPORTED ----------
    // withoutTx: radi bez transakcije (izmena se NE cuva u bazi)
    @PutMapping("notSupported/withoutTx/{id}")
    public ResponseEntity<Update.NotSupported.Response> updateNotSupportedWithoutTx(
            @PathVariable("id") Long id,
            @RequestBody Update.NotSupported.Request request) {
        txInspector.log("update not supported WITHOUT tx in controller method");
        return ResponseEntity.ok(productService.updateNotSupported(id, request));
    }

    // withTx: spoljna se suspenduje, metoda radi bez transakcije, bez izuzetka (izmena se NE cuva)
    @PutMapping("notSupported/withTx/{id}")
    @Transactional
    public ResponseEntity<Update.NotSupported.Response> updateNotSupportedWithTx(
            @PathVariable("id") Long id,
            @RequestBody Update.NotSupported.Request request) {
        txInspector.log("update not supported WITH tx in controller method");
        return ResponseEntity.ok(productService.updateNotSupported(id, request));
    }

    // ---------- NESTED ----------
    // Spoljna transakcija (kontroler) ubacuje "outer" transkacija, a unutrasnja NESTED metoda ubacuje "inner" transakcija i baca izuzetak.
    // Izuzetak se hvata, rollback ide samo do savepoint-a, pa u bazi ostaje samo "outer".
    @PostMapping("nested/withTx")
    @Transactional
    public ResponseEntity<Void> nestedCaughtWithTx() {
        txInspector.log("nested caught WITH tx in controller method");
        productService.create(new Create.Request("outer"));
        try {
            productService.createNestedAndFail("inner");
        } catch (IllegalStateException e) {
            txInspector.log("nested failed, exception caught in controller");
        }
        return ResponseEntity.noContent().build();
    }
}