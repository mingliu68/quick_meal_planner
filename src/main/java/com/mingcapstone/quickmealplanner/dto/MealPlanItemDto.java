package com.mingcapstone.quickmealplanner.dto;

import com.mingcapstone.quickmealplanner.entity.MealPlan;
import com.mingcapstone.quickmealplanner.entity.Recipe;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MealPlanItemDto {

    private Long id;

    private MealPlan mealPlan;

    private Long mealPlanId;

    private String mealType;

    private Recipe recipe;

    private Long recipeId;

    public MealPlanItemDto(MealPlan mealPlan, String mealType) {
        this.mealPlan = mealPlan;
        this.mealType = mealType;
        this.mealPlanId = mealPlan.getId();
    }

}
