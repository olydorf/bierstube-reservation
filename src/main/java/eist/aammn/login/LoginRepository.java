package eist.aammn.login;

import eist.aammn.model.user.model.UserR;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginRepository extends JpaRepository<UserR, Long> {
    boolean existsByUsernameAndPassword(String username, String password);
}
