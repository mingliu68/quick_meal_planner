let activeMenuItem = null;

// recipe id for the recipe on page
const recipeId = document.getElementById("recipe").dataset.activerecipeid;

const startDate = document.getElementById("mealplan").dataset.startdate;

const sideBarMenuItems = document.getElementsByClassName("sideBar-menu-item");
const menuItems = [...sideBarMenuItems]
const base = "http://localhost:8080/";

const headers = {
    'Content-Type':'application/json'
}

menuItems.forEach(item => {
    item.addEventListener('click', () => {
        activeMenuItem = item;
        if(activeMenuItem.dataset.recipeid != recipeId) {
            updateMealPlanItem();
        }
        // console.log("Currently Active: " + activeMenuItem.dataset.mealtype);
       
    })
});


async function updateMealPlanItem() {

    const obj =  {
        id : activeMenuItem.dataset.mealplanitemid,
        mealPlanId: activeMenuItem.dataset.mealplanid,
        mealType: activeMenuItem.dataset.mealtype,
        recipeId: recipeId
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
        window.location.href=`${base}user/recipes/recipe?recipeId=${recipeId}&startDate=${startDate}&mealPlan=${activeMenuItem.dataset.mealplanid}`;
    }
}