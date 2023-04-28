package com.mingcapstone.quickmealplanner.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mingcapstone.quickmealplanner.dto.MealPlanDto;
import com.mingcapstone.quickmealplanner.entity.MealPlan;
import com.mingcapstone.quickmealplanner.entity.Recipe;
import com.mingcapstone.quickmealplanner.repository.MealPlanRepository;

@Service
public class MealPlanServiceImpl implements MealPlanService {
    
    private MealPlanRepository mealPlanRepository;

    @Autowired
    public MealPlanServiceImpl(MealPlanRepository mealPlanRepository) {
        this.mealPlanRepository = mealPlanRepository;
    }

    @Override
    public MealPlan findMealPlanById(Long id) {
        return mealPlanRepository.findById(id);
    }

    @Override
    public MealPlan findMealPlanByStartDate(String startDate) {
        
    }

    @Override
    public MealPlan saveMealPlan(MealPlanDto mealPlanDto) {
        
    }

    @Override
    public List<MealPlan> findAllMealPlans() {
        
    }

    @Override
    public void deleteMealPlan(String id) {
        
    }

    @Override
    public MealPlan updateMealPlan(MealPlanDto mealPlanDto) {
        
    }
    
    @Override
    public MealPlan addMealPlanItem(String mealKey, Recipe recipe) {
        
    }
    
    @Override
    public MealPlan removeMealPlanItem(String mealKey) {
        
    }
}
