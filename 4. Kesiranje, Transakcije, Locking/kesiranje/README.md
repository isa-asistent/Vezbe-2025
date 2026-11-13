# Keširanje podataka

Obično zahtevi veb aplikacija specificiraju dobavljanje određenog sadržaja koji se prezentuje korisnicima. Takvi zahtevi se obično odnose na pretraživanje, filtriranje, sortiranje sadržaja. Pri povećanom saobraćaju ili broju korisnika, broj interakcija sa bazom podataka raste. Pri tome ovakvi tipovi zahteva podrazumevaju operacije nad bazom podataka za koje je potrebno više vremena od operacija kreiranja, izmene, brisanja i dobavljanja singularnih redova. Uporedni zahtevi povećavaju pritisak na bazu podataka, uzimaju konekcije ka bazi, i duže drže niti same veb aplikacije. Zbog toga se često javlja problem da ovakvi slučajevi korišćenja imaju visoku latenciju. Isto tako se pomenuti sadržaj često zahteva više puta, od više korisnika. Zbog toga upitivanje baze za često trazene podatke postaje neoptimalno i opterećujuće, te se javlja potreba da se ovakvi podaci (ili njihova reprezentacija) skladište u drugim mestima, koja omogucuju brži pristup i rasterecenje glavne baze podataka.

Mehanizmi keširanja služe kako bi se upravo ovi podaci mogli zapamtiti u nekom skladištu koje nije glavna baza podataka. Obično je pristup ovakvom skladištu neverovatno brz, i kompletno eliminiše interakciju sa glavnom bazom podataka. Sadržaj koji često biva tražen, ili sadržaj koji se ne menja često, može biti skladišten u keš memoriji, te je praktično odmah spreman na dobavljanje i prezentaciju korisnicima.

---

Postoji više nacina keširanja podataka:

- **Keširanje na klijentskoj strani**: Sadržaj koji se jako retko menja može ostati u memoriji klijentske aplikacije, čime se uklanja potreba za slanjem zahteva ka serverskoj aplikaciji, osim kad podaci ne zastare ili postanu nevalidni.

- **Keširanje na serverskoj strani**: Sadržaj koji se često traži može biti skladišten u namenskoj keš memoriji, odakle se izvlači i prezentuje korisnicima. Ovime se odstranjuje potreba da se vrše upiti nad glavnom bazom podataka.

Iako nam keširanje podataka nudi brži pristup podacima, javljaju se pitanja vezana za čuvanje podataka smeštenih u keš memoriji.

- **Vreme koliko se podaci nalaze u kešu**: Termin koji se koristi za vreme skladištenja se zove **vreme zivota** (**_time to live - TTL_**). Možemo specificirati koliko dugo podaci ostaju u keš memoriji. Nakon isteka ovog vremenskog perioda, podaci bivaju uklonjeni, te oslobadjaju prostor za nove podatke koji se češće koriste. Isto tako se sprečava prepunjavanje keš memorije.
- **Kada je neophodno podatke iz keša ukloniti**: Evikcija podataka (_Cache Eviction_) je termin koji se vezuje za operacije koje se sprovode kada se podaci brišu iz keš memorije. Ponekad se mogu sprovesti izmene podataka, i neophodno je da te promene budu sveže i prikazane korisnicima kada ih oni zatraže. Ako su ovi podaci skladišteni u keš memoriji, sve do njihovog isteka života (ako je zadato), biće prezentovani korisnicima. U ovom slučaju korisnici dobijaju pogrešne podatke. Stoga se sprovode određene operacije gde se zastareli podaci brisu iz keša, ukoliko ti podaci bivaju izmenjeni, u cilju da korisnici ne bi dobijali netačne podatke.
  
