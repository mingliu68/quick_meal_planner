package com.mingcapstone.quickmealplanner.entity;

import com.mingcapstone.quickmealplanner.dto.IngredientDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter 
@NoArgsConstructor  
@AllArgsConstructor 
@Entity
@Table(name="ingredients")
public class Ingredient {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="name")
    private String name;

    @Column(name="category")
    private String category;


    // @OneToMany(mappedBy="ingredient", fetch = FetchType.EAGER)
    // @JsonIdentityInfo(
    //     generator = ObjectIdGenerators.PropertyGenerator.class,
    //     property="id"
    // )
    // private List<IngredientLineEntry> ingredientLineEntries;



    public IngredientDto mapToDto() {
        IngredientDto dto = new IngredientDto();
        dto.setId(id);
        dto.setName(name);
        dto.setCategory(category);
        return dto;
    }

}
