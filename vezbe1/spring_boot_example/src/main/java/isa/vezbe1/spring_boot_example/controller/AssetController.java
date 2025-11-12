package isa.vezbe1.spring_boot_example.controller;


import isa.vezbe1.spring_boot_example.model.Asset;
import isa.vezbe1.spring_boot_example.service.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class AssetController {

    // koja tacno implementacija ce biti injektovana?
    private AssetService assetService;

    /**
     * kada postoji samo jedan konstruktor sa parametrima, spring je dovoljno pametan da odradi autowiring
     * nije potrebno dodavati @Autowired anotaciju
     * @Qualifier - specificira naziv bean-a koji je potrebno injektovati
     * @param assetService
     */
    public AssetController(@Qualifier("firstService") AssetService assetService) {
        this.assetService = assetService;
    }


    //alternativno, setter injection
    /*@Autowired
    public void setAssetService(@Qualifier("secondService") AssetService assetService) {
        this.assetService = assetService;
    }*/

    public List<Asset> getAssets() {
        return assetService.listAssets();
    }
}
