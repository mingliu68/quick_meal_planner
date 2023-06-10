package com.mingcapstone.quickmealplanner.control;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mingcapstone.quickmealplanner.dto.IngredientDto;
import com.mingcapstone.quickmealplanner.dto.IngredientTagDto;
import com.mingcapstone.quickmealplanner.dto.MealPlanDto;
import com.mingcapstone.quickmealplanner.dto.UserDto;
import com.mingcapstone.quickmealplanner.entity.IngredientLineEntry;
import com.mingcapstone.quickmealplanner.dto.ListItemMeasurementTotalDto;
import com.mingcapstone.quickmealplanner.service.IngredientService;
import com.mingcapstone.quickmealplanner.service.MealPlanService;
import com.mingcapstone.quickmealplanner.service.UserService;

@Controller
@RequestMapping("/user/shoppinglist")
public class ShoppingListController {
    

    private IngredientService ingredientService;
    private MealPlanService mealPlanService;
    private UserService userService;

    @Autowired
    public ShoppingListController(IngredientService ingredientService, MealPlanService mealPlanService, UserService userService) {
        this.ingredientService = ingredientService;
        this.mealPlanService = mealPlanService;
        this.userService = userService;
    }

    @GetMapping
    public String getShoppingList_2(@RequestParam("mealPlan") Long mealPlanid, Model model, Principal principal) {
        UserDto user = getLoggedInUser(principal);
        MealPlanDto mealPlan = mealPlanService.findMealPlanDtoById(mealPlanid);
        ArrayList<IngredientLineEntry> dbIngredients = new ArrayList<>();
        mealPlan.getMealPlanItemsMap().forEach((key, value) -> {
            dbIngredients.addAll(value.getRecipe().getDbIngredients());
        });
        
        HashMap<String, HashMap<String, ArrayList<ListItemMeasurementTotalDto>>> shoppingList = consolidateShppingList(dbIngredients);

        model.addAttribute("shoppingList" , shoppingList);
        model.addAttribute("user", user);
        
        String[] startDateArr = mealPlan.getStartDate().split("_");
        String startDate = startDateArr[1] + " " + startDateArr[2] + ", " + startDateArr[0];
        model.addAttribute("startDate", startDate);

        return "shopping-list";
    }


    HashMap<String, HashMap<String, ArrayList<ListItemMeasurementTotalDto>>> consolidateShppingList(ArrayList<IngredientLineEntry> dbIngredients) {

        // Key => category, Value => hashmap of <Name of ingredient, List of measurement dtos>
        HashMap<String, HashMap<String, ArrayList<ListItemMeasurementTotalDto>>> shoppingList = new HashMap<>();
       
        // first group all ingredient tag dtos by category, then group ingredient tag dtos in each category by ingredient name

        // key => category, value => list of ingredient tag dtos
        Map<String, List<IngredientLineEntry>> ingredientDtosPerCategory = dbIngredients.stream().collect(Collectors.groupingBy(IngredientLineEntry::getCategory)); 

        ingredientDtosPerCategory.forEach((category, categoryDtos) -> {
            
            // key => ingredient dbName, value => list of ingredient tag dtos
            Map<String, List<IngredientLineEntry>> ingredientDtosPerName = categoryDtos.stream().collect(Collectors.groupingBy(IngredientLineEntry::getName));

            // key => ingredient dbName, value => list of measure total dtos
            HashMap<String, ArrayList<ListItemMeasurementTotalDto>> nameDtosMap = new HashMap<>();

            ingredientDtosPerName.forEach((dbName, nameDtos) -> {

                // key => measure, value => list of ingredient tag dtos
                Map<String, List<IngredientLineEntry>> ingredientDtosPerMeasure = nameDtos.stream().collect(Collectors.groupingBy(IngredientLineEntry::getMeasure));
                
                //list of measure dtos for each ingredients
                ArrayList<ListItemMeasurementTotalDto> measureDtosList = new ArrayList<>();

                ingredientDtosPerMeasure.forEach((measure, measureDtos) -> {
                    ListItemMeasurementTotalDto measureTotalDto = new ListItemMeasurementTotalDto();
                    measureTotalDto.setMeasure(measure);

                    for(IngredientLineEntry measureDto: measureDtos) {

                        measureTotalDto.setAmount(measureTotalDto.getAmount() + measureDto.getAmount());

                        measureTotalDto.addNote(measureDto.getNote());
                    }
                    measureTotalDto.setAmount(Math.round(measureTotalDto.getAmount() * 100.0) / 100.0);
                    measureDtosList.add(measureTotalDto);
                });
                nameDtosMap.put(dbName, measureDtosList);   
            });
            shoppingList.put(category, nameDtosMap);
        });
        // end of mapping, should add to shopping list before closing

        return shoppingList;
    }

    private UserDto getLoggedInUser(Principal principal) {
        return userService.findUserByEmail(principal.getName());
    }

}
