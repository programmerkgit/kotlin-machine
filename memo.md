# 1.File => project structure => Java 10.0を選択
まずはそれ。

# 1.virtual-machine.iml
この設定を適切にする

# Kotlinの設定
1.がうまくいくと、Kotlinが設定されていませんのポップアップがでる  
Copy to Libじゃ無い方を選択する。

# Syntax Tree(parse)

# Equals of Kotlin


# Constructor References
Constructors can be referenced just like methods and properties. They can be used wherever an object of function type is expected that takes the same parameters as the constructor and returns an object of the appropriate type. Constructors are referenced by using the :: operator and adding the class name. Consider the following function that expects a function parameter with no parameters and return type Foo:
[Reflection](https://kotlinlang.org/docs/reference/reflection.html)

```
class Foo

fun function(factory: () -> Foo) {
   val x: Foo = factory()
}
```
Using ::Foo, the zero-argument constructor of the class Foo, we can simply call it like this:

```
function(::Foo)
```

   Callable references to constructors are typed as one of the KFunction<out R> subtypes , depending on the parameter count.
   
#Type variance, out, in
[variance out in](https://kotlinlang.org/docs/reference/generics.html)

# gradle compile should not be used
The compile configuration still exists but should not be used as it will not offer the guarantees that the api and implementation configurations provide

# Internal set

# Kotlin compiler
https://github.com/JetBrains/kotlin

# build script
First of all, I assume you meant to include the “dependencies” block between “buildscript” and “classpath”.

The “buildscript” block only controls dependencies for the buildscript process itself, not for the application code, which the top-level “dependencies” block controls.

For instance, you could define dependencies in “buildscript/classpath” that represent Gradle plugins used in the build process. Those plugins would not be referenced as dependencies for the application code.

Read the Gradle User Guide for more information (the PDF is easy to search).