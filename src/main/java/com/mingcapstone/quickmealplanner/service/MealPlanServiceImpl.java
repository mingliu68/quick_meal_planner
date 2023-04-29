package com.mingcapstone.quickmealplanner.service;

import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mingcapstone.quickmealplanner.dto.MealPlanDto;
import com.mingcapstone.quickmealplanner.entity.MealPlan;
import com.mingcapstone.quickmealplanner.entity.Recipe;
import com.mingcapstone.quickmealplanner.entity.User;
import com.mingcapstone.quickmealplanner.repository.MealPlanRepository;
import com.mingcapstone.quickmealplanner.repository.UserRepository;

@Service
public class MealPlanServiceImpl implements MealPlanService {
    
    private MealPlanRepository mealPlanRepository;
    private UserRepository userRepository;

    @Autowired
    public MealPlanServiceImpl(MealPlanRepository mealPlanRepository, UserRepository userRepository) {
        this.mealPlanRepository = mealPlanRepository;
        this.userRepository = userRepository;
    }

//     @Override
//     public MealPlan findMealPlanById(Long id) {
//         return mealPlanRepository.findById(id);
//     }

    @Override
    public MealPlanDto findMealPlanByStartDateByUser(User user, String startDate, Calendar calendar) {
        System.out.println("User MealPlan Count: " + user.getMealPlans().size());
        if(user.getMealPlans().size() > 0) {
            System.out.println("User 1st MealPlan menu item Count: " + user.getMealPlans().get(0).getMenuItems().size());

        }
        for(MealPlan plan : user.getMealPlans()) {
            
            System.out.println("id: " + plan.getStartDate());
            System.out.println("Parameter StartDate: " + startDate);
            if(plan.getStartDate().equals(startDate)) {
                System.out.println("Meal Plan Menu Item Count: " + plan.getMenuItems().size());
                System.out.println("I'm here");
                return mealPlanToDto(plan);
                
            }
        }
        // if meal plan not found, create a new one and send back a dto
        MealPlan newMealPlan = new MealPlan(user, calendar);
        System.out.println("From Service 1: " + newMealPlan.getMenuItems().get("3L"));
        MealPlan dbMealPlan = mealPlanRepository.saveAndFlush(newMealPlan);
        System.out.println("Before adding meal plan to user meal plan count: " + user.getMealPlans().size());
        user.addMealPlan(dbMealPlan);
        System.out.println("After adding meal plan to user meal plan count: " + user.getMealPlans().size());
        User dbUser = userRepository.saveAndFlush(user);
        System.out.println("From Service dbMealPlan: " + dbMealPlan.getMenuItems().size());
        System.out.println("From Service dbUser first meal plan menu item counts: " + dbUser.getMealPlans().get(0).getMenuItems().size());
        return mealPlanToDto(dbMealPlan);
    }


    private MealPlanDto mealPlanToDto(MealPlan mealPlan) {
        System.out.println("From MealPlanToDto Meal Plan menu item count (before): " + mealPlan.getMenuItems().size());
        MealPlanDto mealPlanDto = new MealPlanDto();
        mealPlanDto.setId(mealPlan.getId());
        mealPlanDto.setCalendarStartDate(mealPlan.getCalendarStartDate());
        mealPlanDto.setStartDate(mealPlan.getStartDate());
        mealPlanDto.setPrevStartDate(mealPlan.getPrevStartDate());
        mealPlanDto.setNextStartDate(mealPlan.getNextStartDate());
        mealPlanDto.setMenuItems(mealPlan.getMenuItems());
        mealPlanDto.setUser(mealPlan.getUser());
        System.out.println("From MealPlanToDto meal plan menu item count (after): " + mealPlan.getMenuItems().size());
        return mealPlanDto;
    }

//     @Override
//     public MealPlan saveMealPlan(MealPlanDto mealPlanDto) {
        
//     }

//     @Override
//     public List<MealPlan> findAllMealPlans() {
        
//     }

//     @Override
//     public void deleteMealPlan(String id) {
        
//     }

//     @Override
//     public MealPlan updateMealPlan(MealPlanDto mealPlanDto) {
        
//     }
    
//     @Override
//     public MealPlan addMealPlanItem(String mealKey, Recipe recipe) {
        
//     }
    
//     @Override
//     public MealPlan removeMealPlanItem(String mealKey) {
        
//     }
}
