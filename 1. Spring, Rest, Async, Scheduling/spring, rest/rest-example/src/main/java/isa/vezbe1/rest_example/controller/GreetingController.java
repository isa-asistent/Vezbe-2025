package isa.vezbe1.rest_example.controller;

import isa.vezbe1.rest_example.domain.Greeting;
import isa.vezbe1.rest_example.dto.GreetingDTO;
import isa.vezbe1.rest_example.dto.GreetingTextDTO;
import isa.vezbe1.rest_example.service.GreetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;


/*
 * @RestController je anotacija nastala od @Controller tako da predstavlja bean komponentu.
 *
 * @RequestMapping anotacija ukoliko se napise iznad kontrolera oznacava da sve rute ovog kontrolera imaju navedeni prefiks.
 * U nasem primeru svaka rute kontrolera ima prefiks 'api/greetings'.
 *
 * OpenAPI dokumentacija se moze pogledati na: http://localhost:8080/swagger-ui/index.html
 */
@RestController
@RequestMapping("/api/greetings")
public class GreetingController {

    @Autowired
    private GreetingService greetingService;

    /*
     * Prilikom poziva metoda potrebno je navesti nekoliko parametara
     * unutar @@GetMapping anotacije: url kao vrednost 'value' atributa (ukoliko se
     * izostavi, ruta do metode je ruta do kontrolera), u slucaju GET zahteva
     * atribut 'produce' sa naznakom tipa odgovora (u nasem slucaju JSON).
     *
     * Kao povratna vrednost moze se vracati klasa ResponseEntity koja sadrzi i telo
     * (sam podatak) i zaglavlje (metapodatke) i status kod, ili samo telo ako se
     * metoda anotira sa @ResponseBody.
     *
     * url: /api/greetings GET
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<Greeting>> getGreetings() {
        Collection<Greeting> greetings = greetingService.findAll();
        return new ResponseEntity<Collection<Greeting>>(greetings, HttpStatus.OK);
    }


    /*
     * U viticastim zagradama se navodi promenljivi deo putanje.
     *
     * url: /api/greetings/1 GET
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Greeting> getGreeting(@PathVariable("id") Long id) {
        Greeting greeting = greetingService.findOne(id);

        if (greeting == null) {
            return new ResponseEntity<Greeting>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<Greeting>(greeting, HttpStatus.OK);
    }

    /*
     * Prilikom poziva metoda potrebno je navesti nekoliko parametara
     * unutar @PostMapping anotacije: url kao vrednost 'value' atributa (ukoliko se
     * izostavi, ruta do metode je ruta do kontrolera), u slucaju POST zahteva
     * atribut 'produces' sa naznakom tipa odgovora (u nasem slucaju JSON) i atribut
     * consumes' sa naznakom oblika u kojem se salje podatak (u nasem slucaju JSON).
     *
     * Anotiranjem parametra sa @RequestBody Spring ce pokusati od prosledjenog JSON
     * podatka da napravi objekat tipa Greeting.
     *
     * url: /api/greetings POST
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Greeting> createGreeting(@RequestBody GreetingDTO greeting)  {
        Greeting savedGreeting = null;
        try {
            savedGreeting = greetingService.create(greeting);
            return new ResponseEntity<Greeting>(savedGreeting, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<Greeting>(savedGreeting, HttpStatus.CONFLICT);
        }
    }

    /*
     * url: PUT /api/greetings/1
     */

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Greeting> updateGreeting(@RequestBody GreetingDTO greeting) {
        try {
            Greeting updatedGreeting = greetingService.update(greeting);
            return new ResponseEntity<>(updatedGreeting, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @PatchMapping (value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Greeting> updateGreetingText(@RequestBody GreetingTextDTO greetingDTO, @PathVariable long id) {
        Greeting updatedGreeting = null;
        try {
            updatedGreeting = greetingService.updateGreetingText(greetingDTO, id);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<Greeting>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<Greeting>(updatedGreeting, HttpStatus.OK);
    }

    /*
     * url: DELETE /api/greetings/1
     */
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Greeting> deleteGreeting(@PathVariable("id") Long id) {
        Greeting deletedGreeting = greetingService.delete(id);
        if (deletedGreeting == null) {
            return new ResponseEntity<Greeting>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Greeting>(HttpStatus.NO_CONTENT);
    }

    /*
      upotreba query parametra
      GET /api/greetings/search?text=nekiTekst
     */
    @GetMapping(value = "/search")
    public ResponseEntity<Collection<Greeting>> searchGreetings(@RequestParam("text") Optional<String> text) {
        ArrayList<Greeting> foundGreetings = greetingService.searchGreetings(text);
        return new ResponseEntity<Collection<Greeting>>(foundGreetings, HttpStatus.OK);
    }

}

