package isa.vezbe1.springapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import isa.vezbe1.springapp.domain.Asset;
import isa.vezbe1.springapp.service.AssetService;

@Controller
public class AssetControllerConstructorDI {

	private AssetService assetService;

	//constructor-based dependency injection
	@Autowired
	public AssetControllerConstructorDI(AssetService assetService) {
		this.assetService = assetService;
	}


	public List<Asset> getAssets(){
		return assetService.listAssets();
	}

}
