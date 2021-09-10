
#IDEA Plugin Guava

## Guava Generators


This is an IDEA(IntelliJ) plugin to generate equals, hashCode and toString using Guava utilities.

Based on the "Live Coding a Plugin From Scratch" webinar, which can be found here: [Jet Brains Blog]( http://blogs.jetbrains.com/idea/2012/12/webinar-recording-live-coding-a-plugin-from-scratch/)

___ 

### Example of generated code:

Equals:

```java
 @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        User that = (User) o;
        
        return com.google.common.base.Objects.equal(this.name, that.name) &&
                com.google.common.base.Objects.equal(this.email, that.email) &&
    }

```

Hash Code:

```java
@Override
    public int hashCode() {
        return com.google.common.base.Objects.hashCode(name, email);
    }
```


ToString:

```java
@Override
    public String toString() {
        return com.google.common.base.MoreObjects.toStringHelper(this)
                .add("name", name)
                .add("email", email)
                .toString();
    }
```

___

### Dependencies

To use this plugin you need to add into your pom or grandle file the guava jar,  as shown at the example:

Maven:
```xml
<dependency>
  <groupId>com.google.guava</groupId>
  <artifactId>guava</artifactId>
  <version>19.0</version>
</dependency>
```

Gradle:
```groovy
com.google.guava:guava:19.0
```
**OBS:** The guava version may change when the guava team releases a new one.