- **Politika brisanja podataka**: Politika evikcije (_Eviction Policy_) je termin koji se odnosi na mehanizam koji rukuje podacima u keš memoriji prilikom njenog kompletnog popunjavanja. Ukoliko se keš memorija popuni, a pristignu novi podaci, potrebno je osloboditi određenu količinu prostora da bi se novi podaci skladištili. Ovde možemo izdvojiti dve najčešće politike:

  - **Najduže nekorišćeni**: LRU (_Least Recently Used_) specificira politku gde se podaci koji su najdavnije korišćeni (odnosno prisupano im je davno, a postoje podaci koji su bili skoro korišćeni) brišu iz memorije kako bi se prostor oslobodio.

  - **Najređe korišćeni**: LFU (_Least Frequently Used_) specificira politiku gde se podaci koji su najređe korišćeni (odnosno ne pristupa im se toliko često, a nalaze se u keš memoriji) brišu iz memorije kako bi se prostor oslobodio.
  
## Ehcache

**Ehcache** je open-source alat za keširanje zasnovan na _Java_ tehnologiji. Naširoko se koristi u _Java_ i _Spring_ aplikacijama zbog svoje jednostavne integracije i fleksibilnosti pri upravljanju memorijom.

Ovaj mehanizam je posrednik između aplikacije i baze podataka: kada aplikacija zatraži podatak, Ehcache prvo proveri da li je taj podatak već skladišten u keš memoriji. Ako jeste, vrati ga instantno; ako nije, podatak se čita iz baze, upisuje u Ehcache i potom vraća aplikaciji za narednu upotrebu.

---

### Karakteristike Ehcache-a

1. **Fleksibilno upravljanje istekom podataka (TTL i TTI):**
   Ehcache pruža dve važne konfiguracije za kontrolu trajanja podataka:
   - **TTL (*Time To Live*):** Ukupno vreme koliko podatak može živeti u kešu od trenutka kada je **kreiran/upisan**.
   - **TTI (*Time To Idle*):** Vreme koliko podatak može ostati u kešu bez da mu se **pristupi**. Ako podatak niko ne zatraži u okviru ovog intervala, biva automatski uklonjen, čak i ako mu TTL nije istekao.

2. **Podrška za politike evikcije:**
   Kada se zadati memorijski kapacitet popuni, Ehcache automatski primenjuje izabranu politiku za oslobađanje prostora, kao što su **LRU** (*Least Recently Used*), **LFU** (*Least Frequently Used*) i **FIFO** (*First In, First Out*).

Ehcache se lako integriše sa _Spring_ i _Spring Boot_ okvirima putem anotacija (poput `@Cacheable`, `@CachePut`, `@CacheEvict`), čime se logika keširanja potpuno odvaja od poslovne logike aplikacije.

---

Ehcache mozete koristi:

- Kada razvijate **Java / Spring** aplikacije koje trpiti veliku količinu čitajućih zahteva.

- Kada želite brzo keširanje unutar procesa iste aplikacije (_In-Memory Caching_) bez potrebe za podizanjem eksternih keš servera (poput _Redis-a_ ili _Memcached-a_).

- Kada vam je potrebna jednostavna konfiguracija i brza implementacija keširanja sa podrškom za Disk/Off-Heap proširenja.

---

Ehcache konfigurišete putem XML fajla, odnosno `ehcache.xml`, koji ćete smestiti u `src/main/resources`. Primer konfiguracije može biti:

```xml
<config xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns="http://www.ehcache.org/v3"
        xsi:schemaLocation="http://www.ehcache.org/v3 http://www.ehcache.org/schema/ehcache-core-3.0.xsd">

    <cache alias="korisniciCache">
        <!-- Vreme života podataka (TTL) - primer: 10 minuta -->
        <expiry>
            <ttl unit="minutes">10</ttl>
        </expiry>
        
        <!-- Ograničenje memorije: maksimalno 1000 unosa u On-Heap kešu -->
        <resources>
            <heap>1000</heap>
        </resources>
    </cache>

</config>
```
---

Da biste koristili **Ehcache** u vašoj aplikaciji, neophodno je da koristite anotacije koje vam nudi _**Spring Cache**_ apstrakcija.
Ove anotacije imaju dosta parametara kojima se upravlja način beleženja i čitanja iz keš memorije. Jezik koji se koristi za definisanje vrednosti parametara se zove _SpEl_ (_Spring Expression Language_).

