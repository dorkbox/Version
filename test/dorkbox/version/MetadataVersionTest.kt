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

import org.junit.Assert
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith

/**
 * @author Zafar Khaja <zafarkhaja></zafarkhaja>@gmail.com>
 */
@RunWith(Enclosed::class)
class MetadataVersionTest {
    class CoreFunctionalityTest {
        @Test
        fun mustCompareEachIdentifierSeparately() {
            val v1 = MetadataVersion(arrayOf("beta", "2", "abc"))
            val v2 = MetadataVersion(arrayOf("beta", "1", "edf"))
            Assert.assertTrue(0 < v1.compareTo(v2))
        }

        @Test
        fun shouldAppendOneAsLastIdentifierIfLastOneIsAlphaNumericWhenIncrementing() {
            val v1 = MetadataVersion(arrayOf("alpha"))
            val v2 = v1.increment()
            Assert.assertEquals("alpha.1", v2.toString())
        }

        @Test
        fun shouldBeImmutable() {
            val v1 = MetadataVersion(arrayOf("alpha", "1"))
            val v2 = v1.increment()
            Assert.assertNotSame(v1, v2)
        }

        @Test
        fun shouldComapareDigitsOnlyIdentifiersNumerically() {
            val v1 = MetadataVersion(arrayOf("alpha", "123"))
            val v2 = MetadataVersion(arrayOf("alpha", "321"))
            Assert.assertTrue(0 > v1.compareTo(v2))
        }

        @Test
        fun shouldCompareIdentifiersCountIfCommonIdentifiersAreEqual() {
            val v1 = MetadataVersion(arrayOf("beta", "abc"))
            val v2 = MetadataVersion(arrayOf("beta", "abc", "def"))
            Assert.assertTrue(0 > v1.compareTo(v2))
        }

        @Test
        fun shouldCompareMixedIdentifiersLexicallyInAsciiSortOrder() {
            val v1 = MetadataVersion(arrayOf("beta", "abc"))
            val v2 = MetadataVersion(arrayOf("beta", "111"))
            Assert.assertTrue(0 < v1.compareTo(v2))
        }

        @Test
        fun shouldOverrideEqualsMethod() {
            val v1 = MetadataVersion(arrayOf("alpha", "123"))
            val v2 = MetadataVersion(arrayOf("alpha", "123"))
            val v3 = MetadataVersion(arrayOf("alpha", "321"))
            Assert.assertTrue(v1.equals(v2))
            Assert.assertFalse(v1.equals(v3))
        }

        @Test
        fun shouldProvideIncrementMethod() {
            val v1 = MetadataVersion(arrayOf("alpha", "1"))
            val v2 = v1.increment()
            Assert.assertEquals("alpha.2", v2.toString())
        }

        @Test
        fun shouldReturnNegativeWhenComparedToNullMetadataVersion() {
            val v1 = MetadataVersion(arrayOf())
            val v2 = MetadataVersion.NULL
            Assert.assertTrue(0 > v1.compareTo(v2))
        }
    }

    class NullMetadataVersionTest {
        @Test
        fun shouldBeEqualOnlyToItsType() {
            val v1 = MetadataVersion.NULL
            val v2 = MetadataVersion.NULL
            val v3 = MetadataVersion(arrayOf())
            Assert.assertTrue(v1.equals(v2))
            Assert.assertTrue(v2.equals(v1))
            Assert.assertFalse(v1.equals(v3))
        }

        @Test
        fun shouldReturnEmptyStringOnToString() {
            val v = MetadataVersion.NULL
            Assert.assertTrue(v.toString().isEmpty())
        }

        @Test
        fun shouldReturnPositiveWhenComparedToNonNullMetadataVersion() {
            val v1 = MetadataVersion.NULL
            val v2 = MetadataVersion(arrayOf())
            Assert.assertTrue(0 < v1.compareTo(v2))
        }

        @Test
        fun shouldReturnZeroOnHashCode() {
            val v = MetadataVersion.NULL
            Assert.assertEquals(0, v.hashCode().toLong())
        }

        @Test
        fun shouldReturnZeroWhenComparedToNullMetadataVersion() {
            val v1 = MetadataVersion.NULL
            val v2 = MetadataVersion.NULL
            Assert.assertTrue(0 == v1.compareTo(v2))
        }

        @Test
        fun shouldThrowNullPointerExceptionIfIncremented() {
            try {
                MetadataVersion.NULL.increment()
            } catch (e: NullPointerException) {
                return
            }
            Assert.fail("Should throw NullPointerException when incremented")
        }
    }

    class EqualsMethodTest {
        @Test
        fun shouldBeConsistent() {
            val v1 = MetadataVersion(arrayOf("alpha", "123"))
            val v2 = MetadataVersion(arrayOf("alpha", "123"))
            Assert.assertTrue(v1.equals(v2))
            Assert.assertTrue(v1.equals(v2))
            Assert.assertTrue(v1.equals(v2))
        }

        @Test
        fun shouldBeReflexive() {
            val v = MetadataVersion(arrayOf("alpha", "123"))
            Assert.assertTrue(v.equals(v))
        }

        @Test
        fun shouldBeSymmetric() {
            val v1 = MetadataVersion(arrayOf("alpha", "123"))
            val v2 = MetadataVersion(arrayOf("alpha", "123"))
            Assert.assertTrue(v1.equals(v2))
            Assert.assertTrue(v2.equals(v1))
        }

        @Test
        fun shouldBeTransitive() {
            val v1 = MetadataVersion(arrayOf("alpha", "123"))
            val v2 = MetadataVersion(arrayOf("alpha", "123"))
            val v3 = MetadataVersion(arrayOf("alpha", "123"))
            Assert.assertTrue(v1.equals(v2))
            Assert.assertTrue(v2.equals(v3))
            Assert.assertTrue(v1.equals(v3))
        }

        @Test
        fun shouldReturnFalseIfOtherVersionIsNull() {
            val v1 = MetadataVersion(arrayOf("alpha", "123"))
            val v2: MetadataVersion? = null
            Assert.assertFalse(v1.equals(v2))
        }

        @Test
        fun shouldReturnFalseIfOtherVersionIsOfDifferentType() {
            val v = MetadataVersion(arrayOf("alpha", "123"))
            Assert.assertFalse(v.equals("alpha.123"))
        }
    }

    class HashCodeMethodTest {
        @Test
        fun shouldReturnSameHashCodeIfVersionsAreEqual() {
            val v1 = MetadataVersion(arrayOf("alpha", "123"))
            val v2 = MetadataVersion(arrayOf("alpha", "123"))
            Assert.assertTrue(v1.equals(v2))
            Assert.assertEquals(v1.hashCode().toLong(), v2.hashCode().toLong())
        }
    }

    class ToStringMethodTest {
        @Test
        fun shouldReturnStringRepresentation() {
            val value = "beta.abc.def"
            val v = MetadataVersion(value.split("\\.").toTypedArray())
            Assert.assertEquals(value, v.toString())
        }
    }
}
