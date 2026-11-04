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

# Svaki Dockerfile MORA poceti komandom FROM, gde se spicificira osnovna slika za kreiranje nove slike.

# -Faza 1: build


# Osnovna slika sa Maven-om i JDK 21. Kljucna rec AS daje ime ovoj fazi
# da bismo je kasnije mogli referencirati (COPY --from=build).
FROM maven:3.9-eclipse-temurin-21 AS build

# Radni direktorijum unutar kontejnera. Sve naredne komande (COPY, RUN) se izvrsavaju
# relativno u odnosu na njega. Ako ne postoji, Docker ga kreira.
WORKDIR /app

# Prvo kopiramo samo pom.xml, pa tek onda source kod. Ovo je namerno zbog Docker keširanja:
# svaka instrukcija pravi sloj (layer), a Docker ponovo koristi sloj ako se nista nije promenilo.
# Zavisnosti se menjaju retko, a kod cesto, pa se zavisnosti ne preuzimaju ispocetka
# pri svakoj izmeni koda.
COPY ./pom.xml .

# Preuzima sve zavisnosti (i plugine) definisane u pom.xml i kesira ih u ovom sloju.

# RUN KORISTITE KADA TREBATE IZVRSITI KOMANDU TOKOM IZGRADJNE SLIKE
# CMD JE KOMANDA KOJA SE POKRENE KADA SE KONTEJNER POKRENE
RUN mvn -q dependency:go-offline

# Tek sada kopiramo source kod. Izmena koda invalidira kes samo od ove tacke nadalje,
# sloj sa zavisnostima ostaje sacuvan.
COPY ./src ./src

# Pravi izvrsni .jar fajl. clean brise stari target direktorijum, a -DskipTests
# preskace testove prilikom build-a slike (testove je bolje pokretati odvojeno).
RUN mvn -q clean package -DskipTests

# -Faza 2: runtime

# Nova, cista osnovna slika. Sve iz prethodne faze se odbacuje, osim onoga sto
# eksplicitno kopiramo. Koristimo samo JRE (ne i JDK) jer nam za pokretanje aplikacije
# kompajler nije potreban, a alpine varijanta je veoma mala.
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Pravimo sistemsku grupu i korisnika "spring" bez privilegija, pa aplikaciju pokrecemo
# pod njim umesto pod root-om. Ako neko kompromituje aplikaciju, ima manje privilegija
# unutar kontejnera. (-S znaci system user/group.)
RUN addgroup -S spring && adduser -S spring -G spring
USER spring

# Kopiramo samo gotov .jar iz faze "build". Zbog wildcard-a (*.jar) ne moramo da znamo
# tacno ime i verziju fajla, ali pazite da u target direktorijumu postoji samo jedan
# takav .jar, inace COPY nece uspeti. Fajl preimenujemo u app.jar radi jednostavnosti.
COPY --from=build /app/target/*.jar app.jar

# EXPOSE je cista dokumentacija: govori da aplikacija slusa na portu 8080.
# On sam NE otvara port prema lokalnoj masini; to radi kljuc "ports" u docker-compose.yml
# (kod nas "8081:8080"). Prakticno simbolicna naznaka :).
EXPOSE 8080

# Komanda koja se izvrsava pri pokretanju kontejnera. Java proces postaje glavni proces kontejnera (PID 1) i ispravno prima
# signale (npr. SIGTERM pri docker compose down), pa se aplikacija gasi uredno.
ENTRYPOINT ["java", "-jar", "app.jar"]