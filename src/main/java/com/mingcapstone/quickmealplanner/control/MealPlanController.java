package com.mingcapstone.quickmealplanner.control;

import java.security.Principal;
import java.util.Calendar;
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
    public String getShoppingList(@RequestParam("mealPlanId") Long mealPlanid, Model model, Principal principal) {
        

        return "shopping-list";
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