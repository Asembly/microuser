package asembly.user_service.kafka;

import asembly.event.chat.ChatEvent;
import asembly.user_service.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@KafkaListener(topics = "chat-events", containerFactory = "chatListener", groupId = "chat")
public class ConsumerChat {

    @Autowired
    private UserRepository userRepository;

    @KafkaHandler
    public void handler(ChatEvent data){
       var users = userRepository.findAllById(data.users_id());
       switch(data.type()){
           case CHAT_CREATED -> users.forEach(user -> {
               user.getChats_id().add(data.chat_id());
           });
           case CHAT_UPDATED -> {
               users.forEach(user -> {
                   user.getChats_id().add(data.chat_id());
               });
           }
           case CHAT_DELETED -> users.forEach(user -> {
               user.getChats_id().remove(data.chat_id());
           });
       }
       userRepository.saveAll(users);
    }
}
