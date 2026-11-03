# Vežbe 5

## cache-example

U primeru je predstavljena ideja o keširanju kao konceptu i o postojanju dva nivoa keša koja Hibernate podržava - L1 i L2.

U primeru je korišćena _in-memory_ baza [H2](http://www.h2database.com/html/main.html) koja je zgodna za brži i lakši razvoj i ne zahteva posebnu instalaciju (_workbench_-u se može pristupiti iz _browser_-a). Još neki proizvođači _in-memory_ baza su [HSQLDB](http://hsqldb.org/) i [Apache Derby](https://db.apache.org/derby/). H2 baza se integriše sa Maven aplikacijom dodavanjem sledeće zavisnosti:

```
<!-- Dependency za in-memory bazu H2 -->
<dependency>
	<groupId>com.h2database</groupId>
	<artifactId>h2</artifactId>
</dependency>
```

### Level 1 keširanje - L1

Ovaj nivo keširanja je podržan od strane Hibernate-a, nije potrebna nikakva dodatna konfiguracija i ne može se isključiti. L1 keširanje se tiče Hibernate sesije. Kada se objekat učita u sesiju, prilikom svakog sledećeg upita za taj isti objekat, Hibernate neće slati upit ka bazi, već će objekat dobavljati iz keša. L1 keš omogućava da, unutar sesije, zahtev za objektom iz baze uvek vraća istu instancu objekta i tako sprečava konflikte u podacima i sprečava Hibernate da učita isti objekat više puta. Bitno je napomenuti da objekat koji se čuva u kešu na ovom nivu nije vidljiv drugim sesijama i "živi" koliko i Hibernate sesija. Kada se sesija uništi, uništava se i keš i svi objekti koji se u njemu nalaze.

Pošto Hibernate čuva sve objekte u L1 kešu, treba pažljivo i efikasno izvršavati _query_-je, da bi se izbegli potencijalni problemi sa memorijom. Na primer, ne treba čitati objekte iz baze ukoliko oni nisu potrebni, ne bi trebalo učitavati objekte u _for_ petlji, voditi računa o _fetch type_-u koji se koristi itd. Prilikom izrade projekta, postavite opciju `spring.jpa.show-sql = true` u `application.properties` fajlu i obratite pažnju kako izgledaju upiti koje Hibernate šalje bazi i koliko ih ima. Hibernate šalje mnogo upita i vrlo brzo keš postaje memorijski veoma zahtev. Problem sa veličinom L1 keša ne bi trebalo da imate (verovatno i nećete) prilikom izrade projektnog zadataka, ali to predstavlja realan problem o kojem treba voditi računa.

### Level 2 keširanje - L2

Ovaj nivo keširanja je podržan od Hibernate-a, ali je neophodan eksterni provajder. U primeru je korišćen [EhCache](http://www.ehcache.org/documentation/), ali postoje i drugi poput [Infinispan](https://infinispan.org/), [Redis](https://redis.io/) ili [JBoss Cache](https://jbosscache.jboss.org/).

Postoje različite strategije keširanja:

1. **Read Only:** strategija koju treba koristiti za objekte koji će se uvek čitati, ali se nikada neće ažurirati. Ova strategija je dobra kada se radi sa statičkim podacima, kao na primer konfiguracija aplikacije. Ovo je najjednostavnija strategija sa najboljim performansama, jer nema dodatnog posla da bi se proverilo da li je objekt ažuriran u bazi podataka ili nije
2. **Read Write:** strategija koju treba koristiti za objekte koji se mogu i ažurirati. Ukoliko se baza podataka ažurira i van aplikacije kroz neke druge, Hibernate neće biti svestan tih izmena, a podaci koji se čuvaju u kešu mogu biti zastareli. Zato treba voditi računa da kada se koristi ova strategija keširanja, baza podataka isključivo ažurira kroz Hibernate API
3. **Nonrestricted Read Write:** strategija koja se koristi ukoliko se podaci ažuriraju retko, skoro nikad. Ne garantuje konzistentnost između keša i baze podataka, i zbog toga je prihvatljiva u sistemima gde zastareli podaci ne predstavljaju kritične probleme
4. **Transactional:** strategija koja se koristi kod visoko konkurentnih sistema gde je ključno sprečiti da se u bazi podataka nalaze zastareli podaci.

#### EHCache

EhCache podržava sve navedene strategije keširanja, i zbog toga predstavlja jedan od najboljih i najpopularnijih provajdera za L2 keširanje u Spring aplikacijama.

Da bi se EhCache uključio u Maven projekat, neophodno je uključiti sledeće zavisnosti:

```
<dependency>
	<groupId>org.ehcache</groupId>
	<artifactId>ehcache</artifactId>
</dependency>
<!-- Potrebno za logovanje dogadjaja -->
 <dependency>
	<groupId>javax.cache</groupId>
	<artifactId>cache-api</artifactId>
</dependency>
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-cache</artifactId>
</dependency>
```

Konfiguracija EhCache-a zahteva definisanje XML fajla `ehcache.xml` koji se nalazi u `resources` folderu, kao i uključivanje podrške za keširanje dodavanjem anotacije `@EnableCaching` u neku od konfiguracionih klasa (u primeru, klasa _CacheExampleApplication_). Takođe, u `application.properties` potrebno je dodati liniju za učitavanje konfiguracije u aplikaciju:

```
spring.cache.jcache.config=classpath:ehcache.xml
```

Ostatak konfiguracije podrazumeva dodavanje anotacija nad klasama modela ili metodama koje vraćaju objekte koji se trebaju keširati, npr. `@Cacheable` ili `@CacheEvict` u klasi _ProductService_.

EhCache pruža mogućnost definisanja vremena koliko dugo objekat živi u kešu putem `<expiry>` elementa:

- ttl - TIME TO LIVE - ukupno vreme koje će objekti provesti u kešu bez obzira da li im se pristupa ili ne i
- tti - TIME TO IDLE - ukupno vreme koje će objekti provesti u kešu ako im se ne pristupa

Implementirana je i klasa `CacheLogger` u paketu _rs.ac.uns.ftn.informatika.cache.logger_ koja osluškuje svaku promenu u kešu i zadužena je za logovanje svih događaja. Događaji koji postoje su sledeći:

1. **CREATED** - dodavanje objekta u keš
2. **EXPIRED** - detekcija da je objektu isteklo vreme u kešu (ttl, tti)
3. **EVICTED** - izbacivanje objekta iz keša (dešava se ili eksplicitnim pozivanjem _evict_-a ili po principu **LRU** (_Least Recently Used_, kada se dodaje novi objekt u popunjen keš izbacuje se iz keša onaj koji se najdavnije koristio)
4. **REMOVED** - uklanjanje objekta iz keša.

EhCache pruža mogućnost čuvanja keširanih objekata na Java heap-u, u RAM memoriji kao i na disku, što se podešava u `<resoruces>` elementu.

U primeru su konfigurisana dva keša:

1. **default**: predstavlja podrazumevani keš u kojem se keširaju svi objekti za koje nije naznačeno drugačije. Definiše se u `<cache-template name="default">` elementu.
2. **product**: keš kolekcija definisana se u `<cache alias="product" uses-template="default">` elementu. Atribut **alias** označava naziv keš kolekcije, dok **uses-template** atribut referencira šablon koji se _override_-uje. Vrednosti koje nisu navedene za keš kolekciju imaju vrednosti koje su definisane u referenciranom šablonu. Element `<key-type>` označava tip podatka koji će se koristiti za ključ, dok `<value-type>` označava tip podatka koji se nalaze u kešu. Prema konfiguraciji, u ovoj kolekciji se keširaju _Product_ objekti, a čuvaju se samo 2 objekta na Java _heap_-u. Kako će _resorces_ element da pregazi isti element iz _default_ keša, ne postoji mogućnost keširanja _Product_-a ni u RAM-u ni na disku.

U klasi `ProductService` iz paketa _rs.ac.uns.ftn.informatika.cache.service_ je definisana dodatna konfiguracija za EhCache:

1. Smeštanje _Product_ objekata u _product_ keš kolekciju

   ```
   @Cacheable("product")
   Product findOne(long id);
   ```

2. Eksplicitno brisanje svih objekata iz _product_ keš kolekcije

   ```
   @CacheEvict(cacheNames = { "product" }, allEntries = true)
   void removeFromCache();
   ```

### Demonstracija primera

Preko Postman-a slati zahteve i u konzoli Spring aplikacije čitati ispise.

1. HTTP GET http://localhost:8080/products/1

   Ispis na konzoli:

   ```
   2020-04-20 16:31:07.627  INFO 54359 --- [nio-8080-exec-1] r.a.u.f.i.c.service.ProductServiceImpl   : Product with id: 1 successfully cached!
   Hibernate: select product0_.id as id1_0_0_, product0_.name as name2_0_0_, product0_.origin as origin3_0_0_, product0_.price as price4_0_0_ from product product0_ where product0_.id=?
   2020-04-20 16:31:07.778  INFO 54359 --- [e [_default_]-0] r.a.u.f.i.cache.logger.CacheLogger       : Key: 1 | EventType: CREATED | Old value: null | New value: rs.ac.uns.ftn.informatika.cache.domain.Product@2d9a6dea
   ```

   Desio se događaj **CREATED** i objekat je uspešno dodat u keš pod ključem 1. Vidimo da je objekat pročitan iz baze (deo loga koji počinje sa _Hibernate:_)

2. Pošto je u `ehcache.xml` fajlu definisano da je _ttl_ vreme 15 sekundi, sačekati više od 15 sekundi pre nego što se pošalje nov zahtev za preuzimanje ponovo istog objekta

3. HTTP GET http://localhost:8080/products/1

   Ispis na konzoli:

   ```
   2020-04-20 16:34:50.116  INFO 54359 --- [nio-8080-exec-5] r.a.u.f.i.c.service.ProductServiceImpl   : Product with id: 1 successfully cached!
   Hibernate: select product0_.id as id1_0_0_, product0_.name as name2_0_0_, product0_.origin as origin3_0_0_, product0_.price as price4_0_0_ from product product0_ where product0_.id=?
   2020-04-20 16:34:50.116  INFO 54359 --- [e [_default_]-1] r.a.u.f.i.cache.logger.CacheLogger       : Key: 1 | EventType: EXPIRED | Old value: rs.ac.uns.ftn.informatika.cache.domain.Product@2d9a6dea | New value: null
   2020-04-20 16:34:50.118  INFO 54359 --- [e [_default_]-1] r.a.u.f.i.cache.logger.CacheLogger       : Key: 1 | EventType: CREATED | Old value: null | New value: rs.ac.uns.ftn.informatika.cache.domain.Product@59fa46ae
   ```

   Prvo se desio događaj **EXPIRED** za objekat koji je u keš dodat u koraku 1. Zatim se ponovo desio događaj **CREATED** i objekat je uspešno dodat u keš pod ključem 1. Vidimo da je objekat pročitan iz baze (deo loga koji počinje sa _Hibernate:_)

4. Za manje od 15 sekundi poslati novi zahtev (da ne istekne definisano _ttl_)
5. HTTP GET http://localhost:8080/products/2

   Ispis na konzoli:

   ```
   2020-04-20 16:38:07.904  INFO 54359 --- [nio-8080-exec-9] r.a.u.f.i.c.service.ProductServiceImpl   : Product with id: 2 successfully cached!
   Hibernate: select product0_.id as id1_0_0_, product0_.name as name2_0_0_, product0_.origin as origin3_0_0_, product0_.price as price4_0_0_ from product product0_ where product0_.id=?
   2020-04-20 16:38:07.907  INFO 54359 --- [e [_default_]-2] r.a.u.f.i.cache.logger.CacheLogger       : Key: 2 | EventType: CREATED | Old value: null | New value: rs.ac.uns.ftn.informatika.cache.domain.Product@91e5df7
   ```

   Desio se događaj **CREATED** i objekat je uspešno dodat u keš pod ključem 2. Vidimo da je objekat pročitan iz baze (deo loga koji počinje sa _Hibernate:_)

6. Za manje od 15 sekundi poslati novi zahtev (da ne istekne definisano _ttl_). Dodaje se treći objekat u keš, a podešeno je da se u kešu čuvaju do dva objekta

7. HTTP GET http://localhost:8080/products/3

   Ispis na konzoli:

   ```
   2020-04-20 16:41:56.692  INFO 54359 --- [nio-8080-exec-4] r.a.u.f.i.c.service.ProductServiceImpl   : Product with id: 3 successfully cached!
   Hibernate: select product0_.id as id1_0_0_, product0_.name as name2_0_0_, product0_.origin as origin3_0_0_, product0_.price as price4_0_0_ from product product0_ where product0_.id=?
   2020-04-20 16:41:56.694  INFO 54359 --- [e [_default_]-3] r.a.u.f.i.cache.logger.CacheLogger       : Key: 3 | EventType: CREATED | Old value: null | New value: rs.ac.uns.ftn.informatika.cache.domain.Product@39a3009d
   2020-04-20 16:41:56.696  INFO 54359 --- [e [_default_]-3] r.a.u.f.i.cache.logger.CacheLogger       : Key: 1 | EventType: EVICTED | Old value: rs.ac.uns.ftn.informatika.cache.domain.Product@7f9c0f4e | New value: null

   ```

   Desio se događaj **CREATED** i objekat je uspešno dodat u keš pod ključem 3. Događaj **EVICTED** se dešava za objekat koji se u kešu čuva pod ključem 1, što znači da se po LRU principu, ovaj objekat izbacuje iz keša, a novokreirani objekat pod ključem 3 ubacuje u keš. Vidimo da je objekat pročitan iz baze (deo loga koji počinje sa _Hibernate:_)

8. Za manje od 15 sekundi poslati novi zahtev (da ne istekne definisano _ttl_)

9. HTTP GET http://localhost:8080/products/3

   Na konzoli nema nikakvog ispisa. Bitno je primetiti da ne postoji deo loga koji počinje sa _Hibernate:_, što znači da objekat koji je vraćen klijentu nije pročitan iz baze već iz keša.

### Dodatni materijali za razumevanje keširanja:

1. [Hibernate Second-Level Cache](https://www.baeldung.com/hibernate-second-level-cache)
2. [Difference between First and Second Level Cache in Hibernate](https://javarevisited.blogspot.com/2017/03/difference-between-first-and-second-level-cache-in-Hibernate.html)
3. [Spring cache annotations: some tips & tricks](https://www.foreach.be/blog/spring-cache-annotations-some-tips-tricks)

## rate-limiter-example

U primeru je predstavljen RateLimiting mehanizam; ograničavanje broja zahteva u određenom vremenskom intervalu.

Korišćena je in-memory baza, slično primeru cache-example.

Biblioteka upotrebljena u okviru primera je [**Resilience4j**](https://resilience4j.readme.io/).
Neophodno je uključiti je kao zavisnost u okviru **pom.xml**, kao i zavisnost za **AOP** na koju se oslanja.

```
<dependency>
	<groupId>io.github.resilience4j</groupId>
	<artifactId>resilience4j-spring-boot2</artifactId>
	<version>1.5.0</version>
</dependency>
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```

Konfigurisanje moguće je kroz application.properties datoteku. Moguće je kreiranje više konfiguracija za više različitih slučajeva, s tim da ime mora biti jedinstveno.

U isečku, definisane su dve instance, **standard** i **premium**. Za svaku definisane su sledeće konfiguracije:

- **limitForPeriod**: maksimalan broj poziva za definisani interval
- **limitRefreshPeriod**: definisani vremenski interval
- **timeoutDuration**: vreme čekanja na obradu zahteva - korisno u slučaju dugih _limitRefreshPeriod_ intervala, kao na primer:
  1.  aplikacija je konfigurisana tako da je **limitForPeriod 10 zahteva** i **limitRefreshPeriod 1h**, dok je **timeoutDuration 2 sekunde**
  2.  klijent je poslao maksimalan broj poziva za definisani interval; svaki sledeći neće biti obrađen dok vremenski interval ne istekne
  3.  prošlo je **59 minuta i 59 sekundi** i klijent šalje zahtev; zahtev bi trebao da bude odbijen pošto ograničen interval nije prošao, ali pošto smo definisali da je vreme čekanja na obradu zahteva 2 sekunde, dok korisnik čeka proći će ograničeni vremenski interval i zahtev će biti obrađen; u ovakvoj situaciji smo uštedeli slanje još jednog zahteva od strane korisnika u slučaju da je vremenski interval blizu isteka

```
resilience4j.ratelimiter.instances.standard.limitForPeriod=1
resilience4j.ratelimiter.instances.standard.limitRefreshPeriod=10s
resilience4j.ratelimiter.instances.standard.timeoutDuration=2

resilience4j.ratelimiter.instances.premium.limitForPeriod=3
resilience4j.ratelimiter.instances.premium.limitRefreshPeriod=1s
resilience4j.ratelimiter.instances.premium.timeoutDuration=0
```

Moguće je deklarativno navesti za koje metode će biti iskorišćen RateLimiting princip. U okviru ProductServiceImpl klase definisana je upotreba **standard** RateLimiter instance:

```
@RateLimiter(name = "standard", fallbackMethod = "standardFallback")
public List<Product> findAll() {
	return productRepository.findAll();
}

// Metoda koja ce se pozvati u slucaju RequestNotPermitted exception-a
public List<Product> standardFallback(RequestNotPermitted rnp) {
	LOG.warn("Prevazidjen broj poziva u ogranicenom vremenskom intervalu");
	throw rnp;
}
```

Upotrebom **@RateLimiter** anotacije navedeno je da prilikom poziva metode _findAll_ treba da se prvo proveri da li je prekoračen maksimalan broj zahteva u zadatom vremenskom intervalu.
Broj zahteva i vremenski interval biblioteka zaključuje na osnovu navedenog parametra **name** (u ovom slučaju koristi se standard instanca definisana u okviru application.properties datoteke).
U slučaju prekoračenja, biblioteka baca **RequestNotPermitted** izuzetak. Moguće je, kao parametar anotacije, navesti i ime metode koja će obraditi nastali izuzetak.

Dokumentaciju prati i lista primera koji su javno dostupni na [GitHub repozitorijumu](https://github.com/resilience4j/resilience4j-spring-boot2-demo).

### Primena

- Sa porastom broja korisnika aplikacije, raste i broj zahteva koje server treba da opsluži. U takvim slučajevima želimo da izbegnemo situacije u kojima server može biti preopterećen. RateLimiting je jedan od načina ograničavanja opterećenja servera.

- Bezbednost aplikacija je bitan aspekt svakog razvoja. Jedan od čestih napada sa kojima se srećemo je i [DoS napad](https://www.paloaltonetworks.com/cyberpedia/what-is-a-denial-of-service-attack-dos). Jedan od načina zaštite od ovakvih napada jeste ograničavanje broja poziva upućenih na naš API.

- Mnogi servisi dostupni preko interneta koji pružaju mogućnost poziva svog API-ja nisu u potpunosti besplatni i podržavaju različite pakete. Način na koji se pomenuti princip može implementirati jeste ograničavanje broja poziva određene grupe korisnika. Moguće je definisati više grupa i za svaku od njih poseban broj poziva za određeni interval.

## redis-cache u okviru rate-limiter primera

U okviru primera prikazan je pristup keširanju upotrebom key-value NoSQL baze [Redis](https://redis.io/). Ova baza podataka spada u **in-memory** baze podataka (sve vrednosti se čuvaju u okviru RAM memorije računara). Dodatno, podržava perzistenciju na disk (slično EhCache).

Bazu je moguće instaliati lokalno na Linux i MacOS operativnim sistemima [(uputstvo za instalaciju)](https://redis.io/docs/getting-started/), dok direktna podrška za Windows mašine ne postoji. Instalacija i pokretanje baze je moguće upotrebom [WSL2 podsistema](https://learn.microsoft.com/en-us/windows/wsl/install) ili upotrebom [Docker kontejnera](https://hub.docker.com/_/redis). Na sledećem linku nalazi se [uputstvo za instalaciju Redis-a](https://redis.io/docs/getting-started/installation/install-redis-on-windows/).

NoSQL baze su sveprisutne u svetu razvoja softvera od 2010-ih godina. Glavna odlika ovih baza je nepostojanje klasične tabelarne strukture podataka kao i SQL-a kao upitnog jezika (odakle i potiče sam naziv). Potreba za NoSQL bazama posledica je raznolikosti i količine podataka koji nastaju u okviru modernih aplikacija, pogotovo socijalnih platformi. Zbog raznolikosti podataka ne možemo lako (ili uopšte) definisati šemu baze podataka i oslanjamo se na skladištenje polustruktuiranih i nestruktuiranih podataka.

U okviru primera demonstrirane su slične funkcionalnosti prikazane u okviru [cache-example](#cache-example).

Kako bi se uključila podrška za rad sa kešom i Redis bazom, u okviru **pom.xml** datoteke dodate su sledeće zavisnosti:

```
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-cache</artifactId>
	<version>2.4.3</version>
</dependency>
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-data-redis</artifactId>
	<version>2.4.3</version>
</dependency>
```

U okviru **CacheConfiguration** klase nalazi se konfiguracija keširanja uz pomoć Redis-a. Dat je primer izmene **default-ne** konfiguracije, kao i fine-tuning keša. U isečku su izdvojena sledeća podešavanja:

- **TTL**: ukupno vreme koje objekat može provesti u kešu pre nego što se automatski briše,
- **rukovanje null vrednostima**: eksplicitno je navedeno da se null vrednosti ne keširaju,
- **prefix**: svaki objekat se čuva u bazi pod ključem čija je predefinisana vrednost "ime_keša:redni_broj_objekta"; dodatno je moguće definisati prefiks koji će ključ sadržati,
- **serijalizacija**: moguće je definisati koji serijalizator će biti korišćen za zapisivanje objekata u bazi.

```
.entryTtl(Duration.ofMinutes(15))
.disableCachingNullValues()
.prefixCacheNameWith("isa-example:")
.serializeValuesWith(RedisSerializationContext.SerializationPair
		.fromSerializer(new GenericJackson2JsonRedisSerializer()));
```

U okviru **ProductService** interfejsa dodate su anotacije iznad **findOne**, **updateOne** i **removeFromCache** metoda.

```
@Cacheable("product")
Product findOne(long id);

@CachePut(cacheNames = {"product"}, key = "#root.args[0]")
Product updateOne(long id, Product product);

@CacheEvict(cacheNames = {"product"}, allEntries = true)
void removeFromCache();
```

Anotacije **@Cachable** i **@CacheEvict** opisane su u okviru prethodno [primera](#cache-example). Anotacija **@CachePut** razlikuje se od @Cachable u tome što će se metoda koju anotira uvek izvršiti. Nakon izvršavanja metode, povratna vrednost (Product objekat) biće **ažurirana u okviru keša**. Poseban značaj imaju podešavanja:

- **cacheNames**: definiše u okviru kog keša se vrši ažuriranje,
- **key**: definiše pod kojim ključem je objekat koji će biti ažuriran.

Vrednost koju key uzima je, u ovom slučaju, vrednost prvog argumenta metode. U okviru [dokumentacije](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/cache/annotation/CachePut.html#key--) dati su način preuzimanja drugih vrednosti koje mogu biti značajne.

Korisna strana @CachePut anotacije je što keš sadrži najnovije objekte koji su u skladu sa stanjem u samoj bazi. Na ovaj način postiže se konzistentnost podataka u kešu i u bazi. Ali, svakako treba obazrivo keširati vrednosti. Performanse će opadati u slučaju da često menjamo entitet i često osvežavamo vrednost keša u odnosu na slučaj kada direktno dobavljamo vrednost iz keša.

### Mehanizam izbacivanja vrednosti iz keša

Moguće je memorijski ograničiti količinu podataka koja se čuva u Redis kešu. Za razliku od EhCache implementacije, nije moguće ograničiti broj objekata u kešu. Redis podržava tri glavna načina za izbacivanje podataka iz keša:

1.  **noeviction** - bez izbacivanja; kada se memorija popuni, nove vrednosti se ne zapisuju u keš;
2.  **allkeys-lru** - Least Recently Used; upotrebom [probabilističkih metoda](https://www.cut-the-knot.org/Probability/ProbabilisticMethod.shtml) izbacuje se objekat kojem je najdavnije pristupano
3.  **allkeys-lfu** - Least Frequently Used; izbacuje se objekat kojem je najmanje pristupano

Vrednost maksimalne dostupne memorije, kao i strategije izbacivanja podataka konfiguriše se direktno nad Redis bazom kroz **redis.conf** datoteku.

```
maxmemory 100mb
maxmemory-policy allkeys-lru
```

### Testiranje

U okviru test direktorijuma nalaze se test slučajevi koji pokrivaju dobavljanje vrednosti objekta iz keša, kao i slučaj ažuriranja objekta koji se već nalazi u kešu.

Kako se primer oslanja Redis bazu, instanca baze mora postojati kako bu aplikacija funkcionisala. Baza se može lokalno instalirati. U tom slučaju, pored instalacije, neophodno je voditi računa o stanju podataka što može biti nezgodan posao u slučaju velikog broja konkurentnih testova. Rešenje kojim se prevazilaze ovi problemi jeste upotreba [Testcontainers biblioteke](https://www.testcontainers.org/) koja omogućava kreiranje lightweight instanci baza podataka, redova čekanja i sličnih zavisnosti neophodnih za izvršavanje testova. Biblioteka se oslanjajući se na [Docker](https://www.docker.com/) kontejnere. (Kako bi testovi mogli da se pokrenu, neophodno je instalirati i pokrenuti Docker.)

Neophodno je uključiti sledeću zavisnost u okviru **pom.xml** datoteke:

```
<dependency>
	<groupId>org.testcontainers</groupId>
	<artifactId>testcontainers</artifactId>
	<version>1.17.3</version>
	<scope>test</scope>
</dependency>
```

Dodatno, u okviru testova, upotrebom gorenavedene biblioteke, potrebno je kreirati sve potrebne zavisnosti na sledeći način:

```
static {
	GenericContainer<?> redis =
			new GenericContainer<>(DockerImageName.parse("redis:5.0.3-alpine")).withExposedPorts(6379);
	redis.start();
	System.setProperty("spring.redis.host", redis.getHost());
	System.setProperty("spring.redis.port", redis.getMappedPort(6379).toString());
}
```

Potrebno je navesti Docker sliku koja će se koristiti za kreiranje instance Redis baze. Nakon toga, biblioteka kreira instancu baze i dodeljuje joj nasumičan slobodan port kog dodatno postavlja u podešavanjima aplikacije kako bi se ostvarila konekcija. Nakon završetka svih testova, instanca baze se automatski uništava.

## Pokretanje instance Redis baze putem Docker alata

Konfiguracija Redisa kroz redis.conf datoteku je opciona!

```
# komanda za dobavljanje slike Redis baze
docker pull redis

# komanda za pokretanje Redis baze uz dodatnu konfiguraciju
docker run -p 6379:6379 -v putanja_do_redis_konfiguracije:/usr/local/etc/redis --name redis-cache redis redis-server /usr/local/etc/redis/redis.conf
```

## Pokretanje Spring aplikacije (Eclipse)

- importovati projekat u workspace: Import -> Maven -> Existing Maven Project
- instalirati sve dependency-je iz pom.xml
- desni klik na projekat -> Run as -> Java Application / Spring Boot app (ako je instaliran STS plugin sa Eclipse marketplace)


## Message Queue

Za potrebe razumevanja primera potrebno je prethodno pročitati [MessageQueue.pdf](https://github.com/ivana-k/isa-vezbe/blob/main/vezbe5/MessageQueue.pdf) iz foldera Vezbe5. Poređenje MQ-ova se nalazi [MQs.pdf](https://github.com/ivana-k/isa-vezbe/blob/main/Vezbe08/MQs.pdf) iz foldera Vezbe5. Kratak pregled prednosti korišćenja MQ nalazi se na [linku](https://blog.iron.io/top-10-uses-for-message-queue/).

Dodatne informacije o konceptima i različitim implementacijama MQ možete pročitati na [1](https://blog.codepath.com/2013/01/06/asynchronous-processing-in-web-applications-part-2-developers-need-to-understand-message-queues/) i [2](https://www.rabbitmq.com/tutorials/amqp-concepts.html).

### RabbitMQ

Primer komunikacije zasnovane na razmeni poruka između dve Spring aplikacije i rada sa [RabbitMQ](https://www.rabbitmq.com/download.html) nalaze se u _rabbitmq-producer-example_ i _rabbitmq-consumer-example_ projektima. Za pokretanje primera potrebno je instalirati [RabbitMQ](https://www.rabbitmq.com/download.html). Kada se server instalira potrebno ga je startovati.

Podrška za korišćenje RabbitMQ u Spring aplikaciji se može uključiti dodavanjem odgovarajuće zavisnosti u `pom.xml`:

```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```

#### rabbitmq-producer-example

Primer Spring aplikacije koja dodaje poruke u red.

Pre svega, potrebno je uspostaviti konekciju sa MQ serverom. MQ server je zadužen za čuvanje pristiglih poruka. U _RabbitmqProducerExampleApplication_ klasi se u `connectionFactory()` metodi uspostavlja konekcija sa MQ serverom. U primeru koristimo lokalni RabbitMQ server, ali to može da bude i neki cloud server, poput [CloudaMQP](https://www.cloudamqp.com/). U ovoj klasi se definišu i dva reda

```
@Bean
Queue queue(){
    return new Queue(queue, false);
}
```

i

```
@Bean
Queue queue2(){
    return new Queue(queue2, false);
}
```

gde je drugi parametar konstruktora vrednost `durable` atributa. Nazivi atributa su definisani u `application.properties` fajlu, gde se uz pomoć `@Value` anotacije injektuju u String varijable.

Definisan je jedan `binding rule`:

```
@Bean
DirectExchange exchange() {
    return new DirectExchange(exchange);
}

@Bean
Binding binding(Queue queue2, DirectExchange exchange) {
    return BindingBuilder.bind(queue2).to(exchange).with(routingkey);
}
```

Ovim se `queue2` vezuje za _exchange_ (parametar _to()_ metode je naziv) pod definisanim ključem (parametar _with()_ metode je ključ, odnosno _routing key_). Definisan je _Direct Exchange_, a podržani tipovi su predstavljeni klasom [Exchange Type](https://docs.spring.io/spring-amqp/api/org/springframework/amqp/core/ExchangeTypes.html). Kako za `queue` nije definisan _exchange_, on se vezuje za _Default Exchange_, gde je njegov naziv zapravo i _Routing Key_.

U klasi _Producer_ su implementirane dve metode:

1. `sendTo()` metoda koja šalje poruku na _Default Exchange_. Parametri metode su _routing key_ i poruka koja se šalje
2. `sendToExchange()` metoda koja šalje poruku na _Exchange_. Parametri metode su _exchange_, _routing key_ i poruka koja se šalje.

Sve poruke se šalju preko [RabbitTemplate](https://docs.spring.io/spring-amqp/docs/current/api/org/springframework/amqp/rabbit/core/RabbitTemplate.html) preko kojeg se ostvaruje komunikacija sa RabbitMQ serverom i pruža mogućnost rutiranja, slanja i primanja poruka.

Klasa _ProducerController_ je REST _controller_ sa dva _endpoint_-a:

1. _endpoint_ na koji se šalju poruke koje se dalje prosleđuju na _Default Exchange_ i
2. _endpoint_ na koji se šalju poruke koje se dalje prosleđuju na određeni _Exchange_.

Ovo znači da imamo jednu Spring aplikaciju koja koristi dva različita načina komunikacije: REST i Message Queue!

#### rabbitmq-consumer-example

Primer Spring aplikacije koja čita poruke iz reda.

Pre svega, potrebno je uspostaviti konekciju sa MQ serverom. MQ server je zadužen za čuvanje pristiglih poruka. U _RabbitmqConsumerExampleApplication_ klasi se u `connectionFactory()` metodi uspostavlja konekcija sa MQ serverom. U primeru koristimo lokalni RabbitMQ server, ali to može da bude i neki cloud server, poput [CloudaMQP](https://www.cloudamqp.com/).

Definisana su dva _Consumer_-a koji čitaju poruke koji su predstavljeni u dve različite klase koje imaju jednu metodu `public void handler(String message)` koja je anotirana `@RabbitListener` anotacijom. Ova anotacija ima jedan parametar `queues=` čija vrednost označava nazive redova sa kojih _Consumer_ čita poruke. U primeru, prvi _Consumer_ čita poruke sa _spring-boot1_ reda, a drugi sa _spring-boot2_ reda.

Listener će konvertovati poruku u odgovarajući tip koristeći odgovarajući konvertor poruka (implementacija [MessageConverter interfejsa](https://docs.spring.io/spring-amqp/api/org/springframework/amqp/support/converter/MessageConverter.html)).

#### Pokretanje primera

Da bi se primer uspešno demonstrirao, neophodno je da _producer_ i _consumer_ budu povezani na isti RabbitMQ server, jer se svi redovi i poruke čuvaju na jednom serveru.

1. Pokrenuti _rabbitmq-producer-example_ (radi na portu 8080)
2. Preko _Postman_-a poslati poruku na _Default Exchange_, red _spring-boot1_ (slika 1)
3. Pogledati ispis u konzoli: `Sending> ... Message=[ hello! ] RoutingKey=[spring-boot1]`
4. Pokrenuti _rabbitmq-consumer-example_ (radi na portu 8081)
5. Odmah nakon pokretanja, u konzoli se ispisuje `Consumer> hello!` zato što prilikom startovanja aplikacije, _consumer_ se automatski pretplati na red na koji je poslata poruka u koraku 3, a kako ta poruka nije obrađena, odmah je čita i obrađuje
6. Preko _Postman_-a poslati još jednu poruku na _Exchange_ pod nazivom _myexchange_, red _spring-boot2_ (slika 2)
7. Pogledati ispis u konzoli _rabbitmq-producer-example_ aplikacije: `Sending> ... Message=[ hello hello ] Exchange=[myexchange] RoutingKey=[spring-boot2]`
8. Pogledati ispis u konzoli _rabbitmq-consumer-example_ aplikacije: `Consumer2> hello hello`
9. Preko _Postman_-a poslati još jednu poruku na _Exchange_ pod nazivom _myexchange_, ali na red _spring-boot1_ koji je vezan za _Default Exchange_ (slika 3)
10. Pogledati ispis u konzoli _rabbitmq-producer-example_ aplikacije: `Sending> ... Message=[ hello hello ] Exchange=[myexchange] RoutingKey=[spring-boot1]`
11. Na konzoli _rabbitmq-consumer-example_ aplikacije nema ispisa da je neki _consumer_ obradio poruku jer za _myexchange_ ne postoji _routing key_ sa vrednošću _spring-boot1_

Takođe, možete da pristupite lokalnoj konzoli RabbitMQ servera tako što ćete u _Browser_-u ukucati http://localhost:15672/#/, kredencijali su _username: guest, password: guest_. Ovde možete da vidite sve redove koji su definisani, da pratite razmenu poruka, šaljete poruke...

Napomena: važno je da se ili ručno kreira red preko konzole ili da se pošalje bar jedna poruka na bilo koji red pre nego što se pokrene _rabbitmq-consumer-example_ jer red mora da postoji na serveru da bi se moglo na njega pretplatiti putem `@RabbitListener` anotacije!

![Slika 1](https://i.imgur.com/mxMCxZo.png "Slika 1")
Slika 1

![Slika 2](https://i.imgur.com/XgleBua.pngj "Slika 2")
Slika 2

![Slika 3](https://i.imgur.com/NwqtPD1.pngj "Slika 3")
Slika 3


### Redis

Primer komunikacije zasnovane na razmeni poruka unutar jedne Spring aplikacije i rada sa [Redis](https://redis.io/) nalaze se u _redis-pub-sub-example_ projektu. Za pokretanje primera potrebno je instalirati [Redis](https://redis.io/download). Kada se server instalira potrebno ga je startovati.

Startovanjem Redis servera - pokretanjem `redis-server.exe`, otvara se njegova konzola:

![slika-redis-server](https://i.imgur.com/cp1bs3h.png "Slika 7 - redis-server")

Slika 7 - konzola pokrenutog Redis servera

Za potrebe monitoringa razmene poruka potrebno je pokrenuti `redis-cli.exe`. Komandom `monitor` obezbeđuje se monitoring razmenjenih poruka:

![slika-redis-cli](https://i.imgur.com/BafK9rT.png "Slika 8 - redis-cli")

Slika 8 - redis-cli konzola sa `monitor` komandom

Podrška za korišćenje Redis u Spring aplikaciji se može uključiti dodavanjem odgovarajuće zavisnosti u `pom.xml`:

```
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

Za Redis postoje razne klijentske biblioteke koje se mogu naći na ovom [linku](https://redis.io/clients#java/). Klijentska biblioteka koja se koristi u ovom primeru je [Jedis](https://github.com/redis/jedis). Za nju je potrebno obezbediti podršku u vidu odgovarajuće zavisnosti u `pom.xml`:

```
<dependency>
	<groupId>redis.clients</groupId>
	<artifactId>jedis</artifactId>
</dependency>
```

#### redis-pub-sub-example

Primer Spring aplikacije koja i dodaje poruke u _topic_ i čita poruke sa _topic_-a.

Pre svega, potrebno je uspostaviti konekciju sa Radis serverom. Redis server je zadužen za čuvanje pristiglih poruka. U _RedisConfiguration_ klasi se u `redisConnectionFactory()` metodi uspostavlja konekcija sa Redis serverom. U primeru koristimo lokalni Redis server.

U istoj klasi metodom `redisTemplate()` definisan je [RedisTemplate](https://docs.spring.io/spring-data/redis/docs/current/api/org/springframework/data/redis/core/RedisTemplate.html) koji će služiti kao pomoćna klasa koja uprošćava sinhronizovani pristup Redis serveru za slanje i primanje poruka.

U ovoj klasi se definišе i _topic_:

```
@Bean
public ChannelTopic topic() {
    return new ChannelTopic(topicName);
}
```

U ovoj konfiguracionoj klasi se definišu i _publisher_ i _subscriber_ koji su ključni za [_Publish/Subscribe Messaging Model_](https://redis.io/topics/pubsub). Potrebno je kreirati i _RedisMessageListenerContainer_ metodom `public RedisMessageListenerContainer container(MessageListenerAdapter messageListenerAdapter)`.

Nazivi atributa su definisani u `application.properties` fajlu, gde se uz pomoć `@Value` anotacije injektuju u String varijable.

Implementacija _publisher_-a nalazi se u klasi _RedisMessagePublisher_, gde se u metodi `public void publish(String message)` šalju tekstualne poruke na prethodno definisan _topic_. Sve poruke se šalju preko _RedisTemplate-a_.

Implementacija _subscriber_-a nalazi se u klasi _RedisMessageSubscriber_, gde se u metodi `public void onMessage(Message message, byte[] bytes)` pročitane poruke sa _topic_-a smeštaju u statičku promenljivu preko koje ćemo dobiti sve poruke tog _subscriber_-a.

Klasa _RedisController_ je REST _controller_ sa dva _endpoint_-a:

1. _endpoint_ na koji se šalju poruke koje se dalje prosleđuju na _topic_ i
2. _endpoint_ preko kojeg se dobija lista svih pristiglih poruka za definisanog _subscriber_-a.

#### Pokretanje primera

1. Pokrenuti _redis-pub-sub-example_ primer.
2. Preko _Postman_-a poslati poruku na _topic_ _messages_ (slika 9).
3. Pogledati ispis u konzoli: `>> Publishing: Message{data='Hello, it's me!', author='Jovan Jovic'}`, a zatim: `>> Receiving: Message{data='Hello, it's me!', author='Jovan Jovic'}`
4. Preko _Postman_-a poslati još jednu poruku na _topic_ _messages_.
5. Pogledati ispis u _redis-cli_ konzoli (slika 10). U konzoli se vidi na koji _topic_ smo _subscribe_-ovani i koje poruke su _publish_-ovane.
6. Preko _Postman_-a poslati _GET_ zahtev kako bi se izlistale sve poruke _subscriber_-a (slika 11).

![Slika 9](https://i.imgur.com/zbv0oaL.png "Slika 9")

Slika 9

![Slika 10](https://i.imgur.com/xjFFvol.png "Slika 10")

Slika 10

![Slika 11](https://i.imgur.com/zIYx4rc.png "Slika 11")

Slika 11



### Kafka

Primer komunikacije zasnovane na razmeni poruka između dve Spring aplikacije i rada sa Kafkom nalaze se u kafka-producer-example i kafka-consumer-example projektima. 
Za pokretanje primera potrebno je preuzeti Kafku - https://dlcdn.apache.org/kafka/3.3.1/kafka-3.3.1-src.tgz.

Podrška za korišćenje Kafke u Spring aplikaciji se može uključiti dodavanjem odgovarajuće zavisnosti u pom.xml:
	
```
<dependency>
	<groupId>org.springframework.kafka</groupId>
	<artifactId>spring-kafka</artifactId>
</dependency>

	
```
#### kafka-producer-example

Potrebno je uspostaviti konekciju sa serverom. Kafkin server je zadužen za čuvanje pristiglih poruka. Da bi čuvali poruke neophodno je napraviti _TOPIC_. Primer kreiranja _TOPIC_-a se nalazi u klasi _KafkaTopic_. U primeru se nalazi jedan _TOPIC_, koga smo napravili od naziva _topic_-a, broja particija i broja replika. Poruka će se poslati tačno jednoj particiji unutar topic-a. Kafka određuje kojoj particiji ide poruka po principu round robin ili koristeći hash vrednost ključa. U primeru smo kreirali jedan topc sa 2 particije i jednom replikom. 

U klasi _KafkaTopics_ je implementirana metoda za kreiranje topic-a koja se poziva samo jednom, prilikom pokretanja aplikacije (PostConstruct anotacija):
	
```
    public void createTopic(String topicName, int partitions) throws Exception {

        try (Admin admin = Admin.create(properties)) {
            Set<String> existingTopics = admin.listTopics().names().get();
            if (existingTopics.contains(topicName)) {
                log.info("Tema vec postoji");
                return;
            }

            short replicationFactor = 1;
            NewTopic newTopic = new NewTopic(topicName, partitions, replicationFactor);

            CreateTopicsResult result = admin.createTopics(Collections.singleton(newTopic));

            KafkaFuture<Void> future = result.values().get(topicName);
            future.get();
            log.info("Kreirana nova tema: {}", topicName);
        }
    }
```


U klasi _ProducerConfiguration_ su konfigurisana dva bean-a:

    _kafkaTemplate()_ -> wrapper klasa za _Producer_ instancu.
    _producerFactory()_ -> konfiguracija _topic_-a.


#### kafka-consumer-example

Primer Spring aplikacije koja čita poruke iz reda.
Pre svega, potrebno je uspostaviti konekciju sa Kafkinim serverom i osluškivati odgovarajući _topic_.

U klasi  _ConsumerConfiguration_ su konfigurisana dva bean-a:

    _kafkaListenerContainerFactory()_ -> za pravljenje template-a.
    _consumerFactory()_ -> za konfigurisanje topic-a kako bi mogli da se pretplatimo na isti.

Klasa je anotirana _@EnableKafka_ anotacijom.

U klasi _Consumer_: 

  _@KafkaListener_ anotira metodu koja osluškuje i obrađuje poruke. Atributi koje podešavamo:
    _topics_ čija vrednost označava nazive _topic_-a sa kojih osluškujemo pristizanje poruka i
    _groupId_ omogućava više implementacija @KafkaListener-a u okviru jedne klase.
    
#### Pokretanje primera pomoću Docker-a
Da bi se primer uspešno demonstrirao, neophodno je instalirati Docker i pokrenuti docker-compose.yaml koji se nalazi u kafka-producer-example projektu. Komanda za pokretanje kafke:
```
    docker-compose up -d
```

Nakon što se kafka kontejner uspešno pokrene, pokrenuti i `kafka-producer-example` (radi na portu 8080). Producer će na svakih 5 sekundi slati poruke na predefinisani topic. 
Za čitanje poruka je potrebno pokrenuti `kafka-consumer-example`. Consumer počinje da čita poruke od poslednje pristigle.

U okviru consumer primera postoji i `application-second properties` koji omogućava pokretanje 2 instance consumera. Prvi consumer radi na portu 8081, a drugi an portu 8082. Pošto su to dve instance iste aplikacije, obe su pretplaćene na isti topic i pripadaju istoj grupi. U konkretnom primeru sa 2 particije, ukoliko su oba consumera aktivna, svaki će čitati poruke sa tačno jedne particije. Konfiguracija za pokretanje druge instance se nalazi na slici. Obratiti pažnju na parametar `-Dspring.profiles.active=second` koji aktivira čitanje porta iz fajla `application-second.properties`

![Slika 12](https://i.imgur.com/m3IVJ5K.png "Slika 12")


