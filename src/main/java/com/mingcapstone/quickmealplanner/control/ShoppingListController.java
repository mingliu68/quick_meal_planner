package com.mingcapstone.quickmealplanner.control;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mingcapstone.quickmealplanner.dto.IngredientDto;
import com.mingcapstone.quickmealplanner.dto.IngredientTagDto;
import com.mingcapstone.quickmealplanner.dto.MealPlanDto;
import com.mingcapstone.quickmealplanner.service.IngredientService;
import com.mingcapstone.quickmealplanner.service.MealPlanService;

@Controller
@RequestMapping("/user/shoppinglist")
public class ShoppingListController {
    

    private IngredientService ingredientService;
    private MealPlanService mealPlanService;

    @Autowired
    public ShoppingListController(IngredientService ingredientService, MealPlanService mealPlanService) {
        this.ingredientService = ingredientService;
        this.mealPlanService = mealPlanService;
    }

    @GetMapping
    public String getShoppingList(@RequestParam("mealPlan") Long mealPlanid, Model model, Principal principal) {
        MealPlanDto mealPlan = mealPlanService.findMealPlanDtoById(mealPlanid);
        List<String> allIngredients = new ArrayList<>();
        mealPlan.getMealPlanItemsMap().forEach((key, value) -> {
            allIngredients.addAll(value.getRecipe().getIngredients()); 
        });
        System.out.println(allIngredients);

        List<IngredientTagDto> ingredientTagDtos = mapIngredientToDto(allIngredients);    

        Collections.sort(ingredientTagDtos, new Comparator<IngredientTagDto>() {
            public int compare(IngredientTagDto dto1, IngredientTagDto dto2) {
                return dto1.getDbName().toLowerCase().compareTo(dto2.getDbName().toLowerCase());
            }
        });

        model.addAttribute("allIngredients", allIngredients);
        model.addAttribute("ingredientDtos", ingredientTagDtos);

        return "shopping-list";
    }


    private List<IngredientTagDto> mapIngredientToDto(List<String> ingredients) {
        List<IngredientTagDto> dtoList = new ArrayList<>();
        for(String ingredient : ingredients) {

            String[] arr = ingredient.split(" ");
            dtoList.add(ingredientTagging(arr));
        }

        return dtoList;
    }

