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
@NoArgsConstructor  
@AllArgsConstructor 
@Entity
@Table(name="meal_plans")
public class MealPlan {
    
    @Id
    private Long id;
    
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="next")
    private MealPlan next;

    @OneToOne(cascade = CascadeType.ALL)
    @Column(name="prev")
    private MealPlan prev;
    
    @Column(name="start_date")
    private String startDate;

    @OneToMany(mappedBy="mealPlan")
    private List<MealPlanItem> mealPlanItems = new ArrayList<>();




    public void addMealPlanItem(MealPlanItem mealPlanItem) {
        mealPlanItems.add(mealPlanItem);
    }

    public void removeMealPlanItem(MealPlanItem mealPlanItem) {
        mealPlanItems.remove(mealPlanItem);
    }
}
