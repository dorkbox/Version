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

import static dorkbox.version.expr.Lexer.Token.Type.AND;
import static dorkbox.version.expr.Lexer.Token.Type.EOI;
import static dorkbox.version.expr.Lexer.Token.Type.NUMERIC;
import static dorkbox.version.expr.Lexer.Token.Type.RIGHT_PAREN;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import dorkbox.version.expr.ExpressionParser;
import dorkbox.version.expr.Lexer.Token;
import dorkbox.version.expr.UnexpectedTokenException;

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
