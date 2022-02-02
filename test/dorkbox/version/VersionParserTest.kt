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

/**
 * @author Zafar Khaja <zafarkhaja></zafarkhaja>@gmail.com>
 */
class VersionParserTest {
    @Test
    fun shouldAllowDigitsInBuildMetadata() {
        try {
            VersionParser.parseBuild("build.01")
        } catch (e: ParseException) {
            Assert.fail("Should allow digits in build metadata")
        }
    }

    @Test
    fun shouldNotAllowDigitsInPreReleaseVersion() {
        try {
            VersionParser.parsePreRelease("alpha.01")
        } catch (e: ParseException) {
            return
        }
        Assert.fail("Should not allow digits in pre-release version")
    }

    @Test
    fun shouldParseBuildMetadata() {
        val build = VersionParser.parseBuild("build.1")
        Assert.assertEquals(MetadataVersion(arrayOf("build", "1")), build)
    }

    @Test
    fun shouldParseNormalVersion() {
        val version = VersionParser.parseVersionCore("1.0.0")
        Assert.assertEquals(NormalVersion(1, 0, 0), version)
    }

    @Test
    fun shouldParsePreReleaseVersion() {
        val preRelease = VersionParser.parsePreRelease("beta-1.1")
        Assert.assertEquals(MetadataVersion(arrayOf("beta-1", "1")), preRelease)
    }

    @Test
    fun shouldParseValidSemVer() {
        val parser = VersionParser("1.0.0-rc.2+build.05")
        val version = parser.parse("")

        Assert.assertEquals(
            Version(
                NormalVersion(1, 0, 0),
                MetadataVersion(arrayOf("rc", "2")),
                MetadataVersion(arrayOf("build", "05"))),
            version
        )
    }

    @Test
    fun shouldRaiseErrorForEmptyBuildIdentifier() {
        try {
            VersionParser.parseBuild(".build.01")
        } catch (e: ParseException) {
            return
        }
        Assert.fail("Identifiers MUST NOT be empty")
    }

    @Test
    fun shouldRaiseErrorForEmptyPreReleaseIdentifier() {
        try {
            VersionParser.parsePreRelease("beta-1..1")
        } catch (e: ParseException) {
            return
        }
        Assert.fail("Identifiers MUST NOT be empty")
    }

    @Test
    fun shouldRaiseErrorForIllegalInputString() {
        for (illegal in arrayOf("", null)) {
            try {
                VersionParser(illegal!!)
            } catch (e: IllegalArgumentException) {
                continue
            } catch (e: NullPointerException) {
                continue
            }
            Assert.fail("Should raise error for illegal input string")
        }
    }

    @Test
    fun shouldRaiseErrorIfNumericIdentifierHasLeadingZeroes() {
        try {
            VersionParser.parseVersionCore("01.1.0")
        } catch (e: ParseException) {
            return
        }
        Assert.fail("Numeric identifier MUST NOT contain leading zeroes")
    }
}
