const historyTable = document.getElementById("history")

/**
 * adds all data from db to html table
 * 
 * must be called only once: at the launch of website 
 */
function getHistory() {
    fetch("http://localhost:8080/history")
    .then(response => {
        console.log(response);
        return response.json();
    }).then(async (json) => {
        await json.reverse().forEach(expression => {
            addToHistoryTable(expression.exprString, expression.value, -1);
        });
    })
}

window.onload = getHistory;

/**
 * adds to html table
 */
function addToHistoryTable(exprString, value, ind = 0) {
    const row = historyTable.insertRow(ind);
    const exprCell = row.insertCell();
    const expr = document.createElement('span');
    expr.classList.add("is-clickable");
    expr.setAttribute("data-expression", exprString)
    expr.textContent = (exprString + ' = ' + value).replaceAll(/([\d)])([=*/+-])/g, "$1 $2 ");
    expr.classList.add("expression");
    exprCell.appendChild(expr);
    expr.onclick = getFromHistoryTable;
}

/**
 * gets expression from html table to input field
 */
function getFromHistoryTable(e) {
    input.value = e.target.getAttribute("data-expression");
}