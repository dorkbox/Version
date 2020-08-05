/*
 * The MIT License
 *
 * Copyright 2012-2018 The SemanticVersioning Authors (see LICENSE file in root of project).
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.dorkbox.version;

import java.io.Serializable;

/**
 * The {@code NormalVersion} class represents the version core.
 * <p>
 * This class is immutable and hence thread-safe.
 *
 * @author Zafar Khaja <zafarkhaja@gmail.com>
 */
class NormalVersion implements Comparable<NormalVersion>, Serializable {

    private static final long serialVersionUID = -5646200921684070847L;

    /**
     * The major version number.
     */
    private final long major;

    /**
     * The minor version number.
     */
    private final long minor;

    /**
     * The patch version number.
     */
    private final long patch;
    protected final boolean patchSpecified;

    /**
     * Constructs a {@code NormalVersion} with the
     * major and minor version numbers.
     *
     * @param major the major version number
     * @param minor the minor version number
     *
     * @throws IllegalArgumentException if one of the version numbers is a negative integer
     */
    NormalVersion(long major, long minor) {
        this(major, minor, 0, false);
    }

    /**
     * Constructs a {@code NormalVersion} with the
     * major, minor and patch version numbers.
     *
     * @param major the major version number
     * @param minor the minor version number
     * @param patch the patch version number
     *
     * @throws IllegalArgumentException if one of the version numbers is a negative integer
     */
    NormalVersion(long major, long minor, long patch) {
        this(major, minor, patch, true);
    }

    /**
     * Constructs a {@code NormalVersion} with the
     * major, minor and patch version numbers.
     *
     * @param major the major version number
     * @param minor the minor version number
     * @param patch the patch version number
     * @param patchSpecified true if the patch version number was specified
     *
     * @throws IllegalArgumentException if one of the version numbers is a negative integer
     */
    private
    NormalVersion(long major, long minor, long patch, boolean patchSpecified) {
        if (major < 0 || minor < 0 || patch < 0) {
            throw new IllegalArgumentException("Major, minor and patch versions MUST be non-negative integers.");
        }

        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.patchSpecified = patchSpecified;
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * <p>The implementor must ensure <tt>sgn(x.compareTo(y)) ==
     * -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
     * implies that <tt>x.compareTo(y)</tt> must throw an exception iff
     * <tt>y.compareTo(x)</tt> throws an exception.)
     *
     * <p>The implementor must also ensure that the relation is transitive:
     * <tt>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</tt> implies
     * <tt>x.compareTo(z)&gt;0</tt>.
     *
     * <p>Finally, the implementor must ensure that <tt>x.compareTo(y)==0</tt>
     * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for
     * all <tt>z</tt>.
     *
     * <p>It is strongly recommended, but <i>not</i> strictly required that
     * <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>.  Generally speaking, any
     * class that implements the <tt>Comparable</tt> interface and violates
     * this condition should clearly indicate this fact.  The recommended
     * language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
     *
     * <p>In the foregoing description, the notation
     * <tt>sgn(</tt><i>expression</i><tt>)</tt> designates the mathematical
     * <i>signum</i> function, which is defined to return one of <tt>-1</tt>,
     * <tt>0</tt>, or <tt>1</tt> according to whether the value of
     * <i>expression</i> is negative, zero or positive.
     *
     * @param other the object to be compared.
     *
     * @return a negative integer, zero, or a positive integer as this object
     *         is less than, equal to, or greater than the specified object.
     *
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException if the specified object's type prevents it
     *         from being compared to this object.
     */
    @Override
    public
    int compareTo(NormalVersion other) {
        long result = major - other.major;
        if (result == 0) {
            result = minor - other.minor;
            if (result == 0) {
                result = patch - other.patch;
                if (result == 0) {
                    return 0;
                }
            }
        }
        return result < 0 ? -1 : 1;
    }

    @Override
    public
    boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof NormalVersion)) {
            return false;
        }
        return compareTo((NormalVersion) other) == 0;
    }

    /**
     * Returns the major version number.
     *
     * @return the major version number
     */
    long getMajor() {
        return major;
    }

    /**
     * Returns the minor version number.
     *
     * @return the minor version number
     */
    long getMinor() {
        return minor;
    }

    /**
     * Returns the patch version number.
     *
     * @return the patch version number
     */
    long getPatch() {
        return patch;
    }

    @Override
    public
    int hashCode() {
        long hash = 17L;
        hash = 31 * hash + major;
        hash = 31 * hash + minor;
        hash = 31 * hash + patch;
        return (int) hash;
    }

    /**
     * Increments the major version number.
     *
     * @return a new instance of the {@code NormalVersion} class
     */
    NormalVersion incrementMajor() {
        return new NormalVersion(major + 1, 0, 0, false);
    }

    /**
     * Increments the minor version number.
     *
     * @return a new instance of the {@code NormalVersion} class
     */
    NormalVersion incrementMinor() {
        return new NormalVersion(major, minor + 1, 0, false);
    }

    /**
     * Increments the patch version number.
     *
     * @return a new instance of the {@code NormalVersion} class
     */
    NormalVersion incrementPatch() {
        return new NormalVersion(major, minor, patch + 1, true);
    }

    /**
     * Returns the string representation of this normal version.
     * <p>
     * A normal version number MUST take the form X.Y.Z or X.Y.Z where X, Y, and (optional) Z are
     * non-negative integers. X is the major version, Y is the minor version,
     * and Z is the patch version. (SemVer p.2)
     *
     * @return the string representation of this normal version
     */
    @Override
    public
    String toString() {
        if (!patchSpecified && patch == 0) {
            return String.format("%d.%d", major, minor);
        }
        else {
            return String.format("%d.%d.%d", major, minor, patch);
        }
    }
}
