# Blob Storage

Efikasno upravljanje datotekama koje su neophodne za ispunjenje domenskih zahteva aplikacije je od kljucnog znacaja. Naivne implementacije obicno se svode na cuvanje datoteka u fajl sistemu (_file system_) ili unutar relacione baze podataka kao varijente LOB(_Large Object_), odnosno BLOB ili CLOB.

Upravljanje datotekama unutar file systema postaje neefikasno ukoliko je neophodno cuvanje vece kolicine datoteka ili ako te datoteke su pozamasne velicine. Isto tako ne postoji uniforman nacin, odnosno API, kojima se omogucava njihovo upravljanje, vec to zavisi od implementacije do implementacije. Isto tako u arhitekturama sa vise servisa, mesto na kom se cuvaju datoteke postaje neuniformno, osim ako to nije eksplicitno odredjeno.

Cuvanje datoteka u relacionoj bazi uvodi jos jednu odgovornost takvog tipa baze podataka, gde ona predstavlja i skladiste datoteka. Osim toga, pojedine baze nemaju dovoljno dobre i skalabilne mehanizme za njihovim upravljanjem. _Cleanup_ postaje otezan, pretraga relevantih podataka postaje usporena.

Time se izrice potreba za definisanjem skladista koje iskjucivo sluzi za cuvanje datoteka, i koje ujedno ima definisan API za pristup i upravljanje tim datotekama.

## AWS S3

_Amazon Web Services_ cloud platforma nudi jedan od svojih servisa po imenu _S3_. _S3_ omogucava cuvanje datoteka gde je u najmanju ruku pristup datoteci odredjen njenim kljucem. Kljuc predstavlja neku vrednost niza karaktera koja uniformno indentifikuje datoteku unutar skladista. Ovime se omogucuje lako definisanje indentifikatora datoteka, kao i njihov pristup.

Pored toga, _S3_ nudi i mehanizam otpremanja i dobavljanja datoteka putem URLova, odnosno mehanizma nazvanog _presigned URLs_. Klasicnim pristupom serverska aplikacija se bavi perzistencijom slika, i ujedno time drzi slike u svojoj memoriji sto moze biti opterecujuce. Mehanizmom pretpotpisanih URL-ova, omogucuje se da se specificira dobavljanje datoteka, kao i njena otprema.

### GET URL

GET URL-om se omogucuje da se obavi pristup datoteci bez ekspicitnog preuzimanja. URL se generise putem _S3_ servisa, te biva dostavljen klijentu na koriscenje. Ovime klijent direktno preuzima sliku i sebi je prezentuje, umesto da serverska aplikacija vraca celokupnu sliku kao rezultat zahteva za njeno preuzimanje. Ovime klijent preuzima odredjeni nivo odgovornosti u slucaju koriscenja gde je dopremanje datoteke neophodno.

### PUT URL

PUT URL-om se omogucuje da se izbegne slanje cele datoteke na serversku aplikaciju. Neophodno je da klijent zahteva generisanje ovakvog URL-a, pri cemu on njemu biva direktno dostavljen. Jednostavnim zahtevom, klijent moze da sprovede otpremanje svoje datoteke direktno u skladiste datoteka, pri cemu ne opterecuje serversku aplikaciju. Ovime klijent preuzima odredjeni nivo dogovornosti u slucaju koriscenja gde je otpremanje datoteke neophodan.

## Lokalni S3 Servisi

_AWS S3_ je primarno cloud servis koji _Amazon Web Services_ nudi. Kako bi se omogucio slican nacin upravljanja skladistem datoteka, postoje servisi koji koriste _S3_ api i standard za upravljanje datotekama.

### MinIO `:(`

_MinIO_ je predstavljao primarni servis za lokalno upravljanje datotekama koji je koristio _S3_ API. Od februara 2025. godine postao je _Deprecated_ i postavljen u `maintenance` status. Na oficijalnom docker registru je povucen _image_ ovog servisa, ali postoje distribucije na odredjenim neoficijalnim registrima. Ovo ostavilo za sobom vakuum, koji novi servisi sa _S3_ APi-jem pokusavaju da popune. Tako da postoje alternative `:)`.

### RustFS

Servis koji je pisan u programskom jeziku `rust` koji je neverovatno srodan _MinIO_. Postuje _S3_ API, te se moze smatrati direktnom zamenom za _MinIO_. Primeri sadrzani u ovim vezbama koriste RustFS i pokrivaju konfiguraciju servisa, kako on radi, demostraciju klasicnog rukovanja datotekama kao i napredno rukovanje datotekama putem _S3_ mehanizama.

## Primeri

### Predkoraci

Neophodno je da imate instaliran `docker`, `docker compose`, `nodejs`, `npm` i `Angular CLI`. Linkove za instalacije mozete pronaci ovde:

- [Docker i docker compose instalacija](https://docs.docker.com/engine/install/)
- [Nodejs i NPM instalacija](https://nodejs.org/en/download)
- [Angular instalacija](https://angular.dev/installation)

### Konfiguracija servisa

U ovom primeru se koristi _RustFS_. Neophodno je pokrenuti servis, cija je konfiguracija slike sadrzana unutar `docker-compose.yml`.

Odnosno:

```bash
docker compose up
```

Ili ako ne zelite da blokirate terminal:

```bash
docker compose up -d
```

Potom da bi smo se navigirali na admin konzolu, u pretrazivacu cemo pristupiti admin panelu i na login strani uneti kredencijale. Kredencijali su specificirani unutar `docker-compose.yml`. Adresa admin panela je:

```text
http://localhost:9001
```

Ovde mozemo putem grafickog interfejsa krerati bucket-e u kojima ce se nalaziti nase datoteke. Primer ce kreirati bucket pod imenom `my-test-bucket` prilikom pozivanja `docker compose up`.

### Demonstracija

Pokrenite serversku aplikaciju u `blob_storage_back` putem svog razvojnog okruzenja. Pokrenuti _Angular_ aplikaciju u `blob_storage_front`, odnosno prvo pokrenuti `npm install` da instalirate zavisnosti, pa putem komande `ng serve` (odnostno `ng serve --open`) je pokrenite. Navigirajte se na `localhost:4200` unutar vaseg pretrazivaca da pristupite _Angular_ aplikaciji.

Primer demonstrira dva pristupa, onaj klasican i onaj putem URL mehanizama. Serverska aplikacija sadrzi dve kontrolerske komponente (`WhatNotToDoImageController.java` i `WhatToDoImageController.java`). Obe klase su pokrivene sa `javadoc`, tako da mozete da analizirate kod i smernice koje su u njemu napisane.
