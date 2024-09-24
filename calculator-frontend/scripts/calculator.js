const input = document.getElementById("calculatorInput")
const resultField = document.getElementById("calculatorResult")
const buttons = document.getElementById("calculatorButtons").childNodes
const keyButtonMap = new Map()

input.focus()
input.onblur = function () {
    const element = this;
    setTimeout(function () {
        element.focus()
    }, 1);
};

function typeCharacter(character) {
    const caretPosition = input.selectionStart + 1
    input.value = input.value.slice(0, input.selectionStart) + character + input.value.slice(input.selectionEnd)
    input.setSelectionRange(caretPosition, caretPosition);
}

function typeDelete() {
    const caretPosition = input.selectionStart
    input.value = input.value.slice(0, input.selectionStart) + input.value.slice(input.selectionEnd + 1)
    input.setSelectionRange(caretPosition, caretPosition);
}

function typeBackspace() {
    const caretPosition = input.selectionStart - 1
    input.value = input.value.slice(0, input.selectionStart - 1) + input.value.slice(input.selectionEnd)
    input.setSelectionRange(caretPosition, caretPosition);
}

input.addEventListener('keydown', function (event) {
    let key = event.key.toLowerCase();

    // Prevent typing of non-permitted visible characters
    if (!/[\n\d,.*/()=+-]/.test(key) && key.length === 1) {
        event.preventDefault()
        return;
    }

    resetSelectionColor()

    if (key === ",") {
        key = '.'
    }

    if (keyButtonMap.has(key)) {
        event.preventDefault()
        keyButtonMap.get(key).click()
        return
    }

    if (key === "delete") {
        event.preventDefault()
        typeDelete()
    }

    if (key === "enter") {
        event.preventDefault()
        postExpression()
    }
})

document.addEventListener("mousedown", () => {
    resetSelectionColor()
})

function unwrapError(json) {
    return JSON.parse(json)
}

function highlightSymbol(position) {
    input.setSelectionRange(position - 1, position)

}

function changeSelectionColor(type) {
    input.classList.add(type)
}

function resetSelectionColor() {
    input.classList.remove("error")
}

function postExpression() {
    resultField.innerText = "******"
    resultField.classList.add("has-skeleton")
    fetch("http://localhost:8080/calculator", {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({"expression": input.value})
    }).then(async (response) => {
        resultField.classList.remove("has-skeleton")
        const data = await response.text()
        let result = parseFloat(data);

        if (!isNaN(result)) {
            resultField.classList.remove("has-text-danger")
            if (Math.abs(result - Math.round(result)) < 1e-9) {
                result = Math.round(result)
            } else {
                result = result.toFixed(8) * 1
            }
            resultField.innerText = "Result: " + result.toString()
            addToHistoryTable(input.value, result)
            return
        }

        resultField.classList.add("has-text-danger")
        const error = unwrapError(data)
        resultField.innerText = "Error: " + error.message
        if (Object.hasOwn(error, "position")) {
            changeSelectionColor("error")
            highlightSymbol(parseInt(error.position))
        }
    })
}

buttons.forEach(button => {
    keyButtonMap.set(button.id, button)
    button.addEventListener("click", () => {
        switch (button.id) {
            case "AC":
                input.value = ""
                break
            case "backspace":
                typeBackspace()
                break
            case "=":
                postExpression()
                break
            default:
                typeCharacter(button.id)
                break
        }
    })
});