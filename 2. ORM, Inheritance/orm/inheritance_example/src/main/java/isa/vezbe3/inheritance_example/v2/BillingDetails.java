package isa.vezbe3.inheritance_example.v2;

import static jakarta.persistence.DiscriminatorType.STRING;
import static jakarta.persistence.InheritanceType.SINGLE_TABLE;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

/*
 * Prednosti strategije nasledjivanja gde se koristi jedna tabela za sve entitete:
 * - postoji samo jedna tabela :)
 * - nema JOIN operacija za pribavljanje podataka
 * - bilo kakvo dodavanje novih atributa u bilo koju od klasa modela nasledjivanja ne zahteva promenu baze
 *
 * Mane strategije:
 * - ako neko koristi tabelu direktno, van koda, moze biti zbunjujuce jer ce neke kolone biti relevantne,
 * 		a neke ne (cak ako neka kolona pripada klasi naslednici a ima null vrednost moze uneti zabunu)
 * - potencijalno previse null vrednosti za tudja obelezja (bespotrebno koriscenje prostora),
 * 		ali baze imaju mehanizme za ustedu bacenog prostora, pogotovo za opcione kolone koje se nalaze sa desne strane tabela
 * - jedan tabela moze postati prevelika, cesto joj se pristupa i cesto se zakljucava,
 * 		pa to moze uticati na performanse
 * - postoje iskljucivo jedinstveni nazivi za kolone u toj jednoj tabeli,
 * 		pa se mora voditi racuna da kolone na koje se mapiraju atributi razlicitih klasa naslednica
 * 		ne imenuju isto
 */

@Entity(name="BillingDetailsV2")
@Table(name="v2_billingdetails")
// ovom anotacijom se naglasava tip mapiranja "jedna tabela po hijerarhiji"
@Inheritance(strategy=SINGLE_TABLE)
// ovom anotacijom se navodi diskriminatorska kolona
@DiscriminatorColumn(name="type", discriminatorType=STRING)
public abstract class BillingDetails {

    @Id
    @SequenceGenerator(name = "mySeqGenV2", sequenceName = "mySeqV2", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mySeqGenV2")
    @Column(name="id", unique=true, nullable=false)
    private Integer id;

    @Column(name="owner", unique=false, nullable=false)
    private String owner;

    public BillingDetails() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}