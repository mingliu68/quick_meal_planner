
// const userId = document.getElementById("userId").innerText;
const recipeName = document.getElementById("recipeName").innerText;
const directions = document.getElementsByClassName("directions");
const ingredients = document.getElementsByClassName("ingredients");
const saveButton = document.getElementById("saveButton");
const recipeObj = {
    name : "",
    ingredients: [],
    directions : []
};

saveButton.addEventListener("click", (e)=>{
    recipeObj.name = recipeName;
    for(const ingredient of ingredients) {
        recipeObj.ingredients.push(ingredient.innerText);
    }
    for(const direction of directions) {
        recipeObj.directions.push(direction.innerText);
    }
    addRecipe(recipeObj);
})

const base = "http://localhost:8080/";

const headers = {
    'Content-Type':'application/json'
}

async function addRecipe(obj) {
    
    // need updating routes / urls

    const response = await fetch(`${base}api/recipe`, {
        method: "POST",
        body: JSON.stringify(obj),
        headers: headers
    })
    .catch(error => console.error(error.message))
    const json = await response.json();
    if(response.status == 200) {
        window.location.href=`${base}user/recipes/recipe?recipeId=${json.id}`;
    }
}