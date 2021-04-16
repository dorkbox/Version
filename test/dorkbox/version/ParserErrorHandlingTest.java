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

import static dorkbox.version.VersionParser.CharType.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import dorkbox.version.ParseException;
import dorkbox.version.UnexpectedCharacterException;
import dorkbox.version.VersionParser;
import dorkbox.version.VersionParser.CharType;

/**
 * @author Zafar Khaja <zafarkhaja@gmail.com>
 */
@RunWith(Parameterized.class)
public
class ParserErrorHandlingTest {

    @Parameters(name = "{0}")
    public static
    Collection<Object[]> parameters() {
        return Arrays.asList(new Object[][] {
                // {"1", null, 1, new CharType[] {DOT}}, // exception to semver, is that '1' is permitted (ie: leaving out the minor number)
                 {"1.FINAL", 'F', 2, new CharType[] {DIGIT}},  // double check for exception to SEMVER
                 {"1 ", ' ', 1, new CharType[] {DOT}},
                 {"1.", null, 2, new CharType[] {DIGIT}},
                // {"1.2", null, 3, new CharType[] {DOT}},  // exception to semver, is that 1.2 is permitted (ie: leaving out the patch number)
                 {"1.2.", null, 4, new CharType[] {DIGIT}},
                 {"a.b.c", 'a', 0, new CharType[] {DIGIT}},
                 {"1.b.c", 'b', 2, new CharType[] {DIGIT}},
                // {"1.2.c", 'c', 4, new CharType[] {DIGIT}}, // exception to semver. "c" can be the build (ie: when leaving out the patch number)
                 {"!.2.3", '!', 0, new CharType[] {DIGIT}},
                 {"1.!.3", '!', 2, new CharType[] {DIGIT}},
                 {"1.2.!", '!', 4, new CharType[] {DIGIT}},
                 {"v1.2.3", 'v', 0, new CharType[] {DIGIT}},
                 {"1.2.3-", null, 6, new CharType[] {DIGIT, LETTER, HYPHEN, UNDER_SCORE}},
                 {"1.2. 3", ' ', 4, new CharType[] {DIGIT}},
                 {"1.2.3=alpha", '=', 5, new CharType[] {HYPHEN, PLUS, UNDER_SCORE, EOI}},
                 {"1.2.3~beta", '~', 5, new CharType[] {HYPHEN, PLUS, UNDER_SCORE, EOI}},
                 {"1.2.3-be$ta", '$', 8, new CharType[] {PLUS, EOI}},
                 {"1.2.3+b1+b2", '+', 8, new CharType[] {EOI}},
                 {"1.2.3-rc!", '!', 8, new CharType[] {PLUS, EOI}},
                 {"1.2.3-+", '+', 6, new CharType[] {DIGIT, LETTER, HYPHEN, UNDER_SCORE}},
                 {"1.2.3-@", '@', 6, new CharType[] {DIGIT, LETTER, HYPHEN, UNDER_SCORE}},
                 {"1.2.3+@", '@', 6, new CharType[] {DIGIT, LETTER, HYPHEN}},
                 {"1.2.3-rc.", null, 9, new CharType[] {DIGIT, LETTER, HYPHEN}},
                 {"1.2.3+b.", null, 8, new CharType[] {DIGIT, LETTER, HYPHEN}},
                 {"1.2.3-b.+b", '+', 8, new CharType[] {DIGIT, LETTER, HYPHEN}},
                 {"1.2.3-rc..", '.', 9, new CharType[] {DIGIT, LETTER, HYPHEN}},
                 {"1.2.3-a+b..", '.', 10, new CharType[] {DIGIT, LETTER, HYPHEN}},
                 });
    }
    private final String invalidVersion;
    private final Character unexpected;
    private final int position;
    private final CharType[] expected;

    public
    ParserErrorHandlingTest(String invalidVersion, Character unexpected, int position, CharType[] expected) {
        this.invalidVersion = invalidVersion;
        this.unexpected = unexpected;
        this.position = position;
        this.expected = expected;
    }

    @Test
    public
    void shouldCorrectlyHandleParseErrors() {
        try {
            VersionParser.parseValidSemVer(invalidVersion);
        } catch (UnexpectedCharacterException e) {
            assertEquals(unexpected, e.getUnexpectedCharacter());
            assertEquals(position, e.getPosition());
            assertArrayEquals(expected, e.getExpectedCharTypes());
            return;
        } catch (ParseException e) {
            if (e.getCause() != null) {
                UnexpectedCharacterException cause = (UnexpectedCharacterException) e.getCause();
                assertEquals(unexpected, cause.getUnexpectedCharacter());
                assertEquals(position, cause.getPosition());
                assertArrayEquals(expected, cause.getExpectedCharTypes());
            }
            return;
        }
        fail("Uncaught exception");
    }
}
