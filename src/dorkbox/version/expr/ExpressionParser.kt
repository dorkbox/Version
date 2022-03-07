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

import dorkbox.version.Parser
import dorkbox.version.Version
import dorkbox.version.util.Stream
import dorkbox.version.util.UnexpectedElementException
import java.util.*

/**
 * A parser for the SemVer Expressions.
 *
 * @author Zafar Khaja <zafarkhaja></zafarkhaja>@gmail.com>
 */
@Suppress("UNNECESSARY_NOT_NULL_ASSERTION")
class ExpressionParser
/**
 * Constructs a `ExpressionParser` instance
 * with the corresponding lexer.
 *
 * @param lexer the lexer to use for tokenization of the input string
 */ internal constructor(
    /**
     * The lexer instance used for tokenization of the input string.
     */
    private val lexer: Lexer
) : Parser<Expression> {
    /**
     * The stream of tokens produced by the lexer.
     */
    private lateinit var tokens: Stream<Lexer.Token>

    /**
     * Parses the SemVer Expressions.
     *
     * @param input a string representing the SemVer Expression
     *
     * @return the AST for the SemVer Expressions
     *
     * @throws LexerException when encounters an illegal character
     * @throws UnexpectedTokenException when consumes a token of an unexpected type
     */
    override fun parse(input: String): Expression {
        tokens = lexer.tokenize(input)

        val expr: Expression = parseSemVerExpression()
        consumeNextToken(Lexer.Token.Type.EOI)
        return expr
    }

    /**
     * Tries to consume the next token in the stream.
     *
     * @param expected the expected types of the next token
     *
     * @return the next token in the stream or `null` if no more elements left
     *
     * @throws UnexpectedTokenException when encounters an unexpected token type
     */
    private fun consumeNextToken(vararg expected: Lexer.Token.Type): Lexer.Token? {
        return try {
            tokens.consume(*expected)
        } catch (e: UnexpectedElementException) {
            throw UnexpectedTokenException(e)
        }
    }

    /**
     * Returns a `int` representation of the specified string.
     *
     * @param value the string to convert into an integer
     *
     * @return the integer value of the specified string
     */
    private fun intOf(value: String): Int {
        return value.toInt()
    }

    /**
     * Determines if the following version terminals are
     * part of the &lt;hyphen-range&gt; non-terminal.
     *
     * @return `true` if the following version terminals are
     * part of the &lt;hyphen-range&gt; non-terminal or
     * `false` otherwise
     */
    private val isHyphenRange: Boolean
        get() = isVersionFollowedBy(Lexer.Token.Type.HYPHEN)

    /**
     * Determines if the following version terminals are part
     * of the &lt;partial-version-range&gt; non-terminal.
     *
     * @return `true` if the following version terminals are part
     * of the &lt;partial-version-range&gt; non-terminal or
     * `false` otherwise
     */
    private val isPartialVersionRange: Boolean
        get() {
            if (!tokens!!.positiveLookahead(Lexer.Token.Type.NUMERIC)) {
                return false
            }
            val expected = EnumSet.complementOf(EnumSet.of(Lexer.Token.Type.NUMERIC, Lexer.Token.Type.DOT))
            return tokens!!.positiveLookaheadUntil(5, *expected.toTypedArray())
        }

    /**
     * Determines if the version terminals are
     * followed by the specified token type.
     *
     *
     * This method is essentially a `lookahead(k)` method
     * which allows to solve the grammar's ambiguities.
     *
     * @param type the token type to check
     *
     * @return `true` if the version terminals are followed by
     * the specified token type or `false` otherwise
     */
    private fun isVersionFollowedBy(type: Stream.ElementType<Lexer.Token>): Boolean {
        val expected = EnumSet.of(Lexer.Token.Type.NUMERIC, Lexer.Token.Type.DOT)
        val it: Iterator<Lexer.Token> = tokens!!.iterator()
        var lookahead: Lexer.Token? = null
        while (it.hasNext()) {
            lookahead = it.next()
            if (!expected.contains(lookahead.type)) {
                break
            }
        }
        return type.isMatchedBy(lookahead)
    }

    /**
     * Determines if the following version terminals are part
     * of the &lt;wildcard-range&gt; non-terminal.
     *
     * @return `true` if the following version terminals are
     * part of the &lt;wildcard-range&gt; non-terminal or
     * `false` otherwise
     */
    private val isWildcardRange: Boolean
        get() = isVersionFollowedBy(Lexer.Token.Type.WILDCARD)

    /**
     * Parses the &lt;caret-range&gt; non-terminal.
     *
     * <pre>
     * &lt;caret-range&gt; ::= &quot;^&quot; &lt;version&gt;
     *
    </pre> *
     *
     * @return the expression AST
     */
    private fun parseCaretRange(): CompositeExpression {
        consumeNextToken(Lexer.Token.Type.CARET)

        val major = intOf(consumeNextToken(Lexer.Token.Type.NUMERIC)!!.lexeme)
        if (!tokens!!.positiveLookahead(Lexer.Token.Type.DOT)) {
            return CompositeExpression.Helper.gte(versionFor(major)).and(CompositeExpression.Helper.lt(versionFor(major + 1)))
        }

        consumeNextToken(Lexer.Token.Type.DOT)
        val minor = intOf(consumeNextToken(Lexer.Token.Type.NUMERIC)!!.lexeme)
        if (!tokens!!.positiveLookahead(Lexer.Token.Type.DOT)) {
            val lower = versionFor(major, minor)
            val upper = if (major > 0) lower.incrementMajorVersion() else lower.incrementMinorVersion()
            return CompositeExpression.Helper.gte(lower).and(CompositeExpression.Helper.lt(upper))
        }

        consumeNextToken(Lexer.Token.Type.DOT)
        val patch = intOf(consumeNextToken(Lexer.Token.Type.NUMERIC)!!.lexeme)
        val version = versionFor(major, minor, patch)
        val gte = CompositeExpression.Helper.gte(version)

        return when {
            major > 0 -> gte.and(CompositeExpression.Helper.lt(version.incrementMajorVersion()))
            minor > 0 -> gte.and(CompositeExpression.Helper.lt(version.incrementMinorVersion()))
            patch > 0 -> gte.and(CompositeExpression.Helper.lt(version.incrementPatchVersion()))
            else -> CompositeExpression.Helper.eq(version)
        }
    }

    /**
     * Parses the <comparison-range> non-terminal.
     *
     *
     * ```
     *  sad
     * ```
     * <pre>
     * &lt;comparison-range&gt; ::= &lt;comparison-op&gt; &lt;version&gt; | &lt;version&gt;
     *
    </pre> *
     *
     * @return the expression AST
     */
    private fun parseComparisonRange(): CompositeExpression {
        val token: Lexer.Token? = tokens.lookahead()

        val expr = when (token?.type) {
            Lexer.Token.Type.EQUAL -> {
                tokens!!.consume()
                CompositeExpression.Helper.eq(parseVersion())
            }
            Lexer.Token.Type.NOT_EQUAL -> {
                tokens!!.consume()
                CompositeExpression.Helper.neq(parseVersion())
            }
            Lexer.Token.Type.GREATER -> {
                tokens!!.consume()
                CompositeExpression.Helper.gt(parseVersion())
            }
            Lexer.Token.Type.GREATER_EQUAL -> {
                tokens!!.consume()
                CompositeExpression.Helper.gte(parseVersion())
            }
            Lexer.Token.Type.LESS -> {
                tokens!!.consume()
                CompositeExpression.Helper.lt(parseVersion())
            }
            Lexer.Token.Type.LESS_EQUAL -> {
                tokens!!.consume()
                CompositeExpression.Helper.lte(parseVersion())
            }
            else -> CompositeExpression.Helper.eq(parseVersion())
        }

        return expr
    }

    /**
     * Parses the &lt;hyphen-range&gt; non-terminal.
     *
     * <pre>
     * &lt;hyphen-range&gt; ::= &lt;version&gt; &quot;-&quot; &lt;version&gt;
     *
    </pre> *
     *
     * @return the expression AST
     */
    private fun parseHyphenRange(): CompositeExpression {
        val gte = CompositeExpression.Helper.gte(parseVersion())
        consumeNextToken(Lexer.Token.Type.HYPHEN)
        return gte.and(CompositeExpression.Helper.lte(parseVersion()))
    }

    /**
     * Parses the &lt;more-expr&gt; non-terminal.
     *
     * <pre>
     * &lt;more-expr&gt; ::= &lt;boolean-op&gt; &lt;semver-expr&gt; | epsilon
     *
    </pre> *
     *
     * @param expr the left-hand expression of the logical operators
     *
     * @return the expression AST
     */
    private fun parseMoreExpressions(expr: CompositeExpression): CompositeExpression {
        @Suppress("NAME_SHADOWING")
        var expr = expr
        if (tokens!!.positiveLookahead(Lexer.Token.Type.AND)) {
            tokens!!.consume()
            expr = expr.and(parseSemVerExpression())
        } else if (tokens!!.positiveLookahead(Lexer.Token.Type.OR)) {
            tokens!!.consume()
            expr = expr.or(parseSemVerExpression())
        }
        return expr
    }

    /**
     * Parses the &lt;partial-version-range&gt; non-terminal.
     *
     * <pre>
     * &lt;partial-version-range&gt; ::= &lt;major&gt; | &lt;major&gt; &quot;.&quot; &lt;minor&gt;
     *
    </pre> *
     *
     * @return the expression AST
     */
    private fun parsePartialVersionRange(): CompositeExpression {
        val major = intOf(consumeNextToken(Lexer.Token.Type.NUMERIC)!!.lexeme)
        if (!tokens!!.positiveLookahead(Lexer.Token.Type.DOT)) {
            return CompositeExpression.Helper.gte(versionFor(major)).and(CompositeExpression.Helper.lt(versionFor(major + 1)))
        }

        consumeNextToken(Lexer.Token.Type.DOT)
        val minor = intOf(consumeNextToken(Lexer.Token.Type.NUMERIC)!!.lexeme)
        return CompositeExpression.Helper.gte(versionFor(major, minor)).and(CompositeExpression.Helper.lt(versionFor(major, minor + 1)))
    }

    /**
     * Parses the &lt;range&gt; non-terminal.
     *
     * <pre>
     * &lt;expr&gt; ::= &lt;comparison-range&gt;
     * | &lt;wildcard-expr&gt;
     * | &lt;tilde-range&gt;
     * | &lt;caret-range&gt;
     * | &lt;hyphen-range&gt;
     * | &lt;partial-version-range&gt;
     *
    </pre> *
     *
     * @return the expression AST
     */
    private fun parseRange(): CompositeExpression {
        return when {
            tokens!!.positiveLookahead(Lexer.Token.Type.TILDE) -> parseTildeRange()
            tokens!!.positiveLookahead(Lexer.Token.Type.CARET) -> parseCaretRange()
            isWildcardRange -> parseWildcardRange()
            isHyphenRange -> parseHyphenRange()
            isPartialVersionRange -> parsePartialVersionRange()
            else -> parseComparisonRange()
        }
    }

    /**
     * Parses the &lt;semver-expr&gt; non-terminal.
     *
     * <pre>
     * &lt;semver-expr&gt; ::= &quot;(&quot; &lt;semver-expr&gt; &quot;)&quot;
     * | &quot;!&quot; &quot;(&quot; &lt;semver-expr&gt; &quot;)&quot;
     * | &lt;semver-expr&gt; &lt;more-expr&gt;
     * | &lt;range&gt;
     *
    </pre> *
     *
     * @return the expression AST
     */
    private fun parseSemVerExpression(): CompositeExpression {
        val expr: CompositeExpression
        if (tokens!!.positiveLookahead(Lexer.Token.Type.NOT)) {
            tokens!!.consume()
            consumeNextToken(Lexer.Token.Type.LEFT_PAREN)
            expr = CompositeExpression.Helper.not(parseSemVerExpression())
            consumeNextToken(Lexer.Token.Type.RIGHT_PAREN)
        } else if (tokens!!.positiveLookahead(Lexer.Token.Type.LEFT_PAREN)) {
            consumeNextToken(Lexer.Token.Type.LEFT_PAREN)
            expr = parseSemVerExpression()
            consumeNextToken(Lexer.Token.Type.RIGHT_PAREN)
        } else {
            expr = parseRange()
        }
        return parseMoreExpressions(expr)
    }

    /**
     * Parses the &lt;tilde-range&gt; non-terminal.
     *
     * <pre>
     * &lt;tilde-range&gt; ::= &quot;~&quot; &lt;version&gt;
     *
    </pre> *
     *
     * @return the expression AST
     */
    private fun parseTildeRange(): CompositeExpression {
        consumeNextToken(Lexer.Token.Type.TILDE)
        val major = intOf(consumeNextToken(Lexer.Token.Type.NUMERIC)!!.lexeme)
        if (!tokens!!.positiveLookahead(Lexer.Token.Type.DOT)) {
            return CompositeExpression.Helper.gte(versionFor(major)).and(CompositeExpression.Helper.lt(versionFor(major + 1)))
        }
        consumeNextToken(Lexer.Token.Type.DOT)
        val minor = intOf(consumeNextToken(Lexer.Token.Type.NUMERIC)!!.lexeme)
        if (!tokens!!.positiveLookahead(Lexer.Token.Type.DOT)) {
            return CompositeExpression.Helper.gte(versionFor(major, minor)).and(CompositeExpression.Helper.lt(versionFor(major, minor + 1)))
        }
        consumeNextToken(Lexer.Token.Type.DOT)
        val patch = intOf(consumeNextToken(Lexer.Token.Type.NUMERIC)!!.lexeme)
        return CompositeExpression.Helper.gte(versionFor(major, minor, patch))
            .and(CompositeExpression.Helper.lt(versionFor(major, minor + 1)))
    }

    /**
     * Parses the &lt;version&gt; non-terminal.
     *
     * <pre>
     * &lt;version&gt; ::= &lt;major&gt;
     * | &lt;major&gt; &quot;.&quot; &lt;minor&gt;
     * | &lt;major&gt; &quot;.&quot; &lt;minor&gt; &quot;.&quot; &lt;patch&gt;
     *
    </pre> *
     *
     * @return the parsed version
     */
    private fun parseVersion(): Version {
        val major = intOf(consumeNextToken(Lexer.Token.Type.NUMERIC)!!.lexeme)
        var minor = 0
        if (tokens!!.positiveLookahead(Lexer.Token.Type.DOT)) {
            tokens!!.consume()
            minor = intOf(consumeNextToken(Lexer.Token.Type.NUMERIC)!!.lexeme)
        }
        var patch = 0
        if (tokens!!.positiveLookahead(Lexer.Token.Type.DOT)) {
            tokens!!.consume()
            patch = intOf(consumeNextToken(Lexer.Token.Type.NUMERIC)!!.lexeme)
        }
        return versionFor(major, minor, patch)
    }

    /**
     * Parses the &lt;wildcard-range&gt; non-terminal.
     *
     * <pre>
     * &lt;wildcard-range&gt; ::= &lt;wildcard&gt;
     * | &lt;major&gt; &quot;.&quot; &lt;wildcard&gt;
     * | &lt;major&gt; &quot;.&quot; &lt;minor&gt; &quot;.&quot; &lt;wildcard&gt;
     *
     * &lt;wildcard&gt; ::= &quot;*&quot; | &quot;x&quot; | &quot;X&quot;
     *
    </pre> *
     *
     * @return the expression AST
     */
    private fun parseWildcardRange(): CompositeExpression {
        if (tokens!!.positiveLookahead(Lexer.Token.Type.WILDCARD)) {
            tokens!!.consume()
            return CompositeExpression.Helper.gte(versionFor(0, 0, 0))
        }
        val major = intOf(consumeNextToken(Lexer.Token.Type.NUMERIC)!!.lexeme)
        consumeNextToken(Lexer.Token.Type.DOT)
        if (tokens!!.positiveLookahead(Lexer.Token.Type.WILDCARD)) {
            tokens!!.consume()
            return CompositeExpression.Helper.gte(versionFor(major)).and(CompositeExpression.Helper.lt(versionFor(major + 1)))
        }
        val minor = intOf(consumeNextToken(Lexer.Token.Type.NUMERIC)!!.lexeme)
        consumeNextToken(Lexer.Token.Type.DOT)
        consumeNextToken(Lexer.Token.Type.WILDCARD)
        return CompositeExpression.Helper.gte(versionFor(major, minor)).and(CompositeExpression.Helper.lt(versionFor(major, minor + 1)))
    }
    /**
     * Creates a `Version` instance for the
     * specified major, minor and patch versions.
     *
     * @param major the major version number
     * @param minor the minor version number
     * @param patch the patch version number
     *
     * @return the version for the specified major, minor and patch versions
     */
    /**
     * Creates a `Version` instance for the specified major version.
     *
     * @param major the major version number
     *
     * @return the version for the specified major version
     */
    /**
     * Creates a `Version` instance for
     * the specified major and minor versions.
     *
     * @param major the major version number
     * @param minor the minor version number
     *
     * @return the version for the specified major and minor versions
     */
    private fun versionFor(major: Int, minor: Int = 0, patch: Int = 0): Version {
        return Version(major, minor, patch)
    }

    companion object {
        /**
         * Creates and returns new instance of the `ExpressionParser` class.
         *
         *
         * This method implements the Static Factory Method pattern.
         *
         * @return a new instance of the `ExpressionParser` class
         */
        fun newInstance(): Parser<Expression> {
            return ExpressionParser(Lexer())
        }
    }
}
