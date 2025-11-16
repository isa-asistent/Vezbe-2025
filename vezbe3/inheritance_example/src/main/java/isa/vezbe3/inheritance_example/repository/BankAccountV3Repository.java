package isa.vezbe3.inheritance_example.repository;

import isa.vezbe3.inheritance_example.v3.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountV3Repository extends JpaRepository<BankAccount, Integer> {

}
