package com.mingcapstone.quickmealplanner.control;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user/mealplans")
public class MealPlanController {

    @GetMapping
    public String mealPlans() {
        return "mealplan";
    }
}
