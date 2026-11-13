package isa.vezbe3.inheritance_example.repository;

import isa.vezbe3.inheritance_example.v1.CreditCard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreditCardV1Repository extends JpaRepository<CreditCard, Integer> {

}
