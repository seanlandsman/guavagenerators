
#IDEA Plugin Guava 

## Guava Generators


__

This is an IDEA plugins to generate equals, hashCode and toString using Guava utilities.

Based on the "Live Coding a Plugin From Scratch" webinar, which can be found here:[Jet Brains Blog]( http://blogs.jetbrains.com/idea/2012/12/webinar-recording-live-coding-a-plugin-from-scratch/)

__ 

### Example of generated code:

Equals:

`
 @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User that = (User) o;

        return com.google.common.base.Objects.equal(this.name, that.name) &&
                com.google.common.base.Objects.equal(this.email, that.email) &&
    }

`

Hash Code:

`
@Override
    public int hashCode() {
        return com.google.common.base.Objects.hashCode(name, email);
    }
`


ToString:

`  @Override
    public String toString() {
        return com.google.common.base.Objects.toStringHelper(this)
                .add("name", name)
                .add("email", email)
                .toString();
    }
`

__

###Dependences

To use this plugin it will be needed to be add into your pom or grandle file the guava jar,  as the examples shows:

 Maven:
`
<dependency>
	<groupId>com.google.guava</groupId>
	<artifactId>guava</artifactId>
	<version>19.0</version>
</dependency>
`

Gradle:
`
com.google.guava:guava:19.0'

`
**OBS:** The guava version may change when the guava team release it a new one.



