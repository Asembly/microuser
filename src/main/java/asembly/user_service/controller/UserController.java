package asembly.user_service.controller;

import asembly.dto.user.UserUpdateRequest;
import asembly.user_service.entity.User;
import asembly.user_service.service.BaseService;
import asembly.user_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private BaseService service;

    @GetMapping
    public ResponseEntity<List<User>> findAll()
    {
        return service.findAll();
    }

    @GetMapping("{id}")
    public ResponseEntity<User> findById(@PathVariable String id)
    {
        return service.findById(id);
    }

    @PatchMapping("{id}")
    public ResponseEntity<User> update(@PathVariable String id,@RequestBody UserUpdateRequest dto)
    {
       return userService.update(id, dto);
    }

    @DeleteMapping("/")
    public ResponseEntity<String> deleteAll()
    {
        return service.deleteAll();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<User> delete(@PathVariable String id)
    {
        return userService.delete(id);
    }

}
