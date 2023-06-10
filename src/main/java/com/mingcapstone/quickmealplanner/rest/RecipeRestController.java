package com.mingcapstone.quickmealplanner.rest;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mingcapstone.quickmealplanner.dto.RecipeDto;
import com.mingcapstone.quickmealplanner.dto.UserDto;
import com.mingcapstone.quickmealplanner.entity.Recipe;
import com.mingcapstone.quickmealplanner.service.RecipeService;
import com.mingcapstone.quickmealplanner.service.UserService;

@RestController
@RequestMapping("/api")
public class RecipeRestController {
    
    private RecipeService recipeService;
    private UserService userService;

    @Autowired
    public RecipeRestController(RecipeService recipeService, UserService userService){
        this.recipeService = recipeService;
        this.userService = userService;
    }


    @PostMapping("/recipe")
    public Recipe saveRecipe(@RequestBody RecipeDto recipeDto, Principal principal) {
        // System.out.println("This recipe has " + recipeDto.getDirections().size() + " steps");
        // if(recipeDto.getDirections().size() == 1) {
        //     System.out.println("This recipe has 1 step, converted to multiple.");
        //    List<String> newDirections = Arrays.asList(recipeDto.getDirections().get(0).split("\\."));
        //     recipeDto.setDirections(newDirections);
        // }
        // System.out.println("This recipe has " + recipeDto.getDirections().size() + " steps");

        UserDto user = getLoggedInUser(principal);
        return recipeService.save(recipeDto, user.getId());
    } 

    private UserDto getLoggedInUser(Principal principal) {
        return userService.findUserByEmail(principal.getName());
    }   
}
