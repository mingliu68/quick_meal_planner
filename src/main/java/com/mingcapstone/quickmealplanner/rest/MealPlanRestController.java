package com.mingcapstone.quickmealplanner.rest;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mingcapstone.quickmealplanner.dto.MealPlanItemDto;
import com.mingcapstone.quickmealplanner.entity.MealPlan;
import com.mingcapstone.quickmealplanner.entity.MealPlanItem;
import com.mingcapstone.quickmealplanner.entity.User;
import com.mingcapstone.quickmealplanner.service.MealPlanItemService;
import com.mingcapstone.quickmealplanner.service.MealPlanService;
import com.mingcapstone.quickmealplanner.service.RecipeService;
import com.mingcapstone.quickmealplanner.service.UserService;

@RestController
@RequestMapping("/api")
public class MealPlanRestController {
    
    private MealPlanItemService mealPlanItemService;
    
    private UserService userService;

    @Autowired
    public MealPlanRestController(MealPlanItemService mealPlanItemService, UserService userService){
        this.mealPlanItemService = mealPlanItemService;
        this.userService = userService;
    }

    @Autowired
    MealPlanService mealPlanService;

    @PostMapping("/mealPlanItem")
    public MealPlanItemDto saveMealPlanItem(@RequestBody MealPlanItemDto mealPlanItemDto, Principal principal) {
        User user = getLoggedInUser(principal);
        MealPlan mealPlan = mealPlanService.findMealPlanById(mealPlanItemDto.getMealPlanId());
        mealPlanItemDto.setMealPlan(mealPlan);

        

        if(mealPlanItemDto.getId() != null) {
            mealPlanItemService.updateMealPlanItem(mealPlanItemDto);
            return mealPlanItemDto;
        }
        MealPlanItem dbMealPlanItem = mealPlanItemService.saveMealPlanItem(mealPlanItemDto); 
        mealPlanItemDto.setId(dbMealPlanItem.getId());
        return mealPlanItemDto;
    } 

    private User getLoggedInUser(Principal principal) {
        return userService.findUserByEmail(principal.getName());
    }   
}
