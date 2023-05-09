package com.mingcapstone.quickmealplanner.rest;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mingcapstone.quickmealplanner.dto.MealPlanItemDto;
import com.mingcapstone.quickmealplanner.dto.UserDto;
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
    
    private MealPlanService mealPlanService;
    private UserService userService;

    @Autowired
    public MealPlanRestController(MealPlanService mealPlanService, UserService userService){
        this.mealPlanService = mealPlanService;
        this.userService = userService;
    }

    @PostMapping("/mealPlanItem")
    public void saveMealPlanItem(@RequestBody MealPlanItemDto mealPlanItemDto, Principal principal) {
        // User user = getLoggedInUser(principal);

        
        if(mealPlanItemDto.getId() != null) {
            System.out.println("meal plan item id from controller: " + mealPlanItemDto.getId());
            mealPlanService.updateMealPlanItem(mealPlanItemDto);
        } else {
            MealPlanItem dbMealPlanItem = mealPlanService.saveMealPlanItem(mealPlanItemDto); 
            mealPlanItemDto.setId(dbMealPlanItem.getId());
        }
    } 

    private UserDto getLoggedInUser(Principal principal) {
        return userService.findUserByEmail(principal.getName());
    }   
}
