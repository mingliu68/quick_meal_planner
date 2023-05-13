package com.mingcapstone.quickmealplanner.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IngredientTagDto {
    
    private String name;

    private String amount;

    private String measure;

    private String note;

    private List<IngredientDto> dbNameList;

    private IngredientDto dbNameDto;
    
    private String dbName;


}
