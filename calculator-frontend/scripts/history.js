const historyTable = document.getElementById("history")

/**
 * adds all data from db to html table
 * 
 * must be called only once: at the launch of website 
 */
function getHistory() {
    fetch("/history")
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
    exprCell.classList.add("is_clickable");
    const expr = document.createElement('span');
    expr.textContent = exprString + ' = ' + value;
    expr.classList.add("expression");
    exprCell.appendChild(expr);
    exprCell.onclick = getFromHistoryTable;
}

/**
 * gets expression from html table to input field
 */
function getFromHistoryTable(e) {
    const exprAndValue = e.target.textContent.split('=');
    console.assert(exprAndValue.length === 2);
    input.value = exprAndValue[0].trim();
}