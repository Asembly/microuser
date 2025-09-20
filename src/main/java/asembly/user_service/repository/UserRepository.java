package asembly.user_service.repository;

import asembly.user_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    @Query(value = "select * from users where username = :username", nativeQuery = true)
    public Optional<User> findByUsername(String username);
}
