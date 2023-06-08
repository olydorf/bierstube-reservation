package eist.aammn.model.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRRepository extends JpaRepository<UserR,Integer> {
    Optional<UserR> findUserByEmail(String email);

}
