# Vremenske baze podataka

Pojedini zahtevi veb aplikacija se tiču napredne analitike i praćenja stanja sistema kroz vreme. Možemo uzeti primer pametne kuće i senzora koji šalju očitavanja temperature. Korisnicima je ove podatke potrebno prikazati u vidu linijskih grafika (kako temperatura varira u jedinici vremena), ali i kroz druge vizuelizacije poput stubičastih grafika (_bar plots_).

## Zašto relacione baze nisu dovoljne?

Čuvanje velikog broja istorijskih podataka i merenja u tradicionalnim relacionim bazama podataka često nije optimalno iz nekoliko razloga:
* **Obim upisa:** Podaci stižu konstantno i u velikom broju, što može zagušiti i prepuniti standardne tabele.
* **Veličina indeksa:** Vremenom indeksi postaju preveliki, što usporava pretragu po vremenskim opsezima.
* **Upravljanje životnim ciklusom podataka:** Relacione baze nemaju ugrađene mehanizme za automatsko agregiranje ili brisanje starih podataka koji više nisu potrebni.

Stoga se javlja potreba za korišćenjem **vremenskih baza podataka**, koje su projektovane za skladištenje, efikasno indeksiranje i brze analitičke upite nad vremenskim serijama.

Vremenske baze podataka skladište podatke u vidu **tačaka (_data points_)**, pri čemu svaka tačka obavezno sadrži:
1. **Vremenski žig (_Timestamp_):** Tačan trenutak kada je merenje izvršeno.
2. **Vrednosti (_Fields_):** Jedna ili više numeričkih veličina koje se mere (obično realni brojevi).
3. **Oznake (_Tags / Metadata_):** Ključ-vrednost parovi koji bliže opisuju merenje (npr. `lokacija: zgrada_A`, `senzor: sprat_2`), što olakšava filtriranje i grupisanje.

## InfluxDB

_InfluxDB_ je jedna od najpopularnijih i najčešće korišćenih vremenskih baza podataka na tržištu. Dizajnirana je da podrži visoke performanse upisa, efikasnu kompresiju i brze analitičke upite nad ogromnim količinama vremenskih serija.

InfluxDB za koncept tačaka, koji reprezentuje skladištene podatke, vezuje nekoliko pojmova:

- **Measurement** - Logički naziv skupa merenja (npr. `temperature`). Ovo je najbliži ekvivalent pojma "tabela" u relacionim bazama podataka. Za razliku od relacionih baza, šema se ne definiše unapred: measurement, tagovi i polja nastaju prvim upisom.

- **Tags** - Oznake, koje mogu biti proizvoljni parovi ključ-vrednost (i ključ i vrednost su uvek stringovi). Oznake se automatski indeksiraju radi brzih pretraga, filtriranja i grupisanja podataka.

- **Fields** - Polja koja reprezentuju stvarne vrednosti koje se mere i čuvaju (mogu biti tipa float, integer, boolean ili string). Polja se **ne indeksiraju**, jer se obično koriste za matematičke operacije i agregaciju. Zbog toga se podaci po kojima se filtrira i grupiše (lokacija, senzor) čuvaju kao tagovi, a izmerene vrednosti kao polja.

- **Timestamp** - Precizan trenutak kada je podatak zabeležen. Ukoliko ne biva eksplicitno poslat, baza automatski upisuje trenutno vreme servera.

Različite verzije _InfluxDB_ koriste različite jezike za upite. Verzija u ovom primeru, odnosno verzija v2, koristi _Flux_ jezik za upite. Verzija v1 koristi _InfluxQL_, dok verzija v3 koristi _SQL_ (i _InfluxQL_), a _Flux_ nije podržan.

_Flux_ za razliku od _SQL-a_, koji se zasniva na jednom upitu sa klauzulama (`SELECT`, `WHERE`, `GROUP BY`), koristi koncept cevovoda (_pipeline_) - podaci prolaze kroz niz koraka, gde se u svakom koraku transformišu, filtriraju ili agregiraju. Kao ključni operator koristi se `|>`, koji rezultat prethodne funkcije prosleđuje kao ulaz u sledeću.

Tipičan Flux upit se sastoji od nekoliko logičkih koraka u nizu:

- **Definisanje izvora (from)**: Određuje iz kog _bucket_-a se povlače podaci.

- **Vremenski opseg (range)**: Definiše vremenski prozor od kog do kog trenutka se podaci pretražuju (npr. poslednjih sat vremena, poslednji dan).

- **Filtriranje (filter)**: Sužava izbor podataka na osnovu merenja (_measurement_), tagova (lokacija, senzori) ili polja. Koristi anonimnu funkciju oblika (r) => ... gde r predstavlja red podataka. Ugrađene kolone `_measurement`, `_field`, `_value` i `_time` sadrže redom naziv merenja, naziv polja, vrednost polja i vremenski žig, dok se tagovi pojavljuju kao kolone sa svojim imenom. Kolonama se može pristupati i kao `r["lokacija"]` i kao `r.lokacija`.

- **Agregacija i transformacija (aggregateWindow ili funkcije poput mean, sum)**: Grupiše podatke u vremenske intervale (npr. na svakih 5 minuta) i primenjuje matematičku funkciju nad njima. Parametar `createEmpty: false` znači da se prozori bez ijednog podatka ne vraćaju u rezultatu.

- **Ispis rezultata (yield)**: Vraća konačan skup podataka krajnjem klijentu ili vizuelizaciji. Parametar `name` daje ime dobijenom rezultatu, što je korisno kada upit vraća više rezultata.

Odnosno, sklopljeno u jedan _Flux_ upit:

