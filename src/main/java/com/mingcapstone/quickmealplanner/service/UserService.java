package com.mingcapstone.quickmealplanner.service;


import java.util.List;

import com.mingcapstone.quickmealplanner.dto.RecipeDto;
import com.mingcapstone.quickmealplanner.dto.UserDto;
import com.mingcapstone.quickmealplanner.entity.Recipe;
import com.mingcapstone.quickmealplanner.entity.User;

public interface UserService {
    
    UserDto saveUser(UserDto userDto);
    
    UserDto findUserByEmail(String email);

    User findUserById(Long id);

    List<UserDto> findAllUsers();

    void deleteUser(Long id);

    User updateUser(UserDto userDto);
    
    User addRecipeToList(Long recipeId, Long userId);
    
    User removeRecipeFromList(Long recipeId, Long userId);

    Boolean recipeSavedByUser(Long recipeId, Long userId);
}
