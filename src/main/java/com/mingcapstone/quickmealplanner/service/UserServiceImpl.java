package com.mingcapstone.quickmealplanner.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mingcapstone.quickmealplanner.dto.UserDto;
import com.mingcapstone.quickmealplanner.entity.Recipe;
import com.mingcapstone.quickmealplanner.entity.Role;
import com.mingcapstone.quickmealplanner.entity.User;
import com.mingcapstone.quickmealplanner.repository.RecipeRepository;
import com.mingcapstone.quickmealplanner.repository.RoleRepository;
import com.mingcapstone.quickmealplanner.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    RecipeRepository recipeRepository;


    @Override
    public UserDto saveUser(UserDto userDto) {
        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        Role role = roleRepository.findByName("USER");
        if (role == null) {
            role = checkRoleExist();
        }
        user.setRoles(Arrays.asList(role));
        User dbUser = userRepository.save(user);
        userDto.setId(dbUser.getId());

        return userDto;
    }

    @Override
    public User updateUser(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        user.setRecipes(userDto.getRecipes());
        Role role = roleRepository.findByName("USER");
        if (role == null) {
            role = checkRoleExist();
        }
        user.setRoles(Arrays.asList(role));

        return userRepository.saveAndFlush(user);
    }

    @Override
    public UserDto findUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if(user != null) {
            return mapUserToDto(user);
        }
        return null; 
    }

    private UserDto mapUserToDto(User user) {
        UserDto userDto = new UserDto();

        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setRecipes(user.getRecipes());
        userDto.setMealPlans(user.getMealPlans());

        return userDto;
    }

    @Override
    public User findUserById(Long id) {
        Optional<User> result = userRepository.findById(id);
        User user = null;
        if (result.isPresent()) {
            user = result.get();
        } else {
            throw new RuntimeException("User not found");
        }
        return user;
    }

    @Override
    public List<UserDto> findAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map((user) -> mapToUserDto(user))
                .collect(Collectors.toList());
    }

    private UserDto mapToUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setEmail(user.getEmail());
        userDto.setRecipes(user.getRecipes());
        return userDto;
    }

    private Role checkRoleExist() {
        Role role = new Role();
        role.setName("USER");
        return roleRepository.save(role);
    }

    @Override
    public Boolean recipeSavedByUser(Long recipeId, Long userId) {
        User user = findUserById(userId);
        Optional<Recipe> result = recipeRepository.findById(recipeId);
        Recipe recipe = null;
        if (result.isPresent()) {
            recipe = result.get();
        } else {
            throw new RuntimeException("Recipe not found");
        }
        return user.getRecipes().contains(recipe);
    }
    
    
    
    // adding recipe to user recipes list and update recipe saved count
    @Override
    public User addRecipeToList(Long recipeId, Long userId) {
        User user = findUserById(userId);
        Optional<Recipe> result = recipeRepository.findById(recipeId);
        Recipe recipe = null;
        if (result.isPresent()) {
            recipe = result.get();
        } else {
            throw new RuntimeException("Recipe not found");
        }
        user.addRecipe(recipe);
        return userRepository.save(user);
    }

    @Override
    public User removeRecipeFromList(Long recipeId, Long userId) {
        User user = findUserById(userId);
        Optional<Recipe> result = recipeRepository.findById(recipeId);
        Recipe recipe = null;
        if (result.isPresent()) {
            recipe = result.get();
        } else {
            throw new RuntimeException("Recipe not found");
        }
        user.removeRecipe(recipe);
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
