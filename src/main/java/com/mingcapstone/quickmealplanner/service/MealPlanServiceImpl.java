package com.mingcapstone.quickmealplanner.service;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mingcapstone.quickmealplanner.dto.MealPlanDto;
import com.mingcapstone.quickmealplanner.dto.MealPlanItemDto;
import com.mingcapstone.quickmealplanner.entity.MealPlan;
import com.mingcapstone.quickmealplanner.entity.MealPlanItem;
import com.mingcapstone.quickmealplanner.entity.Recipe;
import com.mingcapstone.quickmealplanner.entity.User;
import com.mingcapstone.quickmealplanner.repository.MealPlanItemRepository;
import com.mingcapstone.quickmealplanner.repository.MealPlanRepository;
import com.mingcapstone.quickmealplanner.repository.UserRepository;

@Service
public class MealPlanServiceImpl implements MealPlanService {
    
    private MealPlanRepository mealPlanRepository;
    private MealPlanItemRepository mealPlanItemRepository;
    private MealPlanItemService mealPlanItemService;
    private UserRepository userRepository;
    private RecipeService recipeService;
    private UserService userService;


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
    public MealPlanServiceImpl(MealPlanRepository mealPlanRepository, MealPlanItemRepository mealPlanItemRepository, UserRepository userRepository, MealPlanItemService mealPlanItemService, RecipeService recipeService, UserService userService) {
        this.mealPlanRepository = mealPlanRepository;
        this.mealPlanItemRepository = mealPlanItemRepository;
        this.userRepository = userRepository;
        this.mealPlanItemService = mealPlanItemService;
        this.recipeService = recipeService;
        this.userService = userService;
    }

    @Override
    public MealPlan saveMealPlan(MealPlanDto mealPlanDto){
        MealPlan mealPlan = new MealPlan();
        mealPlan.setUser(mealPlanDto.getUser());
        mealPlan.setStartDate(mealPlanDto.getStartDate());
        mealPlan.setPrev(mealPlanDto.getPrev());
        mealPlan.setNext(mealPlanDto.getNext());
        mealPlan.setMealPlanItems(mealPlanDto.getMealPlanItems());
        return mealPlanRepository.save(mealPlan);
    }

    @Override
    public MealPlan findMealPlanById(Long id){
        Optional<MealPlan> result = mealPlanRepository.findById(id);
        MealPlan mealPlan = null;
        if(result.isPresent()) {
            mealPlan = result.get();
        } else {
            throw new RuntimeException("Meal Plan Not Found.");
        }



        return mealPlan;
    }

    @Override
    public MealPlanDto findMealPlanDtoById(Long id){
        Optional<MealPlan> result = mealPlanRepository.findById(id);
        MealPlan mealPlan = null;
        if(result.isPresent()) {
            mealPlan = result.get();
        } else {
            throw new RuntimeException("Meal Plan Not Found.");
        }

        return mapToMealPlanDto(mealPlan);
    }



    @Override
    public MealPlanDto findUserMealPlanByStartDate(Long userId, String startDate, Calendar calendar) {
        User user = userService.findUserById(userId);
        // find a meal plan in user.mealplans that matches startDate
        for(MealPlan mealPlan : user.getMealPlans()) {
            // if a meal plan exist, return mealplan dto
            if(mealPlan.getStartDate().equals(startDate)) {
                return mapToMealPlanDto(mealPlan);
            }
        }
        
        // if not creating one, save, add to user, save user, then return a mealplan dto
        MealPlan mealPlan = new MealPlan(user, startDate);
        setPreAndNext(user, mealPlan, calendar);
        MealPlan dbMealPlan = mealPlanRepository.save(mealPlan);
        user.addMealPlan(dbMealPlan);
        userRepository.save(user);
        return mapToMealPlanDto(dbMealPlan);
    }

    private void setPreAndNext(User user, MealPlan mealPlan, Calendar calendar) {

        // get next monday's calendar and convert to startDate string
        calendar.add(Calendar.DATE, 7);
        String nextString = getStartDateString(calendar);

        // get prev monday's calendar and convert to startDate string
        calendar.add(Calendar.DATE, -14);
        String preString = getStartDateString(calendar);

        // reset calendar to current monday for future operation
        calendar.add(Calendar.DATE, 7);

        for(MealPlan mp : user.getMealPlans()) {
            if(mp.getStartDate().equals(nextString)) {
                mealPlan.setNext(mp);
                mp.setPrev(mealPlan);
            } 
            if(mp.getStartDate().equals(preString)) {
                mealPlan.setPrev(mp);
                mp.setNext(mealPlan);
            }
        }
    }

    private String getStartDateString(Calendar calendar) {
        
        String[] str = calendar.getTime().toString().split(" ");
        String startDate = str[5] + "_" + str[1] + "_" + str[2];
        return startDate;
    }

