package com.mingcapstone.quickmealplanner.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter 
@Setter 
// @NoArgsConstructor  
// @AllArgsConstructor 
@Entity
@Table(name="meal_plans")
public class MealPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @Column(name="start_date")
    private String startDate;

    @Column(name="calendar_start_date")
    private Calendar calendarStartDate;

    @Column(name="prev")
    private String prevStartDate;

    @Column(name="next")
    private String nextStartDate;

    @ElementCollection    
    // @CollectionTable(name="meal_plan_menu_items", joinColumns=@JoinColumn(name="meal_plan_id"))
    // @MapKeyColumn(name="meal_type")
    // @Column(name="menu_item")
    // hashmap of meal type for key ("1L" => monday lunch ... etc)
    // recipe id for value
    // private Map<String, Long> menuItems;
    private Map<String, Recipe> menuItems;

    
    public MealPlan() {
        menuItems = setMealPlanMap();
    }

    public MealPlan(User user, Calendar calendarStartDate) {
        // set user
        this.user = user;
        // set calendarStartDate object
        this.calendarStartDate = calendarStartDate;
        // set startdate string
        String[] str = calendarStartDate.getTime().toString().split(" ");
        this.startDate = str[1] + str[2] + str[5];

        // setting prev and next, make changes to calendar object then change back to original
        // set pre
        calendarStartDate.add(Calendar.DATE, -7);
        str = calendarStartDate.getTime().toString().split(" ");
        this.prevStartDate = str[1] + str[2] + str[5];

        // set next
        calendarStartDate.add(Calendar.DATE, 14);
        str = calendarStartDate.getTime().toString().split(" ");
        this.nextStartDate = str[1] + str[2] + str[5];

        // reset back to original
        calendarStartDate.add(Calendar.DATE, -7);

        // set menu items
        menuItems = setMealPlanMap();
    }

    public MealPlan(User user, Calendar calendarStartDate, String startDate, String prevStartDate, String nextStartDate){
        this.user = user;
        this.calendarStartDate = calendarStartDate;
        this.startDate = startDate;
        this.prevStartDate = prevStartDate;
        this.nextStartDate = nextStartDate;
        menuItems = setMealPlanMap();
    }

    public Map<String, Recipe> setMealPlanMap() {
        Map<String, Recipe> menuItems = new HashMap<>();
        String[] meals = {"L","D"};
        for(int i = 1; i <= 7; i++) {
            for(String m : meals) {
                String key = i + m;
                // System.out.println(key);
                menuItems.put(key, null);
            }
        }
        return menuItems;
    }

    // public void addMenuItem(String key, Long recipeId) {
    //     menuItems.put(key, recipeId);
    // }

    // public void removeMenuItem(String key) {
    //     menuItems.put(key, null);
    // }

    public void addMenuItem(String key, Recipe recipe) {
        menuItems.put(key, recipe);
    }

    public void removeMenuItem(String key) {
        menuItems.put(key, null);
    }

    



}
