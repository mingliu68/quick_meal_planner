package com.mingcapstone.quickmealplanner.service;

import java.util.Calendar;
import java.util.List;

import com.mingcapstone.quickmealplanner.dto.MealPlanDto;
import com.mingcapstone.quickmealplanner.entity.MealPlan;
import com.mingcapstone.quickmealplanner.entity.Recipe;
import com.mingcapstone.quickmealplanner.entity.User;

public interface MealPlanService {

    // MealPlan findMealPlanById(Long id);

    MealPlanDto findMealPlanByStartDateByUser(User user, String startDate, Calendar calendar);

    // MealPlan saveMealPlan(MealPlanDto mealPlanDto);

    // List<MealPlan> findAllMealPlans();

    // void deleteMealPlan(Long id);

    // MealPlan updateMealPlan(MealPlanDto mealPlanDto);
    
    // MealPlan addMealPlanItem(String mealKey, Recipe recipe);
    
    // MealPlan removeMealPlanItem(String mealKey);
}