    @Override
    public MealPlanDto mapToMealPlanDto(MealPlan mealPlan) {
        MealPlanDto mealPlanDto = new MealPlanDto();
        mealPlanDto.setId(mealPlan.getId());
        mealPlanDto.setUser(mealPlan.getUser());
        mealPlanDto.setStartDate(mealPlan.getStartDate());
        mealPlanDto.setNext(mealPlan.getNext());
        mealPlanDto.setPrev(mealPlan.getPrev());
        // List<MealPlanItemDto> itemDtos= new ArrayList<>();
        // // get 14 dtos for both emtpies and existing mealplanitems
        // for(String mealType : mealTypes) {
        //     itemDtos.add(mealPlanItemService.findMealPlanItemByMealPlanAndMealType(mealPlan, mealType));
        // }
        // mealPlanDto.setMealPlanItemsDtos(itemDtos);
        HashMap<String, MealPlanItem> items = new HashMap<>();
        for(MealPlanItem item : mealPlan.getMealPlanItems()) {
            items.put(item.getMealType(), item);
        }   
        mealPlanDto.setMealPlanItemsMap(items);
        System.out.println("MealPlanDto ItemsMap size: " + mealPlanDto.getMealPlanItemsMap().size());
        System.out.println("MealPlan Items size: " + mealPlan.getMealPlanItems().size());
        return mealPlanDto;
    } 
    
    // not mapping to DTO since we won't be sending the list to FE
    @Override
    public List<MealPlan> findAllMealPlans(){
        return mealPlanRepository.findAll();
    }

    @Override
    public void deleteMealPlan(Long id){
       mealPlanRepository.deleteById(id);
    }

    @Override
    public MealPlan updateMealPlan(MealPlanDto mealPlanDto){
        MealPlan mealPlan = new MealPlan();
        mealPlan.setId(mealPlanDto.getId());
        mealPlan.setUser(mealPlanDto.getUser());
        mealPlan.setStartDate(mealPlanDto.getStartDate());
        mealPlan.setPrev(mealPlanDto.getPrev());
        mealPlan.setNext(mealPlanDto.getNext());
        mealPlan.setMealPlanItems(mealPlanDto.getMealPlanItems());
        return mealPlanRepository.save(mealPlan);
    }
    
    @Override
    public MealPlan addMealPlanItem(MealPlan mealPlan, MealPlanItem mealPlanItem){
        mealPlan.addMealPlanItem(mealPlanItem);
        MealPlan dbMealPlan = mealPlanRepository.save(mealPlan);
        return dbMealPlan;
    }
    
    @Override
    public MealPlan removeMealPlanItem(MealPlan mealPlan, MealPlanItem mealPlanItem){
        mealPlan.removeMealPlanItem(mealPlanItem);
        MealPlan dbMealPlan = mealPlanRepository.save(mealPlan);
        return dbMealPlan;
    }


     // saving a brand new meal plan item
     @Override
     public MealPlanItem saveMealPlanItem(MealPlanItemDto mealPlanItemDto){
        //  System.out.println(mealPlanItemDto.toString());
 
         MealPlanItem mealPlanItem = new MealPlanItem();
         MealPlan mealPlan = findMealPlanById(mealPlanItemDto.getMealPlanId());
 
        //  System.out.println("MealPlanID from service: " + mealPlan.getId());
 
         mealPlanItem.setMealPlan(mealPlan);
         mealPlanItem.setMealType(mealPlanItemDto.getMealType());
         Recipe recipe = recipeService.findById(mealPlanItemDto.getRecipeId());
         mealPlanItem.setRecipe(recipe);
         MealPlanItem dbMealPlanItem = mealPlanItemRepository.save(mealPlanItem);
         mealPlan.addMealPlanItem(dbMealPlanItem);
         mealPlanRepository.save(mealPlan);
         return dbMealPlanItem;
     }


    @Override
    public MealPlanItem updateMealPlanItem(MealPlanItemDto mealPlanItemDto){
        // mealplanitem update method only update recipe.  all other stays the same
        MealPlanItem mealPlanItem = mealPlanItemService.findMealPlanItemById(mealPlanItemDto.getId());

        if(mealPlanItemDto.getRecipeId() == null) {
            mealPlanItem.setRecipe(null);
        } else if (mealPlanItemDto.getRecipeId() != mealPlanItem.getRecipe().getId()) {
            Recipe recipe = recipeService.findById(mealPlanItemDto.getRecipeId());
            mealPlanItem.setRecipe(recipe);
        }
        
        MealPlanItem dbMealPlanItem = mealPlanItemRepository.save(mealPlanItem);
        return dbMealPlanItem;
    }
 
}
