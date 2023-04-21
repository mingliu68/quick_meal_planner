package com.mingcapstone.quickmealplanner.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor 
@Entity
@Table(name="recipes")
public class Recipe {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String name;

    @Column(name="ingredients",length = 2500)
    private List<String> ingredients;

    @Column(name="directions",length = 2500)
    private List<String> directions;

    @Column(name="saved")
    private int saved;

    @ManyToMany(mappedBy="recipes") 
    private List<User> users;
    
    public void setSaved(int num) {
        saved += num;
    }
}
