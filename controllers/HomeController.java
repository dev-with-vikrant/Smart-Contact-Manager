package com.smart.smartcontactmanager.controllers;

import com.smart.smartcontactmanager.entities.User;
import com.smart.smartcontactmanager.helper.Message;
import com.smart.smartcontactmanager.repositories.UserRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
public class HomeController {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private ChatClient chatClient;

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        model.addAttribute("title", "Home – Smart Contact Manager");
        return "home";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("title", "About");
        return "about";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("title", "Login");
        return "login";
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("title", "Sign Up");
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/do_register")
    public String doRegister(
            @Valid @ModelAttribute("user") User user,
            BindingResult bindingResult,
            @RequestParam(value = "agreement", defaultValue = "false") boolean agreement,
            Model model,
            HttpSession session) {

        // Step 1: Field validation errors (@NotBlank, @Email, @Size)
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            return "signup";
        }

        // Step 2: Terms checkbox must be checked
        if (!agreement) {
            session.setAttribute("message",
                    new Message("Please accept the Terms and Conditions.", "alert-danger"));
            model.addAttribute("user", user);
            return "signup";
        }

        // Step 3: Check for duplicate email
        if (userRepository.existsByEmail(user.getEmail())) {
            session.setAttribute("message",
                    new Message("This email is already registered. Please login.", "alert-warning"));
            model.addAttribute("user", new User());
            return "signup";
        }

        // Step 4: Hash password, set defaults, save to MySQL
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_USER");
        user.setEnabled(true);
        user.setImageUrl("default.png");
        userRepository.save(user);

        // Step 5: Success — redirect to login with flash
        session.setAttribute("message",
                new Message("Account created successfully! You can now login.", "alert-success"));
        return "redirect:/login";
    }

    // Spring AI endpoint — called by fetch() from signup/login pages
    @PostMapping("/ai/chat")
    @ResponseBody
    public String aiChat(@RequestParam("message") String userMessage) {
        try {
            SystemMessage system = new SystemMessage("""
                You are a helpful assistant for Smart Contact Manager, a Spring Boot web app
                that lets users manage their personal contacts securely.
                Help users with registration, login, and using the dashboard.
                Keep responses concise (2-3 sentences). Be friendly.
                """);
            Prompt prompt = new Prompt(List.of(system, new UserMessage(userMessage)));
            return chatClient.call(prompt).getResult().getOutput().getContent();
        } catch (Exception e) {
            return "AI is unavailable. Ensure your OpenAI API key is set in application.properties.";
        }
    }
}