package asembly.user_service.service;

import asembly.dto.user.UserCreateRequest;
import asembly.event.user.UserEventType;
import asembly.user_service.entity.User;
import asembly.user_service.kafka.ProducerUser;
import asembly.user_service.repository.UserRepository;
import asembly.util.GeneratorId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BaseService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProducerUser producerService;

    public ResponseEntity<List<User>> findAll()
    {
        var users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    public ResponseEntity<User> findById(String id)
    {
        var user = userRepository.findById(id).orElseThrow();

        return ResponseEntity.ok(user);
    }

    public ResponseEntity<String> deleteAll()
    {
        userRepository.deleteAll();
        return ResponseEntity.ok("Users deleted");
    }

    public ResponseEntity<User> delete(String id)
    {
        var user = userRepository.findById(id).orElseThrow();

        producerService.sendEvent(
                UserEventType.USER_DELETED,
                id,
                user.getChats_id()
        );

        userRepository.delete(user);
        return ResponseEntity.ok(user);
    }

    public User create(UserCreateRequest dto)
    {
        var newUser = new User(
                GeneratorId.generateShortUuid(),
                dto.username(),
                dto.password(),
                List.of(),
                LocalDate.now());

        return userRepository.save(newUser);
    }
}
