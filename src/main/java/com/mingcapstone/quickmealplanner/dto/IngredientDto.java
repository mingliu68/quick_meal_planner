package com.mingcapstone.quickmealplanner.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IngredientDto {
    
    private String name;

    private String amount;

    private String measure;

    private String note;


}
