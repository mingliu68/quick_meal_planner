package com.mingcapstone.quickmealplanner.service;

import java.util.List;

import com.mingcapstone.quickmealplanner.dto.RecipeDto;
import com.mingcapstone.quickmealplanner.entity.Recipe;
import com.mingcapstone.quickmealplanner.entity.User;

public interface RecipeService {

    Recipe findById(Long id);

    Recipe save(RecipeDto RecipeDto, User user);

    void deleteById(Long id);

    List<RecipeDto> findAllRecipes();

    List<RecipeDto> getAllSavedRecipesByUser(User user);

    Recipe updateRecipe(RecipeDto recipeDto);

    List<Recipe> getRecentRecipes();
    
}
