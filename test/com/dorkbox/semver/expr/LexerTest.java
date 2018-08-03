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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.dorkbox.semver.expr.Lexer.Token;
import com.dorkbox.semver.expr.Lexer.Token.Type;
import com.dorkbox.semver.util.Stream;

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
