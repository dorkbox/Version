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

import dorkbox.version.Version
import org.junit.Assert
import org.junit.Test

/**
 * @author Zafar Khaja <zafarkhaja></zafarkhaja>@gmail.com>
 */
class ExpressionParserTest {
    @Test
    fun shouldParseCaretRange() {
        val parser = ExpressionParser(Lexer())
        val expr1 = parser.parse("^1")
        Assert.assertTrue(expr1.interpret(Version("1.2.3")))
        Assert.assertFalse(expr1.interpret(Version("3.2.1")))
        val expr2 = parser.parse("^0.2")
        Assert.assertTrue(expr2.interpret(Version("0.2.3")))
        Assert.assertFalse(expr2.interpret(Version("0.3.0")))
        val expr3 = parser.parse("^0.0.3")
        Assert.assertTrue(expr3.interpret(Version("0.0.3")))
        Assert.assertFalse(expr3.interpret(Version("0.0.4")))
    }

    @Test
    fun shouldParseComplexExpressions() {
        val parser = ExpressionParser(Lexer())
        val expr = parser.parse("((>=1.0.1 & <2) | (>=3.0 & <4)) & ((1-1.5) & (~1.5))")
        Assert.assertTrue(expr.interpret(Version("1.5.0")))
        Assert.assertFalse(expr.interpret(Version("2.5.0")))
    }

    @Test
    fun shouldParseEqualComparisonRange() {
        val parser = ExpressionParser(Lexer())
        val eq = parser.parse("=1.0.0")
        Assert.assertTrue(eq.interpret(Version("1.0.0")))
    }

    @Test
    fun shouldParseEqualComparisonRangeIfOnlyFullVersionGiven() {
        val parser = ExpressionParser(Lexer())
        val eq = parser.parse("1.0.0")
        Assert.assertTrue(eq.interpret(Version("1.0.0")))
    }

    @Test
    fun shouldParseExpressionWithMultipleParentheses() {
        val parser = ExpressionParser(Lexer())
        val expr = parser.parse("((1))")
        Assert.assertTrue(expr.interpret(Version("1.2.3")))
        Assert.assertFalse(expr.interpret(Version("2.0.0")))
    }

    @Test
    fun shouldParseGreaterComparisonRange() {
        val parser = ExpressionParser(Lexer())
        val gt = parser.parse(">1.0.0")
        Assert.assertTrue(gt.interpret(Version("1.2.3")))
    }

    @Test
    fun shouldParseGreaterOrEqualComparisonRange() {
        val parser = ExpressionParser(Lexer())
        val ge = parser.parse(">=1.0.0")
        Assert.assertTrue(ge.interpret(Version("1.0.0")))
        Assert.assertTrue(ge.interpret(Version("1.2.3")))
    }

    @Test
    fun shouldParseHyphenRange() {
        val parser = ExpressionParser(Lexer())
        val range = parser.parse("1.0.0 - 2.0.0")
        Assert.assertTrue(range.interpret(Version("1.2.3")))
        Assert.assertFalse(range.interpret(Version("3.2.1")))
    }

    @Test
    fun shouldParseLessComparisonRange() {
        val parser = ExpressionParser(Lexer())
        val lt = parser.parse("<1.2.3")
        Assert.assertTrue(lt.interpret(Version("1.0.0")))
    }

    @Test
    fun shouldParseLessOrEqualComparisonRange() {
        val parser = ExpressionParser(Lexer())
        val le = parser.parse("<=1.2.3")
        Assert.assertTrue(le.interpret(Version("1.0.0")))
        Assert.assertTrue(le.interpret(Version("1.2.3")))
    }

    @Test
    fun shouldParseMultipleRangesJoinedWithAnd() {
        val parser = ExpressionParser(Lexer())
        val and = parser.parse(">=1.0.0 & <2.0.0")
        Assert.assertTrue(and.interpret(Version("1.2.3")))
        Assert.assertFalse(and.interpret(Version("3.2.1")))
    }

    @Test
    fun shouldParseMultipleRangesJoinedWithOr() {
        val parser = ExpressionParser(Lexer())
        val or = parser.parse("1.* | =2.0.0")
        Assert.assertTrue(or.interpret(Version("1.2.3")))
        Assert.assertFalse(or.interpret(Version("2.1.0")))
    }

    @Test
    fun shouldParseNotEqualComparisonRange() {
        val parser = ExpressionParser(Lexer())
        val ne = parser.parse("!=1.0.0")
        Assert.assertTrue(ne.interpret(Version("1.2.3")))
    }

    @Test
    fun shouldParseNotExpression() {
        val parser = ExpressionParser(Lexer())
        val not1 = parser.parse("!(1)")
        Assert.assertTrue(not1.interpret(Version("2.0.0")))
        Assert.assertFalse(not1.interpret(Version("1.2.3")))
        val not2 = parser.parse("0.* & !(>=1 & <2)")
        Assert.assertTrue(not2.interpret(Version("0.5.0")))
        Assert.assertFalse(not2.interpret(Version("1.0.1")))
        val not3 = parser.parse("!(>=1 & <2) & >=2")
        Assert.assertTrue(not3.interpret(Version("2.0.0")))
        Assert.assertFalse(not3.interpret(Version("1.2.3")))
    }

    @Test
    fun shouldParseParenthesizedExpression() {
        val parser = ExpressionParser(Lexer())
        val expr = parser.parse("(1)")
        Assert.assertTrue(expr.interpret(Version("1.2.3")))
        Assert.assertFalse(expr.interpret(Version("2.0.0")))
    }

    @Test
    fun shouldParsePartialVersionRange() {
        val parser = ExpressionParser(Lexer())
        val expr1 = parser.parse("1")
        Assert.assertTrue(expr1.interpret(Version("1.2.3")))
        val expr2 = parser.parse("2.0")
        Assert.assertTrue(expr2.interpret(Version("2.0.9")))
    }

    @Test
    fun shouldParseTildeRange() {
        val parser = ExpressionParser(Lexer())
        val expr1 = parser.parse("~1")
        Assert.assertTrue(expr1.interpret(Version("1.2.3")))
        Assert.assertFalse(expr1.interpret(Version("3.2.1")))
        val expr2 = parser.parse("~1.2")
        Assert.assertTrue(expr2.interpret(Version("1.2.3")))
        Assert.assertFalse(expr2.interpret(Version("2.0.0")))
        val expr3 = parser.parse("~1.2.3")
        Assert.assertTrue(expr3.interpret(Version("1.2.3")))
        Assert.assertFalse(expr3.interpret(Version("1.3.0")))
    }

    @Test
    fun shouldParseWildcardRange() {
        val parser = ExpressionParser(Lexer())
        val expr1 = parser.parse("1.*")
        Assert.assertTrue(expr1.interpret(Version("1.2.3")))
        Assert.assertFalse(expr1.interpret(Version("3.2.1")))
        val expr2 = parser.parse("1.2.x")
        Assert.assertTrue(expr2.interpret(Version("1.2.3")))
        Assert.assertFalse(expr2.interpret(Version("1.3.2")))
        val expr3 = parser.parse("X")
        Assert.assertTrue(expr3.interpret(Version("1.2.3")))
    }

    @Test
    fun shouldRespectPrecedenceWhenUsedWithParentheses() {
        val parser = ExpressionParser(Lexer())
        val expr1 = parser.parse("(~1.0 & <2.0) | >2.0")
        Assert.assertTrue(expr1.interpret(Version("2.5.0")))
        val expr2 = parser.parse("~1.0 & (<2.0 | >2.0)")
        Assert.assertFalse(expr2.interpret(Version("2.5.0")))
    }
}