Dokumentaciju i informacije o _Spring Cache_ i _SpEl_ možete naći ovde:
- [Spring Cache](https://docs.spring.io/spring-boot/reference/io/caching.html)
- [Spring Expression Language](https://docs.spring.io/spring-framework/reference/core/expressions.html)

--- 
Anotaciju `@Cacheable` možete postaviti iznad metode unutar svoje komponente (najprirodnije iznad servisne komponente). 

```java
@Service
public class KorisnikService {

    // Metoda koja poziva bazu podataka samo ako podatak nije već u kešu
    @Cacheable(value = "korisniciCache", key = "#id")
    public Korisnik pronadjiKorisnikaPoId(Long id) {
        // Simulacija teškog upita ka bazi podataka
        System.out.println("Dohvatanje podataka iz baze za ID: " + id);
        return korisnikRepository.findById(id).orElse(null);
    }
}
```

Ako se proveri sadržaj keša `korisniciCache` sa ključem koja je vrednost `id` i vrednost postoji u memoriji, metoda servisa neće biti pozvana, a sadržaj skladišten u keš memoriji biće vraćen pozivaocu. Ukoliko ne postoji zapis sa ovim ključem, metoda servisa će se izvršiti, nakon čega se njen rezultat upisuje u keš memoriju.

**Napomena**: Da bi se vasa POJO klasa smestila u keš memoriju, neophodno je da implementira interfejs `Serializable`.

---

Pored `@Cacheable`, _Spring Cache_ apstrakcija nudi još dve anotacije koje služe da keš ostane usklađen sa bazom podataka:

- `@CachePut` uvek izvršava metodu, a njen rezultat upisuje u keš pod zadatim ključem (prepisuje staru vrednost). Koristi se pri izmeni podataka.

- `@CacheEvict` uklanja zapis iz keša. Koristi se pri brisanju podataka, ili kada želimo da se pri sledećem čitanju podatak ponovo učita iz baze.
```java
@Service
public class KorisnikService {

    // Metoda se uvek izvršava, a vraćeni objekat se upisuje u keš pod ključem korisnik.id
    @CachePut(value = "korisniciCache", key = "#korisnik.id")
    public Korisnik azurirajKorisnika(Korisnik korisnik) {
        return korisnikRepository.save(korisnik);
    }

    // Nakon brisanja iz baze, uklanja se i zapis iz keša za dati ključ
    @CacheEvict(value = "korisniciCache", key = "#id")
    public void obrisiKorisnika(Long id) {
        korisnikRepository.deleteById(id);
    }

    // Uklanja sve zapise iz keša korisniciCache
    @CacheEvict(value = "korisniciCache", allEntries = true)
    public void obrisiSveIzKesa() {
    }
}
```

Ključ u slucaju anotacije `@CachePut` mora biti isti kao kod `@Cacheable` metode, inače se vrednost upisuje pod drugim ključem i čitanje ga neće pronaći.

## Redis

**Redis** (**_REmote DIctionary Server_**) je izuzetno popularan _in-memory_ sistem za skladištenje podataka koji se najčešće koristi kao keš baza podataka, broker poruka i keš memorija sa mogućnošću perzistentnosti. Za razliku od _Ehcache-a_ koji je _in-process_ (živi unutar memorije same _Java_ aplikacije), _Redis_ funkcioniše kao zaseban, eksterni server.

Ovakav pristup ga čini idealnim za distribuirane sisteme, mikro servise gde više instanci iste aplikacije treba da deli isti keš, kao i za situacije kada je potrebno memorijsko skladište velikog kapaciteta koje je nezavisno od životnog ciklusa jedne aplikacije.


---

Glavne karakteristike koje odlikuju _Redis_:

- **Visoke performanse i brzina**: Budući da sve podatke čuva u radnoj memoriji, _Redis_ postiže neverovatno brze odzive i može da obradi stotine hiljada operacija u sekundi.

- **Perzistentnost podataka**: Iako je u osnovi memorijska baza, _Redis_ omogućava opcionalno čuvanje podataka na disku (putem snapshot-ova ili _AOF - Append Only File_ strategije), čime se sprečava gubitak keširanih podataka u slučaju restartovanja servera.

- **Napredne strukture podataka**: Za razliku od običnih ključ-vrednost baza, _Redis_ podržava bogat set struktura podataka: stringove, heš tabele, liste, skupove, sortirane skupove i bitmape, što daje veliku fleksibilnost u modelovanju keša.

- **Napredne politike evikcije**: Kada dostigne zadati memorijski limit, _Redis_ automatski primenjuje konfigurisanu politiku brisanja, uključujući _LRU_, _LFU_, nasumično brisanje ili istekle ključeve sa _TTL-om_.

---

### Kada koristiti Redis?

- Kada razvijate distribuirane sisteme ili arhitekturu sa više instanci mirkoservisa koje moraju da dele jedinstveni keš.

- Kada vam je potrebna perzistentnost keša i nezavisnost keš memorije od životnog ciklusa jedne aplikacije.

- Kada su vam potrebne napredne strukture podataka ili naprednije funkcije poput Pub/Sub mehanizama za razmenu poruka.

---

### Primer konfiguracije i upotrebe u Spring Boot-u

Da biste povezali _Spring Boot_ aplikaciju sa _Redis-om_, najpre ga konfigurišete kroz `application.yml` (ili `application.properties`) fajl:

```yml
spring:
  data:
    redis:
      host: localhost
      port: 6379
```

Zatim, potpuno isto kao kod Ehcache-a, možete koristiti standardne _Spring_ anotacije za keširanje nad vašim servisima:

```java
@Service
public class KorisnikService {

    // Metoda koja proverava Redis keš pre upita u bazu podataka
    @Cacheable(value = "korisniciCache", key = "#id")
    public Korisnik pronadjiKorisnikaPoId(Long id) {
        System.out.println("Dohvatanje podataka iz baze za ID: " + id);
        return korisnikRepository.findById(id).orElse(null);
    }
}
```

Time sto koristite **Spring Cache** apstrakciju, prelazak na _Redis_ sa _Ehcache_ se svodi na izmenu konfiguracije. _XML_ konfiguracija nije potrebna, vec ćete definisati jednu konfiguracionu klasu koja pruža _Redis_ _Bean-ove_ u _runtime_.


**Napomena**: Da bi se vaša POJO klasa smestila u keš memoriju, neophodno je da implementira interfejs `Serializable`.

## Primeri

U ovim vežbama naci ćete dva primera _Spring Boot_ aplikacije. Jedan primer podržava _Ehcache_, drugi _Redis_. Da bi ste testirali aplikacije, možete koristiti alat kao što je **_Postman_**.

---

### Ehcache primer

U folderu `ehcache-primer` naći ćete primer _Spring Boot_ aplikacije gde se koristi _Ehcache_. Pod folderom `src/main/resources` imate konfiguraciju _Ehcache_. Ova konfiguracija je pokrivena komentarima, te je možete proučiti. U servisnoj klasi `ProductServiceImpl.java` naći ćete metode koje primenjuju mehanizam keširanja, zajedno sa dodeljenim ključevima koji su navedeni u `src/main/resources/ehcache.xml`.

---

### Redis primer

Da biste pokrenuli primer sa _Redis-om_, neophodno je da imate instaliran _Redis_ na svojoj lokalnoj mašini. Podrazumevani port koji je naveden u `application.properties` je 6379. Ukoliko nemate _Redis_ instaliran, možete pokrenuti `docker-compose.yml` komandom:

```bash
docker compose up
# ili ako ne zelite da blokirate terminal
docker compose up -d
```

Nakon što se (po potrebi) slika za _Redis_ preuzme i pokrene, možete pokrenuti _Redis_ primer, koji se nalazi u folderu `redis-primer`.

Unutar projekta, odnosno unutar koda klase `RedisConfig.java` naći ćete konfiguraciju _Redis_ keša, kao i definicije _Spring Bean-ova_ koji se koriste za upravljanje kešom.

U servisnoj klasi `ProductServiceImpl.java` naći ćete metode koje primenjuju mehanizam keširanja, zajedno sa proizvoljno dodeljenim ključevima, budući da nije neophodno prethodno ih navesti putem konfiguracije.

Unutar `docker-compose.yml` možete pročitati komentare koji su ostavljeni, a vezani su za konfiguraciju servisa.