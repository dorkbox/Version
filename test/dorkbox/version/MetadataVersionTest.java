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

import dorkbox.version.MetadataVersion;

/**
 * @author Zafar Khaja <zafarkhaja@gmail.com>
 */
@RunWith(Enclosed.class)
public
class MetadataVersionTest {

    public static
    class CoreFunctionalityTest {

        @Test
        public
        void mustCompareEachIdentifierSeparately() {
            MetadataVersion v1 = new MetadataVersion(new String[] {"beta", "2", "abc"});
            MetadataVersion v2 = new MetadataVersion(new String[] {"beta", "1", "edf"});
            assertTrue(0 < v1.compareTo(v2));
        }

        @Test
        public
        void shouldAppendOneAsLastIdentifierIfLastOneIsAlphaNumericWhenIncrementing() {
            MetadataVersion v1 = new MetadataVersion(new String[] {"alpha"});
            MetadataVersion v2 = v1.increment();
            assertEquals("alpha.1", v2.toString());
        }

        @Test
        public
        void shouldBeImmutable() {
            MetadataVersion v1 = new MetadataVersion(new String[] {"alpha", "1"});
            MetadataVersion v2 = v1.increment();
            assertNotSame(v1, v2);
        }

        @Test
        public
        void shouldComapareDigitsOnlyIdentifiersNumerically() {
            MetadataVersion v1 = new MetadataVersion(new String[] {"alpha", "123"});
            MetadataVersion v2 = new MetadataVersion(new String[] {"alpha", "321"});
            assertTrue(0 > v1.compareTo(v2));
        }

        @Test
        public
        void shouldCompareIdentifiersCountIfCommonIdentifiersAreEqual() {
            MetadataVersion v1 = new MetadataVersion(new String[] {"beta", "abc"});
            MetadataVersion v2 = new MetadataVersion(new String[] {"beta", "abc", "def"});
            assertTrue(0 > v1.compareTo(v2));
        }

        @Test
        public
        void shouldCompareMixedIdentifiersLexicallyInAsciiSortOrder() {
            MetadataVersion v1 = new MetadataVersion(new String[] {"beta", "abc"});
            MetadataVersion v2 = new MetadataVersion(new String[] {"beta", "111"});
            assertTrue(0 < v1.compareTo(v2));
        }

        @Test
        public
        void shouldOverrideEqualsMethod() {
            MetadataVersion v1 = new MetadataVersion(new String[] {"alpha", "123"});
            MetadataVersion v2 = new MetadataVersion(new String[] {"alpha", "123"});
            MetadataVersion v3 = new MetadataVersion(new String[] {"alpha", "321"});
            assertTrue(v1.equals(v2));
            assertFalse(v1.equals(v3));
        }

        @Test
        public
        void shouldProvideIncrementMethod() {
            MetadataVersion v1 = new MetadataVersion(new String[] {"alpha", "1"});
            MetadataVersion v2 = v1.increment();
            assertEquals("alpha.2", v2.toString());
        }

        @Test
        public
        void shouldReturnNegativeWhenComparedToNullMetadataVersion() {
            MetadataVersion v1 = new MetadataVersion(new String[] {});
            MetadataVersion v2 = MetadataVersion.NULL;
            assertTrue(0 > v1.compareTo(v2));
        }
    }


    public static
    class NullMetadataVersionTest {

        @Test
        public
        void shouldBeEqualOnlyToItsType() {
            MetadataVersion v1 = MetadataVersion.NULL;
            MetadataVersion v2 = MetadataVersion.NULL;
            MetadataVersion v3 = new MetadataVersion(new String[] {});
            assertTrue(v1.equals(v2));
            assertTrue(v2.equals(v1));
            assertFalse(v1.equals(v3));
        }

        @Test
        public
        void shouldReturnEmptyStringOnToString() {
            MetadataVersion v = MetadataVersion.NULL;
            assertTrue(v.toString().isEmpty());
        }

        @Test
        public
        void shouldReturnPositiveWhenComparedToNonNullMetadataVersion() {
            MetadataVersion v1 = MetadataVersion.NULL;
            MetadataVersion v2 = new MetadataVersion(new String[] {});
            assertTrue(0 < v1.compareTo(v2));
        }

        @Test
        public
        void shouldReturnZeroOnHashCode() {
            MetadataVersion v = MetadataVersion.NULL;
            assertEquals(0, v.hashCode());
        }

        @Test
        public
        void shouldReturnZeroWhenComparedToNullMetadataVersion() {
            MetadataVersion v1 = MetadataVersion.NULL;
            MetadataVersion v2 = MetadataVersion.NULL;
            assertTrue(0 == v1.compareTo(v2));
        }

        @Test
        public
        void shouldThrowNullPointerExceptionIfIncremented() {
            try {
                MetadataVersion.NULL.increment();
            } catch (NullPointerException e) {
                return;
            }
            fail("Should throw NullPointerException when incremented");
        }
    }


    public static
    class EqualsMethodTest {

        @Test
        public
        void shouldBeConsistent() {
            MetadataVersion v1 = new MetadataVersion(new String[] {"alpha", "123"});
            MetadataVersion v2 = new MetadataVersion(new String[] {"alpha", "123"});
            assertTrue(v1.equals(v2));
            assertTrue(v1.equals(v2));
            assertTrue(v1.equals(v2));
        }

        @Test
        public
        void shouldBeReflexive() {
            MetadataVersion v = new MetadataVersion(new String[] {"alpha", "123"});
            assertTrue(v.equals(v));
        }

        @Test
        public
        void shouldBeSymmetric() {
            MetadataVersion v1 = new MetadataVersion(new String[] {"alpha", "123"});
            MetadataVersion v2 = new MetadataVersion(new String[] {"alpha", "123"});
            assertTrue(v1.equals(v2));
            assertTrue(v2.equals(v1));
        }

        @Test
        public
        void shouldBeTransitive() {
            MetadataVersion v1 = new MetadataVersion(new String[] {"alpha", "123"});
            MetadataVersion v2 = new MetadataVersion(new String[] {"alpha", "123"});
            MetadataVersion v3 = new MetadataVersion(new String[] {"alpha", "123"});
            assertTrue(v1.equals(v2));
            assertTrue(v2.equals(v3));
            assertTrue(v1.equals(v3));
        }

        @Test
        public
        void shouldReturnFalseIfOtherVersionIsNull() {
            MetadataVersion v1 = new MetadataVersion(new String[] {"alpha", "123"});
            MetadataVersion v2 = null;
            assertFalse(v1.equals(v2));
        }

        @Test
        public
        void shouldReturnFalseIfOtherVersionIsOfDifferentType() {
            MetadataVersion v = new MetadataVersion(new String[] {"alpha", "123"});
            assertFalse(v.equals(new String("alpha.123")));
        }
    }


    public static
    class HashCodeMethodTest {

        @Test
        public
        void shouldReturnSameHashCodeIfVersionsAreEqual() {
            MetadataVersion v1 = new MetadataVersion(new String[] {"alpha", "123"});
            MetadataVersion v2 = new MetadataVersion(new String[] {"alpha", "123"});
            assertTrue(v1.equals(v2));
            assertEquals(v1.hashCode(), v2.hashCode());
        }
    }


    public static
    class ToStringMethodTest {

        @Test
        public
        void shouldReturnStringRepresentation() {
            String value = "beta.abc.def";
            MetadataVersion v = new MetadataVersion(value.split("\\."));
            assertEquals(value, v.toString());
        }
    }
}
