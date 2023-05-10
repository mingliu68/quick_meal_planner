package com.mingcapstone.quickmealplanner.rest;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mingcapstone.quickmealplanner.dto.MealPlanItemDto;
import com.mingcapstone.quickmealplanner.entity.MealPlanItem;
import com.mingcapstone.quickmealplanner.service.MealPlanService;

@RestController
@RequestMapping("/api")
public class MealPlanRestController {
    
    private MealPlanService mealPlanService;

    @Autowired
    public MealPlanRestController(MealPlanService mealPlanService){
        this.mealPlanService = mealPlanService;
    }

    @PostMapping("/mealPlanItem")
    public void saveMealPlanItem(@RequestBody MealPlanItemDto mealPlanItemDto, Principal principal) {
        
        if(mealPlanItemDto.getId() != null) {
            mealPlanService.updateMealPlanItem(mealPlanItemDto);
        } else {
            MealPlanItem dbMealPlanItem = mealPlanService.saveMealPlanItem(mealPlanItemDto); 
            mealPlanItemDto.setId(dbMealPlanItem.getId());
        }
    } 

}
