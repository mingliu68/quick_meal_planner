package com.mingcapstone.quickmealplanner.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter 
@NoArgsConstructor  
@AllArgsConstructor 
@Entity
@Table(name="ingredient_line_entries")
public class IngredientLineEntry {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="name") 
    private String name;

    @Column(name="amount")
    private double amount;

    @Column(name="measure")
    private String measure;

    @Column(name="category")
    private String category;

    @Column(name="note")
    private String note;

    @ManyToOne
    @JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property="id"
    )
    @JoinColumn(name="recipe_id")
    private Recipe recipe;

    // @ManyToOne
    // @JsonIdentityInfo(
    //     generator = ObjectIdGenerators.PropertyGenerator.class,
    //     property="id"
    // )
    // @JoinColumn(name="ingredient_id")
    // private Ingredient ingredient;

}
