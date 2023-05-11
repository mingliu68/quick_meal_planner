package com.mingcapstone.quickmealplanner.control;

import java.security.Principal;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mingcapstone.quickmealplanner.dto.MealPlanDto;
import com.mingcapstone.quickmealplanner.dto.RecipeDto;
import com.mingcapstone.quickmealplanner.dto.UserDto;
import com.mingcapstone.quickmealplanner.service.MealPlanService;
import com.mingcapstone.quickmealplanner.service.RecipeService;
import com.mingcapstone.quickmealplanner.service.UserService;

@Controller
@RequestMapping("/user/recipes")
public class RecipeController {

    private RecipeService recipeService;
    private UserService userService;
    private MealPlanService mealPlanService;

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
    public RecipeController(RecipeService recipeService, UserService userService, MealPlanService mealPlanService) {
        this.recipeService = recipeService;
        this.userService = userService;
        this.mealPlanService = mealPlanService;
    }

    @GetMapping
    public String recipes(Model model, Principal principal) {
        UserDto user = getLoggedInUser(principal);
        model.addAttribute("user", user);

        return "recipes";
    }

    @GetMapping("/recipe")
    public String recipe(
        @RequestParam("recipeId") Long recipeId, 
        @RequestParam(name="startDate", required=false) String paramStartDate, 
        @RequestParam(name="mealPlan", required=false) Long mealPlanId, 
        Model model, 
        Principal principal,
        RedirectAttributes redirectAttributes
        ) {
        UserDto user = getLoggedInUser(principal);

        RecipeDto recipe = recipeService.findDtoById(recipeId);
        MealPlanDto mealPlanDto;

        Calendar c = Calendar.getInstance(); // current calendar obj   
        c.setFirstDayOfWeek(Calendar.MONDAY);  // set first day from Sunday to Monday
               
        // if date is not on monday, set date to current Monday
        if(c.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);  
        }

        Boolean savedByUser = userService.recipeSavedByUser(recipeId, user.getId());

        model.addAttribute("mealTypes", mealTypes);
        model.addAttribute("user", user);
        model.addAttribute("recipe", recipe);
        model.addAttribute("savedByUser", savedByUser);

        if(mealPlanId != null) {
            try {
                mealPlanDto = mealPlanService.findMealPlanDtoById(mealPlanId);
                    if(mealPlanDto != null && user.getId() == mealPlanDto.getUser().getId()) {

                    model.addAttribute("mealPlan", mealPlanDto);
                    resetCalendar(c, mealPlanDto.getStartDate());
                    model.addAttribute("weeklyDates", getWeeklyDates(c));
                    model.addAttribute("nextStartDate", getStartDateString(c));
                    c.add(Calendar.DATE, -14); 
                    model.addAttribute("prevStartDate", getStartDateString(c));

                    return "recipe";
                }
            } catch(Exception e){
                redirectAttributes.addAttribute("recipeId", recipeId);
                redirectAttributes.addAttribute("startDate", getStartDateString(c));
                return "redirect:/user/recipes/recipe";
            }
        }

        if(paramStartDate != null) {
            // if startDate param exist, set calendar to startDate param
            resetCalendar(c, paramStartDate); 
        } 

        mealPlanDto = mealPlanService.findUserMealPlanByStartDate(user.getId(), getStartDateString(c), c);
        model.addAttribute("mealPlan", mealPlanDto);
        model.addAttribute("weeklyDates", getWeeklyDates(c));
        model.addAttribute("nextStartDate", getStartDateString(c));
        c.add(Calendar.DATE, -14);
        model.addAttribute("prevStartDate", getStartDateString(c));

        return "recipe";
    }

    @GetMapping("/otherRecipes")
    public String otherRecipe(Model model, Principal principal) {
        UserDto user = getLoggedInUser(principal);
        List<RecipeDto> recipes = recipeService.getRecentRecipesNotByUser(user.getId());
        model.addAttribute("recipes", recipes);
        model.addAttribute("user", user);

        return "other-recipes";
    }

    @GetMapping("/removeRecipeFromList")
    public String removeRecipeFromList(@RequestParam("recipeId") Long recipeId, Principal principal) {

        UserDto user = getLoggedInUser(principal);
        userService.removeRecipeFromList(recipeId, user.getId());
        return "redirect:/user/recipes";
    }

    @GetMapping("/addRecipeToList")
    public String addRecipeTolist(@RequestParam("recipeId") Long recipeId, Principal principal) {
        UserDto user = getLoggedInUser(principal);
        userService.addRecipeToList(recipeId, user.getId());
        return "redirect:/user/recipes";
    }

    private UserDto getLoggedInUser(Principal principal) {
        return userService.findUserByEmail(principal.getName());
    }

    private void resetCalendar(Calendar calendar, String startDate) {
        // {year, month, day}
        String[] str = startDate.split("_");
        calendar.set(Integer.parseInt(str[0]), getMonth(str[1]), Integer.parseInt(str[2]));
        if(calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);  
        }
    }

    private String getStartDateString(Calendar calendar) {
        String startDate;
        String[] str;

        Calendar currentWeek = Calendar.getInstance();// current calendar obj   
        currentWeek.setFirstDayOfWeek(Calendar.MONDAY);  // set first day from Sunday to Monday
        currentWeek.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);  // set date to current Monday
        
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
            // {month, day, day of the week}
            weeklyDates[i] = new String[] {str[1] , str[2] , days[i]};
            
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
}
