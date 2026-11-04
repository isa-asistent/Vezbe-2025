# Docker

Kada aplikaciju razvijate lokalno, ona radi na vašem racunaru sa vašim verzijama jezika, biblioteka i alata. Kada istu aplikaciju prebacite na drugi računar ili server, često se javljaju problemi: različite verzije programskih jezika i biblioteka, nedostajuće zavisnosti, drugačiji operativni sistem. Poznata izjava "kod mene radi" (_works on my machine_) je upravo posledica ovoga.

**Docker** rešava ovaj problem tako što aplikaciju pakuje zajedno sa svim što joj je potrebno za rad (runtime, biblioteke, konfiguracija) u jedinstvenu celinu koja se izvršava isto na bilo kom računaru koji ima instaliran **Docker**.

Pored toga, **Docker** omogućava da se više servisa (web aplikacija, baza podataka, red poruka, _reverse proxy_) pokrene na istom računaru, međusobno izolovani, bez potrebe da se bilo koji od njih instalira direktno na operativni sistem.

## Osnovni pojmovi

**Image (slika)** je šablon, odnosno _read-only_ paket koji sadrži aplikaciju i sve što joj je potrebno. _Image_ se ne izvršava, već se iz njega kreiraju kontejneri.

**Container (kontejner)** je pokrenuta instanca _image-a_. Iz jednog _image-a_ možete pokrenuti više kontejnera, odnosno više nezavisnih instanci iste aplikacije.

**Dockerfile** je tekstualna datoteka koja opisuje korake izgradnje _image-a_. Sadrži instrukcije koje su neophodne da bi se kreirala slika, sa svojim _file_ sistemom, zavisnostima i konfiguracijom.

