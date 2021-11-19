# Style guide

- Indentation: spaces (mostly 2, but 8 for assignment continuation)
- Maximum line length: 120


## Java

### Classes

```java
public class SimpleFoo
{
  // etc. etc.
}
```

```java
public class ComplicatedFoo
  extends Bar
  implements WiseClass.WiseInterface, FoolishClass.FoolishInterface
{
  // etc. etc.
}
```

### Comments

```java
/*
  Block comments.
  Or descriptions.
*/
```

```java
// Heading for a bunch of logically grouped declarations
private Foo foo;
private Bar bar;
```

```java
doSomeExtraCheck(); // short remark (e.g. bemoaning a deprecation)
```

### Methods

```java
public void doSomething()
{
  // etc. etc.
}
```

```java
public boolean AreThereTooManyParameters(
  final Body commonLaw,
  final Contract misleadingOrDeceptiveAgreement,
  final Party greedyDeveloper,
  final Party peasant
)
{
  // etc. etc.
}
```


## XML

### Comments

```xml
<!--
  Multi-line comment.
  Blah blah blah.
-->
```

```xml
<!-- One-liner -->
```

### Elements

One-liners:

```xml
<Paired attribute="value">content</Paired>
<SelfClosing attribute="value" />
```

Multi-liners:

```xml
<Paired
  attribute1="value1"
  attribute2="value2"
  attribute3="value3"
>
  <SelfClosing1
    attribute1="value1"
    attribute2="value2"
    attribute3="value3"
  />
  <SelfClosing2
    attribute1="value1"
    attribute2="value2"
    attribute3="value3"
  />
</Paired>
```
