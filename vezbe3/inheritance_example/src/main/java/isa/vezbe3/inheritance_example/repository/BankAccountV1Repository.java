package isa.vezbe3.inheritance_example.repository;

import isa.vezbe3.inheritance_example.v1.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountV1Repository extends JpaRepository<BankAccount, Integer> {

}
