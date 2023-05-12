package com.mingcapstone.quickmealplanner.control;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mingcapstone.quickmealplanner.dto.IngredientDto;
import com.mingcapstone.quickmealplanner.dto.MealPlanDto;
import com.mingcapstone.quickmealplanner.dto.MealPlanItemDto;
import com.mingcapstone.quickmealplanner.dto.RecipeDto;
import com.mingcapstone.quickmealplanner.dto.UserDto;

import com.mingcapstone.quickmealplanner.service.MealPlanService;
import com.mingcapstone.quickmealplanner.service.RecipeService;

import com.mingcapstone.quickmealplanner.service.UserService;




@Controller
@RequestMapping("/user/mealplans")
public class MealPlanController {


    private MealPlanService mealPlanService;
    private UserService userService;
    private RecipeService recipeService;

    private String[] mealTypes = {
        "MONDAY_LUNCH", "MONDAY_DINNER", 
        "TUESDAY_LUNCH", "TUESDAY_DINNER",
        "WEDNESDAY_LUNCH", "WEDNESDAY_DINNER",
        "THURSDAY_LUNCH", "THURSDAY_DINNER",
        "FRIDAY_LUNCH", "FRIDAY_DINNER",
        "SATURDAY_LUNCH", "SATURDAY_DINNER",
        "SUNDAY_LUNCH", "SUNDAY_DINNER"
    };

    @Autowired
    public MealPlanController(MealPlanService mealPlanService, UserService userService, RecipeService recipeService) {
        this.mealPlanService = mealPlanService;
        this.userService = userService;
        this.recipeService = recipeService;
    }

    // get mealplan by using startdate
    @GetMapping
    public String mealPlans(
        @RequestParam(name="startDate", required=false) String paramStartDate, 
        @RequestParam(name="mealPlan", required=false) Long mealPlanId, 
        Model model, Principal principal) {
        
        // if id param exist, 
            // find mealplan and double check if the user is the true owner of the meal plan, then return dto
            // if mealplan does not belong to user, redirect to mealplan home
        // if startDate param exist, search for user's mealplans if there is a match, 
            // if one is found, return dto
            // if none is found, create a new MealPlan instance and return dto
        // if no startDate Param, get current startDate and search for matching mealplan 
            // if one is found, return dto
            // if none is found, create a new mealPlan instance and return dto

        UserDto currentUser = getLoggedInUser(principal);  
        List<RecipeDto> savedRecipes = recipeService.getAllSavedRecipesByUser(currentUser.getId());
        MealPlanDto mealPlanDto;
        Calendar c = Calendar.getInstance(); // current calendar obj   
        c.setFirstDayOfWeek(Calendar.MONDAY);  // set first day from Sunday to Monday
        // if date is not on monday, set date to current Monday
        if(c.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);  
        }

        model.addAttribute("user", currentUser);
        model.addAttribute("savedRecipes", savedRecipes);
        model.addAttribute("mealTypes", mealTypes);

        if(mealPlanId != null) {
            try {
                mealPlanDto = mealPlanService.findMealPlanDtoById(mealPlanId);
                    if(mealPlanDto != null && currentUser.getId() == mealPlanDto.getUser().getId()) {

                    model.addAttribute("mealPlan", mealPlanDto);
                    resetCalendar(c, mealPlanDto.getStartDate());
                    model.addAttribute("weeklyDates", getWeeklyDates(c));
                    model.addAttribute("nextStartDate", getStartDateString(c));
                    c.add(Calendar.DATE, -14);
                    model.addAttribute("prevStartDate", getStartDateString(c));

                    return "mealPlan";
                }
            } catch(Exception e){
                return "redirect:/user/mealplans";
            }
        }

        if(paramStartDate != null) {
            // if startDate param exist, set calendar to startDate param      
            c = resetCalendar(c, paramStartDate); 
        } 

        mealPlanDto = mealPlanService.findUserMealPlanByStartDate(currentUser.getId(), getStartDateString(c), c);

        model.addAttribute("mealPlan", mealPlanDto);
        model.addAttribute("weeklyDates", getWeeklyDates(c));
        model.addAttribute("nextStartDate", getStartDateString(c));
        c.add(Calendar.DATE, -14);
        model.addAttribute("prevStartDate", getStartDateString(c));

