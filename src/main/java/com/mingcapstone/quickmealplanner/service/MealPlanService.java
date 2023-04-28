package com.mingcapstone.quickmealplanner.service;

import java.util.List;

import com.mingcapstone.quickmealplanner.dto.MealPlanDto;
import com.mingcapstone.quickmealplanner.entity.MealPlan;
import com.mingcapstone.quickmealplanner.entity.MealPlanItem;
import com.mingcapstone.quickmealplanner.entity.Recipe;

public interface MealPlanService {

    MealPlan findMealPlanById(Long id);

    MealPlan findMealPlanByStartDate(String startDate);

    MealPlan saveMealPlan(MealPlanDto mealPlanDto);

    List<MealPlan> findAllMealPlans();

    void deleteMealPlan(Long id);

    MealPlan updateMealPlan(MealPlanDto mealPlanDto);
    
    MealPlan addMealPlanItem(String mealKey, Recipe recipe);
    
    MealPlan removeMealPlanItem(String mealKey);
}
