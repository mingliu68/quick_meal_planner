const searchButtons = document.getElementsByClassName("recipe_search_button");

// let activeButton = null;

[...searchButtons].forEach(button => {
    button.addEventListener("click", () => {
        activeButton = button;
        disableAllButtons();
        const spinnerDiv = document.createElement("div");
        spinnerDiv.className = "spinner-border";
        spinnerDiv.role = "status";
        const spinnerSpan = document.createElement("span");
        spinnerSpan.className = "visually-hidden";
        spinnerSpan.innerText = "Loading...";
        spinnerDiv.appendChild(spinnerSpan);
        activeButton.appendChild(spinnerDiv);
    }, {once: true})
})

const disableAllButtons = () => {
    [...searchButtons].forEach(button => {
       button.setAttribute("disabled", "disabled");
    })
}