package com.mingcapstone.quickmealplanner.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter 
@NoArgsConstructor  
@AllArgsConstructor 
@Entity
@Table(name="meal_plan_items")
public class MealPlanItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    // @JsonBackReference
    @JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property="id"
    )
    @JoinColumn(name="meal_plan_id")
    private MealPlan mealPlan;

    @Column(name="meal_type")
    private String mealType;

    @ManyToOne
    @JoinColumn(name="recipe_id")
    private Recipe recipe;

    public MealPlanItem(MealPlan mealPlan, String mealType) {
        this.mealPlan = mealPlan;
        this.mealType = mealType;
    }

}
