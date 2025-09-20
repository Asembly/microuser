package asembly.user_service.kafka;

import asembly.event.user.UserEvent;
import asembly.event.user.UserEventType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ProducerUser {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void sendEvent(UserEventType type, String user_id, String username, List<String> chats_id)
    {
        UserEvent event = new UserEvent(
                user_id,
                type,
                username,
                chats_id
        );

        kafkaTemplate.send("user-events",user_id,event);
    }
}
