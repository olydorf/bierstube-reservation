package eist.aammn.login;

import java.util.concurrent.CompletableFuture;
import java.security.SecureRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import eist.aammn.model.email.EmailService;
import eist.aammn.model.user.UserR;
import eist.aammn.model.user.UserRRepository;

@Service
public class LoginService {
    private final UserRRepository userRepository;
    private final EmailService emailService;
    //private final PasswordEncoder passwordEncoder;

    Logger logger = LoggerFactory.getLogger(LoginService.class);

    @Autowired
    public LoginService(UserRRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
       // this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public CompletableFuture<Boolean> login(String username, String password) {
        logger.info("Login for User "+ username + " started.");

        return CompletableFuture.supplyAsync(() -> {
            var userOptional = userRepository.findByUsername(username);
            if (userOptional.isPresent()) {
            //&& passwordEncoder.matches(password, user.getPassword())) {
                // Login successful TODO: redirect to admin panel
                var user = userOptional.get();
                return true;
            }
            logger.warn("Username is not found.");
            // Login failed TODO: redirect to login with error message
            return false;
        });
    }

    public CompletableFuture<Boolean> resetPassword(String email) {
        logger.info("Password reset for " + email + " started.");

        return CompletableFuture.supplyAsync(() -> {
            var userOptional = userRepository.findUserByEmail(email);
            if (userOptional.isPresent()) {
                var user = userOptional.get();

                var newPassword = generateNewPassword();
                logger.debug("New generated password is "+ newPassword);
                // user.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(user);

                emailService.sendPasswordResetEmail(user.getEmail(), newPassword);
                return true; // Password reset successful
            } else {
                return false; // User not found
            }
        });
    }

    public CompletableFuture<Void> addUser(String email, String username, String password) {
        if (userRepository.existsByUsernameOrEmail(username, email)) {
            throw new IllegalArgumentException("User already exists");
        }

        var newUser = new UserR();
        newUser.setEmail(email);
        newUser.setName(username);
        // newUser.setPassword(passwordEncoder.encode(password));

        return CompletableFuture.runAsync(() -> userRepository.save(newUser));
    }

    public Boolean existsByUsernameOrEmail(String username, String email) {
         return userRepository.existsByUsernameOrEmail(username, email);
    }

    public CompletableFuture<Void> deleteUserByUsernameOrEmail(String username, String email) {
        var user = userRepository.findByUsernameOrEmail(username, email)
                .orElseThrow(() -> new IllegalArgumentException("Eror: User could not be deleted!"));

        return CompletableFuture.runAsync(() -> userRepository.delete(user));
    }

    /**
     * Inspired from https://stackoverflow.com/questions/19743124/java-password-generator .
     * Yuliana's cyber security background at work.
     * @return
     */
    private String generateNewPassword() {
        final char[] lowercase = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        final char[] uppercase = "ABCDEFGJKLMNPRSTUVWXYZ".toCharArray();
        final char[] numbers = "0123456789".toCharArray();

        var length = 16;

        var secureRandom = new SecureRandom();
        var passwordBuilder = new StringBuilder(length);

        // Generate at least one character from each character set.
        passwordBuilder.append(lowercase[secureRandom.nextInt(lowercase.length)]);
        passwordBuilder.append(uppercase[secureRandom.nextInt(uppercase.length)]);
        passwordBuilder.append(numbers[secureRandom.nextInt(numbers.length)]);

        // Generate the remaining characters.
        for (int i = 3; i < length; i++) {
            var randomSet = secureRandom.nextInt(3);
            char randomChar;
            switch (randomSet) {
                case 0:
                    randomChar = lowercase[secureRandom.nextInt(lowercase.length)];
                    break;
                case 1:
                    randomChar = uppercase[secureRandom.nextInt(uppercase.length)];
                    break;
                default:
                    randomChar = numbers[secureRandom.nextInt(numbers.length)];
                    break;
            }
            passwordBuilder.append(randomChar);
        }

        return passwordBuilder.toString();
    }
}

