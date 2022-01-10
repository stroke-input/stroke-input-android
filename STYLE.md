# Style guide

- Maximum line length: 120 code points
- Indentation: 2 spaces
- Assignment continuation: 8 spaces


## Java

### Arithmetic

```java
positionForTheNextElection =
        descriptivelyNamedDefaultPosition
          - NAYSAYER_POSITIONAL_INFLUENCE * naysayerCount
          + YEASAYER_POSITIONAL_INFLUENCE * yeasayerCount;
```

```java
totalQuantity =
        descriptivelyNamedTerm
          +
        SOME_RATE * whateverIsDimensionallyConsistent
          +
        (justDo - whateverGroupingOrIndentation / feelsLogical);
```

### Assignments

```java
Foo foo = fitsOnOneLine;
```

```java
final FunnyObject descriptivelyNamedInstance =
        (FunnyObject) anotherDescriptivelyNamedInstanceThatNeedsCasting;
```

```java
final String corruptedText =
        originalText
          .makeImperfectCopy()
          .makeImperfectCopy()
          .makeImperfectCopy()
          .makeImperfectCopy();
```

```java
final int dollarFigure =
        (int) someMethodThatReturnsFloat(
          verboseButDescriptiveMattress,
          verboseButDescriptivePillow,
          verboseButDescriptiveSheet
        );
```

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

### Conditionals

```java
if (conditionIsShort())
{
  // etc. etc.
}
else
{
  // etc. etc.
}
```

```java
if (
  someBoolean && someOtherBoolean
    ||
  someInequality && anotherInequality && blahBlahBlah
)
{
  // etc. etc.
}
```

### Loops

```java
for (int index = 0; index < count; index++)
{
  // etc. etc.
}
```

```java
for (
  int index = verboseButDescriptiveInitialValue;
  areYouSureYouAreSure(index);
  cruelAndUnusualAndVerboseIncrement(index);
)
{
  // etc. etc.
}
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

### Returns

```java
return OneLiner;
```

```java
return (
  (isQualified || didDueDiligence && looksQualified)
    &&
  horriblyComplicatedBooleanThingy
    &&
  (thisLooksDodgy || shouldProbablyRefactorThis)
);
```

```java
return
  someReallyLongMethodCall(whichFitsOnOneLine, ifNotOnTheSameLineAsReturn);
```

```java
return
  someReallyReallyReallyLongMethodCall(
    whichWillNotFitOnOneLine,
    evenIfNotOnTheSameLineAsReturn,
    becauseItIsReallyReallyReallyLong
  );
```

### Switches

```java
label:
switch (expression)
{
  case goodValue:
    doGood();
    break;
  
  case evilValue:
    doEvil();
    break;
  
  case neutralValue1:
  case neutralValue2:
    doNothing();
    break;
  
  default:
    throwHandsInTheAir();
}
```

```java
moreComplicated:
switch (expression)
{
  case multipleBreaksInConditionals:
    
    leaveBlankLineAbove();
    if (conditional)
    {
      doSomething();
      break;
    }
    
    if (anotherConditional)
    {
      doAnotherThing();
      break;
    }
    
    doSomeOtherStuff();
    break;
  
  case singleBreak:
    takeSighOfRelief();
    break;
}
```

### Ternaries

```java
final Foo foo =
        (isThisConditionalSatisfied)
          ? yesValue
          : noValue;
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
