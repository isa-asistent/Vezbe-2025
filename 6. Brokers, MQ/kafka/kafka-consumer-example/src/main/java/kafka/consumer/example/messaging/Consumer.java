package kafka.consumer.example.messaging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class Consumer {

    private final Logger log = LogManager.getLogger(Consumer.class);

    /*
     * @KafkaListener anotacijom se definiše naziv topic-a iz koje anotirana metoda
     * osluškuje poruke.
     */
    @KafkaListener(topics = "${topic-name}", groupId = "${group-id}")
    public void listen(String message) {
        log.info(message);
    }

}
