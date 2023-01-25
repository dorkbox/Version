/*
 * Copyright 2023 dorkbox, llc
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
package dorkbox.version.expr

import org.junit.Assert
import org.junit.Test

/**
 * @author Zafar Khaja <zafarkhaja></zafarkhaja>@gmail.com>
 */
class LexerTest {
    @Test
    fun shouldEndWithEol() {
        val expected = arrayOf(
            Lexer.Token(Lexer.Token.Type.NUMERIC, "1", 0),
            Lexer.Token(Lexer.Token.Type.DOT, ".", 1),
            Lexer.Token(Lexer.Token.Type.NUMERIC, "2", 2),
            Lexer.Token(
                Lexer.Token.Type.DOT, ".", 3
            ),
            Lexer.Token(
                Lexer.Token.Type.NUMERIC, "3", 4
            ),
            Lexer.Token(Lexer.Token.Type.EOI, "", 5)
        )
        val lexer = Lexer()
        val stream = lexer.tokenize("1.2.3")
        Assert.assertArrayEquals(expected, stream.toArray())
    }

    @Test
    fun shouldRaiseErrorOnIllegalCharacter() {
        val lexer = Lexer()
        try {
            lexer.tokenize("@1.0.0")
        } catch (e: LexerException) {
            return
        }
        Assert.fail("Should raise error on illegal character")
    }

    @Test
    fun shouldSkipWhitespaces() {
        val expected = arrayOf(
            Lexer.Token(Lexer.Token.Type.GREATER, ">", 0), Lexer.Token(Lexer.Token.Type.NUMERIC, "1", 2), Lexer.Token(
                Lexer.Token.Type.EOI, "", 3
            )
        )
        val lexer = Lexer()
        val stream = lexer.tokenize("> 1")
        Assert.assertArrayEquals(expected, stream.toArray())
    }

    @Test
    fun shouldTokenizeVersionString() {
        val expected = arrayOf(
            Lexer.Token(Lexer.Token.Type.GREATER, ">", 0),
            Lexer.Token(Lexer.Token.Type.NUMERIC, "1", 1),
            Lexer.Token(Lexer.Token.Type.DOT, ".", 2),
            Lexer.Token(
                Lexer.Token.Type.NUMERIC, "0", 3
            ),
            Lexer.Token(
                Lexer.Token.Type.DOT, ".", 4
            ),
            Lexer.Token(Lexer.Token.Type.NUMERIC, "0", 5),
            Lexer.Token(Lexer.Token.Type.EOI, "", 6)
        )
        val lexer = Lexer()
        val stream = lexer.tokenize(">1.0.0")
        Assert.assertArrayEquals(expected, stream.toArray())
    }
}
