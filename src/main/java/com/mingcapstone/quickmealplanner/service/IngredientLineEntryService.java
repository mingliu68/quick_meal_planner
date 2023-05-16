package com.mingcapstone.quickmealplanner.service;

import java.util.List;

import com.mingcapstone.quickmealplanner.entity.IngredientLineEntry;
import com.mingcapstone.quickmealplanner.entity.Recipe;

public interface IngredientLineEntryService {
    
    IngredientLineEntry saveLineEntry(IngredientLineEntry IngredientLineEntry);

    void setDbIngredients(List<String> ingredients, Recipe recipe);
}
