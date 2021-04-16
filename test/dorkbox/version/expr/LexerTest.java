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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import dorkbox.version.expr.Lexer;
import dorkbox.version.expr.Lexer.Token;
import dorkbox.version.expr.Lexer.Token.Type;
import dorkbox.version.expr.LexerException;
import dorkbox.version.util.Stream;

/**
 * @author Zafar Khaja <zafarkhaja@gmail.com>
 */
public
class LexerTest {

    @Test
    public
    void shouldEndWithEol() {
        Token[] expected = {new Token(Type.NUMERIC, "1", 0), new Token(Type.DOT, ".", 1), new Token(Type.NUMERIC, "2", 2), new Token(Type.DOT, ".", 3), new Token(
                Type.NUMERIC, "3", 4),
                            new Token(Type.EOI, null, 5),};
        Lexer lexer = new Lexer();
        Stream<Token> stream = lexer.tokenize("1.2.3");
        assertArrayEquals(expected, stream.toArray());
    }

    @Test
    public
    void shouldRaiseErrorOnIllegalCharacter() {
        Lexer lexer = new Lexer();
        try {
            lexer.tokenize("@1.0.0");
        } catch (LexerException e) {
            return;
        }
        fail("Should raise error on illegal character");
    }

    @Test
    public
    void shouldSkipWhitespaces() {
        Token[] expected = {new Token(Type.GREATER, ">", 0), new Token(Type.NUMERIC, "1", 2), new Token(Type.EOI, null, 3),};
        Lexer lexer = new Lexer();
        Stream<Token> stream = lexer.tokenize("> 1");
        assertArrayEquals(expected, stream.toArray());
    }

    @Test
    public
    void shouldTokenizeVersionString() {
        Token[] expected = {new Token(Type.GREATER, ">", 0), new Token(Type.NUMERIC, "1", 1), new Token(Type.DOT, ".", 2), new Token(Type.NUMERIC, "0", 3), new Token(
                Type.DOT, ".", 4),
                            new Token(Type.NUMERIC, "0", 5), new Token(Type.EOI, null, 6),};
        Lexer lexer = new Lexer();
        Stream<Token> stream = lexer.tokenize(">1.0.0");
        assertArrayEquals(expected, stream.toArray());
    }
}
