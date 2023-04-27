package com.mingcapstone.quickmealplanner.control;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.mingcapstone.quickmealplanner.dto.OptionsDto;
import com.mingcapstone.quickmealplanner.dto.UserDto;
import com.mingcapstone.quickmealplanner.entity.User;
import com.mingcapstone.quickmealplanner.service.UserService;

import jakarta.validation.Valid;

@Controller
public class AuthController {

    private UserService userService;
    // private User currentUser;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/index")
    public String home(Model model, Principal principal) {
        // Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // if(principal == null) {
        // currentUser = null;
        // } else {
        // getPrincipal(principal);
        // }
        // getPrincipal(principal);
        // model.addAttribute("user", currentUser);

        return principal == null ? "index" : "redirect:/user";

    }

    @GetMapping("/login")
    public String login(Principal principal) {
        // if(principal != null) {
        // return "redirect:/user";
        // }
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        UserDto user = new UserDto();
        model.addAttribute("user", user);
        return "register";
    }

    @PostMapping("/register/save")
    public String registration(@Valid @ModelAttribute("user") UserDto userDto, BindingResult result, Model model) {

        User existingUser = userService.findUserByEmail(userDto.getEmail());
        if (existingUser != null && existingUser.getEmail() != null && !existingUser.getEmail().isEmpty()) {
            result.rejectValue("email", null, "There is already an account registered with that email address.");
        }
        if (result.hasErrors()) {
            model.addAttribute("user", userDto);
            return "/register";
        }
        userService.saveUser(userDto);
        // User dbUser = userService.saveUser(userDto);
        // System.out.println(dbUser.getRecipes());

        return "redirect:/register?success";
    }

    @PostMapping("/user")
    public String updateUser(@Valid @ModelAttribute("userDto") UserDto userDto, BindingResult result, Model model,
            Principal principal) {

        System.out.println("userDto id: " + userDto.getId());
        System.out.println("userDto first name: " + userDto.getFirstName());
        User currentUser = getPrincipal(principal);
        User updatedUser = userService.findUserByEmail(userDto.getEmail());

        // user trying to update email
        if (updatedUser != null && updatedUser.getId() != currentUser.getId()) {
            System.out.println("REJECTED");
            result.rejectValue("email", null, "There is already an account registered with that email address.");
        }
        if (userDto.getFirstName().isEmpty()) {
            result.rejectValue("firstName", null, "First name cannot be empty");
        }
        if (userDto.getLastName().isEmpty()) {
            result.rejectValue("lastName", null, "Last name cannot be empty");
        }
        if (userDto.getEmail().isEmpty()) {
            result.rejectValue("email", null, "Email cannot be empty");
        }
        // if(result.hasErrors()) {

        // model.addAttribute("user", currentUser);
        // model.addAttribute("userDto", userDto);
        // return "user-profile";
        // }
        userDto.setPassword(currentUser.getPassword());
        userDto.setRecipes(currentUser.getRecipes());
        userService.updateUser(userDto);

        return "redirect:/user/profile";
    }

    @GetMapping("/users")
    public String users(Model model, Principal principal) {
        List<UserDto> users = userService.findAllUsers();
        model.addAttribute("users", users);
        // User currentUser = userService.findUserByEmail(principal.getName());
        User currentUser = getPrincipal(principal);
        model.addAttribute("user", currentUser);

        System.out.println("Principal id: " + currentUser.getId());
        System.out.println("Principal first role: " + currentUser.getRoles().get(0).getName());

        return "users";
    }

    @GetMapping("/user")
    public String currentUser(Model model, Principal principal) {
        User currentUser = getPrincipal(principal);
        OptionsDto optionsDto = new OptionsDto();
        model.addAttribute("user", currentUser);
        model.addAttribute("optionsDto", optionsDto);
        return "user";
    }

    @GetMapping("/user/profile")
    public String userProfileForm(Model model, Principal principal) {
        User currentUser = getPrincipal(principal);
        UserDto userDto = new UserDto();
        userDto.setFirstName(currentUser.getFirstName());
        userDto.setLastName(currentUser.getLastName());
        userDto.setEmail(currentUser.getEmail());
        userDto.setId(currentUser.getId());
        userDto.setRecipes(currentUser.getRecipes());

        model.addAttribute("user", currentUser);
        model.addAttribute("userDto", userDto);

        return "user-profile";

    }

    // @GetMapping("/recipes")
    // public String recipes() {
    // return "recipes";
    // }

    private User getPrincipal(Principal principal) {

        return userService.findUserByEmail(principal.getName());
    }

}
