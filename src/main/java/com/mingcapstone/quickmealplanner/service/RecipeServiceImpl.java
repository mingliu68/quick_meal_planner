package com.mingcapstone.quickmealplanner.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mingcapstone.quickmealplanner.dto.RecipeDto;
import com.mingcapstone.quickmealplanner.entity.Recipe;
import com.mingcapstone.quickmealplanner.entity.User;
import com.mingcapstone.quickmealplanner.repository.RecipeRepository;
import com.mingcapstone.quickmealplanner.repository.UserRepository;

@Service
public class RecipeServiceImpl implements RecipeService {
   
    
    private RecipeRepository recipeRepository;
    private UserRepository userRepository;
    private UserService userService;

    @Autowired
    public RecipeServiceImpl(RecipeRepository recipeRepository, UserRepository userRepository, UserService userService) {
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }
    
    

    @Override
    public Recipe findById(Long id){
        Optional<Recipe> result = recipeRepository.findById(id);
        Recipe recipe = null;
        if(result.isPresent()) {
            recipe = result.get();
        } else {
            throw new RuntimeException("Recipe not found");
        }

        return recipe;
    }

    // saving brand new user searched recipe 
    @Override
    public Recipe save(RecipeDto recipeDto, Long userId){
        User user = userService.findUserById(userId);
        Recipe recipe = new Recipe();
        recipe.setName(recipeDto.getName());
        recipe.setDirections(recipeDto.getDirections());
        recipe.setIngredients(recipeDto.getIngredients());
        // recipe.setSaved(1);
        Recipe dbRecipe = recipeRepository.save(recipe);
        user.addRecipe(dbRecipe);
        userRepository.save(user);
        return dbRecipe;
    }

    @Override
    public void deleteById(Long id){
        recipeRepository.deleteById(id);
    }

    @Override
    public List<RecipeDto> findAllRecipes(){
        List<Recipe> recipes = recipeRepository.findAll();
        return recipes.stream()
            .map((recipe) -> mapToRecipeDto(recipe))
            .collect(Collectors.toList());
    }

    @Override
    public List<RecipeDto> getAllSavedRecipesByUser(Long userId){
        User user = userService.findUserById(userId);
        return user.getRecipes().stream()
            .map((recipe) -> mapToRecipeDto(recipe))
            .collect(Collectors.toList());
    }




    @Override
    public Recipe updateRecipe(RecipeDto recipeDto){
        Recipe recipe = new Recipe();
        recipe.setId(recipeDto.getId());
        recipe.setName(recipeDto.getName());
        recipe.setDirections(recipeDto.getDirections());
        recipe.setIngredients(recipeDto.getIngredients());
        // recipe.setSaved(recipeDto.getSaved());
        
        return recipeRepository.save(recipe);
    }   

    

    @Override
    public List<Recipe> getRecentRecipes(){
        // need new query in repository
        return recipeRepository.findRecentRecipes();
    }
    
    @Override
    public List<Recipe> getRecentRecipesNotByUser(Long userId){
        User user = userService.findUserById(userId);
        List<Recipe> recentRecipes = getRecentRecipes();
        List<Recipe> recipes = new ArrayList<>();
        for(Recipe recipe: recentRecipes) {
            if(!user.getRecipes().contains(recipe)) {
                recipes.add(recipe);
            }
        }

        return recipes;
    }



    private RecipeDto mapToRecipeDto(Recipe recipe) {
        RecipeDto recipeDto = new RecipeDto();
        recipeDto.setId(recipe.getId());
        recipeDto.setName(recipe.getName());
        recipeDto.setDirections(recipe.getDirections());
        recipeDto.setIngredients(recipe.getIngredients());
        // recipeDto.setSaved(recipe.getSaved());
        return recipeDto;
    }

}
