package com.mingcapstone.quickmealplanner.service;

import java.util.Calendar;
import java.util.List;

import com.mingcapstone.quickmealplanner.dto.MealPlanDto;
import com.mingcapstone.quickmealplanner.dto.MealPlanItemDto;
import com.mingcapstone.quickmealplanner.entity.MealPlan;
import com.mingcapstone.quickmealplanner.entity.MealPlanItem;
import com.mingcapstone.quickmealplanner.entity.User;

public interface MealPlanService {

    MealPlan saveMealPlan(MealPlanDto mealPlanDto);

    MealPlan findMealPlanById(Long id);

    MealPlanDto findMealPlanDtoById(Long id);

    List<MealPlan> findAllMealPlans();

    void deleteMealPlan(Long id);

    MealPlan updateMealPlan(MealPlanDto mealPlanDto);
    
    MealPlan addMealPlanItem(MealPlan mealPlan, MealPlanItem mealPlanItem);
    
    MealPlan removeMealPlanItem(MealPlan mealPlan, MealPlanItem mealPlanItem);

    MealPlanDto findUserMealPlanByStartDate(Long userId, String startDate, Calendar calendar);

    MealPlanDto mapToMealPlanDto(MealPlan mealPlan);
    
    MealPlanItem saveMealPlanItem(MealPlanItemDto mealPlanItemDto);

    MealPlanItem updateMealPlanItem(MealPlanItemDto mealPlanItemDto);
}
