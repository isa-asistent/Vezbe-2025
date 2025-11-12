package isa.vezbe1.rest_example.repository;

import isa.vezbe1.rest_example.domain.Greeting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;



public interface GreetingRepository {

    Collection<Greeting> findAll();

    Greeting create(Greeting greeting);

    Greeting findOne(Long id);

    ArrayList<Greeting> searchByText(Optional<String> text);

    Greeting update(Greeting greeting);

    Greeting delete(Long id);

}