        return "mealplan";
    }
    
    @PostMapping("/mealPlanItem")
    public String updateMealPlanItem(@ModelAttribute MealPlanItemDto mealPlanItemDto, Model model,
    Principal principal, RedirectAttributes redirectAttributes) {

        // if mealplanitem id exist, find mealplanitem by id, set recipe then update/save
        if(mealPlanItemDto.getId() != null) {
            // try {
                
                mealPlanService.updateMealPlanItem(mealPlanItemDto);
            // } catch (Exception e){
            //     return "redirect:/user/mealplans";
            // }        
        } else if(mealPlanItemDto.getRecipeId() != null) {
            // else, only update if there is a recipe it, if it doens't include a recipe id, it's just an empty dto
            // try {
                mealPlanService.saveMealPlanItem(mealPlanItemDto);
            // } catch (Exception e) {
            //     return "redirect:/user/mealplans";
            // }
        }

        // need to return meal plan id and start date string
        redirectAttributes.addAttribute("mealPlan", mealPlanItemDto.getMealPlanId());
        
        return "redirect:/user/mealplans";
    }


    @GetMapping("/shoppinglist")
    public String getShoppingList(@RequestParam("mealPlan") Long mealPlanid, Model model, Principal principal) {
        MealPlanDto mealPlan = mealPlanService.findMealPlanDtoById(mealPlanid);
        List<String> allIngredients = new ArrayList<>();
        mealPlan.getMealPlanItemsMap().forEach((key, value) -> {
            allIngredients.addAll(value.getRecipe().getIngredients()); 
        });
        System.out.println(allIngredients);

        List<IngredientDto> ingredientDtos = mapIngredientToDto(allIngredients);    

        Collections.sort(ingredientDtos, new Comparator<IngredientDto>() {
            public int compare(IngredientDto dto1, IngredientDto dto2) {
                return dto1.getName().toLowerCase().compareTo(dto2.getName().toLowerCase());
            }
        });
        

        model.addAttribute("allIngredients", allIngredients);
        model.addAttribute("ingredientDtos", ingredientDtos);

        return "shopping-list";
    }


    private List<IngredientDto> mapIngredientToDto(List<String> ingredients) {
        List<IngredientDto> dtoList = new ArrayList<>();
        for(String ingredient : ingredients) {

            String[] arr = ingredient.split(" ");

            dtoList.add(ingredientTagging(arr));
        }

        return dtoList;
    }

    private IngredientDto ingredientTagging(String[] ingredientArr) {

        IngredientDto ingredientDto = new IngredientDto();

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
                amount += ingredientArr[i] + " ";
                prevIdx = i;
            } else if (ingredientArr[i].charAt(0) == '¼' || ingredientArr[i].charAt(0) == '½') {
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

        ingredientDto.setName(name);
        ingredientDto.setAmount(amount);
        ingredientDto.setMeasure(measure);
        ingredientDto.setNote(note);

        System.out.println();
        System.out.println("Name: " + name + ", Amount: " + amount + ", Measure: " + measure + ", Note: " + note + ", Index: " + prevIdx);

        return ingredientDto;
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


    private Calendar resetCalendar(Calendar calendar, String startDate) {

        String[] str = startDate.split("_");
        calendar.set(Integer.parseInt(str[0]), getMonth(str[1]), Integer.parseInt(str[2]));
        if(calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);  
        }

        return calendar;
    }

    private String getStartDateString(Calendar calendar) {
        String startDate;
        String[] str;

        Calendar currentWeek = Calendar.getInstance();// current calendar obj   
        currentWeek.setFirstDayOfWeek(Calendar.MONDAY);  // set first day from Sunday to Monday
        // if current date is not monday, set date to current Monday 
        if(currentWeek.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            currentWeek.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);  
        }

        if(calendar.before(currentWeek)) {
            str = currentWeek.getTime().toString().split(" ");
        } else {
            str = calendar.getTime().toString().split(" ");
        }
        startDate = str[5] + "_" + str[1] + "_" + str[2];

        return startDate;
    }

    private String[][] getWeeklyDates(Calendar calendar) {
        String[][] weeklyDates = new String[7][];
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        String[] str;
        for(int i = 0; i < 7; i++) {
            str = calendar.getTime().toString().split(" ");
            weeklyDates[i] = new String[] {str[1] + " " + str[2] + ", " + str[5], days[i]};
            
            calendar.add(Calendar.DATE, 1);
        }

        return weeklyDates;
    }

    private int getMonth(String month) {
        switch (month) {
            case "Jan": return 0;
            case "Feb": return 1;
            case "Mar": return 2;
            case "Apr": return 3;
            case "May": return 4;
            case "Jun": return 5;
            case "Jul": return 6;
            case "Aug": return 7;
            case "Sep": return 8;
            case "Oct": return 9;
            case "Nov": return 10;
            case "Dec": return 11;
        }
        return 0;
    }

    private UserDto getLoggedInUser(Principal principal) {
        return userService.findUserByEmail(principal.getName());
    }

    
}