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

import dorkbox.version.expr.CompositeExpression.Helper.gte
import dorkbox.version.expr.CompositeExpression.Helper.lt
import org.junit.Assert
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import java.io.*

/**
 * @author Zafar Khaja <zafarkhaja></zafarkhaja>@gmail.com>
 */
@RunWith(Enclosed::class)
class VersionTest {
    class CoreFunctionalityTest {
        // MODIFY TEST
        @Test
        fun onlyMajor() {
            // not valid, but we should STILL be able to parse it (we just cannot write it).
            val v = Version("20180813")
            Assert.assertEquals(20180813, v.major)
            Assert.assertEquals(0, v.minor)
        }

        // MODIFY TEST
        @Test
        fun mayHaveFinalDot() {
            // not valid, but we should STILL be able to parse it (we just cannot write it).
            val v = Version("4.1.50.Final")
            Assert.assertEquals("Final", v.buildMetadata)
        }

        // MODIFY TEST
        @Test
        fun mayHaveFinalModifiedDot() {
            // not valid, but we should STILL be able to parse it (we just cannot write it).
            val v = Version("4.1.Final")
            Assert.assertEquals("Final", v.buildMetadata)
        }

        // MODIFY TEST
        @Test
        fun mayHaveNumbersAsBuild() {
            // not valid, but we should STILL be able to parse it (we just cannot write it).
            val v = Version("4.5.4.201711221230-r")
            Assert.assertEquals("201711221230-r", v.buildMetadata)
        }

        // MODIFY TEST
        @Test
        fun mayHavePreRelease() {
            // not valid, but we should STILL be able to parse it (we just cannot write it).
            val v = Version("4.1-alpha")
            Assert.assertEquals("alpha", v.preReleaseVersion)
        }

        // MODIFY TEST
        @Test
        fun mayHavePreReleaseWithUnderscore() {
            // not valid, but we should STILL be able to parse it (we just cannot write it).
            val v = Version("4.1_alpha")
            Assert.assertEquals("alpha", v.preReleaseVersion)
        }

        // MODIFY TEST
        @Test
        fun mayHaveBuildAppendedWithPlus() {
            // not valid, but we should STILL be able to parse it (we just cannot write it).
            val v = Version("4.1+alpha")
            Assert.assertEquals("alpha", v.buildMetadata)
        }

        // MODIFY TEST
        @Test
        fun modifiedShouldBeAbleToCompareWithoutIgnoringBuildMetadata() {
            val v1 = Version("1.3-beta+build.1")
            val v2 = Version("1.3-beta+build.2")
            Assert.assertTrue(0 == v1.compareTo(v2))
            Assert.assertTrue(0 > v1.compareWithBuildsTo(v2))
        }

        @Test
        fun mayHaveBuildFollowingPatchOrPreReleaseAppendedWithPlus() {
            val v = Version("1.2.3+build")
            Assert.assertEquals("build", v.buildMetadata)
        }

        @Test
        fun mayHavePreReleaseFollowingPatchAppendedWithHyphen() {
            val v = Version("1.2.3-alpha")
            Assert.assertEquals("alpha", v.preReleaseVersion)
        }

        @Test
        fun preReleaseShouldHaveLowerPrecedenceThanAssociatedNormal() {
            val v1 = Version("1.3.7")
            val v2 = Version("1.3.7-alpha")
            Assert.assertTrue(0 < v1.compareTo(v2))
            Assert.assertTrue(0 > v2.compareTo(v1))
        }

        @Test
        fun shouldBeAbleToCompareWithoutIgnoringBuildMetadata() {
            val v1 = Version("1.3.7-beta+build.1")
            val v2 = Version("1.3.7-beta+build.2")
            Assert.assertTrue(0 == v1.compareTo(v2))
            Assert.assertTrue(0 > v1.compareWithBuildsTo(v2))
        }

