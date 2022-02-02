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
class NormalVersionTest {
    class CoreFunctionalityTest {
        @Test
        fun mustCompareMajorMinorAndPatchNumerically() {
            val v = NormalVersion(1, 2, 3)
            Assert.assertTrue(0 < v.compareTo(NormalVersion(0, 2, 3)))
            Assert.assertTrue(0 == v.compareTo(NormalVersion(1, 2, 3)))
            Assert.assertTrue(0 > v.compareTo(NormalVersion(1, 2, 4)))
        }

        @Test
        fun mustConsistOfMajorMinorAndPatchVersions() {
            val v = NormalVersion(1, 2, 3)
            Assert.assertEquals(1, v.major)
            Assert.assertEquals(2, v.minor)
            Assert.assertEquals(3, v.patch)
        }

        @Test
        fun mustIncreaseEachElementNumericallyByIncrementsOfOne() {
            val major = 1
            val minor = 2
            val patch = 3
            val v = NormalVersion(major.toLong(), minor.toLong(), patch.toLong())
            val incrementedPatch = v.incrementPatch()
            Assert.assertEquals((patch + 1).toLong(), incrementedPatch.patch)
            val incrementedMinor = v.incrementMinor()
            Assert.assertEquals((minor + 1).toLong(), incrementedMinor.minor)
            val incrementedMajor = v.incrementMajor()
            Assert.assertEquals((major + 1).toLong(), incrementedMajor.major)
        }

        @Test
        fun mustResetMinorAndPatchToZeroWhenMajorIsIncremented() {
            val v = NormalVersion(1, 2, 3)
            val incremented = v.incrementMajor()
            Assert.assertEquals(2, incremented.major)
            Assert.assertEquals(0, incremented.minor)
            Assert.assertEquals(0, incremented.patch)
        }

        @Test
        fun mustResetPatchToZeroWhenMinorIsIncremented() {
            val v = NormalVersion(1, 2, 3)
            val incremented = v.incrementMinor()
            Assert.assertEquals(1, incremented.major)
            Assert.assertEquals(3, incremented.minor)
            Assert.assertEquals(0, incremented.patch)
        }

        @Test
        fun mustResetPatchToZeroWhenMajorIsIncrementedAnPatchIsIgnoredForString() {
            val v = NormalVersion(1, 2, 3)
            val incremented = v.incrementMajor()
            Assert.assertEquals("2.0", incremented.toString())
        }

        @Test
        fun mustResetPatchToZeroWhenMinorIsIncrementedAnPatchIsIgnoredForString() {
            val v = NormalVersion(1, 2, 3)
            val incremented = v.incrementMinor()
            Assert.assertEquals("1.3", incremented.toString())
        }

        @Test
        fun patchMustBeRemovedWhenNotSpecified() {
            val v = NormalVersion(1, 2)
            Assert.assertEquals("1.2", v.toString())
        }

        @Test
        fun patchMustBeRemainWhenSpecified() {
            val v = NormalVersion(1, 2, 0)
            Assert.assertEquals("1.2.0", v.toString())
        }

        @Test
        fun mustTakeTheFormOfXDotYDotZWhereXyzAreNonNegativeIntegers() {
            val v = NormalVersion(1, 2, 3)
            Assert.assertEquals("1.2.3", v.toString())
        }

        @Test
        fun shoudBeImmutable() {
            val version = NormalVersion(1, 2, 3)
            val incementedMajor = version.incrementMajor()
            Assert.assertNotSame(version, incementedMajor)
            val incementedMinor = version.incrementMinor()
            Assert.assertNotSame(version, incementedMinor)
            val incementedPatch = version.incrementPatch()
            Assert.assertNotSame(version, incementedPatch)
        }

        @Test
        fun shouldAcceptOnlyNonNegativeMajorMinorAndPatchVersions() {
            val invalidVersions = arrayOf(intArrayOf(-1, 2, 3), intArrayOf(1, -2, 3), intArrayOf(1, 2, -3))
            for (versionParts in invalidVersions) {
                try {
                    NormalVersion(versionParts[0].toLong(), versionParts[1].toLong(), versionParts[2].toLong())
                } catch (e: IllegalArgumentException) {
                    continue
                }
                Assert.fail("Major, minor and patch versions MUST be non-negative integers.")
            }
        }

        @Test
        fun shouldOverrideEqualsMethod() {
            val v1 = NormalVersion(1, 2, 3)
            val v2 = NormalVersion(1, 2, 3)
            val v3 = NormalVersion(3, 2, 1)
            Assert.assertTrue(v1.equals(v2))
            Assert.assertFalse(v1.equals(v3))
        }
    }

    class EqualsMethodTest {
        @Test
        fun shouldBeConsistent() {
            val v1 = NormalVersion(1, 2, 3)
            val v2 = NormalVersion(1, 2, 3)
            Assert.assertTrue(v1.equals(v2))
            Assert.assertTrue(v1.equals(v2))
            Assert.assertTrue(v1.equals(v2))
        }

        @Test
        fun shouldBeReflexive() {
            val v = NormalVersion(1, 2, 3)
            Assert.assertTrue(v.equals(v))
        }

        @Test
        fun shouldBeSymmetric() {
            val v1 = NormalVersion(1, 2, 3)
            val v2 = NormalVersion(1, 2, 3)
            Assert.assertTrue(v1.equals(v2))
            Assert.assertTrue(v2.equals(v1))
        }

        @Test
        fun shouldBeTransitive() {
            val v1 = NormalVersion(1, 2, 3)
            val v2 = NormalVersion(1, 2, 3)
            val v3 = NormalVersion(1, 2, 3)
            Assert.assertTrue(v1.equals(v2))
            Assert.assertTrue(v2.equals(v3))
            Assert.assertTrue(v1.equals(v3))
        }

        @Test
        fun shouldReturnFalseIfOtherVersionIsNull() {
            val v1 = NormalVersion(1, 2, 3)
            val v2: NormalVersion? = null
            Assert.assertFalse(v1.equals(v2))
        }

        @Test
        fun shouldReturnFalseIfOtherVersionIsOfDifferentType() {
            val v = NormalVersion(1, 2, 3)
            Assert.assertFalse(v.equals("1.2.3"))
        }
    }

    class HashCodeMethodTest {
        @Test
        fun shouldReturnSameHashCodeIfVersionsAreEqual() {
            val v1 = NormalVersion(1, 2, 3)
            val v2 = NormalVersion(1, 2, 3)
            Assert.assertTrue(v1.equals(v2))
            Assert.assertEquals(v1.hashCode().toLong(), v2.hashCode().toLong())
        }
    }

    class ToStringMethodTest {
        @Test
        fun shouldReturnStringRepresentation() {
            val v = NormalVersion(1, 2, 3)
            Assert.assertEquals("1.2.3", v.toString())
        }
    }
}
