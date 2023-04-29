package com.mingcapstone.quickmealplanner.control;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/user/mealplans")
public class MealPlanController {

    @GetMapping
    public String mealPlans(Model model, Principal principal) {
        





        return "mealplan";
    }
}
