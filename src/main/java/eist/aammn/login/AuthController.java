package eist.aammn.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import eist.aammn.model.user.model.UserDetailsImpl;
import eist.aammn.model.user.repository.UserRRepository;
import eist.aammn.security.JwtUtils;
import eist.aammn.security.payload.JwtResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Controller
@ResponseBody
@RequestMapping("/start/")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    private final AuthService loginService;

    public AuthController(AuthService _loginService) {
        this.loginService = _loginService;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestParam("username") String username, @RequestParam("password") String password) {

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(username, password));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity
                .ok(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles));
    }

    @PostMapping("/login")
    public String login(@RequestParam("username") String username, @RequestParam("password") String password, RedirectAttributes redirectAttributes) {
        if (!isValidUsername(username)) {
            redirectAttributes.addAttribute("error", "Username can only contains letters and digits");
            return "redirect:/login";
        }

        if (!isValidPassword(password)) {
            redirectAttributes.addAttribute("error", "Invalid characters in the password");
            return "redirect:/login";
        }

        // TODO: login logic here

        //UserDetails userDetails = new UserDetails(username, password);
        //Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        //SecurityContextHolder.getContext().setAuthentication(authentication);

        try{            
            if(loginService.login(username, password).get())
                return "redirect:/dashboard";
        } catch (Exception e){
            loginService.logger.info("Login failed.");
            return "Error: " + e.getMessage(); // TODO: get the reason why login failed
        }

        if (username.equals("admin") && password.equals("password")) {
            return "redirect:/dashboard"; // Successful login for admin.
        } else {
            redirectAttributes.addAttribute("error", "Invalid credentials");
            return "redirect:/login";
        }
    }

    @GetMapping("/login")
    public String showLoginPage(@RequestParam(value = "reset", required = false) String reset, Model model) {
        if (reset != null && reset.equals("true")) {
            model.addAttribute("resetMessage", "Password reset successful. Please log in with your new password.");
        }

        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        return "redirect:/login?logout"; // Redirect to the login page with a logout parameter
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam("email") String email) {

        loginService.resetPassword(email);

        return "redirect:/login?reset";
    }

    @PostMapping("/add-user")
    public CompletableFuture<String> addUser(@RequestParam("email") String email,
            @RequestParam("username") String username,
            @RequestParam("password") String password, 
            RedirectAttributes redirectAttributes) {

        var isValidEmailFuture = isValidEmailAsync(email);

        return isValidEmailFuture.thenCompose(isValidEmail -> {
            if (!isValidEmail) {
                loginService.logger.error("Invalid email adress. Cannot add the user.");
                redirectAttributes.addAttribute("error", "Invalid email adress");
                return CompletableFuture.completedFuture("redirect:/dashboard?error");
            }

            if (!isValidUsername(username)) {
            redirectAttributes.addAttribute("error", "Username can only contains letters and digits");
            return CompletableFuture.completedFuture("Invalid username");
            }

            if (!isValidPassword(password)) {
                redirectAttributes.addAttribute("error", "Invalid characters in the password");
                return CompletableFuture.completedFuture("Invalid password");
            }

            loginService.addUser(email, username, password);
            return CompletableFuture.completedFuture("redirect:/dashboard");
        });
    }

    @PostMapping("/delete-user")
    public String deleteUser(@RequestParam("username") String username, @RequestParam("email") String email,
            Model model) {

        if (!isValidUsername(username)) {
            return "redirect:/login";
        }

        var userExists = loginService.existsByUsername(username);

        if (!userExists) {
            model.addAttribute("error", "User not found");
            return "delete-user";
        }

        model.addAttribute("username", username);
        model.addAttribute("email", email);

        return "confirm-delete-user";
    }

    @PostMapping("/confirm-delete-user")
    public String confirmDeleteUser(@RequestParam("username") String username, @RequestParam("email") String email,
            Model model) {
        loginService.deleteUserByUsernameOrEmail(username, email);

        return "redirect:/login?deleted";
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
