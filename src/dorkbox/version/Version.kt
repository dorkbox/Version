/*
 * Copyright 2021 dorkbox, llc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dorkbox.version

import dorkbox.updates.Updates.add
import dorkbox.version.expr.Expression
import dorkbox.version.expr.ExpressionParser.Companion.newInstance
import java.io.Serializable

/**
 * The `Version` class is the main class of the Java SemVer library.
 *
 *
 * This class implements the Facade design pattern.
 * It is also immutable, which makes the class thread-safe.
 *
 * @author Zafar Khaja <zafarkhaja></zafarkhaja>@gmail.com>
 * @author dorkbox, llc <info></info>@dorkbox.com>
 */
class Version
/**
 * Constructs a `Version` instance with the
 * normal version and the pre-release version.
 *
 * @param normal the normal version
 * @param preRelease the pre-release version
 */ @JvmOverloads internal constructor(
    /**
     * The normal version.
     */
    private val normal: NormalVersion,
    /**
     * The pre-release version.
     */
    private val preRelease: MetadataVersion? = MetadataVersion.NULL,
    /**
     * The build metadata.
     */
    private val build: MetadataVersion? = MetadataVersion.NULL

) : Comparable<Version>, Serializable {
    /**
     * A mutable builder for the immutable `Version` class.
     */
    class Builder {
        /**
         * The normal version string.
         */
        private var normal: String? = null

        /**
         * The pre-release version string.
         */
        private var preRelease: String? = null

        /**
         * The build metadata string.
         */
        private var metaData: String? = null

        /**
         * Constructs a `Builder` instance.
         */
        constructor() {}

        /**
         * Constructs a `Builder` instance with the
         * string representation of the normal version.
         *
         * @param normal the string representation of the normal version
         */
        constructor(normal: String?) {
            this.normal = normal
        }

        /**
         * Builds a `Version` object.
         *
         * @return a newly built `Version` instance
         *
         * @throws ParseException when invalid version string is provided
         * @throws UnexpectedCharacterException is a special case of `ParseException`
         */
        fun build(): Version {
            val sb = StringBuilder()
            if (isFilled(normal)) {
                sb.append(normal)
            }
            if (isFilled(preRelease)) {
                sb.append(PRE_RELEASE_PREFIX).append(preRelease)
            }
            if (isFilled(metaData)) {
                sb.append(BUILD_PREFIX).append(metaData)
            }
            return VersionParser.parseValidSemVer(sb.toString())
        }

        /**
         * Checks if a string has a usable value.
         *
         * @param str the string to check
         *
         * @return `true` if the string is filled or `false` otherwise
         */
        private fun isFilled(str: String?): Boolean {
            return str != null && !str.isEmpty()
        }

        /**
         * Sets the build metadata.
         *
         * @param metaData the string representation of the build metadata
         *
         * @return this builder instance
         */
        fun setBuildMetadata(metaData: String?): Builder {
            this.metaData = metaData
            return this
        }

        /**
         * Sets the normal version.
         *
         * @param normal the string representation of the normal version
         *
         * @return this builder instance
         */
        fun setNormalVersion(normal: String?): Builder {
            this.normal = normal
            return this
        }

        /**
         * Sets the pre-release version.
         *
         * @param preRelease the string representation of the pre-release version
         *
         * @return this builder instance
         */
        fun setPreReleaseVersion(preRelease: String?): Builder {
            this.preRelease = preRelease
            return this
        }
    }

    /**
     * A build-aware comparator.
     */
    private class BuildAwareOrder : Comparator<Version> {
        /**
         * Compares two `Version` instances taking
         * into account their build metadata.
         *
         *
         * When compared build metadata is divided into identifiers. The
         * numeric identifiers are compared numerically, and the alphanumeric
         * identifiers are compared in the ASCII sort order.
         *
         *
         * If one of the compared versions has no defined build
         * metadata, this version is considered to have a lower
         * precedence than that of the other.
         *
         * @return a negative integer, zero, or a positive integer as the
         * first argument is less than, equal to, or greater than the
         * second.
         */
        override fun compare(v1: Version, v2: Version): Int {
            var result = v1.compareTo(v2)
            if (result == 0) {
                result = v1.build!!.compareTo(v2.build!!)
                if (v1.build === MetadataVersion.NULL || v2.build === MetadataVersion.NULL) {
                    /*
                     * Build metadata should have a higher precedence
                     * than the associated normal version which is the
                     * opposite compared to pre-release versions.
                     */
                    result = -1 * result
                }
            }
            return result
        }
    }

    companion object {
        private const val serialVersionUID = -2008891377046871654L

        /**
         * A comparator that respects the build metadata when comparing versions.
         */
        val BUILD_AWARE_ORDER: Comparator<Version> = BuildAwareOrder()

        /**
         * A separator that separates the build metadata from
         * the normal version or the pre-release version.
         */
        private const val BUILD_PREFIX = "+"

        /**
         * A separator that separates the pre-release
         * version from the normal version.
         */
        private const val PRE_RELEASE_PREFIX = "-"

        /**
         * Gets the version number.
         */
        val version: String
            get() = "2.4"

        init {
            // Add this project to the updates system, which verifies this class + UUID + version information
            add(Version::class.java, "ccdb2067336845faa33b04ac57892205", version)
        }
    }

    /**
     * Creates a new instance of `Version` as a
     * result of parsing the specified version string.
     *
     * @param version the version string to parse
     *
     * @throws IllegalArgumentException if the input string is `NULL` or empty
     * @throws ParseException when invalid version string is provided
     * @throws UnexpectedCharacterException is a special case of `ParseException`
     */
    constructor(version: String) : this(VersionParser.parseValidSemVer(version))

    /**
     * Creates a new instance of `Version`
     * for the specified version numbers.
     *
     * @param majorAndMinor the major and minor version number, in double notation
     *
     * @throws IllegalArgumentException if a negative double is passed
     */
    constructor(majorAndMinor: Double) : this(VersionParser.parseValidSemVer(majorAndMinor))

    /**
     * Creates a new instance of `Version`
     * for the specified version numbers.
     *
     * @param major the major version number
     *
     * @throws IllegalArgumentException if a negative integer is passed
     */
    constructor(major: Int) : this(NormalVersion(major.toLong(), 0))

    /**
     * Creates a new instance of `Version`
     * for the specified version numbers.
     *
     * @param major the major version number
     * @param minor the minor version number
     *
     * @throws IllegalArgumentException if a negative integer is passed
     */
    constructor(major: Int, minor: Int) : this(NormalVersion(major.toLong(), minor.toLong()))

    /**
     * Creates a new instance of `Version`
     * for the specified version numbers.
     *
     * @param major the major version number
     * @param minor the minor version number
     * @param patch the patch version number
     *
     * @throws IllegalArgumentException if a negative integer is passed
     */
    constructor(major: Int, minor: Int, patch: Int) : this(NormalVersion(major.toLong(), minor.toLong(), patch.toLong()))

    /**
     * Creates a new instance of `Version` for the specified version numbers.
     *
     * @param version the version information to make a copy of
     */
    constructor(version: Version) : this(version.normal, version.preRelease, version.build)

    /**
     * The major version number.
     */
    val major: Long get() = normal.major

    /**
     * The minor version number.
     */
    val minor: Long get() = normal.minor

    /**
     * The patch version number.
     */
    val patch: Long get() = normal.patch

    /**
     * Returns the string representation of the normal version.
     *
     * @return the string representation of the normal version
     */
    val normalVersion: String
        get() = normal.toString()

    /**
     * Returns the string representation of the pre-release version.
     *
     * @return the string representation of the pre-release version
     */
    val preReleaseVersion: String
        get() = preRelease.toString()

    /**
     * Returns the string representation of the build metadata.
     *
     * @return the string representation of the build metadata
     */
    val buildMetadata: String
        get() = build.toString()





    /**
     * Compares this version to the other version.
     *
     *
     * This method does not take into account the versions' build
     * metadata. If you want to compare the versions' build metadata
     * use the `Version.compareWithBuildsTo` method or the
     * `Version.BUILD_AWARE_ORDER` comparator.
     *
     * @param other the other version to compare to
     *
     * @return a negative integer, zero or a positive integer if this version
     * is less than, equal to or greater the the specified version
     *
     * @see .BUILD_AWARE_ORDER
     *
     * @see .compareWithBuildsTo
     */
    override fun compareTo(other: Version): Int {
        var result = normal.compareTo(other.normal)
        if (result == 0) {
            result = preRelease!!.compareTo(other.preRelease!!)
        }
        return result
    }

    /**
     * Compare this version to the other version
     * taking into account the build metadata.
     *
     *
     * The method makes use of the `Version.BUILD_AWARE_ORDER` comparator.
     *
     * @param other the other version to compare to
     *
     * @return integer result of comparison compatible with
     * that of the `Comparable.compareTo` method
     *
     * @see .BUILD_AWARE_ORDER
     */
    fun compareWithBuildsTo(other: Version): Int {
        return BUILD_AWARE_ORDER.compare(this, other)
    }

    /**
     * Checks if this version equals the other version.
     *
     *
     * The comparison is done by the `Version.compareTo` method.
     *
     * @param other the other version to compare to
     *
     * @return `true` if this version equals the other version
     * or `false` otherwise
     *
     * @see .compareTo
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        return if (other !is Version) {
            false
        } else compareTo(other) == 0
    }

    /**
     * Sets the build metadata.
     *
     * @param build the build metadata to set
     *
     * @return a new instance of the `Version` class
     *
     * @throws IllegalArgumentException if the input string is `NULL` or empty
     * @throws ParseException when invalid version string is provided
     * @throws UnexpectedCharacterException is a special case of `ParseException`
     */
    fun setBuildMetadata(build: String): Version {
        return Version(normal, preRelease, VersionParser.parseBuild(build))
    }

    /**
     * Sets the pre-release version.
     *
     * @param preRelease the pre-release version to set
     *
     * @return a new instance of the `Version` class
     *
     * @throws IllegalArgumentException if the input string is `NULL` or empty
     * @throws ParseException when invalid version string is provided
     * @throws UnexpectedCharacterException is a special case of `ParseException`
     */
    fun setPreReleaseVersion(preRelease: String): Version {
        return Version(normal, VersionParser.parsePreRelease(preRelease))
    }

    /**
     * Checks if this version is greater than the other version.
     *
     * @param other the other version to compare to
     *
     * @return `true` if this version is greater than the other version
     * or `false` otherwise
     *
     * @see .compareTo
     */
    fun greaterThan(other: Version): Boolean {
        return compareTo(other) > 0
    }

    /**
     * Checks if this version is greater than or equal to the other version.
     *
     * @param other the other version to compare to
     *
     * @return `true` if this version is greater than or equal
     * to the other version or `false` otherwise
     *
     * @see .compareTo
     */
    fun greaterThanOrEqualTo(other: Version): Boolean {
        return compareTo(other) >= 0
    }

    override fun hashCode(): Int {
        var hash = 5
        hash = 97 * hash + normal.hashCode()
        hash = 97 * hash + preRelease.hashCode()
        return hash
    }

    /**
     * Increments the build metadata.
     *
     * @return a new instance of the `Version` class
     */
    fun incrementBuildMetadata(): Version {
        return Version(normal, preRelease, build!!.increment())
    }

    /**
     * Increments the major version.
     *
     * @return a new instance of the `Version` class
     */
    fun incrementMajorVersion(): Version {
        return Version(normal.incrementMajor())
    }

    /**
     * Increments the major version and appends the pre-release version.
     *
     * @param preRelease the pre-release version to append
     *
     * @return a new instance of the `Version` class
     *
     * @throws IllegalArgumentException if the input string is `NULL` or empty
     * @throws ParseException when invalid version string is provided
     * @throws UnexpectedCharacterException is a special case of `ParseException`
     */
    fun incrementMajorVersion(preRelease: String): Version {
        return Version(normal.incrementMajor(), VersionParser.parsePreRelease(preRelease))
    }

    /**
     * Increments the minor version.
     *
     * @return a new instance of the `Version` class
     */
    fun incrementMinorVersion(): Version {
        return Version(normal.incrementMinor())
    }

    /**
     * Increments the minor version and appends the pre-release version.
     *
     * @param preRelease the pre-release version to append
     *
     * @return a new instance of the `Version` class
     *
     * @throws IllegalArgumentException if the input string is `NULL` or empty
     * @throws ParseException when invalid version string is provided
     * @throws UnexpectedCharacterException is a special case of `ParseException`
     */
    fun incrementMinorVersion(preRelease: String): Version {
        return Version(normal.incrementMinor(), VersionParser.parsePreRelease(preRelease))
    }

    /**
     * Increments the patch version.
     *
     * @return a new instance of the `Version` class
     */
    fun incrementPatchVersion(): Version {
        return Version(normal.incrementPatch())
    }

    /**
     * Increments the patch version and appends the pre-release version.
     *
     * @param preRelease the pre-release version to append
     *
     * @return a new instance of the `Version` class
     *
     * @throws IllegalArgumentException if the input string is `NULL` or empty
     * @throws ParseException when invalid version string is provided
     * @throws UnexpectedCharacterException is a special case of `ParseException`
     */
    fun incrementPatchVersion(preRelease: String): Version {
        return Version(normal.incrementPatch(), VersionParser.parsePreRelease(preRelease))
    }

    /**
     * Increments the pre-release version.
     *
     * @return a new instance of the `Version` class
     */
    fun incrementPreReleaseVersion(): Version {
        return Version(normal, preRelease!!.increment())
    }

    /**
     * Checks if this version is compatible with the
     * other version in terms of their major versions.
     *
     *
     * When checking compatibility no assumptions
     * are made about the versions' precedence.
     *
     * @param other the other version to check with
     *
     * @return `true` if this version is compatible with
     * the other version or `false` otherwise
     */
    fun isMajorVersionCompatible(other: Version): Boolean {
        return this.major == other.major
    }

    /**
     * Checks if this version is compatible with the
     * other version in terms of their minor versions.
     *
     *
     * When checking compatibility no assumptions
     * are made about the versions' precedence.
     *
     * @param other the other version to check with
     *
     * @return `true` if this version is compatible with
     * the other version or `false` otherwise
     */
    fun isMinorVersionCompatible(other: Version): Boolean {
        return this.major == other.major && this.minor == other.minor
    }

    /**
     * Checks if this version is less than the other version.
     *
     * @param other the other version to compare to
     *
     * @return `true` if this version is less than the other version
     * or `false` otherwise
     *
     * @see .compareTo
     */
    fun lessThan(other: Version): Boolean {
        return compareTo(other) < 0
    }

    /**
     * Checks if this version is less than or equal to the other version.
     *
     * @param other the other version to compare to
     *
     * @return `true` if this version is less than or equal
     * to the other version or `false` otherwise
     *
     * @see .compareTo
     */
    fun lessThanOrEqualTo(other: Version): Boolean {
        return compareTo(other) <= 0
    }

    /**
     * Checks if this version satisfies the specified SemVer Expression string.
     *
     *
     * This method is a part of the SemVer Expressions API.
     *
     * @param expr the SemVer Expression string
     *
     * @return `true` if this version satisfies the specified
     * SemVer Expression or `false` otherwise
     *
     * @throws ParseException in case of a general parse error
     * @throws LexerException when encounters an illegal character
     * @throws UnexpectedTokenException when comes across an unexpected token
     */
    fun satisfies(expr: String?): Boolean {
        val parser = newInstance()
        return satisfies(parser.parse(expr!!))
    }

    /**
     * Checks if this version satisfies the specified SemVer Expression.
     *
     *
     * This method is a part of the SemVer Expressions API.
     *
     * @param expr the SemVer Expression
     *
     * @return `true` if this version satisfies the specified
     * SemVer Expression or `false` otherwise
     */
    fun satisfies(expr: Expression): Boolean {
        return expr.interpret(this)
    }

    override fun toString(): String {
        val sb = StringBuilder(normalVersion)
        if (!preReleaseVersion.isEmpty()) {
            sb.append(PRE_RELEASE_PREFIX).append(preReleaseVersion)
        }
        if (!buildMetadata.isEmpty()) {
            sb.append(BUILD_PREFIX).append(buildMetadata)
        }
        return sb.toString()
    }
    /**
     * Constructs a `Version` instance with the normal
     * version, the pre-release version and the build metadata.
     *
     * @param normal the normal version
     * @param preRelease the pre-release version
     * @param build the build metadata
     */
    /**
     * Constructs a `Version` instance with the normal version.
     *
     * @param normal the normal version
     */
}
