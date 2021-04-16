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
package dorkbox.version;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import dorkbox.version.NormalVersion;

/**
 * @author Zafar Khaja <zafarkhaja@gmail.com>
 */
@RunWith(Enclosed.class)
public
class NormalVersionTest {

    public static
    class CoreFunctionalityTest {

        @Test
        public
        void mustCompareMajorMinorAndPatchNumerically() {
            NormalVersion v = new NormalVersion(1, 2, 3);
            assertTrue(0 < v.compareTo(new NormalVersion(0, 2, 3)));
            assertTrue(0 == v.compareTo(new NormalVersion(1, 2, 3)));
            assertTrue(0 > v.compareTo(new NormalVersion(1, 2, 4)));
        }

        @Test
        public
        void mustConsistOfMajorMinorAndPatchVersions() {
            NormalVersion v = new NormalVersion(1, 2, 3);
            assertEquals(1, v.getMajor());
            assertEquals(2, v.getMinor());
            assertEquals(3, v.getPatch());
        }

        @Test
        public
        void mustIncreaseEachElementNumericallyByIncrementsOfOne() {
            int major = 1, minor = 2, patch = 3;
            NormalVersion v = new NormalVersion(major, minor, patch);
            NormalVersion incrementedPatch = v.incrementPatch();
            assertEquals(patch + 1, incrementedPatch.getPatch());
            NormalVersion incrementedMinor = v.incrementMinor();
            assertEquals(minor + 1, incrementedMinor.getMinor());
            NormalVersion incrementedMajor = v.incrementMajor();
            assertEquals(major + 1, incrementedMajor.getMajor());
        }

        @Test
        public
        void mustResetMinorAndPatchToZeroWhenMajorIsIncremented() {
            NormalVersion v = new NormalVersion(1, 2, 3);
            NormalVersion incremented = v.incrementMajor();
            assertEquals(2, incremented.getMajor());
            assertEquals(0, incremented.getMinor());
            assertEquals(0, incremented.getPatch());
        }

        @Test
        public
        void mustResetPatchToZeroWhenMinorIsIncremented() {
            NormalVersion v = new NormalVersion(1, 2, 3);
            NormalVersion incremented = v.incrementMinor();
            assertEquals(1, incremented.getMajor());
            assertEquals(3, incremented.getMinor());
            assertEquals(0, incremented.getPatch());
        }

        @Test
        public
        void mustResetPatchToZeroWhenMajorIsIncrementedAnPatchIsIgnoredForString() {
            NormalVersion v = new NormalVersion(1, 2, 3);
            NormalVersion incremented = v.incrementMajor();
            assertEquals("2.0", incremented.toString());
        }

        @Test
        public
        void mustResetPatchToZeroWhenMinorIsIncrementedAnPatchIsIgnoredForString() {
            NormalVersion v = new NormalVersion(1, 2, 3);
            NormalVersion incremented = v.incrementMinor();
            assertEquals("1.3", incremented.toString());
        }


        @Test
        public
        void patchMustBeRemovedWhenNotSpecified() {
            NormalVersion v = new NormalVersion(1, 2);
            assertEquals("1.2", v.toString());
        }

        @Test
        public
        void patchMustBeRemainWhenSpecified() {
            NormalVersion v = new NormalVersion(1, 2,0);
            assertEquals("1.2.0", v.toString());
        }

        @Test
        public
        void mustTakeTheFormOfXDotYDotZWhereXyzAreNonNegativeIntegers() {
            NormalVersion v = new NormalVersion(1, 2, 3);
            assertEquals("1.2.3", v.toString());
        }

        @Test
        public
        void shoudBeImmutable() {
            NormalVersion version = new NormalVersion(1, 2, 3);
            NormalVersion incementedMajor = version.incrementMajor();
            assertNotSame(version, incementedMajor);
            NormalVersion incementedMinor = version.incrementMinor();
            assertNotSame(version, incementedMinor);
            NormalVersion incementedPatch = version.incrementPatch();
            assertNotSame(version, incementedPatch);
        }

        @Test
        public
        void shouldAcceptOnlyNonNegativeMajorMinorAndPatchVersions() {
            int[][] invalidVersions = {{-1, 2, 3}, {1, -2, 3}, {1, 2, -3}};
            for (int[] versionParts : invalidVersions) {
                try {
                    new NormalVersion(versionParts[0], versionParts[1], versionParts[2]);
                } catch (IllegalArgumentException e) {
                    continue;
                }
                fail("Major, minor and patch versions MUST be non-negative integers.");
            }
        }

        @Test
        public
        void shouldOverrideEqualsMethod() {
            NormalVersion v1 = new NormalVersion(1, 2, 3);
            NormalVersion v2 = new NormalVersion(1, 2, 3);
            NormalVersion v3 = new NormalVersion(3, 2, 1);
            assertTrue(v1.equals(v2));
            assertFalse(v1.equals(v3));
        }
    }


    public static
    class EqualsMethodTest {

        @Test
        public
        void shouldBeConsistent() {
            NormalVersion v1 = new NormalVersion(1, 2, 3);
            NormalVersion v2 = new NormalVersion(1, 2, 3);
            assertTrue(v1.equals(v2));
            assertTrue(v1.equals(v2));
            assertTrue(v1.equals(v2));
        }

        @Test
        public
        void shouldBeReflexive() {
            NormalVersion v = new NormalVersion(1, 2, 3);
            assertTrue(v.equals(v));
        }

        @Test
        public
        void shouldBeSymmetric() {
            NormalVersion v1 = new NormalVersion(1, 2, 3);
            NormalVersion v2 = new NormalVersion(1, 2, 3);
            assertTrue(v1.equals(v2));
            assertTrue(v2.equals(v1));
        }

        @Test
        public
        void shouldBeTransitive() {
            NormalVersion v1 = new NormalVersion(1, 2, 3);
            NormalVersion v2 = new NormalVersion(1, 2, 3);
            NormalVersion v3 = new NormalVersion(1, 2, 3);
            assertTrue(v1.equals(v2));
            assertTrue(v2.equals(v3));
            assertTrue(v1.equals(v3));
        }

        @Test
        public
        void shouldReturnFalseIfOtherVersionIsNull() {
            NormalVersion v1 = new NormalVersion(1, 2, 3);
            NormalVersion v2 = null;
            assertFalse(v1.equals(v2));
        }

        @Test
        public
        void shouldReturnFalseIfOtherVersionIsOfDifferentType() {
            NormalVersion v = new NormalVersion(1, 2, 3);
            assertFalse(v.equals(new String("1.2.3")));
        }
    }


    public static
    class HashCodeMethodTest {

        @Test
        public
        void shouldReturnSameHashCodeIfVersionsAreEqual() {
            NormalVersion v1 = new NormalVersion(1, 2, 3);
            NormalVersion v2 = new NormalVersion(1, 2, 3);
            assertTrue(v1.equals(v2));
            assertEquals(v1.hashCode(), v2.hashCode());
        }
    }


    public static
    class ToStringMethodTest {

        @Test
        public
        void shouldReturnStringRepresentation() {
            NormalVersion v = new NormalVersion(1, 2, 3);
            assertEquals("1.2.3", v.toString());
        }
    }
}
