package com.smart.smartcontactmanager.controllers;

import com.smart.smartcontactmanager.entities.contact;
import com.smart.smartcontactmanager.entities.user;
import com.smart.smartcontactmanager.repositories.ContactRepository;
import com.smart.smartcontactmanager.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ContactRepository contactRepository;

    // Method to add common data to model
    @ModelAttribute
    public void addCommonData(Model model, Principal principal) {
        String userName = principal.getName();
        user user = userRepository.findByEmail(userName).get();
        model.addAttribute("user", user);
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("title", "User Dashboard");
        return "user/dashboard";
    }

    @GetMapping("/add-contact")
    public String openAddContactForm(Model model) {
        model.addAttribute("title", "Add Contact");
        model.addAttribute("contact", new contact());
        return "user/add_contact_form";
    }

    @PostMapping("/process-contact")
    public String processContact(@ModelAttribute contact contact, Principal principal) {
        String name = principal.getName();
        user user = this.userRepository.findByEmail(name).get();

        this.contactRepository.save(contact);
        return "redirect:/user/show-contacts";
    }

    @GetMapping("/show-contacts")
    public String showContacts(Model model, Principal principal) {
        String userName = principal.getName();
        user user = this.userRepository.findByEmail(userName).get();
        List<contact> contacts = this.contactRepository.findByUser_Id(user.getId());
        model.addAttribute("contacts", contacts);
        model.addAttribute("title", "View Contacts");
        return "user/show_contacts";
    }

    // Handlers for update and delete...
}