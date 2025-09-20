package asembly.user_service.service;

import asembly.dto.user.UserCreateRequest;
import asembly.dto.user.UserUpdateRequest;
import asembly.event.user.UserEventType;
import asembly.user_service.entity.User;
import asembly.user_service.kafka.ProducerUser;
import asembly.user_service.repository.UserRepository;
import asembly.util.GeneratorId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ProducerUser producerService;

    //USER CREATE IN SYSTEM
    public void create(UserCreateRequest dto)
    {
        userRepository.findByUsername(dto.username()).orElseThrow();

        var newUser = new User(
                GeneratorId.generateShortUuid(),
                dto.username(),
                dto.password(),
                List.of(),
                LocalDate.now());

        producerService.sendEvent(
                UserEventType.USER_CREATED,
                newUser.getId(),
                newUser.getUsername(),
                newUser.getChats_id()
        );

        userRepository.save(newUser);
    }

    //USER UPDATE IN SYSTEM
    public ResponseEntity<User> update(String id, UserUpdateRequest dto)
    {
        var user = userRepository.findById(id).orElseThrow();
        var chats_id = user.getChats_id();


        if(!dto.chats_id().isEmpty())
        {
            if(new HashSet<>(chats_id).containsAll(dto.chats_id()))
                return ResponseEntity.badRequest().build();
        }

        if(!dto.username().isEmpty())
        {
            if(userRepository.findByUsername(dto.username()).isPresent())
                return ResponseEntity.badRequest().build();
            else
                user.setUsername(dto.username());
        }

        if(!dto.password().isEmpty())
            user.setPassword(dto.password());

        producerService.sendEvent(
                UserEventType.USER_UPDATED,
                user.getId(),
                user.getUsername(),
                user.getChats_id()
        );

        userRepository.save(user);

        return ResponseEntity.ok(user);
    }

    //USER DELETE IN SYSTEM
    public ResponseEntity<User> delete(String id)
    {
        var user = userRepository.findById(id).orElseThrow();

        producerService.sendEvent(
                UserEventType.USER_DELETED,
                user.getId(),
                user.getUsername(),
                user.getChats_id()
        );

        userRepository.delete(user);
        return ResponseEntity.ok(user);
    }


}
