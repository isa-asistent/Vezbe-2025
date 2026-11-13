package isa.vezbe3.inheritance_example.repository;

import isa.vezbe3.inheritance_example.v2.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountV2Repository extends JpaRepository<BankAccount, Integer> {

}
