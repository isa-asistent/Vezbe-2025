# Vežbe 

## async-example

Asinhrono procesiranje je u Spring radnom okviru pojednostavljeno korišćenjem specijalnih anotacija.
Potrebno je uključiti podršku za asinhrono izvršavanje metoda pomoću anotacije `@EnableAsync` i anotirati metodu koja treba asinhrono da se izvršava pomoću anotacije `@Async`.
Kada se metoda anotira `@Async` anotacijom, Spring će izdvojiti izvršavanje te metode u odvojenu nit iz TaskExecutor thread pool-a, a pozivalac metode neće morati da čeka na njeno izvršavanje.

U **async-example** primeru prikazano je jednostavan kod za slanje e-maila sinhrono i asinhrono. Dodat je `Thread.sleep()` kako bi se istakao efekat dugotrajne operacije koja ima smisla da se izvršava asinhrono i kakav efekat takvo izvršavanje ima za korisnika. Za potrebe slanja e-maila koristi se objekat klase `JavaMailSender`. Konekcioni parametri za programsko slanje e-maila zadati su kroz `application.properties`.

Napomena: Za programsko slanje e-maila u primeru je korišćen Gmail nalog. Kako bi primer radio, potrebno je na nalogu koji ste postavili u `application.properties` omogućiti dvofaktorsku autentifikaciju. Pored toga, potrebno je izgenerisati lozinku za aplikaciju, koja će se postaviti kao vrednost `spring.mail.password` parametra. Na [linku](https://support.google.com/accounts/answer/185833?hl=en) se nalazi uputstvo.

Dodatni materijali za razumevanje asinhronog izvršavanja metoda u Springu:

1. [Task Execution and Scheduling](https://docs.spring.io/spring/docs/current/spring-framework-reference/integration.html#scheduling)
2. [Effective Advice on Spring Async: Part 1](https://dzone.com/articles/effective-advice-on-spring-async-part-1)
3. [Effective Advice on Spring Async (ExceptionHandler): Part 2](https://dzone.com/articles/effective-advice-on-spring-async-exceptionhandler-1)
4. [Effective Advice on Spring @Async: Final Part](https://dzone.com/articles/effective-advice-on-spring-async-final-part-1)
5. [How To Do @Async in Spring](https://www.baeldung.com/spring-async)

###### Pokretanje primera (Eclipse)

* importovati projekat u workspace: Import -> Maven -> Existing Maven Project
* instalirati sve dependency-je iz pom.xml
* desni klik na projekat -> Run as -> Java Application / Spring Boot app (ako je instaliran STS plugin sa Eclipse marketplace)

