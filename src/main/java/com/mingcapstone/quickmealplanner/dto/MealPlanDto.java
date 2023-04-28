package com.mingcapstone.quickmealplanner.dto;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import com.mingcapstone.quickmealplanner.entity.MealPlanItem;
import com.mingcapstone.quickmealplanner.entity.Recipe;
import com.mingcapstone.quickmealplanner.entity.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
// @NoArgsConstructor
// @AllArgsConstructor
public class MealPlanDto {


    private Long id;

    private User user;

    private String startDate;

    private Calendar calendarStartDate;

    private String prevStartDate;

    private String nextStartDate;

    private HashMap<String, Recipe> menuItems;

    
    public MealPlanDto() {
        setMealPlanDtoMap();
    }

    public MealPlanDto(User user, Calendar calendarStartDate) {
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
        setMealPlanDtoMap();
    }
    
    // key format "1L" => monday lunch
    public void setMealPlanDtoMap() {
        menuItems = new HashMap<>();
        String[] meals = {"L","D"};
        for(int i = 1; i <= 7; i++) {
            for(String m : meals) {
                String key = i + m;
                menuItems.put(key, null);
            }
        }
    }

    public void addMenuItem(String key, Recipe recipe) {
        menuItems.put(key, recipe);
    }

    public void removeMenuItem(String key) {
        menuItems.put(key, null);
    }


    // private String id;

    // private List<MealPlanItem> mealPlanItems;

    // private User user;

    // private String next;

    // private String prev;

    // public void addMealPlanItem(MealPlanItem mealPlanItem) {
    //     mealPlanItems.add(mealPlanItem);
    // }

    // public void removeMealPlanItem(MealPlanItem mealPlanItem) {
    //     mealPlanItems.remove(mealPlanItem);
    // }
}
