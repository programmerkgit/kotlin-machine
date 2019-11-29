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