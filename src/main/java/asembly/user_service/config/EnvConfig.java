package asembly.user_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EnvConfig {
    @Value("${spring.jwt.secret.auth_service}")
    public String auth_secret;
}
