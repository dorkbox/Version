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
class VersionParserCharTypeTest {
    @Test
    fun shouldBeMatchedByDigit() {
        Assert.assertTrue(CharType.DIGIT.isMatchedBy('0'))
        Assert.assertTrue(CharType.DIGIT.isMatchedBy('9'))
        Assert.assertFalse(CharType.DIGIT.isMatchedBy('a'))
        Assert.assertFalse(CharType.DIGIT.isMatchedBy('A'))
    }

    @Test
    fun shouldBeMatchedByDot() {
        Assert.assertTrue(CharType.DOT.isMatchedBy('.'))
        Assert.assertFalse(CharType.DOT.isMatchedBy('-'))
        Assert.assertFalse(CharType.DOT.isMatchedBy('0'))
        Assert.assertFalse(CharType.DOT.isMatchedBy('9'))
    }

    @Test
    fun shouldBeMatchedByEol() {
        Assert.assertTrue(CharType.EOI.isMatchedBy(null))
        Assert.assertFalse(CharType.EOI.isMatchedBy('-'))
        Assert.assertFalse(CharType.EOI.isMatchedBy('a'))
        Assert.assertFalse(CharType.EOI.isMatchedBy('0'))
    }

    @Test
    fun shouldBeMatchedByHyphen() {
        Assert.assertTrue(CharType.HYPHEN.isMatchedBy('-'))
        Assert.assertFalse(CharType.HYPHEN.isMatchedBy('+'))
        Assert.assertFalse(CharType.HYPHEN.isMatchedBy('a'))
        Assert.assertFalse(CharType.HYPHEN.isMatchedBy('0'))
    }

    @Test
    fun shouldBeMatchedByIllegal() {
        Assert.assertTrue(CharType.ILLEGAL.isMatchedBy('!'))
        Assert.assertFalse(CharType.ILLEGAL.isMatchedBy('-'))
        Assert.assertFalse(CharType.ILLEGAL.isMatchedBy('a'))
        Assert.assertFalse(CharType.ILLEGAL.isMatchedBy('0'))
    }

    @Test
    fun shouldBeMatchedByLetter() {
        Assert.assertTrue(CharType.LETTER.isMatchedBy('a'))
        Assert.assertTrue(CharType.LETTER.isMatchedBy('A'))
        Assert.assertFalse(CharType.LETTER.isMatchedBy('0'))
        Assert.assertFalse(CharType.LETTER.isMatchedBy('9'))
    }

    @Test
    fun shouldBeMatchedByPlus() {
        Assert.assertTrue(CharType.PLUS.isMatchedBy('+'))
        Assert.assertFalse(CharType.PLUS.isMatchedBy('-'))
        Assert.assertFalse(CharType.PLUS.isMatchedBy('a'))
        Assert.assertFalse(CharType.PLUS.isMatchedBy('0'))
    }

    @Test
    fun shouldReturnCharTypeForCharacter() {
        Assert.assertEquals(CharType.DIGIT, CharType.forCharacter('1'))
        Assert.assertEquals(CharType.LETTER, CharType.forCharacter('a'))
        Assert.assertEquals(CharType.DOT, CharType.forCharacter('.'))
        Assert.assertEquals(CharType.HYPHEN, CharType.forCharacter('-'))
        Assert.assertEquals(CharType.PLUS, CharType.forCharacter('+'))
        Assert.assertEquals(CharType.EOI, CharType.forCharacter(null))
        Assert.assertEquals(CharType.ILLEGAL, CharType.forCharacter('!'))
    }
}
