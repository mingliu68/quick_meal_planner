package com.mingcapstone.quickmealplanner.control;

import java.security.Principal;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mingcapstone.quickmealplanner.dto.MealPlanDto;
import com.mingcapstone.quickmealplanner.entity.MealPlan;
import com.mingcapstone.quickmealplanner.entity.User;
import com.mingcapstone.quickmealplanner.repository.MealPlanRepository;
import com.mingcapstone.quickmealplanner.service.MealPlanService;
import com.mingcapstone.quickmealplanner.service.UserService;


@Controller
@RequestMapping("/user/mealplans")
public class MealPlanController {


    private MealPlanService mealPlanService;
    private UserService userService;

    @Autowired
    public MealPlanController(MealPlanService mealPlanService, UserService userService) {
        this.mealPlanService = mealPlanService;
        this.userService = userService;
    }

    // get mealplan by using startdate
    @GetMapping
    public String mealPlans(
        @RequestParam(name="startDate", required=false) String paramStartDate, 
        @RequestParam(name="mealPlan", required=false) Long mealPlanId, 
        Model model, Principal principal) {
        
        // if id param exist, 
            // find mealplan and double check if the user is the true owner of the meal plan, then return dto
            // if mealplan does not belong to user, continue with following if statements
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
                    c.add(Calendar.DATE, 7);
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
        c.add(Calendar.DATE, 7);
        model.addAttribute("nextStartDate", getStartDateString(c));
        c.add(Calendar.DATE, -14);
        model.addAttribute("prevStartDate", getStartDateString(c));
        
        return "mealplan";

    }
    
    private void resetCalendar(Calendar calendar, String startDate) {
        String[] str = startDate.split("_");
        calendar.set(Integer.parseInt(str[0]), getMonth(str[1]), Integer.parseInt(str[2]));
    }

    private String getStartDateString(Calendar calendar) {
        
        String[] str = calendar.getTime().toString().split(" ");
        String startDate = str[5] + "_" + str[1] + "_" + str[2];
        return startDate;
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