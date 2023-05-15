package com.mingcapstone.quickmealplanner.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mingcapstone.quickmealplanner.dto.IngredientDto;
import com.mingcapstone.quickmealplanner.entity.Ingredient;
import com.mingcapstone.quickmealplanner.repository.IngredientRepository;


@Service
public class IngredientServiceImpl implements IngredientService {
    
    private IngredientRepository ingredientRepository;

    @Autowired
    public IngredientServiceImpl(IngredientRepository ingredientRepository) {

        this.ingredientRepository = ingredientRepository;

    }
    
    @Override
    public IngredientDto findIngredientById(Long id) {

        Optional<Ingredient> result = ingredientRepository.findById(id);
        IngredientDto dto = new IngredientDto();
        if(result.isPresent()) {
            dto = result.get().mapToDto();
        } else {
            throw new RuntimeException("Ingredient not found");
        }

        return dto;
        
    }

    @Override 
    public IngredientDto findIngredientByName(String name) {
        List<Ingredient> result = ingredientRepository.findByNameIgnoreCase(name.toLowerCase().trim());
        IngredientDto dto = null;
        if(result.size() != 0) {
            dto = result.get(0).mapToDto();
        }
            
        // Optional<Ingredient> result = ingredientRepository.findOneByNameIgnoreCase(name.toLowerCase().trim());
        // IngredientDto dto = null;
        // if(result.isPresent()) {
        //     dto = result.get().mapToDto();
        // }
        return dto;
    }

    @Override
    public List<IngredientDto> findIngredientByNameLike(String name) {

        List<Ingredient> ingredients = ingredientRepository.findByNameLikeIgnoreCase(name);
        return ingredients.stream().map(ingredient -> ingredient.mapToDto()).collect(Collectors.toList());

    }

    @Override
    public IngredientDto saveIngredient(IngredientDto ingredientDto) {
        
        Ingredient ingredient = new Ingredient();
        ingredient.setName(ingredientDto.getName());
        ingredient.setCategory(ingredientDto.getCategory());
        
        return ingredientRepository.save(ingredient).mapToDto();
         
    }

    @Override
    public List<IngredientDto> findAllIngredients() {

        List<Ingredient> ingredients = ingredientRepository.findAll();

        return ingredients.stream().map(ingredient -> ingredient.mapToDto()).collect(Collectors.toList());

    }

    @Override
    public List<IngredientDto> findAllIngredientsByCategory(String category) {

        List<Ingredient> ingredients = ingredientRepository.findByCategoryIgnoreCase(category);

        return ingredients.stream().map(ingredient -> ingredient.mapToDto()).collect(Collectors.toList());

    }
}
