Java Semantic Versioning Changelog
=====================

### 1.0 (Aug 3, 2018) ###
* Updated to Semantic Versioning 2.1, with optional patch information exception
    * If the patch version information is excluded (or the major/minor version is incremented), then the patch version is dropped. The patch version information is only used if non-zero.
* Added Gradle build files
* Added License files
* Renamed Version.valueOf -> Version.from
* Renamed Version.forInteger -> Version.from



### 0.11.0 (Oct 29, 2016) ###
* [Support long versions](https://github.com/lbordowitz/jsemver/commit/38bacfe8c1331b3cb5dfebb5639e6aee51e9b79e)

### 0.10.0 (Oct 24, 2016) ###
* [Add methods for checking major and minor compatibility](https://github.com/zafarkhaja/jsemver/commit/f6abcf5dd79ea9ee8e076cdeff71644012c1f7ac)
* [Fix Travis build](https://github.com/zafarkhaja/jsemver/commit/a34d6f2a7631d576304b91e6e388cd0c776edd58)
* [Upgrade the POM for easy deployment](https://github.com/zafarkhaja/jsemver/commit/2a77345c5f5a58aa67edb675b437bb5b2eb17e10)
* [Fix a typo in CHANGELOG](https://github.com/zafarkhaja/jsemver/commit/c1de46d8b62fe0aa0df08d89424060a894335eb6)

### 0.9.1 (Sep 18, 2015) ###
* [Convert to Eclipse plugin](https://github.com/OSSIndex/jsemver/commit/ac1d5121e3e2e2a11de67347de4044ce615523d9)

### 0.9.0 (Mar 19, 2015) ###
* [[#7](https://github.com/zafarkhaja/jsemver/issues/6)] Implemented internal DSL for the SemVer Expressions and Ranges
* [[PR#18](https://github.com/zafarkhaja/jsemver/pull/18)] Introduced partial compatibility with `node-semver` ranges
* Reworked the BNF grammar of the SemVer Expressions
* Made some enhancements and improvements
* Updated project dependencies and Maven plugins

### 0.8.0 (Aug 18, 2014) ###
* Rearranged exceptions hierarchy
* Refactored `VersionParser` and `ExpressionParser`
* [[#7](https://github.com/zafarkhaja/jsemver/issues/7)] Improved error handling and error reporting in the Parsers
* Made some minor improvements and bug fixes
* Updated the `README` file with the "Exception Handling" section

### 0.7.2 (Dec 30, 2013) ###
* [[#8](https://github.com/zafarkhaja/jsemver/issues/8)] Fixed `Version.hashCode()` to comply with `Version.equals()`

### 0.7.1 (Dec 01, 2013) ###
* [[#5](https://github.com/zafarkhaja/jsemver/issues/5)] Got rid of 'unchecked' warnings
* Made minor Javadoc corrections
* Made small code improvements
* Configured `maven-compiler-plugin` to show all warnings
* Updated the `CHANGELOG.md` and `README.md` files

### 0.7.0 (Nov 16, 2013) ###
* Adapted the library to the SemVer 2.0.0
* [[#1](https://github.com/zafarkhaja/jsemver/issues/1)] Created the SemVer Expressions Parser
* [[#2](https://github.com/zafarkhaja/jsemver/issues/2)] Added Javadoc to the source code
* [[#4](https://github.com/zafarkhaja/jsemver/issues/4)] Deployed to the Maven Central Repository
* Implemented a parser instead of RegExps for the version parsing
* Created the `MetadataVersion.NULL` object, refactored
* Made some refactoring and minor improvements to the code
* Updated and reformated the `README.md` file
* Renamed the `artifactId` and changed the `name` in the `pom.xml` file
* Updated the JUnit dependency to 4.11
* Prepared the `pom.xml` file for the repository
* Created the `CHANGELOG.md` file

### 0.6.0 (Mar 31, 2013) ###
* Adapted the library to the SemVer 2.0.0-rc.2
* Added setters for the pre-release version and the build metadata
* Added the incrementors for the pre-release version and the build metadata
* Created the `Version.Builder` class
* Created the `Version.BUILD_AWARE_ORDER` comparator
* Added support for the Travis CI
* Made minor refactoring
* Updated the `README.md` file

### 0.5.0 (Mar 8, 2013) ###
* Updated the `README.md` file
* Made minor refactoring

### 0.4.2 (Mar 6, 2013) ###
* Renamed the `README` file to `README.md`

### 0.4.1 (Mar 6, 2013) ###
* Bumped the version for the previous release

### 0.4.0 (Mar 6, 2013) ###
* Made the version classes immutable
* Made minor imrovements to the code

### 0.3.0 (Mar 4, 2013) ###
* Added incrementor methods for the `NormalVersion`

### 0.2.1 (Mar 3, 2013) ###
* Made minor improvements to the code

### 0.2.0 (Mar 3, 2013) ###
* Separated the logic into different classes

### 0.1.0 (Jan 27, 2013) ###
* Implemented basic functionality, single `Version` class
