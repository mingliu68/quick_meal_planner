package com.mingcapstone.quickmealplanner.service;

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

    @Autowired
    public RecipeServiceImpl(RecipeRepository recipeRepository, UserRepository userRepository) {
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
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
    public Recipe save(RecipeDto recipeDto, User user){
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
    public List<RecipeDto> getAllSavedRecipesByUser(User user){
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
