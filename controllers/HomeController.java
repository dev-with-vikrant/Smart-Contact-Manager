package com.smart.smartcontactmanager.controllers;

import com.smart.smartcontactmanager.entities.user; // Note: Class names should usually be PascalCase (User)
import com.smart.smartcontactmanager.helper.Message;
import com.smart.smartcontactmanager.repositories.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("title", "Home - Smart Contact Manager");
        return "home";
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("title", "Register - Smart Contact Manager");
        model.addAttribute("user", new user());
        return "signup";
    }

    @PostMapping("/do_register")
    public String registerUser(@ModelAttribute("user") user user,
                               @RequestParam(value = "agreement", defaultValue = "false") boolean agreement,
                               Model model,
                               HttpSession session) {
        try {
            // 1. VALIDATION
            if (!agreement) {
                System.out.println("You have not agreed to the terms and conditions");
                throw new Exception("You must agree to the terms and conditions");
            }

            // 2. PREPARE USER DATA
            user.setRole("ROLE_USER");
            user.setEnabled(true);

            // Encode the password before saving
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            System.out.println("Agreement: " + agreement);
            System.out.println("User Data: " + user);

            // 3. SAVE TO DATABASE
            user result = this.userRepository.save(user);

            // 4. SUCCESS RESPONSE
            model.addAttribute("user", new user()); // Clear form
            session.setAttribute("message", new Message("Successfully Registered!!", "alert-success"));

            return "signup";

        } catch (Exception e) {
            // 5. ERROR RESPONSE
            e.printStackTrace();
            model.addAttribute("user", user); // Keep filled data
            session.setAttribute("message", new Message("Something Went wrong!! " + e.getMessage(), "alert-danger"));

            return "signup";
        }
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("title", "Login");
        return "login";
    }
}