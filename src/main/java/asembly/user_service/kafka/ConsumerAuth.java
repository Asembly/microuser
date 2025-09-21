package asembly.user_service.kafka;

import asembly.dto.auth.AuthRequest;
import asembly.dto.auth.AuthResult;
import asembly.dto.auth.AuthStatus;
import asembly.dto.auth.ValidResponse;
import asembly.dto.user.UserCreateRequest;
import asembly.user_service.repository.UserRepository;
import asembly.user_service.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ConsumerAuth {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    /**
    * Обработчик для авторизации пользователя
    *В случае успеха сообщается событие о валидных данных пользователя
    *Сообщение доходит до слушателя SignIn(Consumer)*/
    @KafkaListener(topics = "signin-requests", containerFactory = "authListener", groupId = "auth")
    public void handlerSignIn(AuthRequest data){
        try {
            var optUser = userRepository.findByUsername(data.username());

            AuthStatus status;

            if (optUser.isEmpty()) {
                status = AuthStatus.USER_NOT_FOUND;
            } else if(!passwordEncoder.matches(data.password(), optUser.get().getPassword())) {
                status = AuthStatus.INVALID_CREDENTIALS;
            } else {
                status = AuthStatus.VALID;
            }

            var user = optUser.get();

            kafkaTemplate.send("auth-responses", new ValidResponse(
                    data.correlationId(),
                    new AuthResult(
                            status,
                            user.getId(),
                            user.getUsername()
                    )
            ));
        }catch (Exception e)
        {
            throw new IllegalStateException("Validation failed");
        }
    }

    /**
     * Обработчик для регистрации пользователя
     * В случае успеха сообщается событие о валидных данных
     * пользователя(т.е то что пользователь с таким именем пользователя не существует в системе)
     * Сообщение доходит до слушателя SignUp(Consumer)
    */
    @KafkaListener(topics = "signup-requests", containerFactory = "authListener", groupId = "auth")
    public void handlerSignUp(@Payload AuthRequest data){
        try {
            var optUser = userRepository.findByUsername(data.username());
            AuthStatus status;

            if (optUser.isPresent()) {
                status = AuthStatus.USER_ALREADY_EXIST;
            } else {
                status = AuthStatus.VALID;
            }

            String encoded_pass = passwordEncoder.encode(data.password());

            var user = userService.create(new UserCreateRequest(data.username(), encoded_pass));
            log.info(user.getId());

            ValidResponse response = new ValidResponse(data.correlationId(), new AuthResult(
                    status,
                    user.getId(),
                    user.getUsername())
            );

            kafkaTemplate.send("auth-responses", response);
        }catch (Exception e)
        {
            throw new IllegalStateException("Validation failed");
        }
    }
}