package eist.aammn.login;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import eist.aammn.model.user.UserR;

@Repository
public interface LoginRepository extends JpaRepository<UserR, Long> {
    boolean existsByUsernameAndPassword(String username, String password);
}
