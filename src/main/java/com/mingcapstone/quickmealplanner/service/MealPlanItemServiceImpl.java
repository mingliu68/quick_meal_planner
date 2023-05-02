package com.mingcapstone.quickmealplanner.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.QAbstractAuditable;
import org.springframework.stereotype.Service;

import com.mingcapstone.quickmealplanner.dto.MealPlanItemDto;
import com.mingcapstone.quickmealplanner.entity.MealPlan;
import com.mingcapstone.quickmealplanner.entity.MealPlanItem;
import com.mingcapstone.quickmealplanner.entity.Recipe;
import com.mingcapstone.quickmealplanner.repository.MealPlanItemRepository;
import com.mingcapstone.quickmealplanner.repository.MealPlanRepository;

@Service
public class MealPlanItemServiceImpl implements MealPlanItemService {
    
    private MealPlanItemRepository mealPlanItemRepository;
    private MealPlanRepository mealPlanRepository;
    private RecipeService recipeService;

    @Autowired
    public MealPlanItemServiceImpl(MealPlanItemRepository mealPlanItemRepository, MealPlanRepository mealPlanRepository, RecipeService recipeService) {
        this.mealPlanItemRepository = mealPlanItemRepository;
        this.mealPlanRepository = mealPlanRepository;
        this.recipeService = recipeService;
    }
    

    @Override
    public MealPlanItem saveMealPlanItem(MealPlanItemDto mealPlanItemDto){

        MealPlanItem mealPlanItem = new MealPlanItem();
        MealPlan mealPlan = mealPlanItemDto.getMealPlan();
        System.out.println("MealPlanID: " + mealPlanItem.getId());
        mealPlanItem.setMealPlan(mealPlan);
        mealPlanItem.setMealType(mealPlanItemDto.getMealType());
        Recipe recipe = recipeService.findById(mealPlanItemDto.getRecipeId());
        mealPlanItem.setRecipe(recipe);
        MealPlanItem dbMealPlanItem = mealPlanItemRepository.save(mealPlanItem);
        mealPlan.addMealPlanItem(dbMealPlanItem);
        mealPlanRepository.save(mealPlan);
        return dbMealPlanItem;
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

    @Override
    public MealPlanItem updateMealPlanItem(MealPlanItemDto mealPlanItemDto){
        // mealplanitem update method only update recipe.  all other stays the same
        MealPlanItem mealPlanItem = findMealPlanItemById(mealPlanItemDto.getId());
        // mealPlanItem.setId(mealPlanItemDto.getId());
        // mealPlanItem.setMealPlan(mealPlanItemDto.getMealPlan());
        // mealPlanItem.setMealType(mealPlanItemDto.getMealType());

        if(mealPlanItemDto.getRecipeId() == null) {
            mealPlanItem.setRecipe(null);
        } else if (mealPlanItemDto.getRecipeId() != mealPlanItem.getRecipe().getId()) {
            Recipe recipe = recipeService.findById(mealPlanItemDto.getRecipeId());
            mealPlanItem.setRecipe(recipe);
        }
       
        return mealPlanItemRepository.save(mealPlanItem);
    }
}
