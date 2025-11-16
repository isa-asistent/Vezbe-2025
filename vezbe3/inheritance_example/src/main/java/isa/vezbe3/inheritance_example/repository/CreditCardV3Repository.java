package isa.vezbe3.inheritance_example.repository;

import isa.vezbe3.inheritance_example.v3.CreditCard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreditCardV3Repository extends JpaRepository<CreditCard, Integer> {

}