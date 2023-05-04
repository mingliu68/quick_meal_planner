package com.mingcapstone.quickmealplanner.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Getter 
@Setter 
@NoArgsConstructor  
@AllArgsConstructor 
@Entity
@Table(name="meal_plans")
public class MealPlan {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="next")
    private MealPlan next;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="prev")
    private MealPlan prev;
    
    @Column(name="start_date")
    private String startDate;

    @OneToMany(mappedBy="mealPlan", fetch = FetchType.EAGER)
    // @JsonManagedReference
    @JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property="id"
    )
    private List<MealPlanItem> mealPlanItems = new ArrayList<>();


    
    public MealPlan(User user, String startDate) {
        this.user = user;
        this.startDate = startDate;
    }


    public void addMealPlanItem(MealPlanItem mealPlanItem) {
        mealPlanItems.add(mealPlanItem);
    }

    public void removeMealPlanItem(MealPlanItem mealPlanItem) {
        mealPlanItems.remove(mealPlanItem);
    }
}
