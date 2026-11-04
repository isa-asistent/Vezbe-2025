package rs.ac.uns.isa.transactions.service;

import rs.ac.uns.isa.transactions.dto.Create;
import rs.ac.uns.isa.transactions.dto.Update;

/**
 * Ovaj servis enkapsulira sve metode koje sluze za demonstraciju razlicitih propagacija transakcija.
 */
public interface ProductService {

    Create.Response create(Create.Request request);

    Update.RequiresNew.Response updateRequiresNew(Long id, Update.RequiresNew.Request request);

    Update.Required.Response updateRequired(Long id, Update.Required.Request request);

    Update.Supports.Response updateSupports(Long id, Update.Supports.Request request);

    Update.Mandatory.Response updateMandatory(Long id, Update.Mandatory.Request request);

    Update.NotSupported.Response updateNotSupported(Long id, Update.NotSupported.Request request);

    void createNestedAndFail(String name);

    Update.Never.Response updateNever(Long id, Update.Never.Request request);

}
