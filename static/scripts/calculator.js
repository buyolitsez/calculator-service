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

    if (key === "\n") {
        event.preventDefault()
        postExpression()
    }
})

function postExpression() {
    resultField.innerText = "******"
    resultField.classList.add("has-skeleton")
    fetch("/calculator", {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({"expression": input.value})
    }).then((response) => {
        resultField.classList.remove("has-skeleton")
        if (!response.ok) {
            resultField.innerText = "Result:"
            return null
        }
        return response.text()
    }).then(async (text) => {
        // TODO: handle expression evaluation errors
        resultField.innerText += " " + await text

        // call it only when expression is correct!
        addToHistory(input.value, resultField.innerText)
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