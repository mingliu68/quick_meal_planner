package com.mingcapstone.quickmealplanner.service;

import java.util.List;

import com.mingcapstone.quickmealplanner.dto.RecipeDto;
import com.mingcapstone.quickmealplanner.entity.Recipe;

public interface RecipeService {

    RecipeDto findDtoById(Long id);

    Recipe findById(Long id);    

    Recipe save(RecipeDto RecipeDto, Long userId);

    void deleteById(Long id);


    List<RecipeDto> findAllRecipes();

    List<RecipeDto> getAllSavedRecipesByUser(Long userId);

    Recipe updateRecipe(RecipeDto recipeDto);

    List<Recipe> getRecentRecipes();

    List<RecipeDto> getRecentRecipesNotByUser(Long userId);
    
}
