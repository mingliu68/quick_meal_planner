package com.mingcapstone.quickmealplanner.control;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.theokanning.openai.model.Model;

@Controller
public class HomeController {
    
    @GetMapping("/")
    public String home(Model model, Principal principal) {
        
        return principal == null ? "index" : "redirect:/user";

    }
}
