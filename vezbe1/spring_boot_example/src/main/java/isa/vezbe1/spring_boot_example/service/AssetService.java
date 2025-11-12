package isa.vezbe1.spring_boot_example.service;

import isa.vezbe1.spring_boot_example.model.Asset;

import java.util.List;

public interface AssetService {
    List<Asset> listAssets();
}