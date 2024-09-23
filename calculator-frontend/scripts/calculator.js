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
    const key = event.key.toLowerCase()

    // Prevent typing of non-permitted visible characters
    if (!/[\n\d*/()=+-]/.test(key) && key.length === 1) {
        event.preventDefault()
        return;
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

function unwrapError(json) {
    return JSON.parse(json).message
}

function postExpression() {
    resultField.innerText = "******"
    resultField.classList.add("has-skeleton")
    fetch("/calculator", {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({"expression": input.value})
    }).then(async (response) => {
        resultField.classList.remove("has-skeleton")
        if (!response.ok) {
            resultField.classList.add("has-text-danger")
            resultField.innerText = "Error: " + unwrapError(await response.text())
            return
        }
        resultField.classList.remove("has-text-danger")
        const result = await response.text()
        resultField.innerText = "Result: " + result
        addToHistoryTable(input.value, result)
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