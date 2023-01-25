/*
 * Copyright 2023 dorkbox, llc
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

/**
 * The implementation of the Null Object design pattern, a `NullMetadataVersion` instance
 */
internal class NullMetadataVersion : MetadataVersion(arrayOf()) {

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     * ```
     * ```
     * The implementor must ensure <tt>sgn(x.compareTo(y)) ==
     * -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
     * implies that <tt>x.compareTo(y)</tt> must throw an exception iff
     * <tt>y.compareTo(x)</tt> throws an exception.)
     * ```
     * ```
     * The implementor must also ensure that the relation is transitive:
     * <tt>(x.compareTo(y)>0 &amp;&amp; y.compareTo(z)>0)</tt> implies
     * <tt>x.compareTo(z)>0</tt>.
     * ```
     * ```
     * Finally, the implementor must ensure that <tt>x.compareTo(y)==0</tt>
     * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for
     * all <tt>z</tt>.
     * ```
     * ```
     * It is strongly recommended, but *not* strictly required that
     * <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>.  Generally speaking, any
     * class that implements the <tt>Comparable</tt> interface and violates
     * this condition should clearly indicate this fact.  The recommended
     * language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
     * ```
     * ```
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
    override fun compareTo(other: MetadataVersion): Int {
        return if (!equals(other)) {
            /**
             * Pre-release versions have a lower precedence than the associated normal version. (SemVer p.9)
             */
            1
        } else 0
    }

    override fun equals(other: Any?): Boolean {
        return other is NullMetadataVersion
    }

    override fun hashCode(): Int {
        return 0
    }

    /**
     * @throws NullPointerException as Null metadata cannot be incremented
     */
    override fun increment(): MetadataVersion? {
        throw NullPointerException("Metadata version is NULL")
    }

    override fun toString(): String {
        return ""
    }
}