        @Test
        fun shouldBeImmutable() {
            val version = Version("1.2.3-alpha+build")
            val incementedMajor = version.incrementMajorVersion()
            Assert.assertNotSame(version, incementedMajor)
            val incementedMinor = version.incrementMinorVersion()
            Assert.assertNotSame(version, incementedMinor)
            val incementedPatch = version.incrementPatchVersion()
            Assert.assertNotSame(version, incementedPatch)
            val preReleaseSet = version.setPreReleaseVersion("alpha")
            Assert.assertNotSame(version, preReleaseSet)
            val buildSet = version.setBuildMetadata("build")
            Assert.assertNotSame(version, buildSet)
            val incrementedPreRelease = version.incrementPreReleaseVersion()
            Assert.assertNotSame(version, incrementedPreRelease)
            val incrementedBuild = version.incrementBuildMetadata()
            Assert.assertNotSame(version, incrementedBuild)
        }

        @Test
        fun shouldCheckIfMajorVersionCompatible() {
            val v1 = Version("1.0.0")
            val v2 = Version("1.2.3")
            val v3 = Version("2.0.0")
            Assert.assertTrue(v1.isMajorVersionCompatible(v2))
            Assert.assertFalse(v1.isMajorVersionCompatible(v3))
        }

        @Test
        fun shouldCheckIfMajorVersionCompatibleSimple() {
            val v1 = Version("1.0")
            val v2 = Version("1.2")
            val v3 = Version("2.0")
            Assert.assertTrue(v1.isMajorVersionCompatible(v2))
            Assert.assertFalse(v1.isMajorVersionCompatible(v3))
        }

        @Test
        fun shouldCheckIfMinorVersionCompatible() {
            val v1 = Version("1.1.1")
            val v2 = Version("1.1.2")
            val v3 = Version("1.2.3")
            Assert.assertTrue(v1.isMinorVersionCompatible(v2))
            Assert.assertFalse(v1.isMinorVersionCompatible(v3))
        }

        @Test
        fun shouldCheckIfMinorVersionCompatibleSimple() {
            val v1 = Version("1.1")
            val v2 = Version("1.1")
            val v3 = Version("1.2")
            Assert.assertTrue(v1.isMinorVersionCompatible(v2))
            Assert.assertFalse(v1.isMinorVersionCompatible(v3))
        }

        @Test
        fun shouldCheckIfVersionSatisfiesExpression() {
            val v = Version("2.0.0-beta")
            Assert.assertTrue(v.satisfies(gte("1.0.0").and(lt("2.0.0"))))
            Assert.assertFalse(v.satisfies(gte("2.0.0").and(lt("3.0.0"))))
        }

        @Test
        fun shouldCorrectlyCompareAllVersionsFromSpecification() {
            val versions = arrayOf(
                "1.0.0-alpha",
                "1.0.0-alpha.1",
                "1.0.0-alpha.beta",
                "1.0.0-beta",
                "1.0.0-beta.2",
                "1.0.0-beta.11",
                "1.0.0-rc.1",
                "1.0.0",
                "2.0.0",
                "2.1.0",
                "2.1.1"
            )
            for (i in 1 until versions.size) {
                val v1 = Version(versions[i - 1])
                val v2 = Version(versions[i])
                Assert.assertTrue(v1.lessThan(v2))
            }
        }

        @Test
        fun shouldDropBuildMetadataWhenIncrementing() {
            val v = Version("1.2.3-alpha+build")
            val major1 = v.incrementMajorVersion()
            Assert.assertEquals("2.0", major1.toString())
            val major2 = v.incrementMajorVersion("beta")
            Assert.assertEquals("2.0-beta", major2.toString())
            val minor1 = v.incrementMinorVersion()
            Assert.assertEquals("1.3", minor1.toString())
            val minor2 = v.incrementMinorVersion("beta")
            Assert.assertEquals("1.3-beta", minor2.toString())
            val patch1 = v.incrementPatchVersion()
            Assert.assertEquals("1.2.4", patch1.toString())
            val patch2 = v.incrementPatchVersion("beta")
            Assert.assertEquals("1.2.4-beta", patch2.toString())
        }

        @Test
        fun shouldDropBuildMetadataWhenIncrementingPreReleaseVersion() {
            val v1 = Version("1.0.0-beta.1+build")
            val v2 = v1.incrementPreReleaseVersion()
            Assert.assertEquals("1.0.0-beta.2", v2.toString())
        }

        @Test
        fun shouldDropBuildMetadataWhenSettingPreReleaseVersion() {
            val v1 = Version("1.0.0-alpha+build")
            val v2 = v1.setPreReleaseVersion("beta")
            Assert.assertEquals("1.0.0-beta", v2.toString())
        }

