# Style guide


## Java

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

### One-liners

```xml
<Paired attribute="value">content</Paired>
<SelfClosing attribute="value" />
```

### Multi-liners

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
