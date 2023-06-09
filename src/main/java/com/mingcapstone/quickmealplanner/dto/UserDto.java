package com.mingcapstone.quickmealplanner.dto;

import java.util.List;

import com.mingcapstone.quickmealplanner.entity.MealPlan;
import com.mingcapstone.quickmealplanner.entity.Recipe;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto
{
    private Long id;

    @NotEmpty
    private String firstName;

    @NotEmpty
    private String lastName;

    @NotEmpty(message = "Email should not be empty")
    @Email
    private String email;

    @NotEmpty(message = "Password should not be empty")
    private String password;
    
    private List<Recipe> recipes;   

    private List<MealPlan> mealPlans;


    // public UserDto(User user) {
    //     if(user.getId() != null) {
    //         this.id = user.getId();
    //     }
    //     if(user.getName() != null){
    //         String[] str = user.getName().split(" ");
    //         firstName = str[0];
    //         lastName = str[1];
    //     }
    //     if(user.getEmail() != null) {
    //         this.email = user.getEmail();
    //     }
    //     if(user.getPassword() != null) {
    //         this.password = user.getPassword();
    //     }
    // }

    public void addRecipe(Recipe recipe) {
        recipes.add(recipe);
    }

    public void removeRecipe(Recipe recipe) {
        recipes.remove(recipe);
    }

   
}