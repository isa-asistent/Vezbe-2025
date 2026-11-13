package isa.vezbe1.rest_example.service;

import isa.vezbe1.rest_example.domain.Greeting;import isa.vezbe1.rest_example.domain.Greeting;
import isa.vezbe1.rest_example.dto.GreetingDTO;
import isa.vezbe1.rest_example.dto.GreetingTextDTO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public interface GreetingService {
    Collection<Greeting> findAll();

    Greeting findOne(Long id);

    Greeting create(GreetingDTO greeting) throws Exception;

    Greeting update(GreetingDTO greeting) throws Exception;

    Greeting delete(Long id);

    Greeting updateGreetingText(GreetingTextDTO greetingDTO, long id) throws Exception;

    ArrayList<Greeting> searchGreetings(Optional<String> text);
}
