/*
 * The MIT License
 *
 * Copyright 2012-2018 The SemanticVersioning Authors (see LICENSE file in root of project).
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.dorkbox.version;

import static com.dorkbox.version.VersionParser.CharType.DIGIT;
import static com.dorkbox.version.VersionParser.CharType.DOT;
import static com.dorkbox.version.VersionParser.CharType.EOI;
import static com.dorkbox.version.VersionParser.CharType.HYPHEN;
import static com.dorkbox.version.VersionParser.CharType.ILLEGAL;
import static com.dorkbox.version.VersionParser.CharType.LETTER;
import static com.dorkbox.version.VersionParser.CharType.PLUS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.dorkbox.version.VersionParser.CharType;

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
