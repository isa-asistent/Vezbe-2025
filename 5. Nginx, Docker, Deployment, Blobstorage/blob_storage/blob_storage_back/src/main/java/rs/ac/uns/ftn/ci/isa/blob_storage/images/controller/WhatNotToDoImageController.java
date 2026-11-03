package rs.ac.uns.ftn.ci.isa.blob_storage.images.controller;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import rs.ac.uns.ftn.ci.isa.blob_storage.images.service.ImageService;

import java.io.IOException;
import java.net.URI;

/**
 * Demonstrira klasican primer kako se slika moze upload-ovati ili download-ovati.
 *
 * Nacin koji je ovde demonstrarian efektivno celokupnu
 * sliku salje na server prilikom upload-a, i celokupnu sliku drzi u memoriji prilikom download-a.
 *
 * Ovakav pristup nije los za malu kolicinu korisnika, ili ako su slike relativno male. Prilikom povecanja broja korisnika
 * ili ako se dozvoljava upravljanje slikama vecih velicina, ovaj pristup je neefektivan
 *
 * Sa druge strane, kompletan autoritet prilikom upravljanja slika je delegiran serveru te klijent minimalno
 * ucestvuje u upload-u ili download-u slika.
 */
@RestController
@RequestMapping("api/images/whatNotToDo")
@CrossOrigin(value = "*", exposedHeaders = "Location")
public class WhatNotToDoImageController {
    private final ImageService service;

    public WhatNotToDoImageController(ImageService service) {
        this.service = service;
    }

    /*
   Kako NE skiadati fajl
    */

    /**
     * Metoda sprovodi download slike, gde se ona celokupno prebaciju u memoriju te biva poslata klijentu.
     * @param key Vrednost kljuca pod kojim se slika cuva u skladistu file-ova.
     * @return Sliku kao InputStream te se direktno salje klijentu.
     */
    @GetMapping("/{key}")
    public ResponseEntity<InputStreamResource> download(@PathVariable(value = "key") String key) {
        var response = service.download(key);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM) // Or parse s3Stream.response().contentType()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + key + "\"")
                .body(response);
    }
    /*
    Kako NE upload-ovati fajl
    */

    /**
     * Metoda sprovodi upload, gde se slika salje kao file serveru, u koviru parametara formdata.
     * @param multipartFile Celokupna slika koja se upload-uje.
     * @return 201 Response sa URI koji odgovara novogenerisanom kljucu pod kojim se slika cuva u skaldistu.
     * @throws IOException U slucaju upload ne uspe.
     */
    @PostMapping
    public ResponseEntity<Void> upload(
            @RequestParam("file")
            MultipartFile multipartFile) throws IOException {
        var key = service.upload(multipartFile);
        return ResponseEntity.created(URI.create(key)).build();
    }

}
