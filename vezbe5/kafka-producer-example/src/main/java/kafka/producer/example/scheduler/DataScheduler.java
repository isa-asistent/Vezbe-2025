package kafka.producer.example.scheduler;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class DataScheduler {

    private final ArrayList<String> stockMarket = new ArrayList<>();

    @Value("${topic-name}")
    private String TOPIC;

    @Autowired
    private KafkaTemplate<String, String> template;

    @PostConstruct
    public void init() {
        stockMarket.add("Tesla Inc.");
        stockMarket.add("Nvidia");
        stockMarket.add("Meta");
        stockMarket.add("Apple");
        stockMarket.add("Saudi Aramco");
        stockMarket.add("TSMC");
    }


    /*
        metoda se pokrece automatski i simulira generisanje vrednosti za berzu
        sve vrednosti se salju na topic definisan u application.properties
     */
    @Scheduled(cron = "*/5 * * * * *")
    public void generateStockData() {
        // generisemo random vrednosti za berzu
        String randomCompany = stockMarket.get(new Random().nextInt(stockMarket.size()));
        double randomStock = ThreadLocalRandom.current().nextDouble(100.0, 500.0);
        randomStock = Math.round(randomStock * 100.0) / 100.0;

        /*
            saljemo te vrednosti na predefinisani TOPIC u formatu: topic, key, value
            na osnovu hes vrednosti kljuca Kafka definise u koju particiju smesta podatke
            hash(key) % 2 == 0 ? 0 : 1 u slucaju 2 particije
         */
        template.send(TOPIC, randomCompany, randomCompany + ":" + randomStock);
    }
}