    private IngredientTagDto ingredientTagging(String[] ingredientArr) {

        IngredientTagDto ingredientTagDto = new IngredientTagDto();

        String amount = "";
        String measure = "";
        String name = "";
        String note = "";
        int prevIdx = -1;

        // first find amount, also look for "/" such as "1/4", "-" such as "1-2", "to" such as "1 to 2"
        // stopped when the next element in the array is neither a number nor one of the special symbol / words above

        // find amount
        for(int i = 0; i < ingredientArr.length; i++) {
            
            if(Character.isDigit(ingredientArr[i].charAt(0))) {
                amount += amount.length() == 0 ? ingredientArr[i] : (" " + ingredientArr[i]);
                prevIdx = i;
            } else if (ingredientArr[i].charAt(0) == '¼' || ingredientArr[i].charAt(0) == '½') {
                amount += amount.length() != 0 ? " " : "";
                amount += ingredientArr[i].charAt(0) == '¼' ? "1/4 " : "1/2 ";
                prevIdx = i;
            } else if (
                ((ingredientArr[i].charAt(0) == '/' || ingredientArr[i].charAt(0) == '-') && ingredientArr[i].length() == 1) 
                || 
                ((ingredientArr[i].charAt(0) == '/' || ingredientArr[i].charAt(0) == '-') && Character.isDigit(ingredientArr[i].charAt(1)))
                ) {
                amount += ingredientArr[i];
                prevIdx = i;
            } else if(ingredientArr[i].charAt(0) == '(') {
                // starting searching for closing parenthesis and add them to note variable
                note += ingredientArr[i] + " ";
                if(ingredientArr[i].charAt(ingredientArr[i].length()-1) == ')') {
                    prevIdx = i;
                } else {
                    for(int j = i+1; j < ingredientArr.length; j++ ) {
                        note += ingredientArr[j] + " ";
                        if(ingredientArr[j].charAt(ingredientArr[j].length()-1) == ')') {
                            i = j;
                            prevIdx = i;
                            break;
                        }
                    }
                }
            } else {
                break;
            }
        }

        // find measurement
        String measurement = measureConversion(ingredientArr[prevIdx + 1]);
        if(measurement != "") {
            String[] measureArr = measurement.split(" ");
            if(measureArr.length == 1) {
                measure = measurement;
            } else {
                measure = measureArr[0];
                note += measureArr[1];
            }
            prevIdx++;
        } else {
            measure = "whole";
        }
       
        // get preliminary name
        for(int i = prevIdx+1; i < ingredientArr.length; i++) {
            if(ingredientArr[i].charAt(0) == '(') {
                // starting searching for closing parenthesis and add them to note variable
                note += ingredientArr[i] + " ";
                if(ingredientArr[i].charAt(ingredientArr[i].length()-1) == ')') {
                    continue;
                } else {
                    for(int j = i+1; j < ingredientArr.length; j++ ) {
                        note += ingredientArr[j] + " ";
                        if(ingredientArr[j].charAt(ingredientArr[j].length()-1) == ')') {
                            i = j;
                            prevIdx = i;
                            break;
                        }
                    }
                }
            } else if(!ingredientArr[i].toLowerCase().equals("of")){
                name += ingredientArr[i] + " ";    
            }
        }


        // name drill down, 1st separating by comma, search each term, then separating by space, 
        String[] nameArrByComma = name.split(",");
        IngredientDto dbIngredient = null;
        
        // start with searching by each individual phrases, seperated by comma
        for(int i = 0; i < nameArrByComma.length; i++) {  
            nameArrByComma[i] = nameArrByComma[i].trim();

            if(nameArrByComma[i].length() - 2 >= 0 && nameArrByComma[i].substring(nameArrByComma[i].length() - 2).equals("es")) {
                dbIngredient = pluralSearch(nameArrByComma[i], 2);
            } else if(nameArrByComma[i].length() - 1 >= 0 && nameArrByComma[i].substring(nameArrByComma[i].length() - 1).equals("s")) {
                dbIngredient = pluralSearch(nameArrByComma[i], 1);
            } else {
                dbIngredient = ingredientService.findIngredientByName(nameArrByComma[i]);
            }
           
            if(dbIngredient != null) {
                ingredientTagDto.setDbNameDto(dbIngredient); 
                break;
            }
        }
        
        // if no result came from split by comma (initial search), 
        // continue with greedy search and drill down more by spliting each phrase by space and do combo search

        if(ingredientTagDto.getDbNameDto() == null) {
            for(int i = 0; i < nameArrByComma.length; i++) {
                String[] nameArrBySpace = nameArrByComma[i].split(" ");

                int maxCount = nameArrBySpace.length - 1;
                String searchTerm = "";
                while(maxCount > 0) {
                    for(int j = 0; j <= nameArrBySpace.length - maxCount; j++) {
                        // get current search term
                        for(int k = j; k < j + maxCount; k++) {
                            if(searchTerm != "") {
                                searchTerm += " ";
                            }    
                            searchTerm += nameArrBySpace[k];
                        }

                        // checking for plurals and search database after getting search term
                        if(searchTerm.length() - 2 >= 0 && searchTerm.substring(searchTerm.length() - 2).equals("es")) {
                            dbIngredient = pluralSearch(searchTerm, 2);
                        } else if(searchTerm.length() - 1 >= 0 && searchTerm.substring(searchTerm.length() - 1).equals("s")) {
                            dbIngredient = pluralSearch(searchTerm, 1);
                        } else {
                            dbIngredient = ingredientService.findIngredientByName(searchTerm);
                        }
                        searchTerm = "";
                        // if current search term result in an ingredient record, break out of all loops
                        if(dbIngredient != null) {
                            ingredientTagDto.setDbNameDto(dbIngredient); 
                            break; // break out of the current for loop
                        }
                    }
                    
                    if (ingredientTagDto.getDbNameDto() != null) {
                        break; // break out of the while loop
                    }

                    maxCount--;
                }

                if (ingredientTagDto.getDbNameDto() != null) {
                    break; // break out of the first for loop
                }
            }
        }

        ingredientTagDto.setDbName(ingredientTagDto.getDbNameDto() != null ? ingredientTagDto.getDbNameDto().getName() : "");
        ingredientTagDto.setName(name);
        ingredientTagDto.setAmount(amount);
        ingredientTagDto.setMeasure(measure);
        ingredientTagDto.setNote(note);

        // System.out.println("Name: " + name + ", Amount: " + amount + ", Measure: " + measure + ", Note: " + note + ", Index: " + prevIdx);
        // System.out.println();
        return ingredientTagDto;
    }


    private String measureConversion(String measure) {
        // removing plural
        measure = measure.charAt(measure.length()-1) == 's' ? measure.substring(0, measure.length() - 1) : measure;
        measure = measure.toLowerCase();
        // tsp, tbsp, lb, oz
        String[] measureArr = {"clove", "slice", "dash", "pinch", "drop", "can", "jar", "cup", "sliced"};
        ArrayList<String> measureList = new ArrayList<>(Arrays.asList(measureArr));

        switch(measure) {
            case "tsp" :
            case "teaspoon" : 
                return "teaspoon";
            case "tbsp" :
            case "tablespoon" :
                return "tablespoon";
            case "lb" :
            case "pound" :
                return "pound";
            case "oz" :
            case "ounce" :
                return "ounce";
            case "small" :
            case "medium" :
            case "large" :
                return "whole " + measure;
            case "qt" :
            case "quart" :
                return "quart";
            case "pt" :
            case "pint" :
                return "pint";
            default:
                if(measureList.contains(measure)) {
                    return measure;
                }
                return "";

        }
    }

    private IngredientDto pluralSearch(String searchTerm, int count) {
        IngredientDto dbIngredient = null;
        for(int i = 0; i <= count; i++) {
            dbIngredient = ingredientService.findIngredientByName(searchTerm.substring(0, searchTerm.length() - i));
            if(dbIngredient != null) {
                break;
            }
        }

        return dbIngredient;
    }

}
