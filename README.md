Java Semantic Versioning
============================

A Java implementation of the Semantic Versioning Specification, as per ([http://semver.org](http://semver.org/spec/v2.0.0.html
)) **modified** to exclude the minor/patch version information if zero or not specified. It is additionally **modified**  to permit parsing
 build information after a final '.' following minor/patch, such that 4.1.0.Final will parse a build as "Final".

This is a breaking change when comparing strings to the original Semantic Versioning Specification by Tom Preston-Werner. When comparing
 Version objects, it is non-breaking, and is breaking when writing Version strings.

### Versioning ###

Semantic Versioning Specification (SemVer v2.5-dorkbox)

1. Modified to exclude minor version information.
1. Modified to exclude patch version information.
1. Modified to permit reading build metadata after a final . (with, or without the patch number)
1. Modified to permit reading pre-release information following '_' (in addition to '-')

Creative Commons - CC BY 3.0, by Tom Preston-Werner.

The key words “MUST”, “MUST NOT”, “REQUIRED”, “SHALL”, “SHALL NOT”, “SHOULD”, “SHOULD NOT”, “RECOMMENDED”, “MAY”, and “OPTIONAL” in this document are to be interpreted as described in ([RFC 2119](http://tools.ietf.org/html/rfc2119)).

1. Software using Semantic Versioning MUST declare a public API. This API could be declared in the code itself or exist strictly in documentation. However it is done, it should be precise and comprehensive.

2. A normal version number MUST take the form X.Y.Z where X, Y, and Z are non-negative integers, and MUST NOT contain leading zeroes. X is the major version, Y is the minor version, and Z is the patch version. Each element MUST increase numerically. For instance: 1.9.0 -> 1.10.0 -> 1.11.0.

2. A normal version number MUST take the form X.Y.Z or X.Y where X, Y, and (optional) Z are non-negative integers, and MUST NOT contain leading zeroes. X is the major version, Y is the minor version, and Z is the optional patch version. Each element MUST increase numerically. For instance: 1.9.0 -> 1.10.0 -> 1.11.0.

    **Dorkbox Addendum**:  Patch version information Z is OPTIONAL, and if not specified will be left off the toString() value. Additionally, when incrementing the major/minor versions, the patch information (now 0) will be excluded

3. Once a versioned package has been released, the contents of that version MUST NOT be modified. Any modifications MUST be released as a new version.

4. Major version zero (0.y.z) is for initial development. Anything may change at any time. The public API should not be considered stable.

5. Version 1.0.0 defines the public API. The way in which the version number is incremented after this release is dependent on this public API and how it changes.

6. Patch version Z (x.y.Z | x > 0) MUST be incremented if only backwards compatible bug fixes are introduced. A bug fix is defined as an internal change that fixes incorrect behavior.

7. Minor version Y (x.Y.z | x > 0) MUST be incremented if new, backwards compatible functionality is introduced to the public API. It MUST be incremented if any public API functionality is marked as deprecated. It MAY be incremented if substantial new functionality or improvements are introduced within the private code. It MAY include patch level changes. Patch version MUST be reset to 0 when minor version is incremented.

8. Major version X (X.y.z | X > 0) MUST be incremented if any backwards incompatible changes are introduced to the public API. It MAY include minor and patch level changes. Patch and minor version MUST be reset to 0 when major version is incremented.

9. A pre-release version MAY be denoted by appending a hyphen and a series of dot separated identifiers immediately following the patch version. Identifiers MUST comprise only ASCII alphanumerics and hyphen [0-9A-Za-z-]. Identifiers MUST NOT be empty. Numeric identifiers MUST NOT include leading zeroes. Pre-release versions have a lower precedence than the associated normal version. A pre-release version indicates that the version is unstable and might not satisfy the intended compatibility requirements as denoted by its associated normal version. Examples: 1.0.0-alpha, 1.0.0-alpha.1, 1.0.0-0.3.7, 1.0.0-x.7.z.92.

10. Build metadata MAY be denoted by appending a plus sign and a series of dot separated identifiers immediately following the patch or pre-release version. Identifiers MUST comprise only ASCII alphanumerics and hyphen [0-9A-Za-z-]. Identifiers MUST NOT be empty. Build metadata SHOULD be ignored when determining version precedence. Thus two versions that differ only in the build metadata, have the same precedence. Examples: 1.0.0-alpha+001, 1.0.0+20130313144700, 1.0.0-beta+exp.sha.5114f85.

11. Precedence refers to how versions are compared to each other when ordered. Precedence MUST be calculated by separating the version into major, minor, patch and pre-release identifiers in that order (Build metadata does not figure into precedence). Precedence is determined by the first difference when comparing each of these identifiers from left to right as follows: Major, minor, and patch versions are always compared numerically. Example: 1.0.0 < 2.0.0 < 2.1.0 < 2.1.1. When major, minor, and patch are equal, a pre-release version has lower precedence than a normal version. Example: 1.0.0-alpha < 1.0.0. Precedence for two pre-release versions with the same major, minor, and patch version MUST be determined by comparing each dot separated identifier from left to right until a difference is found as follows: identifiers consisting of only digits are compared numerically and identifiers with letters or hyphens are compared lexically in ASCII sort order. Numeric identifiers always have lower precedence than non-numeric identifiers. A larger set of pre-release fields has a higher precedence than a smaller set, if all of the preceding identifiers are equal. Example: 1.0.0-alpha < 1.0.0-alpha.1 < 1.0.0-alpha.beta < 1.0.0-beta < 1.0.0-beta.2 < 1.0.0-beta.11 < 1.0.0-rc.1 < 1.0.0.


### Table of Contents ###
* [Installation](#installation)
* [Usage](#usage)
  * [Creating Versions](#creating-versions)
  * [Incrementing Versions](#incrementing-versions)
  * [Comparing Versions](#comparing-versions)
* [SemVer Expressions API (Ranges)](#semver-expressions-api-ranges)
* [Exception Handling](#exception-handling)
* [Bugs and Features](#bugs-and-features)
* [License](#license)



Usage
-----
Below are some common use cases for the library.

### Creating Versions ###
The main class of the library is `Version` which implements the
Facade design pattern. By design, the `Version` class is made immutable by
making its constructors package-private, so that it can not be subclassed or
directly instantiated. Instead of public constructors, the `Version` class
provides few _static factory methods_.

One of the methods is the `Version.from` method.

~~~ java
import com.dorkbox.version.Version;

Version v = Version("1.0.0-rc.1+build.1");

int major = v.getMajorVersion(); // 1
int minor = v.getMinorVersion(); // 0
int patch = v.getPatchVersion(); // 0

String normal     = v.getNormalVersion();     // "1.0.0"
String preRelease = v.getPreReleaseVersion(); // "rc.1"
String build      = v.getBuildMetadata();     // "build.1"

String str = v.toString(); // "1.0.0-rc.1+build.1"
~~~

The other static factory method is `Version.from` which is also overloaded to allow fewer arguments.

~~~ java
import com.dorkbox.version.Version;

Version v1 = Version(1);
Version v2 = Version(1, 2);
Version v3 = Version(1, 2, 3);
~~~

Another way to create a `Version` is to use a _builder_ class `Version.Builder`.

~~~ java
import com.dorkbox.version.Version;

Version.Builder builder = new Version.Builder("1.0.0");
builder.setPreReleaseVersion("rc.1");
builder.setBuildMetadata("build.1");

Version v = builder.build();

int major = v.getMajorVersion(); // 1
int minor = v.getMinorVersion(); // 0
int patch = v.getPatchVersion(); // 0

String normal     = v.getNormalVersion();     // "1.0.0"
String preRelease = v.getPreReleaseVersion(); // "rc.1"
String build      = v.getBuildMetadata();     // "build.1"

String str = v.toString(); // "1.0.0-rc.1+build.1"
~~~

### Incrementing Versions ###
Because the `Version` class is immutable, the _incrementors_ return a new
instance of `Version` rather than modifying the given one. Each of the normal
version incrementors has an overloaded method that takes a pre-release version
as an argument.

~~~ java
import com.dorkbox.version.Version;

Version v1 = Version("1.2.3");

// Incrementing the major version (note optional patch information)
Version v2 = v1.incrementMajorVersion();        // "2.0"
Version v2 = v1.incrementMajorVersion("alpha"); // "2.0-alpha"

// Incrementing the minor version (note optional patch information)
Version v3 = v1.incrementMinorVersion();        // "1.3"
Version v3 = v1.incrementMinorVersion("alpha"); // "1.3-alpha"

// Incrementing the patch version
Version v4 = v1.incrementPatchVersion();        // "1.2.4"
Version v4 = v1.incrementPatchVersion("alpha"); // "1.2.4-alpha"

// Original Version is still the same
String str = v1.toString(); // "1.2.3"
~~~

There are also incrementer methods for the pre-release version and the build
metadata.

~~~ java
import com.dorkbox.version.Version;

// Incrementing the pre-release version
Version v1 = Version("1.2.3-rc");                // considered as "rc.0"
Version v2 = v1.incrementPreReleaseVersion();    // "1.2.3-rc.1"
Version v3 = v2.incrementPreReleaseVersion();    // "1.2.3-rc.2"

// Incrementing the build metadata
Version v1 = Version("1.2.3-rc+build");          // considered as "build.0"
Version v2 = v1.incrementBuildMetadata();        // "1.2.3-rc+build.1"
Version v3 = v2.incrementBuildMetadata();        // "1.2.3-rc+build.2"
~~~

When incrementing the normal or pre-release versions the build metadata is
always dropped.

~~~ java
import com.dorkbox.version.Version;

Version v1 = Version("1.2.3-beta+build");

// Incrementing the normal version (note optional patch information)
Version v2 = v1.incrementMajorVersion();        // "2.0"
Version v2 = v1.incrementMajorVersion("alpha"); // "2.0-alpha"

Version v3 = v1.incrementMinorVersion();        // "1.3"
Version v3 = v1.incrementMinorVersion("alpha"); // "1.3-alpha"

Version v4 = v1.incrementPatchVersion();        // "1.2.4"
Version v4 = v1.incrementPatchVersion("alpha"); // "1.2.4-alpha"

// Incrementing the pre-release version
Version v2 = v1.incrementPreReleaseVersion();   // "1.2.3-beta.1"
~~~
**NOTE**: The discussion page https://github.com/mojombo/semver/issues/60 might
be of good use in better understanding some of the decisions made regarding the
incrementor methods.

### Comparing Versions ###
Comparing versions is easy. The `Version` class implements the
`Comparable` interface, it also overrides the `Object.equals` method and provides
some more methods for convenient comparing.

~~~ java
import com.dorkbox.version.Version;

Version v1 = Version("1.0.0-rc.1+build.1");
Version v2 = Version("1.3.7+build.2.b8f12d7");

int result = v1.compareTo(v2);  // < 0
boolean result = v1.equals(v2); // false

boolean result = v1.greaterThan(v2);           // false
boolean result = v1.greaterThanOrEqualTo(v2);  // false
boolean result = v1.lessThan(v2);              // true
boolean result = v1.lessThanOrEqualTo(v2);     // true
~~~

When determining version precedence the build metadata is ignored (SemVer p.10).

~~~ java
import com.dorkbox.version.Version;

Version v1 = Version("1.0.0+build.1");
Version v2 = Version("1.0.0+build.2");

int result = v1.compareTo(v2);  // = 0
boolean result = v1.equals(v2); // true
~~~

Sometimes, however, you might want to compare versions with the build metadata
in mind. For such cases the library provides a _comparator_ `Version.BUILD_AWARE_ORDER`
and a convenience method `Version.compareWithBuildsTo`.

~~~ java
import com.dorkbox.version.Version;

Version v1 = Version("1.0.0+build.1");
Version v2 = Version("1.0.0+build.2");

int result = Version.BUILD_AWARE_ORDER.compare(v1, v2);  // < 0

int result     = v1.compareTo(v2);            // = 0
boolean result = v1.equals(v2);               // true
int result     = v1.compareWithBuildsTo(v2);  // < 0
~~~


SemVer Expressions API (Ranges)
----------------------
Semantic Versioning library supports the SemVer Expressions API which is implemented as both
internal DSL and external DSL. The entry point for the API are
the `Version.satisfies` methods.

### Internal DSL ###
The internal DSL is implemented by the `CompositeExpression` class using fluent
interface. For convenience, it also provides the `Helper` class with static
helper methods.

~~~ java
import com.dorkbox.version.Version;
import static com.dorkbox.version.expr.CompositeExpression.Helper.*;

Version v = Version("1.0.0-beta");
boolean result = v.satisfies(gte("1.0.0").and(lt("2.0.0")));  // false
~~~

### External DSL ###
The BNF grammar for the external DSL can be found in the corresponding
[issue](https://github.com/zafarkhaja/jsemver/issues/1).

~~~ java
import com.dorkbox.version.Version;

Version v = Version("1.0.0-beta");
boolean result = v.satisfies(">=1.0.0 & <2.0.0");  // false
~~~

Below are examples of some common use cases, as well as syntactic sugar and some
other interesting capabilities of the SemVer Expressions external DSL.
* Wildcard Ranges (`*`|`X`|`x`) - `1.*` which is equivalent to `>=1.0.0 & <2.0.0`
* Tilde Ranges (`~`) - `~1.5` which is equivalent to `>=1.5.0 & <1.6.0`
* Hyphen Ranges (`-`) - `1.0-2.0` which is equivalent to `>=1.0.0 & <=2.0.0`
* Caret Ranges (`^`) - `^0.2.3` which is equivalent to `>=0.2.3 & <0.3.0`
* Partial Version Ranges - `1` which is equivalent to `1.X` or `>=1.0.0 & <2.0.0`
* Negation operator - `!(1.x)` which is equivalent to `<1.0.0 & >=2.0.0`
* Parenthesized expressions - `~1.3 | (1.4.* & !=1.4.5) | ~2`


Exception Handling
------------------
There are two types of errors that may arise while using the library
* `IllegalArgumentException` is thrown when the passed value is `NULL` or empty
  if a method accepts `string` argument or a negative integer if a method accepts
  `int` arguments.
* `ParseException` is thrown by methods that perform parsing of SemVer version
  strings or SemVer Expressions. There are few subtypes of the `ParseException`
  error
  - `UnexpectedCharacterException` is thrown when a SemVer version string contains
    an unexpected or illegal character
  - `LexerException` is thrown when a SemVer Expression contains an illegal character
  - `UnexpectedTokenException` is thrown when an unexpected token is encountered
    during the SemVer Expression parsing


Bugs and Features
-----------------
Bug reports and feature requests can be submitted at https://git.dorkbox.com/dorkbox/Version/issues.


Maven Info
---------
```
<dependencies>
    ...
    <dependency>
      <groupId>com.dorkbox</groupId>
      <artifactId>Version</artifactId>
      <version>3.1</version>
    </dependency>
</dependencies>
```

Gradle Info
---------
```
dependencies {
    ...
    implementation("com.dorkbox:Version:3.1")
}
```


License
-------
Java Semantic Versioning is licensed under the MIT License - see the `LICENSE` file for details.
