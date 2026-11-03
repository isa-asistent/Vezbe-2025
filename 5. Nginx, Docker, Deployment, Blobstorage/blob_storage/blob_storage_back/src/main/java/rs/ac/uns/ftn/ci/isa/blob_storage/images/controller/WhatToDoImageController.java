package rs.ac.uns.ftn.ci.isa.blob_storage.images.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.ci.isa.blob_storage.images.dto.GetPutUrlResponse;
import rs.ac.uns.ftn.ci.isa.blob_storage.images.service.ImageService;

import java.io.IOException;
/**
 * Demonstrira alternativu kako se slika moze upload-ovati ili download-ovati.
 *
 * Nacin koji je ovde demonstrarian se oslanja na mehanizam potpisanih URL koje generise servis za skladistenje file-ova.
 *
 * Prilikom download-a slike, dovoljno je generisati GET URL kom klijent moze da pristupi radi prezentacije slike.
 *
 * Prilikom upload-a slike, generise se PUT URL koji biva dostavljen klijentu. Odredjeni nivo odgovornosti se onda prebacuje na klijenta,
 * gde je neophodno da on sam obavi upload slike u skladiste file-ova.
 *
 * Ovakav pristup omogucuje kompletnu seperaciju nacina kako se slika upload-uje ili download-uje od glavne biznis logike.
 * Takodje sprecava da se slika salje direktno na server, eleminisuci da se ona drzi u memoriji servera. Ovime se mogu uplaod-ovati i veci file-ovi.
 *
 * Uprkos tome, na klijenta spada odredjena odgovornost da bi se interakcija sa slikama sprovela.
 */
@RestController
@RequestMapping("api/images/whatToDo")
@CrossOrigin(value = "*", exposedHeaders = "Location")
public class WhatToDoImageController {
    private final ImageService service;


    public WhatToDoImageController(ImageService service) {
        this.service = service;
    }

    /**
     * Metoda generise GET URL kako bi se slika prezentovala na klijentskoj strani
     * @param key Vrednost kljuca pod kojim se slika cuva u skladistu file=ova.
     * @return Vraca GET URL kao string vrednost.
     */
    @GetMapping("url/{key}")
    public ResponseEntity<String> getGetUrl(@PathVariable(value = "key") String key) throws IOException {
        var response = service.generateGetUrl(key);
        return ResponseEntity.ok(response);
    }

    /**
     * Metoda generise PUT URL, koji se dostavlja klijentu kako bi on mogao da obavi direktni upload u skladiste file-ova.
     * @return Vraca PUT URL kao String vrednost.
     */
    @PutMapping("url")
    public ResponseEntity<GetPutUrlResponse> getPutUrl() throws IOException {
        var response = service.generatePutUrl();
        return ResponseEntity.ok(response);
    }
}

