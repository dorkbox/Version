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

import dorkbox.version.CharType.DIGIT
import dorkbox.version.CharType.EOI
import dorkbox.version.CharType.HYPHEN
import dorkbox.version.CharType.LETTER
import dorkbox.version.CharType.PLUS
import dorkbox.version.CharType.UNDER_SCORE
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/**
 * @author Zafar Khaja <zafarkhaja></zafarkhaja>@gmail.com>
 */
@RunWith(Parameterized::class)
class ParserErrorHandlingTest(
    private val invalidVersion: String,
    private val unexpected: Char?,
    private val position: Int,
    private val expected: Array<CharType>
) {
    @Test
    fun shouldCorrectlyHandleParseErrors() {
        try {
            VersionParser.parseValidSemVer(invalidVersion)
        } catch (e: UnexpectedCharacterException) {
            Assert.assertEquals(unexpected, e.unexpectedCharacter)
            Assert.assertEquals(position.toLong(), e.position.toLong())
            Assert.assertArrayEquals(expected, e.expectedCharTypes)
            return
        } catch (e: ParseException) {
            if (e.cause != null) {
                val cause = e.cause as UnexpectedCharacterException
                Assert.assertEquals(unexpected, cause.unexpectedCharacter)
                Assert.assertEquals(position.toLong(), cause.position.toLong())
                Assert.assertArrayEquals(expected, cause.expectedCharTypes)
            }
            return
        }
        Assert.fail("Uncaught exception")
    }

    companion object {
        @Parameterized.Parameters(name = "{0}")
        @JvmStatic
        fun parameters(): Array<Array<Any?>> {
            return arrayOf(
//                arrayOf("1", null, 1, arrayOf(DOT)), // exception to semver, is that '1' is permitted (ie: leaving out the minor number)
//                arrayOf("1.FINAL", 'F', 2, arrayOf(DIGIT)),  // double check for exception to SEMVER
//                arrayOf("1 ", ' ', 1, arrayOf(DOT)),
                arrayOf("1.", null, 2, arrayOf(DIGIT)),
//                arrayOf("1.2", null, 3, arrayOf(DOT)),  // exception to semver, is that 1.2 is permitted (ie: leaving out the patch number)
                arrayOf("1.2.", null, 4, arrayOf(DIGIT)),
                arrayOf("a.b.c", 'a', 0, arrayOf(DIGIT)),
                arrayOf("1.b.c", 'b', 2, arrayOf(DIGIT)),
//                arrayOf("1.2.c", 'c', 4, arrayOf(DIGIT)), // exception to semver. "c" can be the build (ie: when leaving out the patch number)
                arrayOf("!.2.3", '!', 0, arrayOf(DIGIT)),
                arrayOf("1.!.3", '!', 2, arrayOf(DIGIT)),
                arrayOf("1.2.!", '!', 4, arrayOf(DIGIT)),
                arrayOf("v1.2.3", 'v', 0, arrayOf(DIGIT)),
                arrayOf("1.2.3-", null, 6, arrayOf(DIGIT, LETTER, HYPHEN, UNDER_SCORE)),
                arrayOf("1.2. 3", ' ', 4, arrayOf(DIGIT)),
                arrayOf("1.2.3=alpha", '=', 5, arrayOf(HYPHEN, PLUS, UNDER_SCORE, EOI)),
                arrayOf("1.2.3~beta", '~', 5, arrayOf(HYPHEN, PLUS, UNDER_SCORE, EOI)),
                arrayOf("1.2.3-be\$ta", '$', 8, arrayOf(PLUS, EOI)),
                arrayOf("1.2.3+b1+b2", '+', 8, arrayOf(EOI)),
                arrayOf("1.2.3-rc!", '!', 8, arrayOf(PLUS, EOI)),
                arrayOf("1.2.3-+", '+', 6, arrayOf(DIGIT, LETTER, HYPHEN, UNDER_SCORE)),
                arrayOf("1.2.3-@", '@', 6, arrayOf(DIGIT, LETTER, HYPHEN, UNDER_SCORE)),
                arrayOf("1.2.3+@", '@', 6, arrayOf(DIGIT, LETTER, HYPHEN)),
                arrayOf("1.2.3-rc.", null, 9, arrayOf(DIGIT, LETTER, HYPHEN)),
                arrayOf("1.2.3+b.", null, 8, arrayOf(DIGIT, LETTER, HYPHEN)),
                arrayOf("1.2.3-b.+b", '+', 8, arrayOf(DIGIT, LETTER, HYPHEN)),
                arrayOf("1.2.3-rc..", '.', 9, arrayOf(DIGIT, LETTER, HYPHEN)),
                arrayOf("1.2.3-a+b..", '.', 10, arrayOf(DIGIT, LETTER, HYPHEN))
            )
        }
    }
}
