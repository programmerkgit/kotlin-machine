 # Boxing
 Primitives are not nullable.
 So, Wrapped object is implicitly created when integer is assigned to nullable type variable.
 ```kotlin
val a: Int = 1000;
val i: Int? = a // <= a is boxed from primitive to nullable object
```

# Conversion

```kotlin
val b: Byte = 1 // OK, literals are checked statically
val i: Int = b.toInt() 
```

# Array Kotlin is invariant
Arrays in Kotlin are invariant. This means that Kotlin does not let us assign an Array<String> to an Array<Any>, which prevents a possible runtime failure (but you can use Array<out Any>, see Type Projections).

# raw string

```kotlin
val text = """
    for (c in "foo")
        print(c)
""".trimMergine()
```

# String templates
```kotlin
val i: Int = 10
println("i = $i")
println("i = ${i.toString()}")
/* $ doesn't support backslash escape */
println("${'$'}")
```

# Default Imports
Following files are default imported by all files.
- kotlin.*
- kotlin.annotation.*
- kotlin.collections.*
- kotlin.comparisons.* (since 1.1)
- kotlin.io.*
- kotlin.ranges.*
- kotlin.sequences.*
- kotlin.text.*

# Imports

```kotlin
import org.example.PackageName
import org.example.*
import org.example.PackageName as anotherName
```
The import keyword is not restricted to importing classes; you can also use it to import other declarations:

- top-level functions and properties;
- functions and properties declared in object declarations;
- enum constants.

# If returns value
if branches can be blocks, and the last expression is the value of a block:
```kotlin
val max = if (a > b) {
    print("Choose a")
    a
} else {
    print("Choose b")
    b
}
```

# When Expression
when replaces the switch operator of C-like languages. In the simplest form it looks like this

```kotlin
when (x) {
  0, 1 -> print("x == 1 || x == 0")
  2 -> print("x == 2")
  in 1..10 -> print("x is in the range")
  in validNumbers -> print("x is valid")
  !in 10..20 -> print("x is outside the range")    
  else -> { // Note the block
      print("x is neither 1 nor 2")
  }
}

when {
    x.isOdd() -> print("x is odd")
    x.isEven() -> print("x is even")
    else -> print("x is funny")
}

// Since Kotlin 1.3, it is possible to capture when subject in a variable using following syntax:

fun Request.getBody() =
        when (val response = executeRequest()) {
            is Success -> response.body
            is HttpError -> throw HttpException(response.status)
        }
```

# For
```kotlin
for (i in 1..3) {
    println(i)
}
for (i in 6 downTo 0 step 2) {
    println(i)
}
for (i in array.indices) {
    println(array[i])
}
for ((index, value) in array.withIndex()) {
    println("the element at $index is $value")
}
```

# label
Lambda returns from outer function.
label lamda, at returns from lambda.

```kotlin

fun foo() {
    listOf(1, 2, 3, 4, 5).forEach {
        if (it == 3) return // non-local return directly to the caller of foo()
        print(it)
    }
    println("this point is unreachable")
}
```

Target platform: JVMRunning on kotlin v. 1.3.50
The return-expression returns from the nearest enclosing function, i.e. foo. (Note that such non-local returns are supported only for lambda expressions passed to inline functions.) If we need to return from a lambda expression, we have to label it and qualify the return:

```kotlin
fun foo() {
    listOf(1, 2, 3, 4, 5).forEach lit@{
        if (it == 3) return@lit // local return to the caller of the lambda, i.e. the forEach loop
        print(it)
    }
    print(" done with explicit label")
}
```

Implicit lambda. Label name is same as name of function to which lamda is passed.

```kotlin
fun foo() {
    listOf(1, 2, 3, 4, 5).forEach {
        if (it == 3) return@forEach // local return to the caller of the lambda, i.e. the forEach loop
        print(it)
    }
    print(" done with implicit label")
}
```

# range check

```kotlin
1 in 1..3
```

