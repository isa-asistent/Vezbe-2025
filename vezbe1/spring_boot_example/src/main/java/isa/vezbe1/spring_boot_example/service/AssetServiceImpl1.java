package isa.vezbe1.spring_boot_example.service;

import isa.vezbe1.spring_boot_example.model.Asset;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;

@Service
@Qualifier("firstService")
public class AssetServiceImpl1 implements AssetService {

    @PostConstruct
    public void initMetoda() throws Exception {
        System.out.println("Poziv init metode posle inicijalizacije komponente");
    }

    @PreDestroy
    public void cleanUpMetoda() throws Exception {
        System.out.println("Spring kontejner se gasi i komponenta se unistava!");
    }

    @Override
    public List<Asset> listAssets() {
        ArrayList<Asset> assets = new ArrayList<Asset>(2);
        assets.add(new Asset("Asset1", "Asset1 description"));
        assets.add(new Asset("Asset2", "Asset2 description"));
        return assets;
    }
}