package com.mingcapstone.quickmealplanner.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mingcapstone.quickmealplanner.dto.MealPlanDto;
import com.mingcapstone.quickmealplanner.entity.MealPlan;
import com.mingcapstone.quickmealplanner.entity.MealPlanItem;
import com.mingcapstone.quickmealplanner.repository.MealPlanRepository;

@Service
public class MealPlanServiceImpl implements MealPlanService {
    
    private MealPlanRepository mealPlanRepository;

    @Autowired
    public MealPlanServiceImpl(MealPlanRepository mealPlanRepository) {
        this.mealPlanRepository = mealPlanRepository;
    }

    @Override
    public MealPlan saveMealPlan(MealPlanDto mealPlanDto){
        MealPlan mealPlan = new MealPlan();
        mealPlan.setUser(mealPlanDto.getUser());
        mealPlan.setStartDate(mealPlanDto.getStartDate());
        mealPlan.setPrev(mealPlanDto.getPrev());
        mealPlan.setNext(mealPlanDto.getNext());
        mealPlan.setMealPlanItems(mealPlanDto.getMealPlanItems());
        return mealPlanRepository.save(mealPlan);
    }

    @Override
    public MealPlan findMealPlanById(Long id){
        Optional<MealPlan> result = mealPlanRepository.findById(id);
        MealPlan mealPlan = null;
        if(result.isPresent()) {
            mealPlan = result.get();
        } else {
            throw new RuntimeException("Meal Plan Not Found.");
        }

        return mealPlan;
    }

    // not mapping to DTO since we won't be sending the list to FE
    @Override
    public List<MealPlan> findAllMealPlans(){
        return mealPlanRepository.findAll();
    }

    @Override
    public void deleteMealPlan(Long id){
       mealPlanRepository.deleteById(id);
    }

    @Override
    public MealPlan updateMealPlan(MealPlanDto mealPlanDto){
        MealPlan mealPlan = new MealPlan();
        mealPlan.setId(mealPlanDto.getId());
        mealPlan.setUser(mealPlanDto.getUser());
        mealPlan.setStartDate(mealPlanDto.getStartDate());
        mealPlan.setPrev(mealPlanDto.getPrev());
        mealPlan.setNext(mealPlanDto.getNext());
        mealPlan.setMealPlanItems(mealPlanDto.getMealPlanItems());
        return mealPlanRepository.save(mealPlan);
    }
    
    @Override
    public MealPlan addMealPlanItem(MealPlan mealPlan, MealPlanItem mealPlanItem){
        mealPlan.addMealPlanItem(mealPlanItem);
        MealPlan dbMealPlan = mealPlanRepository.save(mealPlan);
        return dbMealPlan;
    }
    
    @Override
    public MealPlan removeMealPlanItem(MealPlan mealPlan, MealPlanItem mealPlanItem){
        mealPlan.removeMealPlanItem(mealPlanItem);
        MealPlan dbMealPlan = mealPlanRepository.save(mealPlan);
        return dbMealPlan;
    }
}