        @Test
        fun shouldHaveGreaterThanMethodReturningBoolean() {
            val v1 = Version("2.3.7")
            val v2 = Version("1.3.7")
            Assert.assertTrue(v1.greaterThan(v2))
            Assert.assertFalse(v2.greaterThan(v1))
            Assert.assertFalse(v1.greaterThan(v1))
        }

        @Test
        fun shouldHaveGreaterThanOrEqualToMethodReturningBoolean() {
            val v1 = Version("2.3.7")
            val v2 = Version("1.3.7")
            Assert.assertTrue(v1.greaterThanOrEqualTo(v2))
            Assert.assertFalse(v2.greaterThanOrEqualTo(v1))
            Assert.assertTrue(v1.greaterThanOrEqualTo(v1))
        }

        @Test
        fun shouldHaveLessThanMethodReturningBoolean() {
            val v1 = Version("2.3.7")
            val v2 = Version("1.3.7")
            Assert.assertFalse(v1.lessThan(v2))
            Assert.assertTrue(v2.lessThan(v1))
            Assert.assertFalse(v1.lessThan(v1))
        }

        @Test
        fun shouldHaveLessThanOrEqualToMethodReturningBoolean() {
            val v1 = Version("2.3.7")
            val v2 = Version("1.3.7")
            Assert.assertFalse(v1.lessThanOrEqualTo(v2))
            Assert.assertTrue(v2.lessThanOrEqualTo(v1))
            Assert.assertTrue(v1.lessThanOrEqualTo(v1))
        }

        @Test
        fun shouldHaveStaticFactoryMethod() {
            val v = Version("1.0.0-rc.1+build.1")
            Assert.assertEquals(1, v.major)
            Assert.assertEquals(0, v.minor)
            Assert.assertEquals(0, v.patch)
            Assert.assertEquals("1.0.0", v.normalVersion)
            Assert.assertEquals("rc.1", v.preReleaseVersion)
            Assert.assertEquals("build.1", v.buildMetadata)
        }

        @Test
        fun shouldIgnoreBuildMetadataWhenDeterminingVersionPrecedence() {
            val v1 = Version("1.3.7-beta")
            val v2 = Version("1.3.7-beta+build.1")
            val v3 = Version("1.3.7-beta+build.2")
            Assert.assertTrue(0 == v1.compareTo(v2))
            Assert.assertTrue(0 == v1.compareTo(v3))
            Assert.assertTrue(0 == v2.compareTo(v3))
        }

        @Test
        fun shouldIncrementMajorVersionWithPreReleaseIfProvided() {
            val v = Version("1.2.3")
            val incrementedMajor = v.incrementMajorVersion("beta")
            Assert.assertEquals("2.0-beta", incrementedMajor.toString())
        }

        @Test
        fun shouldIncrementMinorVersionWithPreReleaseIfProvided() {
            val v = Version("1.2.3")
            val incrementedMinor = v.incrementMinorVersion("alpha")
            Assert.assertEquals("1.3-alpha", incrementedMinor.toString())
        }

        @Test
        fun shouldIncrementPatchVersionWithPreReleaseIfProvided() {
            val v = Version("1.2.3")
            val incrementedPatch = v.incrementPatchVersion("rc")
            Assert.assertEquals("1.2.4-rc", incrementedPatch.toString())
        }

        @Test
        fun shouldOverrideEqualsMethod() {
            val v1 = Version("2.3.7")
            val v2 = Version("2.3.7")
            val v3 = Version("1.3.7")
            Assert.assertTrue(v1 == v1)
            Assert.assertTrue(v1 == v2)
            Assert.assertFalse(v1 == v3)
        }

        @Test
        fun shouldParseLongPatchVersionCorrectly() {
            try {
                Version("3.2.1477710197605")
            } catch (e: NumberFormatException) {
                Assert.fail("Incorrectly got number format exception. " + e.localizedMessage)
            }
        }

        @Test
        fun shouldProvideIncrementBuildMetadataMethod() {
            val v1 = Version("1.0.0+build.1")
            val v2 = v1.incrementBuildMetadata()
            Assert.assertEquals("1.0.0+build.2", v2.toString())
        }

