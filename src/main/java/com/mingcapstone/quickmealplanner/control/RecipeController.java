package com.mingcapstone.quickmealplanner.control;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mingcapstone.quickmealplanner.dto.RecipeDto;
import com.mingcapstone.quickmealplanner.dto.UserDto;
import com.mingcapstone.quickmealplanner.service.RecipeService;
import com.mingcapstone.quickmealplanner.service.UserService;

@Controller
@RequestMapping("/user/recipes")
public class RecipeController {

    private RecipeService recipeService;
    private UserService userService;

    private String[] mealTypes = {
        "MONDAY_LUNCH", "MONDAY_DINNER", 
        "TUESDAY_LUNCH", "TUESDAY_DINNER",
        "WEDNESDAY_LUNCH", "WEDNESDAY_DINNER",
        "THURSDAY_LUNCH", "THURSDAY_DINNER",
        "FRIDAY_LUNCH", "FRIDAY_DINNER",
        "SATURDAY_LUNCH", "SATURDAY_DINNER",
        "SUNDAY_LUNCH", "SUNDAY_DINNER"
    };

    private String[] weekdays = {
        "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"
    };

    @Autowired
    public RecipeController(RecipeService recipeService, UserService userService) {
        this.recipeService = recipeService;
        this.userService = userService;
    }

    @GetMapping
    public String recipes(Model model, Principal principal) {
        UserDto user = getLoggedInUser(principal);
        model.addAttribute("user", user);

        return "recipes";
    }

    @GetMapping("/recipe")
    public String recipe(@RequestParam("recipeId") Long recipeId, Model model, Principal principal) {
        UserDto user = getLoggedInUser(principal);

        RecipeDto recipe = recipeService.findDtoById(recipeId);

        Boolean savedByUser = userService.recipeSavedByUser(recipeId, user.getId());

        model.addAttribute("user", user);
        model.addAttribute("recipe", recipe);
        model.addAttribute("savedByUser", savedByUser);
        model.addAttribute("weekdays", weekdays);

        return "recipe";
    }

    @GetMapping("/otherRecipes")
    public String otherRecipe(Model model, Principal principal) {
        UserDto user = getLoggedInUser(principal);
        List<RecipeDto> recipes = recipeService.getRecentRecipesNotByUser(user.getId());
        model.addAttribute("recipes", recipes);
        model.addAttribute("user", user);

        return "other-recipes";
    }

    @GetMapping("/removeRecipeFromList")
    public String removeRecipeFromList(@RequestParam("recipeId") Long recipeId, Principal principal) {

        UserDto user = getLoggedInUser(principal);
        userService.removeRecipeFromList(recipeId, user.getId());
        return "redirect:/user/recipes";
    }

    @GetMapping("/addRecipeToList")
    public String addRecipeTolist(@RequestParam("recipeId") Long recipeId, Principal principal) {
        UserDto user = getLoggedInUser(principal);
        userService.addRecipeToList(recipeId, user.getId());
        return "redirect:/user/recipes";
    }

    private UserDto getLoggedInUser(Principal principal) {
        return userService.findUserByEmail(principal.getName());
    }
}
