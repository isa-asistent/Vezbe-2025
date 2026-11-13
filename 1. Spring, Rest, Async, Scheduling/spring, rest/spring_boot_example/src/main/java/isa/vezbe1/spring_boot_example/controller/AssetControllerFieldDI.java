package isa.vezbe1.spring_boot_example.controller;

import isa.vezbe1.spring_boot_example.model.Asset;
import isa.vezbe1.spring_boot_example.service.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;

import java.util.List;

/*
 * Klasa je anotirana sa @Controller sto treba da naznaci Springu da je klasa
 * Spring Bean i da treba da bude u nadleznosti Spring kontejnera.
 * Isti efekat bi se dobio koriscenjem anotacije @Component koja je
 * nadanotacija za @Controller.
 */
@Controller
public class AssetControllerFieldDI {

	//field-based dependency injection
	@Autowired
	@Qualifier("firstService")
	private AssetService assetService;

	public List<Asset> getAssets(){
		return assetService.listAssets();
	}
}
