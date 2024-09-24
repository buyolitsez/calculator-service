# Calculator service

This is a simple calculator service that can perform basic arithmetic operations.
Also, the history feature is implemented to store the history of the operations performed with a possibility to restore past operations.

## Executing

To run the backend server run the following command:

```bash
$ ./gradlew run
```

In order to run a frontend server, execute the following command:

```bash
python3 -m http.server --cgi --directory calculator-frontend 8000
```

And the frontend will be available at `http://localhost:8000`.

## Examples

![img.png](pics/correct-example.png)

### Errors examples:

<div style="display:flex; height:300px; align-items:stretch; flex-direction:row">
  <img src="pics/mobile-error-example-opening-parenthesis-has-never-closed.png" style="height:300px"></img>
  <img src="pics/error-example-opening-parenthesis-was-never-closed.png" style="height:300px"></img>
</div>

---

<div style="display:flex; height:300px; align-items:stretch; flex-direction:row">
  <img src="pics/mobile-error-example-division-by-zero.png" style="height:300px"></img>
  <img src="pics/error-example-zero-division.png" style="height:300px"></img>
</div>

---

<div style="display:flex; height:300px; align-items:stretch; flex-direction:row">
  <img src="pics/mobile-error-example-empty-expression-in-parenthesis.png" style="height:300px"></img>
  <img src="pics/error-example-empty-expression.png" style="height:300px"></img>
</div>

---

<div style="display:flex; height:300px; align-items:stretch; flex-direction:row">
  <img src="pics/mobile-error-example-unexpected-delimiter.png" style="height:300px"></img>
  <img src="pics/error-example-unexpected-delimeter.png" style="height:300px"></img>
</div>

---

<div style="display:flex; height:300px; width:100vh; align-items:stretch; flex-direction:row; justify-content:center">
  <img src="pics/mobile-error-example-consecutive-operations.png" style="height:300px"></img>
  <img src="pics/mobile-error-example-extra-closing-parenthesis.png" style="height:300px"></img>
  <img src="pics/mobile-error-example-invalid-expression-in-parenthesis.png" style="height:300px"></img>
  <img src="pics/mobile-error-example-number-starts-with-zero.png" style="height:300px"></img>
  <img src="pics/mobile-error-example-unary-operator-must-be.png" style="height:300px"></img>
</div>
