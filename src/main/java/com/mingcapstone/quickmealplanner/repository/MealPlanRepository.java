package com.mingcapstone.quickmealplanner.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mingcapstone.quickmealplanner.entity.MealPlan;

public interface MealPlanRepository extends JpaRepository<MealPlan, Long> {

}
