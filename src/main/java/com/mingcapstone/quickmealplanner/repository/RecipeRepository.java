package com.mingcapstone.quickmealplanner.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mingcapstone.quickmealplanner.entity.Recipe;

public interface RecipeRepository extends JpaRepository<Recipe, Long>{
    
    @Query("SELECT r from Recipe r Order BY id DESC LIMIT 50")
    List<Recipe> findRecentRecipes();
}
