package com.mingcapstone.quickmealplanner.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mingcapstone.quickmealplanner.entity.Ingredient;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    
    List<Ingredient> findByCategoryIgnoreCase(String category);

    // @Query("SELECT i FROM Ingredient i WHERE LOWER(i.name) = ?1")
    List<Ingredient> findByNameIgnoreCase(String name);

    Optional<Ingredient> findOneByNameIgnoreCase(String name);

    List<Ingredient> findByNameLikeIgnoreCase(String name);

    
}