```flux
from(bucket: "pametna_kuca")
  |> range(start: -24h)
  |> filter(fn: (r) => r["_measurement"] == "temperature")
  |> filter(fn: (r) => r["_field"] == "value")
  |> filter(fn: (r) => r["lokacija"] == "zgrada_A")
  |> aggregateWindow(every: 1h, fn: mean, createEmpty: false)
  |> yield(name: "mean")
```

## Grafana

_Grafana_ je otvorena platforma za vizuelizaciju i analitiku podataka koja se u praksi najčešće kombinuje sa vremenskim bazama poput _InfluxDB-a_. Ona omogućava da sirove podatke i složene upite pretvorite u interaktivne, pregledne i vizuelno privlačne komandne table (_dashboards_).

Osnovni pojmovi u Grafani:
- **Izvor podataka (_Data Source_)**: Konfigurabilna veza između Grafane i baze podataka (u našem slučaju InfluxDB). Grafana se povezuje sa bazom i omogućava izvršavanje upita direktno iz svog korisničkog interfejsa.

- **Komandna tabla (_Dashboard_)**: Centralna stranica koja sadrži skup povezanih vizuelnih prikaza i pruža uvid u stanje sistema u realnom vremenu.

- **Panel**: Pojedinačni vizuelni element unutar komandne table. Svaki panel koristi upit (poput našeg _Flux_ upita) da povuče podatke iz baze i prikaže ih u željenoj formi. Ta forma moze biti linijski grafikon (_time series_), stubičasti dijagram (_bar chart_), brojčani indikator (_stat_), merač (_gauge_), ili tabela.

U radu sa _InfluxDB_, _Flux_ upiti se unose direktno u editor panela unutar _Grafane_. _Grafana_ automatski prepoznaje vremensku osu (`_time`) i vrednosti iz rezultata upita, omogućavajući krajnjim korisnicima da dinamički menjaju opseg prikaza.

## Primer

### Podešavanje

Da biste pokrenuli primer u ovim vežbama, trebate imati instaliran Docker, Java programski jezik, NodeJS i Angular CLI.

- [Docker i docker compose instalacija](https://docs.docker.com/engine/install/)
- [Nodejs i NPM instalacija](https://nodejs.org/en/download)
- [Angular instalacija](https://angular.dev/installation)


Prvo je neophodno da pokrenete sve servise, sadržane u `docker-compose.yml` fajlu. Ovo ćete uraditi putem komande:
```bash
docker compose up
# ili da ne blokirate sebi terminal
docker compose up -d
```

Nakon što se slike kontejnera poskidaju iz registra, i kontejneri pokrenu možete pristupiti servisima _InfluxDB_ i _Grafana_ putem sledećih adresa koje ćete otkucati u pretraživaču:

```bash
# Influx
http://localhost:8086
# Grafana
http://localhost:3000
```

Kredencijali za login na admin panele oba servisa se nalaze unutar `docker-compose.yml`.

#### Pristup InfluxDB-u (port i token)

_InfluxDB_ sluša na portu `8086`. Svaka aplikacija ili servis koji želi da čita ili upisuje podatke (serverska aplikacija u _Spring Boot_-u i _Grafana_) mora da se poveže na tu adresu i da se autentifikuje **API tokenom**. Pored porta i tokena, potrebno je navesti i **organizaciju** (_org_) i **bucket** nad kojim se radi.

- **Port**: `8086` (adresa `http://localhost:8086` iz pretraživača i iz aplikacija koje se pokreću na vašoj mašini). Servisi koji rade unutar Docker mreže (npr. _Grafana_) umesto `localhost` koriste naziv _InfluxDB_ servisa iz `docker-compose.yml`.
- **Token**: API token se kreira u _InfluxDB_ korisničkom interfejsu (_Load Data => API Tokens_), a vrednost tokena korišćena u ovom primeru može biti definisana i u `docker-compose.yml`. Bez ispravnog tokena upiti se odbijaju.
- **Gde se podešava**: token, adresa, organizacija i bucket se navode u podešavanjima serverske aplikacije, kao i u konfiguraciji izvora podataka (_Data Source_) u _Grafani_.

#### Pokretanje frontend aplikacije

Da biste pokrenuli frontend aplikaciju, navigirajte se u folder `influx_front`. Ovde ćete pokrenuti naredne komande u svom terminalu:

```bash
# Instalacija svih zavisnosti aplikacije
npm install
# Serviranje aplikacije na adresi http://localhost:4200
ng serve --open
```
#### Pokretanje backend aplikacije

Neophodno je još da pokrenete serversku aplikaciju koja je napravljena u `Spring Boot`-u unutar svog razvojnog okruženja. Ova aplikacija nakon pokretanja će slušati na portu `8080`.

### Demonstracija

Na frontend aplikaciji imate prikazane tabelarne, grafičke i _Grafana_ prikaze. Možete promeniti u drop-down listi koji interval želite da gledate.

Bitno je uočiti da se selektovani interval šalje serverskoj aplikaciji u slučaju tabelarnog i grafičkog prikaza. Serverska aplikacija vrši upit u InfluxDB, nakon čega rezultat vraća frontend aplikaciji.

Kod _Grafana_ prikaza, vrši se upit direktno _Grafana_ servisu (prikaz je omogućen putem ugrađenih panela i `<iframe>` taga.)

Unutar foldera `grafana-settings` možete analizirati strukturu fajlova koji se tu nalaze. Ovi fajlovi se, zajedno sa `docker-compose.yml`, koriste kako bi se omogućilo automatsko podešavanje i perzistiranje komandnih tabli i panela. Ovaj pristup, takozvani provisioning, vam omogućava da imate konzistentan prikaz i podešavanja, nezavisno od mašine sa koje pokrećete servis.