# Gohhato-j

Gohatto-j is a kind of binary file checker for Java.

Gohatto-j inspects given JAR file and validate that it does not contain references to prohibited packages.

# How to use

## as CLI

Execute jar file with required options
- "--jar" inspection target
- "--forbiddenPackageList" text file (see usage below)

```
$ java -jar build/libs/gohatto-j-0.0.1-SNAPSHOT-all.jar --jar src/test/resources/testproject.jar --forbiddenPackageList  src/test/resources/rule_forbid_jav-util_and_com-sun.txt
[main] INFO io.hrkt.gohatto.Gohatto - ERROR     DoNotUseForbiddenPackage        java.util.HashMap violates rule
[main] INFO io.hrkt.gohatto.Gohatto - ERROR     DoNotUseForbiddenPackage        java.util.Map violates rule
[main] INFO io.hrkt.gohatto.Gohatto - rules applied to: 2
```

## in JUnit 

```java
        String resDir = "src/test/resources/";
        Gohatto gohatto = new Gohatto();
        gohatto.init(resDir + "testproject.jar", resDir + "rule_forbid_jav-util_and_com-sun.txt");
        List<Result> ret = gohatto.executeRules();
        Assert.assertTrue(ret.size() == 0);
```

## forbiddenPackageList

### format

Text file with with package names in each line.

Regex can be used(see Method2),

### Method 1: specify packages to be banned

The example below will produce errors if java.util package or com.sun package is used.

```text
java.util
com.sun
```

### Method 2: specify permitted packages

The example below will produce errors if java.util package or com.sun package is used.

```text
^(?!java.lang|java.io)
```

## Develop

### prerequisites

- Java8 or later
- [Lombok](https://projectlombok.org/)

## CI

[![CircleCI](https://circleci.com/gh/hrkt/gohatto-j/tree/master.svg?style=svg)](https://circleci.com/gh/hrkt/gohatto-j/tree/master)

## LICENSE

MIT

