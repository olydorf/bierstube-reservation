package eist.aammn.login;

import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@ResponseBody
@RequestMapping("/start/")
public class LoginController {

    @Autowired
    private final LoginService loginService;

    public LoginController(LoginService _loginService) {
        this.loginService = _loginService;
    }

    @PostMapping("/login")
    public String login(@RequestParam("username") String username, @RequestParam("password") String password, RedirectAttributes redirectAttributes) {
        if (!isValidUsername(username)) {
            redirectAttributes.addAttribute("error", "Invalid username");
            return "redirect:/login";
        }

        if (!isValidPassword(password)) {
            redirectAttributes.addAttribute("error", "Invalid password");
            return "redirect:/login";
        }

        // TODO: login logic here

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

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam("email") String email) {

        loginService.resetPassword(email);

        return "redirect:/login?reset";
    }

    @PostMapping("/add-user")
    public String addUser(@RequestParam("email") String email,
            @RequestParam("username") String username,
            @RequestParam("password") String password) {

        loginService.addUser(email, username, password);

        return "redirect:/dashboard";
    }

    @PostMapping("/delete-user")
    public String deleteUser(@RequestParam("username") String username, @RequestParam("email") String email,
            Model model) {
        var userExists = loginService.existsByUsernameOrEmail(username, email);

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

    private CompletableFuture<Boolean> isValidEmailAsync(String email) {
        var apiUrl = "https://www.disify.com/api/email/" + email;

        WebClient webClient = WebClient.builder().build();
        Mono<String> responseMono = webClient.get()
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
