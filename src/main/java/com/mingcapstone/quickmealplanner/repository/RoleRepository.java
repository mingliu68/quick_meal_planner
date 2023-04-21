package com.mingcapstone.quickmealplanner.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mingcapstone.quickmealplanner.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long>{
    
    Role findByName(String name);
}
