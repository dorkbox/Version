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
package com.dorkbox.semver.expr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.dorkbox.semver.expr.Lexer.Token;
import com.dorkbox.semver.expr.Lexer.Token.Type;

/**
 * @author Zafar Khaja <zafarkhaja@gmail.com>
 */
@RunWith(Enclosed.class)
public
class LexerTokenTest {

    public static
    class EqualsMethodTest {

        @Test
        public
        void shouldBeConsistent() {
            Token t1 = new Token(Type.HYPHEN, "-", 0);
            Token t2 = new Token(Type.HYPHEN, "-", 0);
            assertTrue(t1.equals(t2));
            assertTrue(t1.equals(t2));
            assertTrue(t1.equals(t2));
        }

        @Test
        public
        void shouldBeReflexive() {
            Token token = new Token(Type.NUMERIC, "1", 0);
            assertTrue(token.equals(token));
        }

        @Test
        public
        void shouldBeSymmetric() {
            Token t1 = new Token(Type.EQUAL, "=", 0);
            Token t2 = new Token(Type.EQUAL, "=", 0);
            assertTrue(t1.equals(t2));
            assertTrue(t2.equals(t1));
        }

        @Test
        public
        void shouldBeTransitive() {
            Token t1 = new Token(Type.GREATER, ">", 0);
            Token t2 = new Token(Type.GREATER, ">", 0);
            Token t3 = new Token(Type.GREATER, ">", 0);
            assertTrue(t1.equals(t2));
            assertTrue(t2.equals(t3));
            assertTrue(t1.equals(t3));
        }

        @Test
        public
        void shouldReturnFalseIfLexemesAreDifferent() {
            Token t1 = new Token(Type.NUMERIC, "1", 0);
            Token t2 = new Token(Type.NUMERIC, "2", 0);
            assertFalse(t1.equals(t2));
        }

        @Test
        public
        void shouldReturnFalseIfOtherVersionIsNull() {
            Token t1 = new Token(Type.AND, "&", 0);
            Token t2 = null;
            assertFalse(t1.equals(t2));
        }

        @Test
        public
        void shouldReturnFalseIfOtherVersionIsOfDifferentType() {
            Token t1 = new Token(Type.DOT, ".", 0);
            assertFalse(t1.equals(new String(".")));
        }

        @Test
        public
        void shouldReturnFalseIfPositionsAreDifferent() {
            Token t1 = new Token(Type.NUMERIC, "1", 1);
            Token t2 = new Token(Type.NUMERIC, "1", 2);
            assertFalse(t1.equals(t2));
        }

        @Test
        public
        void shouldReturnFalseIfTypesAreDifferent() {
            Token t1 = new Token(Type.EQUAL, "=", 0);
            Token t2 = new Token(Type.NOT_EQUAL, "!=", 0);
            assertFalse(t1.equals(t2));
        }
    }


    public static
    class HashCodeMethodTest {

        @Test
        public
        void shouldReturnSameHashCodeIfTokensAreEqual() {
            Token t1 = new Token(Type.NUMERIC, "1", 0);
            Token t2 = new Token(Type.NUMERIC, "1", 0);
            assertTrue(t1.equals(t2));
            assertEquals(t1.hashCode(), t2.hashCode());
        }
    }
}
