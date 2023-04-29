package com.mingcapstone.quickmealplanner.service;

import java.util.List;

import com.mingcapstone.quickmealplanner.dto.MealPlanDto;
import com.mingcapstone.quickmealplanner.entity.MealPlan;
import com.mingcapstone.quickmealplanner.entity.MealPlanItem;

public interface MealPlanService {

    MealPlan saveMealPlan(MealPlanDto mealPlanDto);

    MealPlan findMealPlanById(Long id);

    List<MealPlan> findAllMealPlans();

    void deleteMealPlan(Long id);

    MealPlan updateMealPlan(MealPlanDto mealPlanDto);
    
    MealPlan addMealPlanItem(MealPlan mealPlan, MealPlanItem mealPlanItem);
    
    MealPlan removeMealPlanItem(MealPlan mealPlan, MealPlanItem mealPlanItem);
}
