package com.mingcapstone.quickmealplanner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mingcapstone.quickmealplanner.entity.MealPlan;

public interface MealPlanRepository extends JpaRepository<MealPlan, String> {
    
    @Query("SELECT m from MealPlan m WHERE m.user_id = ?1 and u.start_date = ?2")
    public MealPlan getMealPlanByStartDate(Long userId, String startDate);
}
