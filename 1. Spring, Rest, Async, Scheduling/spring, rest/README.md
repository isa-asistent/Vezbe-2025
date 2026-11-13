# Vežbe 1.1 - Spring

## instaliranje dependency-ja iz pom.xml

Kada učitamo Maven projekat u workspace, sve biblioteke od kojih naš projekat zavisi i koje su navedene u pom.xml fajlu se prvo traže u lokalnom Maven repozitorijumu (folder čiji je naziv _.m2_). Tek ukoliko se ne pronađu tu, biblioteke će se potražiti na remote Maven repozitorujumu i ubaciti u lokalni repozitorijum. Maven kada pronađe sve zavisnosti, uvezaće ih sa projektom. Nakon import-a projekta u workspace, u donjem desnom uglu status bara se nalazi progress učitavanja i build-ovanja Maven projekta. Ukoliko nakon build-a projekat i dalje ima greške, potrebno je ručno uvezati sve zavisnosti iz pom.xml sa projektom. Ovo može da se uradi na sledeći način:

* desni klik na projekat -> Run as -> Maven build... -> u polje _goals_ uneti _clean compile install_ -> Apply -> Run. Da bi ovo radilo, voditi računa da je za pokretanje Maven ciljeva postavljen JDK, a ne JRE. Ovo možete podesiti u tabu _JRE_ kada otvorite _Maven build..._ dijalog.
* desni klik na projekat -> Maven -> Update Project ili
* desni klik na pom.xml -> Maven -> Reload Project (IntelliJ)

Kada dodate novu zavisnost u pom.xml, na snimanje izmena će se automatski pokrenuti uvezivanje zavisnosti sa projektom. Ukoliko se to ne desi, pokrenuti ručno uvezivanje zavisnosti sa projektom.

###### Materijali koje je neophodno proučiti da bi se primer mogao uspešno ispratiti:

