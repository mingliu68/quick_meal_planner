package com.mingcapstone.quickmealplanner.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter 
@Setter 
@NoArgsConstructor  
@AllArgsConstructor 
@Entity
@Table(name="meal_plans")
public class MealPlan {
    
    @Id
    private String id;

    @OneToMany(mappedBy="mealPlan")
    private List<MealPlanItem> mealPlanItems;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @Column(name="next")
    private String next;

    @Column(name="prev")
    private String prev;

    public void addMealPlanItem(MealPlanItem mealPlanItem) {
        mealPlanItems.add(mealPlanItem);
    }

    public void removeMealPlanItem(MealPlanItem mealPlanItem) {
        mealPlanItems.remove(mealPlanItem);
    }
}
