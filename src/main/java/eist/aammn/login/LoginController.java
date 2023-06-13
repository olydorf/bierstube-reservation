package eist.aammn.login;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
@RequestMapping("/start/")
public class LoginController {

    @PostMapping("/login")
    public String login(@RequestParam("username") String username, @RequestParam("password") String password) {
        // TODO: login logic here

        if (username.equals("admin") && password.equals("password")) {
            return "redirect:/dashboard"; // Redirect to the dashboard page after successful login
        } else {
            return "redirect:/login?error"; // Redirect to the login page with an error parameter
        }
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam("email") String email) {
        // TODO: password reset logic here

        return "redirect:/login?reset"; // Redirect to the login page with a reset parameter
    }
}

