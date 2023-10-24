package eist.aammn.model.user.repository;

import eist.aammn.model.user.model.UserR;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRRepository extends JpaRepository<UserR,Integer> {
    Optional<UserR> findUserByEmail(String email);

    Optional<UserR> findByUsername(String username);

    Optional<UserR> findByUsernameOrEmail(String username, String email);
    
    boolean existsByUsername(@Param("username") String username);

    boolean existsByUsernameOrEmail(@Param("username") Optional<String> username, @Param("email") String email);


    boolean existsByUsernameAndPassword(String username, String password);
}
