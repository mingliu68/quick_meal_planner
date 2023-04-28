package com.mingcapstone.quickmealplanner.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter 
@Setter 
// @NoArgsConstructor  
// @AllArgsConstructor 
@Entity
@Table(name="meal_plan_items")
public class MealPlanItem {
    
    // @Id
    // private String id;

    // @ManyToOne
    // @JoinColumn(name="meal_plan_id")
    // private MealPlan mealPlan;

    // @ManyToOne
    // @JoinColumn(name="recipe_id")
    // private Recipe recipe;

}
