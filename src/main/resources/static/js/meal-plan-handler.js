let activeMenuItem = null;

let activeRecipeButton = null;

const base = "http://localhost:8080/";

const headers = {
    'Content-Type':'application/json'
}

const modal = document.getElementById("savedRecipes");

const modalBackdrop = document.getElementsByClassName('modal-backdrop');

const updateButton = document.getElementById("update-button");

const menuItems = document.getElementsByClassName("menu-item");

const recipeButtons = document.getElementsByClassName("recipe-button");


[...menuItems].forEach(item => {
    item.addEventListener('click', () => {
        activeMenuItem = item;
        activeRecipeButton = null;
        updateButton.setAttribute("data-dismiss", "modal");
        [...recipeButtons].forEach(button => {
            if(item.dataset.recipeid != null && button.dataset.recipeid == item.dataset.recipeid) {
                button.classList.add("active")
            } else if(button.classList.contains("active")) {
                button.classList.remove("active");
            }
        })
        console.log("currently active: " + item.dataset.mealtype);
    })
});

[...recipeButtons].forEach(button => {
    button.addEventListener("click", () => {
        if(activeRecipeButton != null) {
            activeRecipeButton.classList.remove("selected");
        }
        activeRecipeButton = button;
        button.classList.add("selected");
        updateButton.removeAttribute("data-dismiss", "modal");
        console.log("currently active recipe button: " + button.dataset.recipeid);
    })
});

updateButton.addEventListener("click", () => {
    if(activeRecipeButton != null) {
        updateMealPlanItem();
    }
})

async function updateMealPlanItem() {

    const obj =  {
        id : activeMenuItem.dataset.recipeid,
        mealPlanId: activeMenuItem.dataset.mealplanid,
        mealType: activeMenuItem.dataset.mealtype,
        recipeId: activeRecipeButton.dataset.recipeid
    }
    // console.log(obj);
    const response = await fetch(`${base}api/mealPlanItem`, {
        method: "POST",
        body: JSON.stringify(obj),
        headers: headers
    })
    .catch(error => console.error(error.message))
    // console.log(response);
    if(response.status == 200) {
        window.location.href=`${base}user/mealplans?mealPlan=${activeMenuItem.dataset.mealplanid}`;
    }
}