* [Opseg i životni ciklus serverskih komponenti](https://www.youtube.com/watch?v=AMQcujPq8Wg)
* [Dependency Injection](https://www.youtube.com/watch?v=XjgA8vZ-TcM)
* Spring.pdf

## springapp-example

Primer Spring aplikacije sa automatskom konfiguracijom.

###### Pokretanje primera (Eclipse):

* importovati projekat u workspace: Import -> Maven -> Existing Maven Project
* instalirati sve dependency-je iz pom.xml
* desni klik na projekat -> Run as -> Java Application

## spring-boot-example

Primer Spring Boot aplikacije. Ovo je današnji preporučeni način za konfigurisanje Spring Boot aplikacija koji će se koristiti u svim narednim primerima.

Kreiranje inicijalnog Spring Boot projekta moguće uraditi na [linku](https://start.spring.io/), gde se može izabrati tip projekta (Maven ili Gradle project - svi primeri za vežbe su Maven projekti), programski jezik (Java), verzija Spring Boot-a (po želji), vrsta arhive (jar ili war), verzija programskog jezika (bar Java 17). Potrebno je popuniti Maven koordinate proizvoljnim informacijama i (opciono) dodati zavisnosti iz dependency liste.

* Uputstvo za kreiranje početnog Spring Boot projekta ručno možete naći [ovde](https://www.youtube.com/watch?v=bDtZvYAT5Sc) i [ovde](https://www.youtube.com/watch?v=E7_a-kB46LU)
* [Kreiranje Spring Boot projekta pomoću STS plugina u Eclipse IDE](https://dzone.com/articles/creating-a-spring-boot-project-with-eclipse-and-ma)

# Vežbe 1.2 - REST i Spring validacija

## rest-example
Primer Spring web aplikacije. Pogledati u **pom.xml** zavisnosti koje moraju da se uključe. Ukoliko je potrebna dodatna konfiguracija projekta (van samih klasa koje predstavljaju konfiguraciju) piše se u **application.properties** fajlu koji se nalazi u src/main/resorces paketu.
Kontroleri su anotirani sa *@RestController*. Anotacija je izvedena od anotacije _@Controller_ i pridodata joj je anotacija _@ResponseBody_ koja kao podrazumevano ponašanje kao rezultat metode koja obrađuje zahtev vraća samo telo odgovora (response body, bez zaglavlja). Za vraćanje i drugih podataka (zaglavlje, status kod,...) osim glavnog objekta koji predstavlja telo odgovora, može se koristiti klasa _ResponseEntity_.

###### Materijali koje je neophodno proučiti da bi se primer mogao uspešno ispratiti:

* [Arhitekture klasičnih i savremenih web aplikacija](https://www.youtube.com/watch?v=XnEnUtSw8Rc)

###### Struktura primera

Paketi su organizovani tako da se različite Spring komponente nalaze u svojim odgovarajućim paketima:

* kontroleri u __controller__ paketu: uloga kontrolera u aplikaciji jeste da samo prihvataju zahteve korisnika i pozivaju odgovarajuće metode servisa
* servisi u __service__ paketu: sva logika aplikacije se piše u servisnim metodama. Servisi pozivaju odgovarajuče metode repozitorijuma
* repozitorijum u __repository__ paketu: repozitorijumi su zaduženi za komunikaciju za bazom podataka. Kako još nismo učili komunikaciju sa bazom podataka, napravljene su privremene klase koje predstavljaju repozitorijume i čuvaju podatke u kolekcijama u memoriji
* model u __domain__ ili __model__ paketu: entiteti koje postoje u sistemu
* paket __dto__: _**D**ata **T**ransfer **O**bjects_ su objekti koji predstavljaju skraćene verzije objekata iz modela i služe da se razmenjuju između klijentske i serverske strane. DTO objekti sadrže samo one atribute koji su u tom trenutku neophodni da se razmene između klijenta i servera.

Sva mapiranja na konkretne metode u kontrolerima koje će obraditi zahteve rade se kroz anotacije:

* ___@RequestMapping___: sa specificiranjem atributa _method_ ili
* izvedenim anotacijama poput ___@GetMapping___, ___@PostMapping___, ___@PutMapping___, ___@DeleteMapping___...

Gore navedene anotacije dodatno mogu da sadrže i sledeće atribute:

* __value__:  predstavlja URL koji određuje putanju do metode
* __method__: označava tip HTTP metode (samo ukoliko se koristi _@RequestMapping_ anotacija)
* __consumes__: označava tip poruke koja se prosleđuje metodi (u kom formatu se podaci zapisuju u telu HTTP zahteva, default je JSON)
* __produces__: označava tip odgovora (u kom formatu se podaci zapisuju u telu HTTP odgovora, default je JSON).

###### Dodatni materijali:

* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)
* [Spring Restful Web Services Example](https://www.journaldev.com/2552/spring-rest-example-tutorial-spring-restful-web-services)

## validation-demo

Primer Spring aplikacije sa Custom validacijom.

Prilikom obrade zahteva, parametar metode u kontroleru koji predstavlja objekat koji se prosleđuje serveru (a koji je anotiran ograničenjima u modelu) anotiran je sa ___@Valid___.

Greške prilikom validiranja se mogu obraditi u komponenti __ValidationErrorsHandler__ koja je anotirana sa __@RestControllerAdvice__. Metoda ove klase se poziva automatski prilikom neuspešne validacije, a tip HTTP odgovora i status se definišu kroz __ResponseEntity__ i __@ResponseStatus__. Greške se nalaze u BindingResult objektu, koji se vezuje za odgovarajući izuzetak.

Pored predefinisanih anotacija za postavljanje ograničenja mogu se praviti nove anotacije. Primer jedne takve anotacije nalazi se u __validator__ paketu.

###### Dodatni materijali:

* [Bean validation Specification](https://beanvalidation.org/1.0/spec/)
* [Validation, Data Binding, and Type Conversion Documentation](https://docs.spring.io/spring/docs/4.1.x/spring-framework-reference/html/validation.html)

## Pokretanje primera (Eclipse):

* importovati projekat u workspace: Import -> Maven -> Existing Maven Project
* instalirati sve dependency-je iz pom.xml
* desni klik na projekat -> Run as -> Java Application / Spring Boot app (ako je instaliran STS plugin sa Eclipse marketplace)
