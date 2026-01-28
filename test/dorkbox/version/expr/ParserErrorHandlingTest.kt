/*
 * Copyright 2026 dorkbox, llc
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

import dorkbox.version.expr.ExpressionParser.Companion.newInstance
import dorkbox.version.expr.Lexer.Token
import dorkbox.version.expr.Lexer.Token.Type.EOI
import dorkbox.version.expr.Lexer.Token.Type.NUMERIC
import dorkbox.version.expr.Lexer.Token.Type.RIGHT_PAREN
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/**
 * @author Zafar Khaja <zafarkhaja></zafarkhaja>@gmail.com>
 */
@RunWith(Parameterized::class)
class ParserErrorHandlingTest(
    private val invalidExpr: String,
    private val unexpected: Token?,
    private val expected: Array<Token.Type?>?
) {
    @Test
    fun shouldCorrectlyHandleParseErrors() {
        try {
            newInstance().parse(invalidExpr)
        }
        catch (e: UnexpectedTokenException) {
            Assert.assertEquals(unexpected, e.unexpectedToken)
            Assert.assertArrayEquals(expected, e.expectedTokenTypes)
            return
        }
        Assert.fail("Uncaught exception")
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun parameters(): Collection<Array<Any?>?> {
            return listOf(
                arrayOf("1)",
                        Token(RIGHT_PAREN, ")", 1),
                        arrayOf(EOI)),
                arrayOf("(>1.0.1",
                        Token(EOI, "", 7),
                        arrayOf(RIGHT_PAREN)),
                arrayOf("((>=1 & <2)",
                        Token(EOI, "", 11),
                        arrayOf(RIGHT_PAREN)),
                arrayOf(">=1.0.0 &",
                        Token(EOI, "", 9),
                        arrayOf(NUMERIC)),
                arrayOf("(>2.0 |)",
                           Token(RIGHT_PAREN, ")", 7),
                           arrayOf(NUMERIC)),
                arrayOf("& 1.2",
                        Token(Token.Type.AND, "&", 0),
                        arrayOf(NUMERIC))
            )
        }
    }
}
