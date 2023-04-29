package com.mingcapstone.quickmealplanner.service;

import java.util.List;

import com.mingcapstone.quickmealplanner.dto.MealPlanItemDto;
import com.mingcapstone.quickmealplanner.entity.MealPlanItem;

public interface MealPlanItemService {
    
    MealPlanItem saveMealPlanItem(MealPlanItemDto mealPlanItemDto);

    MealPlanItem findMealPlanItemById(Long id);

    List<MealPlanItem> findAllMealPlanItems();

    void deleteMealPlanItem(Long id);

    MealPlanItem updateMealPlanItem(MealPlanItemDto mealPlanItemDto);
}
