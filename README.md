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

![img_1.png](pics/error-example-opening-parenthesis-was-never-closed.png)
![img_1.png](pics/mobile-error-example-opening-parenthesis-has-never-closed.png)

---

![img.png](pics/error-example-zero-division.png) 
![img.png](pics/mobile-error-example-division-by-zero.png)

---

![img.png](pics/error-example-empty-expression.png)
![img.png](pics/mobile-error-example-empty-expression-in-parenthesis.png)

---

![img_1.png](pics/error-example-unexpected-delimeter.png)
![img_1.png](pics/mobile-error-example-unexpected-delimiter.png)

---

![img_1.png](pics/mobile-error-example-consecutive-operations.png)
![img_1.png](pics/mobile-error-example-extra-closing-parenthesis.png)
![img_1.png](pics/mobile-error-example-invalid-expression-in-parenthesis.png)
![img_1.png](pics/mobile-error-example-number-starts-with-zero.png) 
![img_1.png](pics/mobile-error-example-unary-operator-must-be.png)