package com.mingcapstone.quickmealplanner.control;

import java.security.Principal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mingcapstone.quickmealplanner.dto.OptionsDto;
import com.mingcapstone.quickmealplanner.dto.RecipeDto;
import com.mingcapstone.quickmealplanner.dto.UserDto;
import com.mingcapstone.quickmealplanner.service.RecipeService;
import com.mingcapstone.quickmealplanner.service.UserService;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;

import jakarta.annotation.PostConstruct;

@Controller
@RequestMapping("/user")
public class ChatGPTController {

    @Value("${chatgptToken}")
    private String chatgptToken;

    private UserService userService;

    private RecipeService recipeService;

    private String basePromptString = "recipe should have name, directions, "
                                    + "and ingredients and return it in json with name in string, "
                                    + "ingredients in array, and directions in array. "
                                    + "if directions is in a single string, split it up into an array. "
                                    + "Only provide a  RFC8259 compliant JSON response in the "
                                    + "following format: "
                                    + "{\"name\":\"name of recipe, cannot be null\", "
                                    + "\"ingredients\": [\"ingredients in array, cannot be null, each ingredient must be enclosed with double quotes \"], "
                                    + "\"directions\":[\"directions in array, cannot be null, each direction must be enclosed with double quotes\"]} "
                                    + "Must user proper brackets and include close marker } for json object."
                                    + "The JSON response:";

    @Autowired
    public ChatGPTController(UserService userService, RecipeService recipeService) {
        this.userService = userService;
        this.recipeService = recipeService;
        initOpenAI();
    }

    @PostConstruct
    private void initOpenAI() {
        if (chatgptToken != null)
            openAiService = new OpenAiService(chatgptToken, Duration.ofSeconds(30));
    }

    private OpenAiService openAiService;

    @PostMapping("/findRecipeWithOptions")
    public String findRecipeWithOptions(@ModelAttribute("optionsDto") OptionsDto optionsDto, Model model,
            Principal principal) {

        String prompt = "I need a quick meal recipe that include the following ingredients "
                + optionsDto.getIngredient1()
                + ", " + optionsDto.getIngredient2()
                + ", " + optionsDto.getIngredient3()
                + "if any of the given ingredients are improper or not part of an actual food group, "
                + "or is a domesticated animal, omit the ingredient in search."
                + basePromptString;

        String jsonString = getResponseJsonString(prompt);

        RecipeDto recipe = null;

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            recipe = mapper.readValue(jsonString, RecipeDto.class);
        } catch (Exception e) {

            System.out.println(e.getMessage());
        }

        UserDto user = getLoggedInUser(principal);

        // split direction by "."
        System.out.println(recipe == null ? "recipe is null" : "This recipe has " + recipe.getDirections().size() + " steps");
        if(recipe != null && recipe.getDirections().size() == 1) splitDirections(recipe);
        System.out.println(recipe == null ? "recipe is null" : "This recipe has " + recipe.getDirections().size() + " steps");

        model.addAttribute("user", user);

        model.addAttribute("recipe", recipe);

        return "get-recipe";

    }

    @PostMapping("/findRecipeWithRestriction")
    public String findRecipeWithRestriction(@ModelAttribute("optionsDto") OptionsDto optionsDto, Model model,
            Principal principal) {

        String prompt = "I need a quick meal recipe of the following dietary restriction "
                + optionsDto.getRestriction() + ". "
                + basePromptString;

        String jsonString = getResponseJsonString(prompt);

        RecipeDto recipe = null;

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            recipe = mapper.readValue(jsonString, RecipeDto.class);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        UserDto user = getLoggedInUser(principal);

        // split direction by "."
        System.out.println(recipe == null ? "recipe is null" : "This recipe has " + recipe.getDirections().size() + " steps");
        if(recipe != null && recipe.getDirections().size() == 1) splitDirections(recipe);
        System.out.println(recipe == null ? "recipe is null" : "This recipe has " + recipe.getDirections().size() + " steps");

        model.addAttribute("user", user);

        model.addAttribute("recipe", recipe);

        return "get-recipe";

    }

    // @GetMapping("/findRecipe")
    // public String findRecipe(Model model, Principal principal) throws
    // JsonMappingException, JsonProcessingException{

    @GetMapping("/findRecipe")
    public String findRecipe(Model model, Principal principal) {

        String prompt = "Give me a quick meal recipe in under 30 mins "
                        + basePromptString;

        String jsonString = getResponseJsonString(prompt);

        RecipeDto recipe = null;

        try {
            ObjectMapper mapper = new ObjectMapper();

            mapper.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            recipe = mapper.readValue(jsonString, RecipeDto.class);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        UserDto user = getLoggedInUser(principal);
        
        // split direction by "."
        System.out.println(recipe == null ? "recipe is null" : "This recipe has " + recipe.getDirections().size() + " steps");
        if(recipe != null && recipe.getDirections().size() == 1) splitDirections(recipe);
        System.out.println(recipe == null ? "recipe is null" : "This recipe has " + recipe.getDirections().size() + " steps");

        model.addAttribute("user", user);

        model.addAttribute("recipe", recipe);

        return "get-recipe";
    }

    private String getResponseJsonString(String prompt) {
        CompletionRequest completionRequest = CompletionRequest.builder()
                .prompt(prompt)
                .model("text-davinci-003")
                .temperature(1.0)
                .echo(false)
                .maxTokens(500)
                .build();

        String jsonString = openAiService.createCompletion(completionRequest).getChoices().get(0).getText();
        jsonString = jsonString.replaceAll("[\\n\\t]", " ");
        System.out.println("---------------------");
        System.out.println("jsonString: " + jsonString);

        return jsonString;
    }

    private UserDto getLoggedInUser(Principal principal) {
        return userService.findUserByEmail(principal.getName());
    }

    private void splitDirections(RecipeDto recipe) {
         // split direction if length is 1
         System.out.println("This recipe has " + recipe.getDirections().size() + " steps");
         List<String> newDirections = new ArrayList<>();
         for(String step : recipe.getDirections()) {
            List<String> splitStep = Arrays.asList(step.split("\\."));
            newDirections.addAll(splitStep);
         }
         recipe.setDirections(newDirections);
        //  if(recipe != null && recipe.getDirections().size() == 1) {
        //      System.out.println("This recipe has 1 step, converted to multiple.");
        //     List<String> newDirections = Arrays.asList(recipe.getDirections().get(0).split("\\."));
        //     recipe.setDirections(newDirections);
        //  }
         System.out.println("This recipe has " + recipe.getDirections().size() + " steps");
    }

}
