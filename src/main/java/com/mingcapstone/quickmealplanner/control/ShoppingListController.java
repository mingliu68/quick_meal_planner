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






    // @GetMapping
    // public String getShoppingList(@RequestParam("mealPlan") Long mealPlanid, Model model, Principal principal) {
    //     UserDto user = getLoggedInUser(principal);
    //     MealPlanDto mealPlan = mealPlanService.findMealPlanDtoById(mealPlanid);
    //     ArrayList<String> allIngredients = new ArrayList<>();
    //     mealPlan.getMealPlanItemsMap().forEach((key, value) -> {
    //         allIngredients.addAll(value.getRecipe().getIngredients()); 
    //     });

    //     ArrayList<IngredientTagDto> ingredientTagDtos = mapIngredientToDto(allIngredients);    

    //     Collections.sort(ingredientTagDtos, new Comparator<IngredientTagDto>() {
    //         public int compare(IngredientTagDto dto1, IngredientTagDto dto2) {
    //             return dto1.getDbName().toLowerCase().compareTo(dto2.getDbName().toLowerCase());
    //         }
    //     });

    //     HashMap<String, HashMap<String, ArrayList<ListItemMeasurementTotalDto>>> shoppingList = consolidateShppingList(ingredientTagDtos);

    //     model.addAttribute("allIngredients", allIngredients);
    //     model.addAttribute("ingredientDtos", ingredientTagDtos);
    //     model.addAttribute("shoppingList" , shoppingList);
    //     model.addAttribute("user", user);
        

    //     String[] startDateArr = mealPlan.getStartDate().split("_");
    //     String startDate = startDateArr[1] + " " + startDateArr[2] + ", " + startDateArr[0];
    //     model.addAttribute("startDate", startDate);

    //     return "shopping-list";
    // }

    // HashMap<String, HashMap<String, ArrayList<ListItemMeasurementTotalDto>>> consolidateShppingList(ArrayList<IngredientTagDto> ingredientTagDtos) {

    //     // Key => category, Value => hashmap of <Name of ingredient, List of measurement dtos>
    //     HashMap<String, HashMap<String, ArrayList<ListItemMeasurementTotalDto>>> shoppingList = new HashMap<>();
       
    //     // first group all ingredient tag dtos by category, then group ingredient tag dtos in each category by ingredient name

    //    // key => category, value => list of ingredient tag dtos
    //    Map<String, List<IngredientTagDto>> ingredientDtosPerCategory = ingredientTagDtos.stream().collect(Collectors.groupingBy(IngredientTagDto::getCategory)); 

    //     ingredientDtosPerCategory.forEach((category, categoryDtos) -> {
            
    //         // key => ingredient dbName, value => list of ingredient tag dtos
    //         Map<String, List<IngredientTagDto>> ingredientDtosPerName = categoryDtos.stream().collect(Collectors.groupingBy(IngredientTagDto::getDbName));

    //         // key => ingredient dbName, value => list of measure total dtos
    //         HashMap<String, ArrayList<ListItemMeasurementTotalDto>> nameDtosMap = new HashMap<>();

    //         ingredientDtosPerName.forEach((dbName, nameDtos) -> {

    //             // key => measure, value => list of ingredient tag dtos
    //             Map<String, List<IngredientTagDto>> ingredientDtosPerMeasure = nameDtos.stream().collect(Collectors.groupingBy(IngredientTagDto::getMeasure));
                
    //             //list of measure dtos for each ingredients
    //             ArrayList<ListItemMeasurementTotalDto> measureDtosList = new ArrayList<>();

    //             ingredientDtosPerMeasure.forEach((measure, measureDtos) -> {
    //                 ListItemMeasurementTotalDto measureTotalDto = new ListItemMeasurementTotalDto();
    //                 measureTotalDto.setMeasure(measure);

    //                 for(IngredientTagDto measureDto: measureDtos) {
    //                     // first see if the amount can be parsed
    //                     double amount = 0;
    //                     String[] amountArr = measureDto.getAmount().split(" ");

    //                     for(String amountStr : amountArr) {
    //                         amount += convertAmount(amountStr);
    //                     }

    //                     measureTotalDto.setAmount(measureTotalDto.getAmount() + amount);
    //                     String str = measureDto.getAmount() + " " + measureDto.getMeasure() + " " + measureDto.getName();
    //                     if(measureDto.getNote() != "") {
    //                         str += " - " + measureDto.getNote();
    //                     }
    //                     measureTotalDto.addNote(str);
    //                 }
    //                 measureTotalDto.setAmount(Math.round(measureTotalDto.getAmount() * 100.0) / 100.0);
    //                 measureDtosList.add(measureTotalDto);
    //             });
    //             nameDtosMap.put(dbName, measureDtosList);   
    //         });
    //         shoppingList.put(category, nameDtosMap);
    //     });
    //     // end of mapping, should add to shopping list before closing

    //     return shoppingList;
    // }

    ///////////////////////////////////////////////////////////////////////



    // private double convertAmount(String amountStr) {
    //     if(amountStr.contains("-")) {
    //         amountStr = amountStr.substring(amountStr.lastIndexOf("-") + 1);
    //     }
    //     switch(amountStr) {
    //         case("1/4") :
    //         case("2/8") :
    //         case("4/16") :
    //             return 0.25;
    //         case("3/4") :
    //         case("6/8") :
    //         case("12/16") :
    //             return 0.75;
    //         case("1/2") :
    //         case("4/8") :
    //         case("8/16") :
    //             return 0.50;
    //         case("1/8") :
    //         case("2/16") :
    //             return 0.125;
    //         case("3/8") :
    //         case("6/16") :
    //             return 0.375;
    //         case("5/8") :
    //         case("10/16") :
    //             return 0.625;
    //         case("7/8") :
    //         case("14/16") :
    //             return 0.875;
    //         case("1/16") :
    //             return 0.625;
    //         case("3/16") :
    //             return 0.1875;
    //         case("5/16") :
    //             return 0.3125;
    //         case("7/16") : 
    //             return 0.4375;
    //         case("9/16") :
    //             return 0.5625;
    //         case("11/16") :
    //             return 0.6875;
    //         case("13/16") :
    //             return 0.8125;
    //         case("15/16") :
    //             return 0.9375;
    //         case("1/3") :
    //             return 0.33;
    //         case("2/3") :
    //             return 0.66;
    //         case("1/5") :
    //             return 0.2;
    //         case("2/5") :
    //             return 0.4;
    //         case("3/5") :
    //             return 0.6;
    //         case("4/5") :
    //             return 0.8;
    //         default:    
    //             return Double.parseDouble(amountStr);

    //     }
    // }

    
    // private ArrayList<IngredientTagDto> mapIngredientToDto(ArrayList<String> ingredients) {
    //     ArrayList<IngredientTagDto> dtoList = new ArrayList<>();
    //     for(String ingredient : ingredients) {

    //         String[] arr = ingredient.split(" ");
    //         dtoList.add(ingredientTagging(arr));
    //     }

    //     return dtoList;
    // }

    // private IngredientTagDto ingredientTagging(String[] ingredientArr) {

    //     IngredientTagDto ingredientTagDto = new IngredientTagDto();

    //     String amount = "";
    //     String measure = "";
    //     String name = "";
    //     String note = "";
    //     int prevIdx = -1;

    //     // first find amount, also look for "/" such as "1/4", "-" such as "1-2", "to" such as "1 to 2"
    //     // stopped when the next element in the array is neither a number nor one of the special symbol / words above

    //     // find amount
    //     for(int i = 0; i < ingredientArr.length; i++) {
            
    //         if(Character.isDigit(ingredientArr[i].charAt(0))) {
    //             if(Character.isDigit(ingredientArr[i].charAt(ingredientArr[i].length() - 1))) {
    //                 amount += amount.length() == 0 ? ingredientArr[i] : (" " + ingredientArr[i]);
                 
    //             } else {
    //                 amount += parsingComboAmount(ingredientArr[i]);
    //             }   
    //             prevIdx = i;
    //         } else if (ingredientArr[i].charAt(0) == '¼' || ingredientArr[i].charAt(0) == '½') {
    //             amount += amount.length() != 0 ? " " : "";
    //             amount += ingredientArr[i].charAt(0) == '¼' ? "1/4 " : "1/2 ";
    //             prevIdx = i;
    //         } else if (ingredientArr[i].toLowerCase().equals("half") || ingredientArr[i].toLowerCase().equals("quarter")){
    //             if(i == 0) {
    //                 amount += amount.length() != 0 ? " " : "";
    //                 amount = ingredientArr[i].toLowerCase().equals("half") ? "1/2" : "1/4";
    //                 prevIdx = getIdxAfterConvertingHalfQuarter(ingredientArr, i);
    //                 i = prevIdx;  
    //             }
    //         } else if (ingredientArr[i].toLowerCase().equals("one") || 
    //                     ingredientArr[i].toLowerCase().equals("a") || 
    //                     ingredientArr[i].toLowerCase().equals("an")) {
    //                 if((i+1) < ingredientArr.length && ingredientArr[i+1].toLowerCase().equals("half") || 
    //                     (i+1) < ingredientArr.length && ingredientArr[i+1].toLowerCase().equals("quarter")) {
    //                     amount += amount.length() != 0 ? " " : "";
    //                     amount = ingredientArr[i+1].toLowerCase().equals("half") ? "1/2" : "1/4";
    //                     prevIdx = getIdxAfterConvertingHalfQuarter(ingredientArr, i+1);
    //                     i = prevIdx;
    //                 } else {
    //                     amount += amount.length() != 0 ? " " : "";
    //                     amount += "1";
    //                     prevIdx = i; 
    //                 }
    //         } else if (
    //             ((ingredientArr[i].charAt(0) == '/' || ingredientArr[i].charAt(0) == '-') && ingredientArr[i].length() == 1) 
    //             || 
    //             ((ingredientArr[i].charAt(0) == '/' || ingredientArr[i].charAt(0) == '-') && Character.isDigit(ingredientArr[i].charAt(1)))
    //             ) {
    //             amount += ingredientArr[i];
    //             prevIdx = i;
    //         } else if(ingredientArr[i].charAt(0) == '(') {
    //             // starting searching for closing parenthesis and add them to note variable
    //             note += ingredientArr[i] + " ";
    //             if(ingredientArr[i].charAt(ingredientArr[i].length()-1) == ')') {
    //                 prevIdx = i;
    //             } else {
    //                 for(int j = i+1; j < ingredientArr.length; j++ ) {
    //                     note += ingredientArr[j] + " ";
    //                     if(ingredientArr[j].charAt(ingredientArr[j].length()-1) == ')') {
    //                         i = j;
    //                         prevIdx = i;
    //                         break;
    //                     }
    //                 }
    //             }
    //         } else {
    //             break;
    //         }
    //     }

    //     // find measurement
    //     String measurement = measureConversion(ingredientArr[prevIdx + 1]);
    //     if(measurement != "") {
    //         String[] measureArr = measurement.split(" ");
    //         if(measureArr.length == 1) {
    //             measure = measurement;
    //         } else {
    //             measure = measureArr[0];
    //             note += measureArr[1];
    //         }
    //         prevIdx++;
    //     } else {
    //         measure = "whole";
    //     }

    //     if(measure.equals("whole") && amount.equals("")) {
    //         amount = "1";
    //     }
       
    //     // get preliminary name
    //     for(int i = prevIdx+1; i < ingredientArr.length; i++) {
    //         if(ingredientArr[i].charAt(0) == '(') {
    //             // starting searching for closing parenthesis and add them to note variable
    //             note += ingredientArr[i] + " ";
    //             if(ingredientArr[i].charAt(ingredientArr[i].length()-1) == ')') {
    //                 continue;
    //             } else {
    //                 for(int j = i+1; j < ingredientArr.length; j++ ) {
    //                     note += ingredientArr[j] + " ";
    //                     if(ingredientArr[j].charAt(ingredientArr[j].length()-1) == ')') {
    //                         i = j;
    //                         prevIdx = i;
    //                         break;
    //                     }
    //                 }
    //             }
    //         } else if(!ingredientArr[i].toLowerCase().equals("of")){
    //             name += ingredientArr[i] + " ";    
    //         }
    //     }

    //     // name drill down, 1st separating by comma, search each term, then separating by space, 
    //     String[] nameArrByComma = name.split(",");
    //     IngredientDto dbIngredient = null;
        
    //     // start with searching by each individual phrases, seperated by comma
    //     for(int i = 0; i < nameArrByComma.length; i++) {  
    //         nameArrByComma[i] = nameArrByComma[i].trim();

    //         if(nameArrByComma[i].length() - 2 >= 0 && nameArrByComma[i].substring(nameArrByComma[i].length() - 2).equals("es")) {
    //             dbIngredient = pluralSearch(nameArrByComma[i], 2);
    //         } else if(nameArrByComma[i].length() - 1 >= 0 && nameArrByComma[i].substring(nameArrByComma[i].length() - 1).equals("s")) {
    //             dbIngredient = pluralSearch(nameArrByComma[i], 1);
    //         } else {
    //             if(measure.equals("teaspoon") || measure.equals("talespoon")) {
    //                 dbIngredient = ingredientService.findIngredientByName(specialNameAlteration(nameArrByComma[i]));
    //             } else {
    //                 dbIngredient = ingredientService.findIngredientByName(nameArrByComma[i]);
    //             }
    //         }
           
    //         if(dbIngredient != null) {
    //             ingredientTagDto.setDbNameDto(dbIngredient); 
    //             break;
    //         }
    //     }
        
    //     // if no result came from split by comma (initial search), 
    //     // continue with greedy search and drill down more by spliting each phrase by space and do combo search
    //     if(ingredientTagDto.getDbNameDto() == null) {
    //         for(int i = 0; i < nameArrByComma.length; i++) {
    //             String[] nameArrBySpace = nameArrByComma[i].split(" ");

    //             int maxCount = nameArrBySpace.length - 1;
    //             String searchTerm = "";
    //             while(maxCount > 0) {
    //                 for(int j = 0; j <= nameArrBySpace.length - maxCount; j++) {
    //                     // get current search term
    //                     for(int k = j; k < j + maxCount; k++) {
    //                         if(searchTerm != "") {
    //                             searchTerm += " ";
    //                         }    
    //                         searchTerm += nameArrBySpace[k];
    //                     }
    //                     // checking for plurals and search database after getting search term
    //                     if(searchTerm.length() - 2 >= 0 && searchTerm.substring(searchTerm.length() - 2).equals("es")) {
    //                         dbIngredient = pluralSearch(searchTerm, 2);
    //                     } else if(searchTerm.length() - 1 >= 0 && searchTerm.substring(searchTerm.length() - 1).equals("s")) {
    //                         dbIngredient = pluralSearch(searchTerm, 1);
    //                     } else {
    //                         if(measure.equals("teaspoon") || measure.equals("talespoon")) {
    //                             dbIngredient = ingredientService.findIngredientByName(specialNameAlteration(searchTerm));
    //                         } else {
    //                             dbIngredient = ingredientService.findIngredientByName(searchTerm);
    //                         }
                            
    //                     }
    //                     searchTerm = "";
    //                     // if current search term result in an ingredient record, break out of all loops
    //                     if(dbIngredient != null) {
    //                         ingredientTagDto.setDbNameDto(dbIngredient); 
    //                         break; // break out of the current for loop
    //                     }
    //                 }
                    
    //                 if (ingredientTagDto.getDbNameDto() != null) {
    //                     break; // break out of the while loop
    //                 }

    //                 maxCount--;
    //             }

    //             if (ingredientTagDto.getDbNameDto() != null) {
    //                 break; // break out of the first for loop
    //             }
    //         }
    //     }
    //     ingredientTagDto.setName(name);
    //     ingredientTagDto.setDbName(ingredientTagDto.getDbNameDto() != null ? ingredientTagDto.getDbNameDto().getName() : ingredientTagDto.getName());
    //     ingredientTagDto.setCategory(ingredientTagDto.getDbNameDto() != null ? ingredientTagDto.getDbNameDto().getCategory() : "General");
    //     ingredientTagDto.setAmount(amount);
    //     ingredientTagDto.setMeasure(measure);
    //     ingredientTagDto.setNote(note);

    //     return ingredientTagDto;
    // }

    // private String specialNameAlteration(String name) {
    //     switch(name) {
    //         case "pepper" :
    //             return "black pepper";
    //         default: 
    //             return name;
    //     }
    // }

    // private String parsingComboAmount(String str) {

    //     String amount = "";
    //     for(int i = 0; i < str.length(); i++) {
    //         if(Character.isDigit(str.charAt(i))) {
    //             amount += str.charAt(i);
    //         } else {
    //             switch(str) {
    //                 case("½"):
    //                     if(amount != "") amount += " ";
    //                     amount += "1/2";
    //                 case("⅓"):
    //                     if(amount != "") amount += " ";
    //                     amount +=  "1/3";
    //                 case("⅔"):
    //                     if(amount != "") amount += " ";
    //                     amount +=  "2/3";
    //                 case("¼"):
    //                     if(amount != "") amount += " ";
    //                     amount +=  "1/4";
    //                 case("¾"):
    //                     if(amount != "") amount += " ";
    //                     amount +=  "3/4";
    //                 case("⅕"):
    //                     if(amount != "") amount += " ";
    //                     amount +=  "1/5";
    //                 case("⅖"):
    //                     if(amount != "") amount += " ";
    //                     amount +=  "2/5";
    //                 case("⅗"):
    //                     if(amount != "") amount += " ";
    //                     amount +=  "3/5";
    //                 case("⅘"):
    //                     if(amount != "") amount += " ";
    //                     amount +=  "4/5";
    //                 case("⅙"):
    //                     if(amount != "") amount += " ";
    //                     amount +=  "1/6";
    //                 case("⅚"):
    //                     if(amount != "") amount += " ";
    //                     amount +=  "5/6";
    //                 case("⅛"):
    //                     if(amount != "") amount += " ";
    //                     amount +=  "1/8";
    //                 case("⅜"):
    //                     if(amount != "") amount += " ";
    //                     amount +=  "3/8";
    //                 case("⅝"):
    //                     if(amount != "") amount += " ";
    //                     amount +=  "5/8";
    //                 case("⅞"):
    //                     if(amount != "") amount += " ";
    //                     amount +=  "7/8";
    //                 default: 
    //                     amount += "";
    //             }
    //         }
    //     }
    //     return amount;
    // }

    // // if word in index is either "half" or "quarter", check the words that follows and look for common pattern and adjust index
    // private int getIdxAfterConvertingHalfQuarter(String[] ingredientArr, int idx) {
    //     // idx is position of half or quarter
    //     if((idx + 1) < ingredientArr.length && ingredientArr[idx+1].equals("of") ) {
    //         if((idx + 2) < ingredientArr.length && (ingredientArr[idx+2].equals("a") || ingredientArr[idx+2].equals("an"))) {
    //             return idx + 2;
    //         }
    //         else return idx + 1;
    //     } else if((idx + 1) < ingredientArr.length && (ingredientArr[idx+1].equals("a") || ingredientArr[idx+1].equals("an"))) {
    //         return idx + 1;
    //     }
    //     return idx;
    // }

    // private String measureConversion(String measure) {
    //     // removing plural
    //     measure = measure.charAt(measure.length()-1) == 's' ? measure.substring(0, measure.length() - 1) : measure;
    //     measure = measure.toLowerCase();
    //     // tsp, tbsp, lb, oz
    //     String[] measureArr = {"clove", "slice", "dash", "pinch", "drop", "can", "jar", "cup", "sliced"};
    //     ArrayList<String> measureList = new ArrayList<>(Arrays.asList(measureArr));

    //     switch(measure) {
    //         case "tsp" :
    //         case "tsp." :
    //         case "teaspoon" : 
    //             return "teaspoon";
    //         case "tbsp" :
    //         case "tbsp." :
    //         case "tablespoon" :
    //             return "tablespoon";
    //         case "lb" :
    //         case "lb." :
    //         case "pound" :
    //             return "pound";
    //         case "oz" :
    //         case "oz." :
    //         case "ounce" :
    //             return "ounce";
    //         case "small" :
    //         case "medium" :
    //         case "large" :
    //             return "whole " + measure;
    //         case "qt" :
    //         case "qt." :
    //         case "quart" :
    //             return "quart";
    //         case "pt" :
    //         case "pt." :
    //         case "pint" :
    //             return "pint";
    //         default:
    //             if(measureList.contains(measure)) {
    //                 return measure;
    //             }
    //             return "";

    //     }
    // }

    // private IngredientDto pluralSearch(String searchTerm, int count) {
    //     IngredientDto dbIngredient = null;
    //     for(int i = 0; i <= count; i++) {
    //         dbIngredient = ingredientService.findIngredientByName(searchTerm.substring(0, searchTerm.length() - i));
    //         if(dbIngredient != null) {
    //             break;
    //         }
    //     }

    //     return dbIngredient;
    // }

    private UserDto getLoggedInUser(Principal principal) {
        return userService.findUserByEmail(principal.getName());
    }

}
