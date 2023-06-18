package eist.aammn.model.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRRepository extends JpaRepository<UserR,Integer> {
    Optional<UserR> findUserByEmail(String email);

    Optional<UserR> findByUsername(String username);

    Optional<UserR> findByUsernameOrEmail(String username, String email);
    
    boolean existsByUsername(@Param("username") String username);

    boolean existsByUsernameOrEmail(@Param("username") String username, @Param("email") String email);


}
