package kafka.producer.example.config;

import jakarta.annotation.PostConstruct;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.KafkaFuture;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Properties;
import java.util.Set;

@Component
public class KafkaTopic {

    private final Properties properties;
    private final Logger log = LogManager.getLogger(KafkaTopic.class);

    public KafkaTopic(Properties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void init() {
        try {
            properties.put("bootstrap.servers", "localhost:9092");
            createTopic("test-topic", 2);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /*
     * Kreiramo topic sa bazičnim podešavanjima i proizvoljnim brojem particija gde ćemo slati poruke
     */
    public void createTopic(String topicName, int partitions) throws Exception {

        try (Admin admin = Admin.create(properties)) {
            Set<String> existingTopics = admin.listTopics().names().get();
            if (existingTopics.contains(topicName)) {
                log.info("Tema vec postoji");
                return;
            }

            short replicationFactor = 1;
            NewTopic newTopic = new NewTopic(topicName, partitions, replicationFactor);

            CreateTopicsResult result = admin.createTopics(Collections.singleton(newTopic));

            // cekamo da se zavrsi kreiranje teme
            KafkaFuture<Void> future = result.values().get(topicName);
            future.get();
            log.info("Kreirana nova tema: {}", topicName);
        }
    }
}
