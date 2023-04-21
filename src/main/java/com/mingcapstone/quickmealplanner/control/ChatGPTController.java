package com.mingcapstone.quickmealplanner.control;

import java.security.Principal;

import org.hibernate.annotations.SourceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mingcapstone.quickmealplanner.dto.RecipeDto;
import com.mingcapstone.quickmealplanner.entity.Recipe;
import com.mingcapstone.quickmealplanner.entity.User;
import com.mingcapstone.quickmealplanner.service.RecipeService;
import com.mingcapstone.quickmealplanner.service.UserService;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;

import jakarta.annotation.PostConstruct;
import jakarta.websocket.server.PathParam;

@Controller
@RequestMapping("/user")
public class ChatGPTController {
    

    @Value("${chatgptToken}")
    private String chatgptToken;
    
    
    public ChatGPTController() {
        initOpenAI();
    }
    @PostConstruct
    private void initOpenAI(){
        if (chatgptToken != null)
            openAiService = new OpenAiService(chatgptToken);
    }


    private OpenAiService openAiService ;

    @Autowired 
    UserService userService;

    @Autowired
    RecipeService recipeService;

    
    @GetMapping("/findRecipe")
    public String findRecipe(Model model, Principal principal) throws JsonMappingException, JsonProcessingException{
        
        CompletionRequest completionRequest = CompletionRequest.builder()
        .prompt("I need a quick meal in under 30 minutes with directions, and ingredient list and return it in json with name in string, ingredients in array, and directions in array as keys in lower cases with double quotes")
        .model("text-davinci-003")
        .temperature(1.0)
        .echo(false)
        .maxTokens(350)
        .build();
                
        String jsonString = openAiService.createCompletion(completionRequest).getChoices().get(0).getText();
        
        RecipeDto recipe = new ObjectMapper().readValue(jsonString, RecipeDto.class);
        
        User user = getLoggedInUser(principal);
        
        model.addAttribute("user", user);

        model.addAttribute("recipe", recipe);
        
        return "get-recipe";
    }  
    
   
    private User getLoggedInUser(Principal principal) {
        return userService.findUserByEmail(principal.getName());
    }   
    

  




    
}
