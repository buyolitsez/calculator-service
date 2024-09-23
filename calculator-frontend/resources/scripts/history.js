const historyTable = document.getElementById("history")

/**
 * adds all data from db to html table
 * 
 * must be called only once: at the launch of website 
 */
function getHistory() {
    fetch("/history")
    .then(response => response.json())
    .then(async (json) => {
        await json.forEach(expression => {
            addToHistoryTable(expression.exprString, expression.value, -1)
        });
    })
}
window.onload = getHistory;

/**
 * adds to html table
 */
function addToHistoryTable(exprString, value, ind = 0) {
    var row = historyTable.insertRow(ind);
    var exprCell = row.insertCell();
    exprCell.classList.add("is_clickable");
    var expr = document.createElement('span');
    expr.textContent = exprString + ' = ' + value;
    expr.classList.add("expression");
    exprCell.appendChild(expr);
    exprCell.onclick = getFromHistoryTable;
}


/**
 * adds to html table and DB
 */
function addToHistory(_exprString, _value) {
    addToHistoryTable(_exprString, _value)

    var data = { exprString: _exprString, value: _value };
    fetch("/history", {
        method: 'POST',
        headers: {
            'Content-Type': 'text;charset=utf-8'
        },
        body: JSON.stringify(data)
      }).then(async (response) => {
        console.log(await response.text());
    });
}

/**
 * gets expression from html table to input field
 */
function getFromHistoryTable(e) {
    var exprAndValue = e.target.textContent.split('=');
    console.assert(exprAndValue.length == 2);
    input.value = exprAndValue[0].trim();
};