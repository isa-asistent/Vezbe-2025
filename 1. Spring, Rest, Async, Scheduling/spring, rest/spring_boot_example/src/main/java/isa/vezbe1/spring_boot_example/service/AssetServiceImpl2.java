package isa.vezbe1.spring_boot_example.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import isa.vezbe1.spring_boot_example.model.Asset;

@Service
// @Scope("prototype")
@Qualifier("secondService")
public class AssetServiceImpl2 implements AssetService {

    @Override
    public List<Asset> listAssets() {
        return new ArrayList<>();
    }
}
