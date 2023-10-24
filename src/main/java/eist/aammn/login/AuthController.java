package eist.aammn.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import eist.aammn.security.JwtUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

@Controller
@ResponseBody
@RequestMapping("/start/")
public class AuthController {
    AuthenticationManager authenticationManager;
    JwtUtils jwtUtils;
    private final AuthService loginService;

    Logger logger = LoggerFactory.getLogger("");

    public AuthController(AuthService _loginService, JwtUtils _jwtUtils) {
        this.loginService = _loginService;
        this.jwtUtils = _jwtUtils;

    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestParam("username") String username, @RequestParam("password") String password) {
        try {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwtToken = jwtUtils.generateJwtToken(username, password);

        return ResponseEntity.ok(jwtToken);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
        }
    }

    @GetMapping("/login")
    public ResponseEntity<String> showLoginPage(@RequestParam(value = "reset", required = false) String reset, Model model) {
        if (reset != null && reset.equals("true")) {
            model.addAttribute("resetMessage", "If you have an account, new password is sent to your email address.");
        }

        return ResponseEntity.ok("login");
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }

        request.getSession().invalidate();
        SecurityContextHolder.clearContext();

        return ResponseEntity.ok("Logout successful");
    }

    @PostMapping("/reset-password")
    public CompletableFuture<ResponseEntity<String>> resetPassword(@RequestParam("email") String email) {
        return isValidEmailAsync(email).thenCompose(isValidEmail -> {
            if (!isValidEmail) {
                return CompletableFuture.completedFuture(ResponseEntity.badRequest().body("Invalid email address"));
            }

            return CompletableFuture.supplyAsync(() -> loginService.getUserByEmail(email))
                    .thenCompose(userOptional -> {
                        if (userOptional.isPresent()) {
                            return loginService.resetPasswordAsync(email)
                                    .thenApply(passwordReset -> {
                                        if (passwordReset) {
                                            return ResponseEntity.ok("Password reset successful");
                                        } else {
                                            return ResponseEntity.badRequest().body("Failed to reset password");
                                        }
                                    });
                        } else {
                            return CompletableFuture.completedFuture(ResponseEntity.notFound().build());
                        }
                    });
        });
    }


    @PostMapping("/add-user")
    public CompletableFuture<ResponseEntity<String>> addUser(@RequestParam("email") String email,
                                                             @RequestParam("username") String username,
                                                             @RequestParam("password") String password,
                                                             RedirectAttributes redirectAttributes) {

        var isValidEmailFuture = isValidEmailAsync(email);

        return isValidEmailFuture.thenCompose(isValidEmail -> {
            if (!isValidEmail) {
                loginService.logger.error("Invalid email address. Cannot add the user.");
                redirectAttributes.addAttribute("error", "Invalid email address");
                return CompletableFuture.completedFuture(ResponseEntity.badRequest().body("Invalid email address"));
            }

            if (!isValidUsername(username)) {
                redirectAttributes.addAttribute("error", "Username can only contain letters and digits");
                return CompletableFuture.completedFuture(ResponseEntity.badRequest().body("Invalid username"));
            }

            if (!isValidPassword(password)) {
                redirectAttributes.addAttribute("error", "Invalid characters in the password");
                return CompletableFuture.completedFuture(ResponseEntity.badRequest().body("Invalid password"));
            }

            loginService.addUser(email, username, password);
            String token = jwtUtils.generateJwtToken(email, password);
            return CompletableFuture.completedFuture(ResponseEntity.ok(token));
        });
    }

    @PostMapping("/delete-user")
    public ResponseEntity<?> deleteUser(@RequestParam("username") String username,
                                             @RequestParam("email") String email, Model model) {

        if (!isValidUsername(username)) {
            return ResponseEntity.badRequest().body("Invalid username");
        }

        var userExists = loginService.existsByUsername(username);

        if (!userExists) {
            return ResponseEntity.notFound().build();
        }

        model.addAttribute("username", username);
        model.addAttribute("email", email);

        return ResponseEntity.ok("Confirm delete user");
    }

    @PostMapping("/confirm-delete-user")
    public ResponseEntity<String> confirmDeleteUser(@RequestParam("username") String username, @RequestParam("email") String email,
            Model model) {
        loginService.deleteUserByUsernameOrEmail(username, email);

        return ResponseEntity.ok("redirect:/login?deleted");
    }

    private boolean isValidUsername(String username) {
        return Pattern.matches("[a-zA-Z0-9]+", username);
    }

    private boolean isValidPassword(String password) {
        return Pattern.matches("[a-zA-Z0-9@#$%^&+=]+", password);
    }

    /**
     * We don't want any unvalid emails in our system. See https://www.disify.com/ . 
     * @param email
     * @return
     */
    private CompletableFuture<Boolean> isValidEmailAsync(String email) {
        var apiUrl = "https://www.disify.com/api/email/" + email;

        var webClient = WebClient.builder().build();
        var responseMono = webClient.get()
                .uri(apiUrl)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class);

        return responseMono.map(response -> {
            try {
                var objectMapper = new ObjectMapper();
                var jsonNode = objectMapper.readTree(response);
                var formatValid = jsonNode.get("format").asBoolean();
                var dnsValid = jsonNode.get("dns").asBoolean();

                loginService.logger.info("Email adress is checked. Format: "+formatValid + " DNS: "+dnsValid);
                return formatValid && dnsValid;
            } catch (Exception e) {
                loginService.logger.error("Error while parsing or network: "+e);
                e.printStackTrace();
                return false;
            }
        }).onErrorReturn(false).toFuture();
    }
}
