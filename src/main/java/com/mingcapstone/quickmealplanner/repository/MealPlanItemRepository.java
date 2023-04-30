package com.mingcapstone.quickmealplanner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mingcapstone.quickmealplanner.entity.MealPlan;
import com.mingcapstone.quickmealplanner.entity.MealPlanItem;

public interface MealPlanItemRepository extends JpaRepository<MealPlanItem, Long> {
    
    @Query("SELECT m FROM MealPlanItem m WHERE m.mealPlan = ?1 and m.mealType = ?2")
    MealPlanItem findByMealPlanAndMealType(MealPlan mealPlan, String mealType);
    
}

