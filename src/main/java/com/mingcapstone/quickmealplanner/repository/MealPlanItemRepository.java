package com.mingcapstone.quickmealplanner.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mingcapstone.quickmealplanner.entity.MealPlanItem;

public interface MealPlanItemRepository extends JpaRepository<MealPlanItem, String> {
    
}

