package rs.ac.uns.isa.transactions.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Pomocna klasa koja sluzi kako bi se utvrdilo postojanje transakcije, inspekcija njene sadrzine i osobina.
 */
@Slf4j
@Component
public class TransactionInspector {

    public record TxInfo(boolean active, String name, boolean readOnly, Integer isolation) {}

    public TxInfo current() {
        return new TxInfo(
                TransactionSynchronizationManager.isActualTransactionActive(),
                TransactionSynchronizationManager.getCurrentTransactionName(),
                TransactionSynchronizationManager.isCurrentTransactionReadOnly(),
                TransactionSynchronizationManager.getCurrentTransactionIsolationLevel()
        );
    }

    public void log(String where) {
        log.info("[{}] {}", where, current());
    }

    public boolean isActive() {
        return TransactionSynchronizationManager.isActualTransactionActive();
    }
}