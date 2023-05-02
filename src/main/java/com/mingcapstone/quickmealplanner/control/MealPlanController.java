package com.mingcapstone.quickmealplanner.control;

import java.security.Principal;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mingcapstone.quickmealplanner.dto.MealPlanDto;
import com.mingcapstone.quickmealplanner.dto.MealPlanItemDto;
import com.mingcapstone.quickmealplanner.entity.MealPlan;
import com.mingcapstone.quickmealplanner.entity.MealPlanItem;
import com.mingcapstone.quickmealplanner.entity.Recipe;
import com.mingcapstone.quickmealplanner.entity.User;
import com.mingcapstone.quickmealplanner.repository.MealPlanRepository;
import com.mingcapstone.quickmealplanner.service.MealPlanItemService;
import com.mingcapstone.quickmealplanner.service.MealPlanService;
import com.mingcapstone.quickmealplanner.service.RecipeService;
import com.mingcapstone.quickmealplanner.service.RecipeServiceImpl;
import com.mingcapstone.quickmealplanner.service.UserService;

import jakarta.validation.Valid;


@Controller
@RequestMapping("/user/mealplans")
public class MealPlanController {


    private MealPlanService mealPlanService;
    private UserService userService;
    private RecipeService recipeService;

    @Autowired
    public MealPlanController(MealPlanService mealPlanService, UserService userService, RecipeService recipeService) {
        this.mealPlanService = mealPlanService;
        this.userService = userService;
        this.recipeService = recipeService;
    }
    
    @Autowired
    MealPlanItemService mealPlanItemService;

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

        User currentUser = getLoggedInUser(principal);  
        MealPlanDto mealPlanDto;
        Calendar c = Calendar.getInstance(); // current calendar obj   
        c.setFirstDayOfWeek(Calendar.MONDAY);  // set first day from Sunday to Monday
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);  // set date to current Monday

        
        if(mealPlanId != null) {
            try {
                MealPlan mealPlan = mealPlanService.findMealPlanById(mealPlanId);
                if(mealPlan != null && currentUser.getMealPlans().contains(mealPlan)) {
                    mealPlanDto = mealPlanService.mapToMealPlanDto(mealPlan);
                    model.addAttribute("user", currentUser);
                    model.addAttribute("mealPlan", mealPlanDto);
                    resetCalendar(c, mealPlan.getStartDate());
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
            resetCalendar(c, paramStartDate); 
            System.out.println(c.getTime());
            c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);  
        } 

        mealPlanDto = mealPlanService.findUserMealPlanByStartDate(currentUser, getStartDateString(c), c);

        model.addAttribute("user", currentUser);
        model.addAttribute("mealPlan", mealPlanDto);
        model.addAttribute("weeklyDates", getWeeklyDates(c));
        model.addAttribute("nextStartDate", getStartDateString(c));
        c.add(Calendar.DATE, -14);
        model.addAttribute("prevStartDate", getStartDateString(c));

        return "mealplan";

    }
    
    // testing

    @PostMapping("/mealPlanItem")
    public String updateMealPlanItem(@ModelAttribute MealPlanItemDto mealPlanItemDto, Model model,
    Principal principal, RedirectAttributes redirectAttributes) {

        MealPlan mealPlan = mealPlanService.findMealPlanById(mealPlanItemDto.getMealPlanId());
        mealPlanItemDto.setMealPlan(mealPlan);
        // if mealplanitem id exist, find mealplanitem by id, set recipe then update/save
        if(mealPlanItemDto.getId() != null) {
            // try {
                
                mealPlanItemService.updateMealPlanItem(mealPlanItemDto);
            // } catch (Exception e){
            //     return "redirect:/user/mealplans";
            // }        
        } else if(mealPlanItemDto.getRecipeId() != null) {
            // else, only update if there is a recipe it, if it doens't include a recipe id, it's just an empty dto
            // try {
                mealPlanItemService.saveMealPlanItem(mealPlanItemDto);
            // } catch (Exception e) {
            //     return "redirect:/user/mealplans";
            // }
        }
       


        // need to return meal plan id and start date string
        System.out.println(mealPlanItemDto.getMealType());
        System.out.println(mealPlanItemDto.getMealPlanId());
        System.out.println(mealPlanItemDto.getRecipeId());
        redirectAttributes.addAttribute("mealPlan", mealPlanItemDto.getMealPlanId());
        
        return "redirect:/user/mealplans";
    }

    // private void setMealPlanDtoAttributes(Model model, MealPlanDto mealPlanDto) {
    //     for(int i = 0; i < mealPlanDto.getMealPlanItemsDtos().size(); i++) {
    //         model.addAttribute("mealPlanItem" + i, mealPlanDto.getMealPlanItemsDtos().get(i));
    //     }
    // }

    private void resetCalendar(Calendar calendar, String startDate) {
        String[] str = startDate.split("_");
        calendar.set(Integer.parseInt(str[0]), getMonth(str[1]), Integer.parseInt(str[2]));
    }

    private String getStartDateString(Calendar calendar) {
        
        String[] str = calendar.getTime().toString().split(" ");
        String startDate = str[5] + "_" + str[1] + "_" + str[2];
        return startDate;
    }

    private String[] getWeeklyDates(Calendar calendar) {
        String[] weeklyDates = new String[7];
        String[] str;
        for(int i = 0; i < 7; i++) {
            str = calendar.getTime().toString().split(" ");
            weeklyDates[i] = str[1] + " " + str[2] + ", " + str[5];
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

    private User getLoggedInUser(Principal principal) {
        return userService.findUserByEmail(principal.getName());
    }
}