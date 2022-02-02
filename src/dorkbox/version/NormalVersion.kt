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

import java.io.Serializable

/**
 * The `NormalVersion` class represents the version core.
 *
 * This class is immutable and hence thread-safe.
 *
 * @author Zafar Khaja <zafarkhaja></zafarkhaja>@gmail.com>
 */
internal class NormalVersion

    /**
     * Constructs a `NormalVersion` with the
     * major, minor and patch version numbers.
     *
     * @param major the major version number
     * @param minor the minor version number
     * @param patch the patch version number
     * @param minorSpecified true if the minor version number was specified
     * @param patchSpecified true if the patch version number was specified
     *
     * @throws IllegalArgumentException if one of the version numbers is a negative integer
     */
    private constructor(
        val major: Long, val minor: Long, val patch: Long,
        val minorSpecified: Boolean, val patchSpecified: Boolean)

    : Comparable<NormalVersion>, Serializable {

    /**
     * Constructs a `NormalVersion` with the
     * major version number.
     *
     * @param major the major version number
     *
     * @throws IllegalArgumentException if one of the version numbers is a negative integer
     */
    constructor(major: Long) : this(major, 0, 0, false, false)

    /**
     * Constructs a `NormalVersion` with the
     * major and minor version numbers.
     *
     * @param major the major version number
     * @param minor the minor version number
     *
     * @throws IllegalArgumentException if one of the version numbers is a negative integer
     */
    constructor(major: Long, minor: Long) : this(major, minor, 0, true, false)

    /**
     * Constructs a `NormalVersion` with the
     * major, minor and patch version numbers.
     *
     * @param major the major version number
     * @param minor the minor version number
     * @param patch the patch version number
     *
     * @throws IllegalArgumentException if one of the version numbers is a negative integer
     */
    constructor(major: Long, minor: Long, patch: Long) : this(major, minor, patch, true, true)

    init {
        require(major >= 0 && minor >= 0 && patch >= 0) {
            "Major, minor and patch versions MUST be non-negative integers."
        }
    }


    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     *
     * The implementor must ensure <tt>sgn(x.compareTo(y)) ==
     * -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
     * implies that <tt>x.compareTo(y)</tt> must throw an exception iff
     * <tt>y.compareTo(x)</tt> throws an exception.)
     *
     *
     * The implementor must also ensure that the relation is transitive:
     * <tt>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</tt> implies
     * <tt>x.compareTo(z)&gt;0</tt>.
     *
     *
     * Finally, the implementor must ensure that <tt>x.compareTo(y)==0</tt>
     * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for
     * all <tt>z</tt>.
     *
     *
     * It is strongly recommended, but *not* strictly required that
     * <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>.  Generally speaking, any
     * class that implements the <tt>Comparable</tt> interface and violates
     * this condition should clearly indicate this fact.  The recommended
     * language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
     *
     *
     * In the foregoing description, the notation
     * <tt>sgn(</tt>*expression*<tt>)</tt> designates the mathematical
     * *signum* function, which is defined to return one of <tt>-1</tt>,
     * <tt>0</tt>, or <tt>1</tt> according to whether the value of
     * *expression* is negative, zero or positive.
     *
     * @param other the object to be compared.
     *
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     *
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException if the specified object's type prevents it
     * from being compared to this object.
     */
    override fun compareTo(other: NormalVersion): Int {
        var result = major - other.major
        if (result == 0L) {
            result = minor - other.minor
            if (result == 0L) {
                result = patch - other.patch
                if (result == 0L) {
                    return 0
                }
            }
        }
        return if (result < 0) -1 else 1
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        return if (other !is NormalVersion) {
            false
        } else compareTo(other) == 0
    }

    override fun hashCode(): Int {
        var hash = 17L
        hash = 31 * hash + major
        hash = 31 * hash + minor
        hash = 31 * hash + patch
        return hash.toInt()
    }

    /**
     * Increments the major version number.
     *
     * 1.2.3 -> 1.2
     * 1 -> 1.0
     *
     * @return a new instance of the `NormalVersion` class
     */
    fun incrementMajor(): NormalVersion {
        return NormalVersion(major + 1, 0, 0, true, false)
    }

    /**
     * Increments the minor version number.
     *
     * 1.2.3 -> 1.3
     * 1.2 -> 1.3
     *
     * @return a new instance of the `NormalVersion` class
     */
    fun incrementMinor(): NormalVersion {
        return NormalVersion(major, minor + 1, 0, false, false)
    }

    /**
     * Increments the patch version number.
     *
     * 1.2.3 -> 1.2.4
     *
     * @return a new instance of the `NormalVersion` class
     */
    fun incrementPatch(): NormalVersion {
        return NormalVersion(major, minor, patch + 1, true, true)
    }

    /**
     * Returns the string representation of this normal version.
     *
     *
     * A normal version number MUST take the form X.Y.Z or X.Y.Z where X, Y, and (optional) Z are
     * non-negative integers. X is the major version, Y is the minor version,
     * and Z is the patch version. (SemVer p.2)
     *
     * @return the string representation of this normal version
     */
    override fun toString(): String {
        return if (!minorSpecified && !patchSpecified && minor == 0L && patch == 0L) {
            String.format("%d", major)
        } else if (!patchSpecified && patch == 0L) {
            String.format("%d.%d", major, minor)
        } else {
            String.format("%d.%d.%d", major, minor, patch)
        }
    }

    companion object {
        private const val serialVersionUID = -5646200921684070847L
    }


}
