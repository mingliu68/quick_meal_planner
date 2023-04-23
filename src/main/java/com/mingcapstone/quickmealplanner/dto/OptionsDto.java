package com.mingcapstone.quickmealplanner.dto;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OptionsDto {
  
    private String ingredient1;
    private String ingredient2;
    private String ingredient3;
    private String restriction;


}
