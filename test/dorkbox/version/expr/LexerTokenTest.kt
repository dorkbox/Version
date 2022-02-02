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
package dorkbox.version.expr

import org.junit.Assert
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith

/**
 * @author Zafar Khaja <zafarkhaja></zafarkhaja>@gmail.com>
 */
@RunWith(Enclosed::class)
class LexerTokenTest {
    class EqualsMethodTest {
        @Test
        fun shouldBeConsistent() {
            val t1 = Lexer.Token(Lexer.Token.Type.HYPHEN, "-", 0)
            val t2 = Lexer.Token(Lexer.Token.Type.HYPHEN, "-", 0)
            Assert.assertTrue(t1.equals(t2))
            Assert.assertTrue(t1.equals(t2))
            Assert.assertTrue(t1.equals(t2))
        }

        @Test
        fun shouldBeReflexive() {
            val token = Lexer.Token(Lexer.Token.Type.NUMERIC, "1", 0)
            Assert.assertTrue(token.equals(token))
        }

        @Test
        fun shouldBeSymmetric() {
            val t1 = Lexer.Token(Lexer.Token.Type.EQUAL, "=", 0)
            val t2 = Lexer.Token(Lexer.Token.Type.EQUAL, "=", 0)
            Assert.assertTrue(t1.equals(t2))
            Assert.assertTrue(t2.equals(t1))
        }

        @Test
        fun shouldBeTransitive() {
            val t1 = Lexer.Token(Lexer.Token.Type.GREATER, ">", 0)
            val t2 = Lexer.Token(Lexer.Token.Type.GREATER, ">", 0)
            val t3 = Lexer.Token(Lexer.Token.Type.GREATER, ">", 0)
            Assert.assertTrue(t1.equals(t2))
            Assert.assertTrue(t2.equals(t3))
            Assert.assertTrue(t1.equals(t3))
        }

        @Test
        fun shouldReturnFalseIfLexemesAreDifferent() {
            val t1 = Lexer.Token(Lexer.Token.Type.NUMERIC, "1", 0)
            val t2 = Lexer.Token(Lexer.Token.Type.NUMERIC, "2", 0)
            Assert.assertFalse(t1.equals(t2))
        }

        @Test
        fun shouldReturnFalseIfOtherVersionIsNull() {
            val t1 = Lexer.Token(Lexer.Token.Type.AND, "&", 0)
            val t2: Lexer.Token? = null
            Assert.assertFalse(t1.equals(t2))
        }

        @Test
        fun shouldReturnFalseIfOtherVersionIsOfDifferentType() {
            val t1 = Lexer.Token(Lexer.Token.Type.DOT, ".", 0)
            Assert.assertFalse(t1.equals("."))
        }

        @Test
        fun shouldReturnFalseIfPositionsAreDifferent() {
            val t1 = Lexer.Token(Lexer.Token.Type.NUMERIC, "1", 1)
            val t2 = Lexer.Token(Lexer.Token.Type.NUMERIC, "1", 2)
            Assert.assertFalse(t1.equals(t2))
        }

        @Test
        fun shouldReturnFalseIfTypesAreDifferent() {
            val t1 = Lexer.Token(Lexer.Token.Type.EQUAL, "=", 0)
            val t2 = Lexer.Token(Lexer.Token.Type.NOT_EQUAL, "!=", 0)
            Assert.assertFalse(t1.equals(t2))
        }
    }

    class HashCodeMethodTest {
        @Test
        fun shouldReturnSameHashCodeIfTokensAreEqual() {
            val t1 = Lexer.Token(Lexer.Token.Type.NUMERIC, "1", 0)
            val t2 = Lexer.Token(Lexer.Token.Type.NUMERIC, "1", 0)
            Assert.assertTrue(t1.equals(t2))
            Assert.assertEquals(t1.hashCode().toLong(), t2.hashCode().toLong())
        }
    }
}
