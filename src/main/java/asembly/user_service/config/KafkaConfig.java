package asembly.user_service.config;

import asembly.dto.auth.AuthRequest;
import asembly.event.chat.ChatEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    public String bootstrapAddress;

    //CONSUMER CHAT
    public ConsumerFactory<String, ChatEvent> chatFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(ChatEvent.class));
    }
    @Bean
    ConcurrentKafkaListenerContainerFactory<String, ChatEvent> chatListener() {
        ConcurrentKafkaListenerContainerFactory<String, ChatEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(chatFactory());
        return factory;
    }

    //CONSUMER AUTH
    public ConsumerFactory<String, AuthRequest> authFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(AuthRequest.class));
    }
    @Bean
    ConcurrentKafkaListenerContainerFactory<String, AuthRequest> authListener() {
        ConcurrentKafkaListenerContainerFactory<String, AuthRequest> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(authFactory());
        return factory;
    }

    //TOPICS
    @Bean
    public NewTopic authResponses()
    {
        return TopicBuilder.name("auth-responses").build();
    }
    @Bean
    public NewTopic userEvents()
    {
        return TopicBuilder.name("user-events").build();
    }

    //PRODUCER USER
    @Bean
    public ProducerFactory<String, Object> producerFactory()
    {
        Map<String, Object> config = new HashMap<>();

        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate()
    {
        return new KafkaTemplate<>(producerFactory());
    }
}
