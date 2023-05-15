package com.mingcapstone.quickmealplanner.service;

import java.util.List;

import com.mingcapstone.quickmealplanner.dto.IngredientDto;

public interface IngredientService {

    IngredientDto findIngredientById(Long id);

    IngredientDto findIngredientByName(String name);

    List<IngredientDto> findIngredientByNameLike(String name);

    IngredientDto saveIngredient(IngredientDto ingredientDto);

    List<IngredientDto> findAllIngredients();

    List<IngredientDto> findAllIngredientsByCategory(String category);

}
