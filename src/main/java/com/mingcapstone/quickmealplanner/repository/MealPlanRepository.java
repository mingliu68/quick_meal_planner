package com.mingcapstone.quickmealplanner.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mingcapstone.quickmealplanner.entity.MealPlan;

public interface MealPlanRepository extends JpaRepository<MealPlan, Long> {
    
}
