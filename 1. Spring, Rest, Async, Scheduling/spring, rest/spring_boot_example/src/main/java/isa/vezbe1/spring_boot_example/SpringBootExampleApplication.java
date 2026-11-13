package isa.vezbe1.spring_boot_example;

import isa.vezbe1.spring_boot_example.config.AppConfiguration;
import isa.vezbe1.spring_boot_example.controller.AssetController;
import isa.vezbe1.spring_boot_example.controller.AssetControllerConstructorDI;
import isa.vezbe1.spring_boot_example.controller.AssetControllerFieldDI;
import isa.vezbe1.spring_boot_example.controller.AssetControllerSetterDI;
import isa.vezbe1.spring_boot_example.model.Asset;
import isa.vezbe1.spring_boot_example.service.AssetService;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Arrays;
import java.util.List;

/*
 * @SpringBootApplication anotacija obuhvata
 * 	1. @Configuration,
 * 	2. @EnableAutoConfiguration i
 * 	3. @ComponentScan anotacije
 * sa njihovim default-nim atributima.
 *
 * @EnableAutoConfiguration anotacija upravlja konfiguracijom aplikacije. Sadrzi 'auto-configuration feature' na osnovu kojeg
 * Spring Boot gledajuci classpath (pom.xml), anotacije i konfiguraciju dodaje potrebne tehnologije i kreira aplikaciju.
 *
 * Iako koristimo @SpringBootApplication anotaciju, mogu se eksplicitno navesti sve navedene anotacije da bismo promenili njihove default-ne vrednost
 * npr. @ComponentScan("rs.ac.uns.ftn.informatika.spring.boot")
 *
 */
@SpringBootApplication
public class SpringBootExampleApplication {

    public static void main(String[] args) {

        inspectContext();
        ConfigurableApplicationContext ctx = SpringApplication.run(SpringBootExampleApplication.class, args);

        // svi registrovani bean-ovi interfejsa AssetService
        ctx.getBeansOfType(AssetService.class).values().stream().forEach(System.out::println);

        AssetController assetController = ctx.getBean(AssetController.class);
        assetController.getAssets().stream().forEach(x -> System.out.println(x.getName()));
    }

    private static void inspectContext(){
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfiguration.class);

        AssetControllerFieldDI assetController = context.getBean(AssetControllerFieldDI.class);
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
