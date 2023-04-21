package com.mingcapstone.quickmealplanner.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mingcapstone.quickmealplanner.entity.Recipe;

public interface RecipeRepository extends JpaRepository<Recipe, Long>{
    
}
