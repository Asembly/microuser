package asembly.user_service.service;

import asembly.user_service.entity.User;
import asembly.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BaseService {

    private final UserRepository userRepository;

    //GET ALL USERS
    public ResponseEntity<List<User>> findAll()
    {
        var users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    //GET USER BY ID
    public ResponseEntity<User> findById(String id)
    {
        var user = userRepository.findById(id).orElseThrow();

        return ResponseEntity.ok(user);
    }

    //DELETE ALL USERS
    public ResponseEntity<String> deleteAll()
    {
        userRepository.deleteAll();
        return ResponseEntity.ok("Users deleted");
    }
}
