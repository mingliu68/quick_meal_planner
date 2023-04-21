package com.mingcapstone.quickmealplanner.control;

import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mingcapstone.quickmealplanner.entity.Recipe;
import com.mingcapstone.quickmealplanner.entity.User;
import com.mingcapstone.quickmealplanner.service.RecipeService;
import com.mingcapstone.quickmealplanner.service.UserService;


@Controller
@RequestMapping("/user/recipes")
public class RecipeController {

    private RecipeService recipeService;
    private UserService userService;

    @Autowired
    public RecipeController(RecipeService recipeService, UserService userService) {
        this.recipeService = recipeService;
        this.userService = userService;
    }
    
    @GetMapping
    public String recipes(Model model, Principal principal) {
        User user = getLoggedInUser(principal);
        model.addAttribute("user", user);
        

        return "recipes";
    }

    @GetMapping("/recipe") 
    public String recipe(@RequestParam("recipeId") Long recipeId, Model model, Principal principal) {
        User user = getLoggedInUser(principal);
        Recipe recipe = recipeService.findById(recipeId);
        model.addAttribute("user", user);
        model.addAttribute("recipe", recipe);

        return "recipe";
    }



    @GetMapping("/removeRecipeFromList")
    public String removeRecipeFromList(@RequestParam("recipeId") Long recipeId, Principal principal) {
        
        User user = getLoggedInUser(principal);
        userService.removeRecipeFromList(recipeId, user);
        return "redirect:/user/recipes";
    }

    private User getLoggedInUser(Principal principal) {
        return userService.findUserByEmail(principal.getName());
    } 
}
