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

import static com.dorkbox.semver.expr.Lexer.Token.Type.AND;
import static com.dorkbox.semver.expr.Lexer.Token.Type.EOI;
import static com.dorkbox.semver.expr.Lexer.Token.Type.NUMERIC;
import static com.dorkbox.semver.expr.Lexer.Token.Type.RIGHT_PAREN;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.dorkbox.semver.expr.Lexer.Token;

/**
 * @author Zafar Khaja <zafarkhaja@gmail.com>
 */
@RunWith(Parameterized.class)
public
class ParserErrorHandlingTest {

    @Parameters(name = "{0}")
    public static
    Collection<Object[]> parameters() {
        return Arrays.asList(new Object[][] {{"1)", new Token(RIGHT_PAREN, ")", 1), new Token.Type[] {EOI}},
                                             {"(>1.0.1", new Token(EOI, null, 7), new Token.Type[] {RIGHT_PAREN}},
                                             {"((>=1 & <2)", new Token(EOI, null, 11), new Token.Type[] {RIGHT_PAREN}},
                                             {">=1.0.0 &", new Token(EOI, null, 9), new Token.Type[] {NUMERIC}},
                                             {"(>2.0 |)", new Token(RIGHT_PAREN, ")", 7), new Token.Type[] {NUMERIC}},
                                             {"& 1.2", new Token(AND, "&", 0), new Token.Type[] {NUMERIC}},});
    }
    private final String invalidExpr;
    private final Token unexpected;
    private final Token.Type[] expected;

    public
    ParserErrorHandlingTest(String invalidExpr, Token unexpected, Token.Type[] expected) {
        this.invalidExpr = invalidExpr;
        this.unexpected = unexpected;
        this.expected = expected;
    }

    @Test
    public
    void shouldCorrectlyHandleParseErrors() {
        try {
            ExpressionParser.newInstance().parse(invalidExpr);
        } catch (UnexpectedTokenException e) {
            assertEquals(unexpected, e.getUnexpectedToken());
            assertArrayEquals(expected, e.getExpectedTokenTypes());
            return;
        }
        fail("Uncaught exception");
    }
}
