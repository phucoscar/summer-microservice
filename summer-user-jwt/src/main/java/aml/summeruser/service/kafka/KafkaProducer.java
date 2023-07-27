package aml.summeruser.service.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);
    private String topicName = "test";
    private String groupId = "group_id";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(Object o) {
        logger.info(String.format("Message sent -> %s", o.toString()));
        kafkaTemplate.send(topicName, o.toString());
    }
}
