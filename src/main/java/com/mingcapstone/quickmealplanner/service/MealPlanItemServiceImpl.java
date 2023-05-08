package com.mingcapstone.quickmealplanner.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mingcapstone.quickmealplanner.dto.MealPlanItemDto;
import com.mingcapstone.quickmealplanner.entity.MealPlan;
import com.mingcapstone.quickmealplanner.entity.MealPlanItem;
import com.mingcapstone.quickmealplanner.repository.MealPlanItemRepository;

@Service
public class MealPlanItemServiceImpl implements MealPlanItemService {
    
    private MealPlanItemRepository mealPlanItemRepository;

    @Autowired
    public MealPlanItemServiceImpl(MealPlanItemRepository mealPlanItemRepository) {
        this.mealPlanItemRepository = mealPlanItemRepository;
    }

    @Override
    public MealPlanItem findMealPlanItemById(Long id){
        Optional<MealPlanItem> result = mealPlanItemRepository.findById(id);
        MealPlanItem mealPlanItem = null;
        if(result.isPresent()) {
            mealPlanItem = result.get();
        } else {
            throw new RuntimeException("Meal Plan Item Not Found");
        }

        return mealPlanItem;
    }

    @Override
    public MealPlanItemDto findMealPlanItemByMealPlanAndMealType(MealPlan mealPlan, String mealType) {
        for(MealPlanItem item : mealPlan.getMealPlanItems()) {
            if(item.getMealType().equals(mealType)) {
                return mapToMealPlanItemDto(item);
            }
        }
        // if no mealplanitem matching mealType, send over a new dto
        MealPlanItemDto mealPlanItemDto = new MealPlanItemDto(mealPlan, mealType);
        return mealPlanItemDto;
    }

    @Override
    public MealPlanItemDto mapToMealPlanItemDto(MealPlanItem mealPlanItem) {
        MealPlanItemDto mealPlanItemDto = new MealPlanItemDto();
        mealPlanItemDto.setId(mealPlanItem.getId());
        mealPlanItemDto.setMealPlan(mealPlanItem.getMealPlan());
        mealPlanItemDto.setMealPlanId(mealPlanItem.getMealPlan().getId());
        mealPlanItemDto.setMealType(mealPlanItem.getMealType());
        mealPlanItemDto.setRecipe(mealPlanItem.getRecipe());
        if(mealPlanItem.getRecipe() != null) {
            mealPlanItemDto.setRecipeId(mealPlanItem.getRecipe().getId());
        }
        return mealPlanItemDto;
    }

    // not mapping to DTO since we won't be sending the list to FE
    @Override
    public List<MealPlanItem> findAllMealPlanItems(){
        return mealPlanItemRepository.findAll();
    }

    @Override
    public void deleteMealPlanItem(Long id){
        mealPlanItemRepository.deleteById(id);
    }
}
