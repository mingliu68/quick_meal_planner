package com.mingcapstone.quickmealplanner.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecipeDto {
    
    private Long id;

    @NotEmpty   
    private String name;

    @NotEmpty 
    private List<String> ingredients;

    @NotEmpty 
    private List<String> directions;

    private int saved;

}
