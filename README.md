# ✈️ Flug ✈️

### Now made with 100% more Kotlin!

Flug is a simple programming language made by Truttle1 to learn how 
programming language implementation works.

## Features

### Integer and Boolean Variables

You can declare variables like so:
```
let x = 3;
const y = true;
```

In this example, `x` is mutable, so later on you can write:

```commandline
x = x + 1;
```
and change the value of `x`. `y` on the other hand is constant, so its value cannot
be changed after it is created.

### Flow Control Statements

Flug supports the following flow control statements:

```commandline
if (<cond>) {
    doSomething()
}
elif (<another>) {
    somethingElse()
}
else {
    optional()
}
```

and

```commandline
while (<cond>) {
    loop()
}
```

### Semicolons

Flug is a language where everything is evaluated as an expression.
Semicolons (`;`) exist to seperate Flug expressions, where the left side of the expression
does side effects (like mutating variables) and the right
side returns its expression to whatever comes next.

So:

```commandline
if(x > y) {
    z = x
}
else {
    z = y
};
z
```
will set `z` to the result of the if statement and return `z`. **Note that because
the if-else statement is an expression on its own, it must end with a semicolon**.

### Functions

You can declare functions in flug like so:

```commandline
const mutThenSum = func(const x, y) => {
    y = y * 2;
    x + y
};
mutThenSum(2, 3)
```

In this example, `y` is a mutable parameter, meaning it can be changed after it is
assigned by the function (though it only changes the version *in scope* of the function. The original
passed in is unaffected). `x` on the other hand cannot be changed in the function.

The function returns whatever expression the code within it evaluates to.

Flug does support recursion, and Flug functions are first class functions, so they are
treated the same as other variable types.

## Example

The following computes the sum of (3 + 2)! and the 7th Fibonacci number:

```commandline
const factorial = func(const x) => {
    if(x <= 1) {
        1
    }
    else {
        x * factorial(x - 1)
    }
};

const fib = func(const x) => {
    if(x <= 1) {
        1
    }
    else {
        fib(x - 1) + fib(x - 2)
    }
};

fib(7) + factorial(3 + 2)
```
