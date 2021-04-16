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

import static dorkbox.version.VersionParser.CharType.DIGIT;
import static dorkbox.version.VersionParser.CharType.DOT;
import static dorkbox.version.VersionParser.CharType.EOI;
import static dorkbox.version.VersionParser.CharType.HYPHEN;
import static dorkbox.version.VersionParser.CharType.ILLEGAL;
import static dorkbox.version.VersionParser.CharType.LETTER;
import static dorkbox.version.VersionParser.CharType.PLUS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import dorkbox.version.VersionParser.CharType;

/**
 * @author Zafar Khaja <zafarkhaja@gmail.com>
 */
public
class VersionParserCharTypeTest {

    @Test
    public
    void shouldBeMatchedByDigit() {
        assertTrue(DIGIT.isMatchedBy('0'));
        assertTrue(DIGIT.isMatchedBy('9'));
        assertFalse(DIGIT.isMatchedBy('a'));
        assertFalse(DIGIT.isMatchedBy('A'));
    }

    @Test
    public
    void shouldBeMatchedByDot() {
        assertTrue(DOT.isMatchedBy('.'));
        assertFalse(DOT.isMatchedBy('-'));
        assertFalse(DOT.isMatchedBy('0'));
        assertFalse(DOT.isMatchedBy('9'));
    }

    @Test
    public
    void shouldBeMatchedByEol() {
        assertTrue(EOI.isMatchedBy(null));
        assertFalse(EOI.isMatchedBy('-'));
        assertFalse(EOI.isMatchedBy('a'));
        assertFalse(EOI.isMatchedBy('0'));
    }

    @Test
    public
    void shouldBeMatchedByHyphen() {
        assertTrue(HYPHEN.isMatchedBy('-'));
        assertFalse(HYPHEN.isMatchedBy('+'));
        assertFalse(HYPHEN.isMatchedBy('a'));
        assertFalse(HYPHEN.isMatchedBy('0'));
    }

    @Test
    public
    void shouldBeMatchedByIllegal() {
        assertTrue(ILLEGAL.isMatchedBy('!'));
        assertFalse(ILLEGAL.isMatchedBy('-'));
        assertFalse(ILLEGAL.isMatchedBy('a'));
        assertFalse(ILLEGAL.isMatchedBy('0'));
    }

    @Test
    public
    void shouldBeMatchedByLetter() {
        assertTrue(LETTER.isMatchedBy('a'));
        assertTrue(LETTER.isMatchedBy('A'));
        assertFalse(LETTER.isMatchedBy('0'));
        assertFalse(LETTER.isMatchedBy('9'));
    }

    @Test
    public
    void shouldBeMatchedByPlus() {
        assertTrue(PLUS.isMatchedBy('+'));
        assertFalse(PLUS.isMatchedBy('-'));
        assertFalse(PLUS.isMatchedBy('a'));
        assertFalse(PLUS.isMatchedBy('0'));
    }

    @Test
    public
    void shouldReturnCharTypeForCharacter() {
        assertEquals(DIGIT, CharType.forCharacter('1'));
        assertEquals(LETTER, CharType.forCharacter('a'));
        assertEquals(DOT, CharType.forCharacter('.'));
        assertEquals(HYPHEN, CharType.forCharacter('-'));
        assertEquals(PLUS, CharType.forCharacter('+'));
        assertEquals(EOI, CharType.forCharacter(null));
        assertEquals(ILLEGAL, CharType.forCharacter('!'));
    }
}
