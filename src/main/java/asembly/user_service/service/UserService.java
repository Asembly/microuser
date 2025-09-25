package asembly.user_service.service;

import asembly.dto.user.UserChatsRequest;
import asembly.dto.user.UserUpdateRequest;
import asembly.event.user.UserEventType;
import asembly.user_service.entity.User;
import asembly.user_service.kafka.ProducerUser;
import asembly.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ProducerUser producerService;

    public ResponseEntity<?> leaveChat(String user_id, UserChatsRequest dto)
    {
        var user = userRepository.findById(user_id).orElseThrow();

        if(dto.chats_id().isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Chats is empty");

        var isRemoved = user.getChats_id().removeAll(dto.chats_id());

        if(!isRemoved)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Chats not found in user");

        producerService.sendEvent(
                UserEventType.USER_LEAVE_CHAT,
                user.getId(),
                dto.chats_id()
        );

        return ResponseEntity.ok(userRepository.save(user));
    }

    public ResponseEntity<?> addChat(String user_id, UserChatsRequest dto)
    {
        var user = userRepository.findById(user_id).orElseThrow();

        if(dto.chats_id().isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Chats is empty");

        if(!new HashSet<>(user.getChats_id()).containsAll(dto.chats_id()))
        {
            user.getChats_id().addAll(dto.chats_id());
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Chats already exits in user");
        }

        producerService.sendEvent(
                UserEventType.USER_ADD_CHAT,
                user.getId(),
                user.getChats_id()
        );

        return ResponseEntity.ok(userRepository.save(user));
    }

    public ResponseEntity<User> update(String id, UserUpdateRequest dto)
    {
        var user = userRepository.findById(id).orElseThrow();

        if(!dto.username().isEmpty())
        {
            if(userRepository.findByUsername(dto.username()).isPresent())
                return ResponseEntity.badRequest().build();
            else
                user.setUsername(dto.username());
        }

        if(!dto.password().isEmpty())
            user.setPassword(dto.password());

        userRepository.save(user);

        return ResponseEntity.ok(user);
    }
}
