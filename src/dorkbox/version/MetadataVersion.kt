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

import java.io.Serializable
import java.util.*

/**
 * The `MetadataVersion` class is used to represent the pre-release version and the build metadata.
 *
 * @author Zafar Khaja <zafarkhaja></zafarkhaja>@gmail.com>
 */
internal open class MetadataVersion(
    /**
     * identifiers array containing the version's identifiers.
     */
    private val identifiers: Array<String>) : Comparable<MetadataVersion>, Serializable {

    companion object {
        private const val serialVersionUID = -1722535646295099910L

        /**
         * Null metadata, the implementation of the Null Object design pattern.
         */
        val NULL: MetadataVersion = NullMetadataVersion()
    }

    /**
     * Compares two arrays of identifiers.
     *
     * @param otherIdents the identifiers of the other version
     *
     * @return integer result of comparison compatible with the `Comparable.compareTo` method
     */
    private fun compareIdentifierArrays(otherIdents: Array<String>): Int {
        var result = 0
        val length = getLeastCommonArrayLength(identifiers, otherIdents)
        for (i in 0 until length) {
            result = compareIdentifiers(identifiers[i], otherIdents[i])
            if (result != 0) {
                break
            }
        }
        return result
    }

    /**
     * Compares two identifiers.
     *
     * @param ident1 the first identifier
     * @param ident2 the second identifier
     *
     * @return integer result of comparison compatible with the `Comparable.compareTo` method
     */
    private fun compareIdentifiers(ident1: String, ident2: String): Int {
        return if (isInt(ident1) && isInt(ident2)) {
            ident1.toInt() - ident2.toInt()
        } else {
            ident1.compareTo(ident2)
        }
    }

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
        if (other === NULL) {
            /**
             * Pre-release versions have a lower precedence than the associated normal version. (SemVer p.9)
             */
            return -1
        }
        var result = compareIdentifierArrays(other.identifiers)
        if (result == 0) {
            /**
             * A larger set of pre-release fields has a higher precedence than a smaller set, if all the
             * preceding identifiers are equal. (SemVer p.11)
             */
            result = identifiers.size - other.identifiers.size
        }
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        return if (other !is MetadataVersion) {
            false
        } else {
            compareTo(other) == 0
        }
    }

    /**
     * Returns the size of the smallest array.
     *
     * @param arr1 the first array
     * @param arr2 the second array
     *
     * @return the size of the smallest array
     */
    private fun getLeastCommonArrayLength(arr1: Array<String>, arr2: Array<String>): Int {
        return if (arr1.size <= arr2.size) arr1.size else arr2.size
    }

    override fun hashCode(): Int {
        return identifiers.contentHashCode()
    }

    /**
     * Increments the metadata version.
     *
     * @return a new instance of the `MetadataVersion` class
     */
    open fun increment(): MetadataVersion? {
        var ids = identifiers
        val lastId = ids[ids.size - 1]

        if (isInt(lastId)) {
            val intId = lastId.toInt()+1
            ids[ids.size - 1] = intId.toString()
        } else {
            ids = Arrays.copyOf(ids, ids.size + 1)
            ids[ids.size - 1] = 1.toString()
        }

        return MetadataVersion(ids)
    }

    /**
     * Checks if the specified string is an integer.
     *
     * @param str the string to check
     *
     * @return `true` if the specified string is an integer or `false` otherwise
     */
    private fun isInt(str: String): Boolean {
        try {
            str.toInt()
        } catch (e: NumberFormatException) {
            return false
        }
        return true
    }

    override fun toString(): String {
        val sb = StringBuilder()
        for (ident in identifiers) {
            sb.append(ident).append(".")
        }
        return sb.deleteCharAt(sb.lastIndexOf(".")).toString()
    }
}
