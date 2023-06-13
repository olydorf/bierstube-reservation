package eist.aammn.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
    public String login(@RequestParam("username") String username, @RequestParam("password") String password) {
        // TODO: login logic here

        if (username.equals("admin") && password.equals("password")) {
            return "redirect:/dashboard"; // Redirect to the dashboard page after successful login
        } else {
            return "redirect:/login?error"; // Redirect to the login page with an error parameter
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
        boolean userExists = loginService.existsByUsernameOrEmail(username, email);

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
}
