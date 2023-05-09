package com.mingcapstone.quickmealplanner.service;

import java.util.List;

import com.mingcapstone.quickmealplanner.dto.MealPlanItemDto;
import com.mingcapstone.quickmealplanner.entity.MealPlan;
import com.mingcapstone.quickmealplanner.entity.MealPlanItem;

public interface MealPlanItemService {

    MealPlanItem findMealPlanItemById(Long id);

    MealPlanItemDto findMealPlanItemByMealPlanAndMealType(MealPlan mealPlan, String mealType);

    List<MealPlanItem> findAllMealPlanItems();

    void deleteMealPlanItem(Long id);

    MealPlanItemDto mapToMealPlanItemDto(MealPlanItem mealPlanItem);
}
