# Dockerfile sluzi da bi se napravila slika vaseg servisa/aplikacije.
# Sadrzi instrukcije kojima se prilikom izgradnje slike kreira osnovna slika,
# file sistem, kopiraju datoteke aplikacije, podesava okruzenje, itd.

# Ovaj Dockerfile koristi tzv. multi-stage build: build se izvrsava u jednoj slici
# (koja ima Maven i JDK), a u finalnu sliku se kopira samo gotov .jar fajl.
# Rezultat je znatno manja i bezbednija finalna slika, jer ne sadrzi alate za build.

# Odvojeno od toga, Docker sliku sastavlja iz slojeva: svaka instrukcija koja menja
# fajl sistem (RUN, COPY, ADD) pravi jedan sloj. Slojevi koji se nisu promenili se
# ponovo koriste iz kesa, dok se izmenjeni (i svi iza njih) ponovo izgradjuju.
# Zbog toga je redosled instrukcija bitan i izgradnja slike je znatno brza.

# Svaki Dockerfile MORA poceti komandom FROM, gde se specificira osnovna slika za kreiranje nove slike.

# -Faza 1: build

# Osnovna slika sa Maven-om i JDK 17. Kljucna rec AS daje ime ovoj fazi
# da bismo je kasnije mogli referencirati (COPY --from=build).
FROM maven:3.9-eclipse-temurin-17 AS build

# Radni direktorijum unutar kontejnera. Sve naredne komande (COPY, RUN) se izvrsavaju
# relativno u odnosu na njega. Ako ne postoji, Docker ga kreira.
WORKDIR /app

# Prvo kopiramo samo pom.xml, pa tek onda source kod. Ovo je namerno zbog Docker kesiranja:
# svaka instrukcija pravi sloj (layer), a Docker ponovo koristi sloj ako se nista nije promenilo.
# Zavisnosti se menjaju retko, a kod cesto, pa se zavisnosti ne preuzimaju ispocetka
# pri svakoj izmeni koda.
COPY pom.xml .

# Preuzima sve zavisnosti (i plugine) definisane u pom.xml i kesira ih u ovom sloju.

# RUN KORISTITE KADA TREBATE IZVRSITI KOMANDU TOKOM IZGRADNJE SLIKE
# CMD JE KOMANDA KOJA SE POKRENE KADA SE KONTEJNER POKRENE
RUN mvn -q dependency:go-offline

# Tek sada kopiramo source kod. Izmena koda invalidira kes samo od ove tacke nadalje,
# sloj sa zavisnostima ostaje sacuvan.
COPY src ./src

# Pravi izvrsni .jar fajl. -DskipTests preskace testove prilikom build-a slike:
# testovi ove aplikacije zahtevaju bazu podataka, koja tokom izgradnje slike nije dostupna
# (testove je bolje pokretati odvojeno).
RUN mvn -q package -DskipTests

# -Faza 2: runtime

# Nova, cista osnovna slika. Sve iz prethodne faze se odbacuje, osim onoga sto
# eksplicitno kopiramo. Koristimo samo JRE (ne i JDK) jer nam za pokretanje aplikacije
# kompajler nije potreban.
FROM eclipse-temurin:17-jre
WORKDIR /app

# Kopiramo samo gotov .jar iz faze "build". Zbog wildcard-a (*.jar) ne moramo da znamo
# tacno ime i verziju fajla, ali pazite da u target direktorijumu postoji samo jedan
# takav .jar, inace COPY nece uspeti. Fajl preimenujemo u app.jar radi jednostavnosti.
COPY --from=build /app/target/*.jar app.jar

# Ovde namerno nema EXPOSE instrukcije, niti porta, imena aplikacije i parametara baze.
# Ista slika se koristi za obe instance aplikacije, a ono po cemu se instance razlikuju
# (SERVER_PORT, SPRING_APPLICATION_NAME) zadaje se pri pokretanju, kroz "environment"
# u docker-compose.yml. EXPOSE je inace cista dokumentacija i sam NE otvara port.

# Komanda koja se izvrsava pri pokretanju kontejnera. Java proces postaje glavni proces
# kontejnera (PID 1) i ispravno prima signale (npr. SIGTERM pri docker compose down),
# pa se aplikacija gasi uredno.
ENTRYPOINT ["java", "-jar", "app.jar"]
