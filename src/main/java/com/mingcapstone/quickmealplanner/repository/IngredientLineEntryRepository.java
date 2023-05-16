package com.mingcapstone.quickmealplanner.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mingcapstone.quickmealplanner.entity.IngredientLineEntry;

public interface IngredientLineEntryRepository extends JpaRepository<IngredientLineEntry, Long>  {
    
}
