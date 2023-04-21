package com.mingcapstone.quickmealplanner.service;


import java.util.List;

import com.mingcapstone.quickmealplanner.dto.UserDto;
import com.mingcapstone.quickmealplanner.entity.User;

public interface UserService {
    
    User saveUser(UserDto userDto);
    
    User findUserByEmail(String email);

    List<UserDto> findAllUsers();
}
