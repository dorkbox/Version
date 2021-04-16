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
package dorkbox.version.expr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import dorkbox.version.expr.Lexer.Token;
import dorkbox.version.expr.Lexer.Token.Type;

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
