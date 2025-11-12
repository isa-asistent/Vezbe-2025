package isa.vezbe1.rest_example.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import isa.vezbe1.rest_example.domain.Greeting;
import isa.vezbe1.rest_example.dto.GreetingDTO;
import isa.vezbe1.rest_example.dto.GreetingTextDTO;
import isa.vezbe1.rest_example.repository.InMemoryGreetingRepository;

@Service
public class GreetingServiceImpl implements GreetingService {

    @Autowired
    private InMemoryGreetingRepository greetingRepository;

    @Override
    public Collection<Greeting> findAll() {
        Collection<Greeting> greetings = greetingRepository.findAll();
        return greetings;
    }

    @Override
    public Greeting findOne(Long id) {
        Greeting greeting = greetingRepository.findOne(id);
        return greeting;
    }

    @Override
    public Greeting create(GreetingDTO greeting) throws Exception {
        if (greeting.getId() != null) {
            throw new Exception("Id mora biti null prilikom perzistencije novog entiteta.");
        }
        Greeting savedGreeting = greetingRepository.create(new Greeting(greeting));
        return savedGreeting;
    }

    @Override
    public Greeting update(GreetingDTO greeting) throws Exception {
        Greeting greetingToUpdate = findOne(greeting.getId());
        if (greetingToUpdate == null) {
            throw new Exception("Trazeni entitet nije pronadjen.");
        }
        greetingToUpdate.setText(greeting.getText());
        greetingToUpdate.setAuthor(greeting.getAuthor());
        Greeting updatedGreeting = greetingRepository.create(greetingToUpdate);
        return updatedGreeting;
    }

    @Override
    public Greeting delete(Long id) {
        return greetingRepository.delete(id);
    }

    @Override
    public Greeting updateGreetingText(GreetingTextDTO greetingDTO, long greetingId) throws Exception {
        Greeting greetingToUpdate = findOne(greetingId);
        if (greetingToUpdate == null) {
            throw new Exception("Trazeni entitet nije pronadjen.");
        }

        greetingToUpdate.setText(greetingDTO.getText());
        return greetingToUpdate;
    }

    @Override
    public ArrayList<Greeting> searchGreetings(Optional<String> text) {
        ArrayList<Greeting> foundGreetings = greetingRepository.searchByText(text);
        return foundGreetings;
    }

}