package isa.vezbe1.springapp;

import java.util.List;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import isa.vezbe1.springapp.config.AppConfiguration;
import isa.vezbe1.springapp.controller.AssetController;
import isa.vezbe1.springapp.controller.AssetControllerConstructorDI;
import isa.vezbe1.springapp.controller.AssetControllerSetterDI;
import isa.vezbe1.springapp.domain.Asset;

public class App {
	public static void main( String[] args ) {
		
		/*
		 * AnnotationConfigApplicationContext omogucava dobavljanje konteksta preko kojeg se
		 * mogu dobiti beanovi iz Spring kontejnera.
		 */
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfiguration.class);
		
		AssetController assetController = context.getBean(AssetController.class);
		AssetControllerConstructorDI assetControllerConst = context.getBean(AssetControllerConstructorDI.class);
		AssetControllerSetterDI assetControllerSet = context.getBean(AssetControllerSetterDI.class);

		List<Asset> assets = assetController.getAssets();
		List<Asset> assetsConst = assetControllerConst.getAssets();
		List<Asset> assetsSet = assetControllerSet.getAssets();

		for(Asset asset : assets){
			System.out.println(asset.getName());
			System.out.println(asset.getDescription());
		}
		
		for(Asset asset : assetsConst){
			System.out.println(asset.getName());
			System.out.println(asset.getDescription());
		}
		
		for(Asset asset : assetsSet){
			System.out.println(asset.getName());
			System.out.println(asset.getDescription());
		}
		
		context.close();
	}
}
