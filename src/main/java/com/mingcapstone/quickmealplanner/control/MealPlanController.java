package com.mingcapstone.quickmealplanner.control;

import java.security.Principal;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mingcapstone.quickmealplanner.dto.MealPlanDto;
import com.mingcapstone.quickmealplanner.entity.MealPlan;
import com.mingcapstone.quickmealplanner.entity.User;
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

    @GetMapping
    public String mealPlans(Model model, Principal principal) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        String[] str = c.getTime().toString().split(" ");
        String startDate = str[1] + str[2] + str[5];
        User currentUser = getLoggedInUser(principal);
        MealPlanDto mealPlanDto = mealPlanService.findMealPlanByStartDateByUser(currentUser, startDate, c);
        model.addAttribute("user", currentUser);
        model.addAttribute("mealPlanDto", mealPlanDto);
        System.out.println(mealPlanDto.getMenuItems().toString());
        return "mealplan";
    }
    

    private User getLoggedInUser(Principal principal) {
        return userService.findUserByEmail(principal.getName());
    }
}
