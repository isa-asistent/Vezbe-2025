# Vežbe 


## schedule-example

Primer Spring aplikacije sa zakazanim taskovima koji se izvršavaju u fiksnim vremenskim intervalima. Kada se primer pokrene, u konzoli analizirati vremenske trenutke kada su se metode izvršile.

Da bi Spring mogao automatski da izvršava zakazane taskove, potrebno je konfiguracinu klasu anotirati anotacijom ___@EnableScheduling___ (u našem primeru, to je main klasa ScheduleExampleApplication.java).

Metode koje predstavljaju taskove koji se izvršavaju potrebno je anotirati sa ___@Scheduled___ i podesiti atribute na željene vrednosti. Atributi su sledeći:

* [__cron__](https://en.wikipedia.org/wiki/Cron): Logika se izvrsava u vremenskim trenucima opisanim cron sintaksom. Cron je izraz koji opisuje neki vremenski trenutak (npr. svaki dan 20:00:00, nedelja 23:59:59...). Ovaj atribut se koristi ukoliko je potrebno definisati task koji će se izvršavati u nekim vremenskim trenucima. Sintaksa cron izraza je prikazana na slici ispod.

![spring cron expression](https://i.imgur.com/T0G0LJM.png)

* __initialDelay__: Atribut čija vrednost označava koje vreme treba da prođe od trenutka kada se aplikacija startuje do trenutka kada se metoda prvi put može izvršiti. Vreme se navodi u milisekundama.
* __fixedRate__: Atribut čija vrednost označava koliko vremena treba da prođe između izvrašavanja (gleda se trenutak kada je metoda _započela_ izvršavanje). Vreme se navodi u milisekundama. Npr. ako je vrednost atributa 5000 ms znači da će se metoda izvršavati na svakih 5 sekundi od trenutka poziva (npr. ako je metoda počela sa izvršavanjem u 20:00:00, završila u 20:03:00, sledeći poziv iste metode će biti u 20:05:00).
* __fixedDelay__: Atribut čija vrednost označava koliko vremena treba da prođe između izvrašavanja (gleda se trenutak kada je metoda _završila_ izvršavanje). Vreme se navodi u milisekundama. Npr. ako je vrednost atributa 5000 ms znači da će se metoda izvršavati na svakih 5 sekundi od trenutka završetka (npr. ako je metoda počela sa izvršavanjem u 20:00:00, završila u 20:03:00, sledeći poziv iste metode će biti u 20:08:00).

Sve taskove koji su zakazani Spring će izvršavati u posebnom thread-u. Koristi se samo jedan thread za sve zadatke, pa treba biti oprezan jer u jednom trenutku samo jedan zakazani task može da se izvršava. Ukoliko neki zakazani task treba da se izvrši, a Spring već izvrašava neki raniji zakazani, task će otići na čekanje i čim se thread oslobi, preuzima se na izvršavanje.

Primer ispisa na konzoli:
```
2020-03-30 14:14:24.226  INFO 28365 --- [           main] r.a.u.f.i.s.ScheduleExampleApplication   : Started ScheduleExampleApplication in 2.171 seconds (JVM running for 2.561)
2020-03-30 14:14:30.000  INFO 28365 --- [   scheduling-1] r.a.u.f.i.s.c.GreetingContoller          : > cronJob
2020-03-30 14:14:35.002  INFO 28365 --- [   scheduling-1] r.a.u.f.i.s.c.GreetingContoller          : Procesiranje je trajalo 5 sekundi.
2020-03-30 14:14:35.005  INFO 28365 --- [   scheduling-1] r.a.u.f.i.s.c.GreetingContoller          : < cronJob
```

U uglastim zagradama je oznaka thread-a koji izvršava metodu. Vidimo da postoje 2 thread-a:

1. __main__: main thread koji startuje aplikaciju i
2. __scheduling-1__: thread koji izvršava zakazane taskove.

###### Dodatni materijali:

* Više o cron sintaksi na [linku](http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/scheduling/support/CronSequenceGenerator.html)
* [The @Scheduled Annotation in Spring](https://www.baeldung.com/spring-scheduled-tasks)
* [Spring Scheduling Tasks Guidelines](https://spring.io/guides/gs/scheduling-tasks/)
* [Task Execution and Scheduling Spring documentation](https://docs.spring.io/spring/docs/current/spring-framework-reference/integration.html#scheduling)

###### Pokretanje primera (Eclipse)

* importovati projekat u workspace: Import -> Maven -> Existing Maven Project
* instalirati sve dependency-je iz pom.xml
* desni klik na projekat -> Run as -> Java Application / Spring Boot app (ako je instaliran STS plugin sa Eclipse marketplace)