# Kotlin points
 # Boxing
 Primitives are not nullable.
 So, Wrapped object is implicitly created when integer is assigned to nullable type variable.
 ```kotlin
val a: Int = 1000;
val i: Int? = a // <= a is boxed from primitive to nullable object
```

# Conversion

```kotlin
val b: Byte = 1 // OK, literals are checked statically
val i: Int = b.toInt() 
```

# Array Kotlin is invariant
Arrays in Kotlin are invariant. This means that Kotlin does not let us assign an Array<String> to an Array<Any>, which prevents a possible runtime failure (but you can use Array<out Any>, see Type Projections).

# raw string

```kotlin
val text = """
    for (c in "foo")
        print(c)
""".trimMergine()
```

# String templates
```kotlin
val i: Int = 10
println("i = $i")
println("i = ${i.toString()}")
/* $ doesn't support backslash escape */
println("${'$'}")
```

# Default Imports
Following files are default imported by all files.
- kotlin.*
- kotlin.annotation.*
- kotlin.collections.*
- kotlin.comparisons.* (since 1.1)
- kotlin.io.*
- kotlin.ranges.*
- kotlin.sequences.*
- kotlin.text.*

# Imports

```kotlin
import org.example.PackageName
import org.example.*
import org.example.PackageName as anotherName
```
The import keyword is not restricted to importing classes; you can also use it to import other declarations:

- top-level functions and properties;
- functions and properties declared in object declarations;
- enum constants.

# If returns value
if branches can be blocks, and the last expression is the value of a block:
```kotlin
val max = if (a > b) {
    print("Choose a")
    a
} else {
    print("Choose b")
    b
}
```

# When Expression
when replaces the switch operator of C-like languages. In the simplest form it looks like this

```kotlin
when (x) {
  0, 1 -> print("x == 1 || x == 0")
  2 -> print("x == 2")
  in 1..10 -> print("x is in the range")
  in validNumbers -> print("x is valid")
  !in 10..20 -> print("x is outside the range")    
  else -> { // Note the block
      print("x is neither 1 nor 2")
  }
}

when {
    x.isOdd() -> print("x is odd")
    x.isEven() -> print("x is even")
    else -> print("x is funny")
}

// Since Kotlin 1.3, it is possible to capture when subject in a variable using following syntax:

fun Request.getBody() =
        when (val response = executeRequest()) {
            is Success -> response.body
            is HttpError -> throw HttpException(response.status)
        }
```

# For
```kotlin
for (i in 1..3) {
    println(i)
}
for (i in 6 downTo 0 step 2) {
    println(i)
}
for (i in array.indices) {
    println(array[i])
}
for ((index, value) in array.withIndex()) {
    println("the element at $index is $value")
}
```

# label
Lambda returns from outer function.
label lamda, at returns from lambda.

```kotlin

fun foo() {
    listOf(1, 2, 3, 4, 5).forEach {
        if (it == 3) return // non-local return directly to the caller of foo()
        print(it)
    }
    println("this point is unreachable")
}
```

Target platform: JVMRunning on kotlin v. 1.3.50
The return-expression returns from the nearest enclosing function, i.e. foo. (Note that such non-local returns are supported only for lambda expressions passed to inline functions.) If we need to return from a lambda expression, we have to label it and qualify the return:

```kotlin
fun foo() {
    listOf(1, 2, 3, 4, 5).forEach lit@{
        if (it == 3) return@lit // local return to the caller of the lambda, i.e. the forEach loop
        print(it)
    }
    print(" done with explicit label")
}
```

Implicit lambda. Label name is same as name of function to which lamda is passed.

```kotlin
fun foo() {
    listOf(1, 2, 3, 4, 5).forEach {
        if (it == 3) return@forEach // local return to the caller of the lambda, i.e. the forEach loop
        print(it)
    }
    print(" done with implicit label")
}
```

# range check

```kotlin
1 in 1..3
```

# Kotlin points
