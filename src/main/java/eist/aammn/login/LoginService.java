package eist.aammn.login;

import java.util.Optional;
import java.security.SecureRandom;

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

    @Autowired
    public LoginService(UserRRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
       // this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public boolean login(String username, String password) {
        Optional<UserR> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
        //&& passwordEncoder.matches(password, user.getPassword())) {
            // Login successful TODO: redirect to admin panel
            UserR user = userOptional.get();
            return true;
        }
        // Login failed TODO: redirect to login with error message
        return false;
    }

    public void resetPassword(String email) {
        Optional<UserR> userOptional = userRepository.findUserByEmail(email);
        if (userOptional.isPresent()) {
            UserR user = userOptional.get();

            String newPassword = generateNewPassword();
            // user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);

            emailService.sendPasswordResetEmail(user.getEmail(), newPassword);
        }
    }

    public void addUser(String email, String username, String password) {

        if (userRepository.existsByUsernameOrEmail(username, email)) {
            throw new IllegalArgumentException("User already exists");
        }

        UserR newUser = new UserR();
        newUser.setEmail(email);
        newUser.setName(username);
        //newUser.setPassword(passwordEncoder.encode(password));

        userRepository.save(newUser);
    }

    public boolean existsByUsernameOrEmail(String username, String email) {
        return userRepository.existsByUsernameOrEmail(username, email);
    }

    public void deleteUserByUsernameOrEmail(String username, String email) {
        UserR user = userRepository.findByUsernameOrEmail(username, email)
                .orElseThrow(() -> new IllegalArgumentException("Eror: User could not be deleted!"));

        userRepository.delete(user);
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

        int length = 16;

        SecureRandom secureRandom = new SecureRandom();
        StringBuilder passwordBuilder = new StringBuilder(length);

        // Generate at least one character from each character set.
        passwordBuilder.append(lowercase[secureRandom.nextInt(lowercase.length)]);
        passwordBuilder.append(uppercase[secureRandom.nextInt(uppercase.length)]);
        passwordBuilder.append(numbers[secureRandom.nextInt(numbers.length)]);

        // Generate the remaining characters.
        for (int i = 3; i < length; i++) {
            int randomSet = secureRandom.nextInt(3);
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