        @Test
        fun shouldProvideIncrementMajorVersionMethod() {
            val v = Version("1.2.3")
            val incrementedMajor = v.incrementMajorVersion()
            Assert.assertEquals("2.0", incrementedMajor.toString())
        }

        @Test
        fun shouldProvideIncrementMinorVersionMethod() {
            val v = Version("1.2.3")
            val incrementedMinor = v.incrementMinorVersion()
            Assert.assertEquals("1.3", incrementedMinor.toString())
        }

        @Test
        fun shouldProvideIncrementPatchVersionMethod() {
            val v = Version("1.2.3")
            val incrementedPatch = v.incrementPatchVersion()
            Assert.assertEquals("1.2.4", incrementedPatch.toString())
        }

        @Test
        fun shouldProvideIncrementPreReleaseVersionMethod() {
            val v1 = Version("1.0.0-beta.1")
            val v2 = v1.incrementPreReleaseVersion()
            Assert.assertEquals("1.0.0-beta.2", v2.toString())
        }

        @Test
        fun shouldProvideSetBuildMetadataMethod() {
            val v1 = Version("1.0.0")
            val v2 = v1.setBuildMetadata("build")
            Assert.assertEquals("1.0.0+build", v2.toString())
        }

        @Test
        fun shouldProvideSetPreReleaseVersionMethod() {
            val v1 = Version("1.0.0")
            val v2 = v1.setPreReleaseVersion("alpha")
            Assert.assertEquals("1.0.0-alpha", v2.toString())
        }

        @Test
        fun shouldThrowExceptionWhenIncrementingBuildIfItsNull() {
            val v1 = Version("1.0.0")
            try {
                v1.incrementBuildMetadata()
            } catch (e: NullPointerException) {
                return
            }
            Assert.fail("Method was expected to throw NullPointerException")
        }

        @Test
        fun shouldThrowExceptionWhenIncrementingPreReleaseIfItsNull() {
            val v1 = Version("1.0.0")
            try {
                v1.incrementPreReleaseVersion()
            } catch (e: NullPointerException) {
                return
            }
            Assert.fail("Method was expected to throw NullPointerException")
        }
    }

    class EqualsMethodTest {
        @Test
        fun shouldBeConsistent() {
            val v1 = Version("2.3.7")
            val v2 = Version("2.3.7")
            Assert.assertTrue(v1 == v2)
            Assert.assertTrue(v1 == v2)
            Assert.assertTrue(v1 == v2)
        }

        @Test
        fun shouldBeReflexive() {
            val v1 = Version("2.3.7")
            Assert.assertTrue(v1 == v1)
        }

        @Test
        fun shouldBeSymmetric() {
            val v1 = Version("2.3.7")
            val v2 = Version("2.3.7")
            Assert.assertTrue(v1 == v2)
            Assert.assertTrue(v2 == v1)
        }

        @Test
        fun shouldBeTransitive() {
            val v1 = Version("2.3.7")
            val v2 = Version("2.3.7")
            val v3 = Version("2.3.7")
            Assert.assertTrue(v1 == v2)
            Assert.assertTrue(v2 == v3)
            Assert.assertTrue(v1 == v3)
        }

        @Test
        fun shouldIgnoreBuildMetadataWhenCheckingForEquality() {
            val v1 = Version("2.3.7-beta+build")
            val v2 = Version("2.3.7-beta")
            Assert.assertTrue(v1 == v2)
        }

        @Test
        fun shouldReturnFalseIfOtherVersionIsNull() {
            val v1 = Version("2.3.7")
            val v2: Version? = null
            Assert.assertFalse(v1 == v2)
        }

        @Test
        fun shouldReturnFalseIfOtherVersionIsOfDifferentType() {
            val v1 = Version("2.3.7")
            Assert.assertFalse(v1.equals("2.3.7"))
        }
    }

    class HashCodeMethodTest {
        @Test
        fun shouldReturnSameHashCodeIfVersionsAreEqual() {
            val v1 = Version("2.3.7-beta+build")
            val v2 = Version("2.3.7-beta")
            Assert.assertTrue(v1 == v2)
            Assert.assertEquals(v1.hashCode().toLong(), v2.hashCode().toLong())
        }
    }

