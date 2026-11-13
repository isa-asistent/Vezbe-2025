package isa.vezbe1.spring_boot_example.controller;

import isa.vezbe1.spring_boot_example.model.Asset;
import isa.vezbe1.spring_boot_example.service.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class AssetControllerConstructorDI {
    private AssetService assetService;

    //constructor-based dependency injection
    @Autowired
    public AssetControllerConstructorDI(@Qualifier("firstService")
                                        AssetService assetService) {
        this.assetService = assetService;
    }


    public List<Asset> getAssets() {
        return assetService.listAssets();
    }

}
