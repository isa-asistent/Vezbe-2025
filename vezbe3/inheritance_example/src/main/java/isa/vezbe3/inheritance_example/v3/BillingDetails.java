package isa.vezbe3.inheritance_example.v3;

import static jakarta.persistence.InheritanceType.JOINED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

/*
 * Prednosti strategije nasledjivanja gde se koristi po jedna tabela za svaki entitet:
 * - sve kolone su relevantne za svaku torku u tabeli, lakse su za razumevanje i nema bacanja prostora
 * - mapiranje modela na bazu je skoro 1 na 1 (svaka klasa ima svoju tabelu, svaki atribut ima svoju kolonu)
 * Mane strategije:
 * - da bi se ucitao objekat mora se koristiti vise tabela, sto znaci neizbeznu upotrebu JOINova
 * - roditeljska klasa moze biti usko grlo jer joj se precesto pristupa
 */

@Entity(name="BillingDetailsV3")
@Table(name="v3_billingdetails")
// ovom anotacijom se naglasava mapiranje tipa "jedna tabela po svakoj klasi"
@Inheritance(strategy=JOINED)
public class BillingDetails {

    @Id
    @SequenceGenerator(name = "mySeqGenV3", sequenceName = "mySeqV3", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mySeqGenV3")
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