    class ToStringMethodTest {
        @Test
        fun shouldReturnStringRepresentation() {
            val value = "1.2.3-beta+build"
            val v = Version(value)
            Assert.assertEquals(value, v.toString())
        }
    }

    class BuilderTest {
        @Test
        fun shouldBuildVersionFromNormalVersion() {
            val builder = Version.Builder("1.0.0")
            Assert.assertEquals(Version("1.0.0"), builder.build())
        }

        @Test
        fun shouldBuildVersionInSteps() {
            val builder = Version.Builder()
            builder.setNormalVersion("1.0.0")
            builder.setPreReleaseVersion("alpha")
            builder.setBuildMetadata("build")
            Assert.assertEquals(Version("1.0.0-alpha+build"), builder.build())
        }

        @Test
        fun shouldBuildVersionWithBuildMetadata() {
            val builder = Version.Builder("1.0.0")
            builder.setBuildMetadata("build")
            Assert.assertEquals(Version("1.0.0+build"), builder.build())
        }

        @Test
        fun shouldBuildVersionWithPreReleaseVersion() {
            val builder = Version.Builder("1.0.0")
            builder.setPreReleaseVersion("alpha")
            Assert.assertEquals(Version("1.0.0-alpha"), builder.build())
        }

        @Test
        fun shouldBuildVersionWithPreReleaseVersionAndBuildMetadata() {
            val builder = Version.Builder("1.0.0")
            builder.setPreReleaseVersion("alpha")
            builder.setBuildMetadata("build")
            Assert.assertEquals(Version("1.0.0-alpha+build"), builder.build())
        }

        @Test
        fun shouldImplementFluentInterface() {
            val builder = Version.Builder()
            val version = builder.setNormalVersion("1.0.0").setPreReleaseVersion("alpha").setBuildMetadata("build").build()
            Assert.assertEquals(Version("1.0.0-alpha+build"), version)
        }
    }

    class BuildAwareOrderTest {
        @Test
        fun shouldCorrectlyCompareAllVersionsWithBuildMetadata() {
            val versions = arrayOf(
                "1.0.0-alpha",
                "1.0.0-alpha.1",
                "1.0.0-beta.2",
                "1.0.0-beta.11",
                "1.0.0-rc.1",
                "1.0.0-rc.1+build.1",
                "1.0.0",
                "1.0.0+0.3.7",
                "1.3.7+build",
                "1.3.7+build.2.b8f12d7",
                "1.3.7+build.11.e0f985a"
            )
            for (i in 1 until versions.size) {
                val v1 = Version(versions[i - 1])
                val v2 = Version(versions[i])
                Assert.assertTrue(0 > Version.BUILD_AWARE_ORDER.compare(v1, v2))
            }
        }
    }

    class SerializationTest {
        @Test
        @Throws(IOException::class, ClassNotFoundException::class)
        fun shouldSerializeAndDeserialize() {
            val versions = arrayOf(
                "1.0.0-alpha",
                "1.0.0-alpha.1",
                "1.0.0-beta.2",
                "1.0.0-beta.11",
                "1.0.0-rc.1",
                "1.0.0-rc.1+build.1",
                "1.0.0",
                "1.0.0+0.3.7",
                "1.3.7+build",
                "1.3.7+build.2.b8f12d7",
                "1.3.7+build.11.e0f985a"
            )
            for (i in 1 until versions.size) {
                val v1a = Version(versions[i - 1])
                val v1ba = pickle(v1a)
                val v1b = unpickle(v1ba, Version::class.java)
                Assert.assertTrue(v1a == v1b)
            }
        }

        companion object {
            @Throws(IOException::class)
            private fun <T : Serializable?> pickle(obj: T): ByteArray {
                val baos = ByteArrayOutputStream()
                val oos = ObjectOutputStream(baos)
                oos.writeObject(obj)
                oos.close()
                return baos.toByteArray()
            }

            @Throws(IOException::class, ClassNotFoundException::class)
            private fun <T : Serializable?> unpickle(b: ByteArray, cl: Class<T>): T {
                val bais = ByteArrayInputStream(b)
                val ois = ObjectInputStream(bais)
                val o = ois.readObject()
                return cl.cast(o)
            }
        }
    }
}
