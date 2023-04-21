package com.mingcapstone.quickmealplanner.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.management.RuntimeErrorException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mingcapstone.quickmealplanner.dto.UserDto;
import com.mingcapstone.quickmealplanner.entity.Recipe;
import com.mingcapstone.quickmealplanner.entity.Role;
import com.mingcapstone.quickmealplanner.entity.User;
import com.mingcapstone.quickmealplanner.repository.RoleRepository;
import com.mingcapstone.quickmealplanner.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {
    

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired 
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User saveUser(UserDto userDto){
        User user = new User();
        user.setName(userDto.getFirstName() + " " + userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        List<Recipe> recipes = new ArrayList<>();
        user.setRecipes(recipes);
        Role role = roleRepository.findByName("USER");
        if(role == null){
            role = checkRoleExist();
        }
        user.setRoles(Arrays.asList(role));
        // User dbUser = userRepository.save(user);
        
        return userRepository.save(user);
    }
    
    @Override
    public User findUserByEmail(String email){
        return userRepository.findByEmail(email);
    }   

    @Override
    public User findUserById(Long id) {
        Optional<User> result = userRepository.findById(id);
        User user = null;
        if(result.isPresent()) {
            user = result.get();
        } else {
            throw new RuntimeException("User not found");
        }
        return user;
    }

    @Override
    public List<UserDto> findAllUsers(){
        List<User> users = userRepository.findAll();
        return users.stream()
                .map((user) -> mapToUserDto(user))
                .collect(Collectors.toList());
    }

    private UserDto mapToUserDto(User user) {
        // System.out.println(user.getRecipes());
        UserDto userDto = new UserDto();
        String[] str = user.getName().split(" ");
        userDto.setFirstName(str[0]);
        userDto.setLastName(str[1]);
        userDto.setEmail(user.getEmail());
        userDto.setRecipes(user.getRecipes());
        return userDto;
    }

    private Role checkRoleExist(){
        Role role = new Role();
        role.setName("USER");
        return roleRepository.save(role);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}

