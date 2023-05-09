package com.mingcapstone.quickmealplanner.security;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mingcapstone.quickmealplanner.entity.Role;
import com.mingcapstone.quickmealplanner.entity.User;
import com.mingcapstone.quickmealplanner.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService{
    
    private UserRepository userRepository;


    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if(user != null) {
            System.out.println(user.getRoles().get(0).getName());
            System.out.println("Principal user id from CustomUserDetailService : " + user.getId());
            return new org.springframework.security.core.userdetails.User(
                user.getEmail(), 
                user.getPassword(),  
                mapRolesToAuthorities(user.getRoles()));
                // getAuthorities("USER"));
        } else {
            throw new UsernameNotFoundException("Invalid username or password");
        }
    }

    private Collection <? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        Collection<? extends GrantedAuthority> mapRoles = roles.stream()
            .map(role -> new SimpleGrantedAuthority(role.getName()))
            .collect(Collectors.toList());
        return mapRoles; 
    }

    // private Collection <? extends GrantedAuthority> getAuthorities(String role) {
    //     return Arrays.asList(new SimpleGrantedAuthority(role));
    // }


}
