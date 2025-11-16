package isa.vezbe3.inheritance_example.repository;

import isa.vezbe3.inheritance_example.v2.CreditCard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreditCardV2Repository extends JpaRepository<CreditCard, Integer> {

}