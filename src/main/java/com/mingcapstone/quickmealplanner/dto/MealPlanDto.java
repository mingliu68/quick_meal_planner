package com.mingcapstone.quickmealplanner.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.mingcapstone.quickmealplanner.entity.MealPlan;
import com.mingcapstone.quickmealplanner.entity.MealPlanItem;
import com.mingcapstone.quickmealplanner.entity.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MealPlanDto {

    private Long id;
    
    private User user;
    
    private MealPlan next;

    private MealPlan prev;

    private String startDate;

    private List<MealPlanItemDto> mealPlanItemsDtos = new ArrayList<>();

    private List<MealPlanItem> mealPlanItems = new ArrayList<>();
    
    private HashMap<String, MealPlanItem> mealPlanItemsMap = new HashMap<>();

    

    public void addMealPlanItem(MealPlanItem mealPlanItem) {
        mealPlanItems.add(mealPlanItem);
    }

    public void removeMealPlanItem(MealPlanItem mealPlanItem) {
        mealPlanItems.remove(mealPlanItem);
    }

    public void addMealPlanItemDto(MealPlanItemDto mealPlanItemDto) {
        mealPlanItemsDtos.add(mealPlanItemDto);
    }

    public void removeMealPlanItemDto(MealPlanItemDto mealPlanItemDto) {
        mealPlanItemsDtos.remove(mealPlanItemDto);
    }
}