**Registry** je mesto gde se image-i čuvaju i odakle se preuzimaju. Najpoznatiji je [Docker Hub](https://hub.docker.com), odnosno _Docker-ov_ oficijalni registar. Moguće je povlačiti slike i sa drugih registara, pa čak imati i vaš lokalni registar (alat _Artifactory_).

**Volume** je mehanizam za trajno čuvanje podataka. Podaci unutar kontejnera nestaju kada se kontejner obriše, pa se, na primer, podaci baze podataka čuvaju u volumenima.

**Network (mreža)** omogućava kontejnerima da komuniciraju jedni sa drugima, i to preko imena servisa umesto IP adresa. Ovo omogućava komunikaciju između servisa u _Docker_ okruženju, moguće ju je specificirati unutar `docker-compose.yml` fajla.

Razlika u odnosu na virtuelne mašine je u tome što kontejneri ne nose sa sobom ceo operativni sistem, već dele jezgro (kernel) domaćina. Zbog toga su znatno lakši i brzi za pokretanje.

## Instalacija

Docker možete preuzeti sa originalnog veb sajta:

```text
https://docs.docker.com/get-docker/
```

Na Windows-u i macOS-u instalirate **Docker Desktop**, koji sadrži sve neophodno, uključujući i `docker compose`.

Na Linux-u možete pratiti zvanično uputstvo za vašu distribuciju, a na Ubuntu/Debian sistemima:

```bash
sudo apt install docker.io docker-compose-v2
```

Ili ako koristite _dnf_ (Fedora), prateći zvanično uputstvo za dodavanje Docker repozitorijuma.

Nakon instalacije neophodno je da pokrenete Docker servis:

```bash
sudo systemctl start docker
sudo systemctl enable docker   # automatsko pokretanje pri startu sistema
```

Da biste Docker komande mogli da pozivate bez `sudo`, dodajte svog korisnika u `docker` grupu (potrebno je da se odjavite i ponovo prijavite):

```bash
sudo usermod -aG docker $USER
```

Instalaciju možete proveriti komandom:

```bash
docker run hello-world
```

## Osnovne komande

Preuzimanje image-a sa registry-ja:

```bash
docker pull nginx
```

Pokretanje kontejnera:

```bash
docker run nginx
```

Ovo će blokirati terminal. Ako to ne želite, koristite `-d` (_detached_) flag. Pored toga, najčešće želite da mapirate port kontejnera na port vašeg racunara pomoću `-p` flag-a (`-p <port_domacina>:<port_kontejnera>`) i da kontejneru zadate ime pomoću `--name`:

```bash
docker run -d -p 8787:80 --name moj-nginx nginx
```

Nakon ovoga, ako u pretraživaču odete na `http://localhost:8787`, videćete početnu stranu Nginx-a.

Prosledjivanje promenljivih okruženja (na primer za bazu podataka):

```bash
docker run -d -p 5432:5432 --name moja-baza \
  -e POSTGRES_USER=admin \
  -e POSTGRES_PASSWORD=admin \
  -e POSTGRES_DB=transactions \
  postgres:16
```

Za pregled i upravljanje kontejnerima koristite sledeće komande:

```bash
docker ps                    # prikaz pokrenutih kontejnera
docker ps -a                 # prikaz svih kontejnera (i zaustavljenih)
docker logs moj-nginx        # ispis logova kontejnera
docker logs -f moj-nginx     # pracenje logova uzivo
docker stop moj-nginx        # zaustavljanje kontejnera
docker start moj-nginx       # ponovno pokretanje
docker rm moj-nginx          # brisanje kontejnera (mora biti zaustavljen)
docker rm -f moj-nginx       # brisanje i pokrenutog kontejnera
docker exec -it moj-nginx bash   # otvaranje terminala unutar kontejnera
docker images                # prikaz preuzetih image-a
docker rmi nginx             # brisanje image-a
```

Ako dobijete poruku da je port već zauzet, ili da kontejner sa tim imenom već postoji, neophodno je da prvo zaustavite i obrišete postojeći kontejner, ili da izaberete drugi port odnosno ime.

## Dockerfile

Kako biste od sopstvene aplikacije napravili image, neophodno je da napišete `Dockerfile`. Primer za _Spring Boot_ aplikaciju možete naći unutar foldera `app` ovih vežbi.

Najvažnije instrukcije:

| Instrukcija          | Opis                                                        |
| -------------------- | ----------------------------------------------------------- |
| `FROM`               | Osnovni image na osnovu koga se gradi novi                  |
| `WORKDIR`            | Radni direktorijum za sve naredne instrukcije               |
| `COPY`               | Kopira datoteke sa vaseg racunara u image                   |
| `RUN`                | Izvrsava komandu prilikom **izgradnje** image-a             |
| `ENV`                | Postavlja promenljivu okruženja                             |
| `EXPOSE`             | Dokumentuje koji port aplikacija koristi                    |
| `ENTRYPOINT` / `CMD` | Komanda koja se izvršava prilikom **pokretanja** kontejnera |

Image se gradi komandom (tačka na kraju označava direktorijum u kome se nalazi Dockerfile):

```bash
docker build -t app:1.0 .
```

Nakon izgradnje, možete ga pokrenuti, i to, na primer, dva puta sa različitim imenima i portovima:

```bash
docker run -d -p 8086:8080 --name app-primary app:1.0
docker run -d -p 8087:8080 --name app-secondary app:1.0
```

Primetite da oba kontejnera unutar sebe koriste isti port `8080`, a razlikuju se samo portovi koji su izloženi na vašem racunaru. Ovo je jedna od prednosti izolacije kontejnera.

Pored toga, možete kreirati i `.dockerignore` datoteku, koja radi isto što i `.gitignore`: navodi datoteke i foldere koji se neće kopirati u _image_ (na primer `.git`, `node_modules`, `.idea`).

## Volume-i

Podaci unutar kontejnera su privremeni. Kada obrišete kontejner baze podataka, nestaju i svi podaci. Kako bi se podaci sačuvali, koristi se volume:

```bash
docker volume create app-data
docker run -d --name moja-baza \
  -v app-data:/var/lib/postgresql/data \
  -e POSTGRES_PASSWORD=admin \
  postgres:16
```

Sada možete obrisati i ponovo kreirati kontejner, a podaci će ostati sačuvani u volume-u `app-data`.

Možete takođe mapirati i datoteku ili folder sa vašeg računara (_bind mount_), na primer neku konfiguraciju ili inicijalizacionu skriptu:

```bash
docker run -d --name moja-baza \
  -v "$(pwd)/init.sql:/docker-entrypoint-initdb.d/init.sql:ro" \
  -e POSTGRES_PASSWORD=admin \
  postgres:16
```

Sufiks `:ro` označava da kontejner može samo da čita datoteku (_read-only_).

## Mreže

Kontejneri koji se nalaze u istoj korisnički definisanoj mreži mogu komunicirati putem imena kontejnera, umesto putem IP adresa:

```bash
docker network create app-net
docker run -d --name moja-baza --network app-net postgres:16
docker run -d --name app --network app-net app:1.0
```

Unutar `app` kontejnera, bazi se pristupa na adresi `moja-baza:5432`.

Treba imati na umu da `localhost` unutar kontejnera oznacava **sam kontejner**, a ne vaš računar. Ovo je česta greška prilikom konfiguracije konekcije ka bazi podataka: aplikacija koja radi u kontejneru mora da gađa ime servisa baze (na primer `postgres`), a ne `localhost`.

## Docker Compose

Pokretanje svakog servisa zasebno, uz sve flag-ove, vrlo brzo postaje nepregledno. Zbog toga se koristi **Docker Compose**. U jednoj `docker-compose.yml` datoteci opisuje se cela infrastruktura, a pokreće se jednom komandom.

Primer `docker-compose.yml` fajla imate u ovim vežbama. Fajl je pokriven komentarima, te ga možete analizirati.

Obratite pažnju na sledeće:

- Servisi se međusobno pronalaze po **imenu servisa** (`app`, `postgres`, `pgadmin`), jer Compose automatski kreira zajedničku mrežu. Zbog toga aplikacija bazi pristupa preko `DB_HOST: postgres`, a ne preko `localhost` ili `container_name`.
- Servis `app` se **gradi** iz izvornog koda (ključ `build`), dok se `postgres` i `pgadmin` preuzimaju kao gotove slike iz registra (ključ `image`).
- Ključ `depends_on` određuje redosled pokretanja. Sam po sebi proverava samo da li je kontejner pokrenut, a ne i da li je servis unutar njega spreman. Zato se uz njega koristi `healthcheck` na bazi i uslov `service_healthy`, kako se aplikacija ne bi pokrenula pre nego što baza može da prihvati konekcije.
- Ključem `ports` portovi kontejnera se mapiraju na portove vašeg računara (`"8081:8080"` znači da je aplikacija koja unutar kontejnera sluša na `8080` dostupna na `localhost:8081`).
- Podaci baze čuvaju se u imenovanom volume-u `pgdata`, pa opstaju i nakon brisanja kontejnera.

Osnovne komande:

```bash
docker compose up              # pokretanje svih servisa
docker compose up -d           # pokretanje u pozadini
docker compose up --build      # ponovna izgradnja image-a pre pokretanja
docker compose ps              # stanje servisa
docker compose logs -f app     # logovi jednog servisa
docker compose stop            # zaustavljanje servisa
docker compose down            # zaustavljanje i brisanje kontejnera i mreze
docker compose down -v         # isto, uz brisanje volume-a (brisu se podaci!)
```

## Primer

### Setup

U primeru ovih vežbi dostavljena vam je backend aplikacija (folder `app`) sa svojim `Dockerfile`-om, `docker-compose.yml` i `pgadmin-servers.json`.

Pokrenite celu infrastrukturu jednom komandom:

```bash
docker compose up --build -d
```

Stanje servisa proverite komandom `docker compose ps`. Primetićete da `postgres` ima status `healthy`, i da se `app` pokreće tek nakon toga. Ako nešto ne radi kako treba, pogledajte logove:

```bash
docker compose logs -f app
```

Ako dobijete grešku da je port `5432` zauzet, verovatno vam na računaru već radi lokalna instanca PostgreSQL-a. Zaustavite je ili promenite levi deo mapiranja porta u `docker-compose.yml`.

### Demonstracija

Aplikacija je dostupna na `http://localhost:8081`, iako ona unutar kontejnera sluša na portu `8080`. Pri tome se **ništa** osim Docker-a ne instalira i ne pokreće na vasem racunaru: ni Postgres, ni alat za pregled baze.

Za pregled baze podataka otvorite `pgAdmin` na `http://localhost:5050` i prijavite se kredencijalima zadatim u `docker-compose.yml` (`admin@example.com` / `admin`). Server i baza `transactions` će se pojaviti automatski, zahvaljujući fajlu `pgadmin-servers.json` koji je mapiran u kontejner. U suprotnom bi ih bilo neophodno ručno dodavati prilikom svakog pokretanja.

Možete proveriti i da podaci zaista opstaju van životnog ciklusa kontejnera. Prvo obrišite kontejnere (volume ostaje):

```bash
docker compose down
docker compose up -d
```

Podaci u bazi će i dalje biti prisutni, jer se čuvaju u volume-u `pgdata`. Ako želite da krenete ispočetka, sa praznom bazom, obrišite i volume:

```bash
docker compose down -v
```

Na kraju, sve možete ugasiti i očistiti komandom:

```bash
docker compose down
```
