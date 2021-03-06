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

import static dorkbox.version.expr.CompositeExpression.Helper.eq;
import static dorkbox.version.expr.CompositeExpression.Helper.gt;
import static dorkbox.version.expr.CompositeExpression.Helper.gte;
import static dorkbox.version.expr.CompositeExpression.Helper.lt;
import static dorkbox.version.expr.CompositeExpression.Helper.lte;
import static dorkbox.version.expr.CompositeExpression.Helper.neq;
import static dorkbox.version.expr.Lexer.Token.Type.AND;
import static dorkbox.version.expr.Lexer.Token.Type.CARET;
import static dorkbox.version.expr.Lexer.Token.Type.DOT;
import static dorkbox.version.expr.Lexer.Token.Type.EOI;
import static dorkbox.version.expr.Lexer.Token.Type.HYPHEN;
import static dorkbox.version.expr.Lexer.Token.Type.LEFT_PAREN;
import static dorkbox.version.expr.Lexer.Token.Type.NOT;
import static dorkbox.version.expr.Lexer.Token.Type.NUMERIC;
import static dorkbox.version.expr.Lexer.Token.Type.OR;
import static dorkbox.version.expr.Lexer.Token.Type.RIGHT_PAREN;
import static dorkbox.version.expr.Lexer.Token.Type.TILDE;
import static dorkbox.version.expr.Lexer.Token.Type.WILDCARD;

import java.util.EnumSet;
import java.util.Iterator;

import dorkbox.version.Parser;
import dorkbox.version.Version;
import dorkbox.version.expr.Lexer.Token;
import dorkbox.version.util.Stream;
import dorkbox.version.util.UnexpectedElementException;

/**
 * A parser for the SemVer Expressions.
 *
 * @author Zafar Khaja <zafarkhaja@gmail.com>
 */
public
class ExpressionParser implements Parser<Expression> {

    /**
     * Creates and returns new instance of the {@code ExpressionParser} class.
     * <p>
     * This method implements the Static Factory Method pattern.
     *
     * @return a new instance of the {@code ExpressionParser} class
     */
    public static
    Parser<Expression> newInstance() {
        return new ExpressionParser(new Lexer());
    }

    /**
     * The lexer instance used for tokenization of the input string.
     */
    private final Lexer lexer;
    /**
     * The stream of tokens produced by the lexer.
     */
    private Stream<Token> tokens;

    /**
     * Constructs a {@code ExpressionParser} instance
     * with the corresponding lexer.
     *
     * @param lexer the lexer to use for tokenization of the input string
     */
    ExpressionParser(Lexer lexer) {
        this.lexer = lexer;
    }

    /**
     * Tries to consume the next token in the stream.
     *
     * @param expected the expected types of the next token
     *
     * @return the next token in the stream
     *
     * @throws UnexpectedTokenException when encounters an unexpected token type
     */
    private
    Token consumeNextToken(Token.Type... expected) {
        try {
            return tokens.consume(expected);
        } catch (UnexpectedElementException e) {
            throw new UnexpectedTokenException(e);
        }
    }

    /**
     * Returns a {@code int} representation of the specified string.
     *
     * @param value the string to convert into an integer
     *
     * @return the integer value of the specified string
     */
    private
    int intOf(String value) {
        return Integer.parseInt(value);
    }

    /**
     * Determines if the following version terminals are
     * part of the {@literal <hyphen-range>} non-terminal.
     *
     * @return {@code true} if the following version terminals are
     *         part of the {@literal <hyphen-range>} non-terminal or
     *         {@code false} otherwise
     */
    private
    boolean isHyphenRange() {
        return isVersionFollowedBy(HYPHEN);
    }

    /**
     * Determines if the following version terminals are part
     * of the {@literal <partial-version-range>} non-terminal.
     *
     * @return {@code true} if the following version terminals are part
     *         of the {@literal <partial-version-range>} non-terminal or
     *         {@code false} otherwise
     */
    private
    boolean isPartialVersionRange() {
        if (!tokens.positiveLookahead(NUMERIC)) {
            return false;
        }
        EnumSet<Token.Type> expected = EnumSet.complementOf(EnumSet.of(NUMERIC, DOT));
        return tokens.positiveLookaheadUntil(5, expected.toArray(new Token.Type[expected.size()]));
    }

    /**
     * Determines if the version terminals are
     * followed by the specified token type.
     * <p>
     * This method is essentially a {@code lookahead(k)} method
     * which allows to solve the grammar's ambiguities.
     *
     * @param type the token type to check
     *
     * @return {@code true} if the version terminals are followed by
     *         the specified token type or {@code false} otherwise
     */
    private
    boolean isVersionFollowedBy(Stream.ElementType<Token> type) {
        EnumSet<Token.Type> expected = EnumSet.of(NUMERIC, DOT);
        Iterator<Token> it = tokens.iterator();
        Token lookahead = null;
        while (it.hasNext()) {
            lookahead = it.next();
            if (!expected.contains(lookahead.type)) {
                break;
            }
        }
        return type.isMatchedBy(lookahead);
    }

    /**
     * Determines if the following version terminals are part
     * of the {@literal <wildcard-range>} non-terminal.
     *
     * @return {@code true} if the following version terminals are
     *         part of the {@literal <wildcard-range>} non-terminal or
     *         {@code false} otherwise
     */
    private
    boolean isWildcardRange() {
        return isVersionFollowedBy(WILDCARD);
    }

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
    @Override
    public
    Expression parse(String input) {
        tokens = lexer.tokenize(input);
        Expression expr = parseSemVerExpression();
        consumeNextToken(EOI);
        return expr;
    }

    /**
     * Parses the {@literal <caret-range>} non-terminal.
     *
     * <pre>
     * {@literal
     * <caret-range> ::= "^" <version>
     * }
     * </pre>
     *
     * @return the expression AST
     */
    private
    CompositeExpression parseCaretRange() {
        consumeNextToken(CARET);
        int major = intOf(consumeNextToken(NUMERIC).lexeme);
        if (!tokens.positiveLookahead(DOT)) {
            return CompositeExpression.Helper.gte(versionFor(major)).and(CompositeExpression.Helper.lt(versionFor(major + 1)));
        }
        consumeNextToken(DOT);
        int minor = intOf(consumeNextToken(NUMERIC).lexeme);
        if (!tokens.positiveLookahead(DOT)) {
            Version lower = versionFor(major, minor);
            Version upper = major > 0 ? lower.incrementMajorVersion() : lower.incrementMinorVersion();
            return CompositeExpression.Helper.gte(lower).and(CompositeExpression.Helper.lt(upper));
        }
        consumeNextToken(DOT);
        int patch = intOf(consumeNextToken(NUMERIC).lexeme);
        Version version = versionFor(major, minor, patch);
        CompositeExpression gte = CompositeExpression.Helper.gte(version);
        if (major > 0) {
            return gte.and(CompositeExpression.Helper.lt(version.incrementMajorVersion()));
        }
        else if (minor > 0) {
            return gte.and(CompositeExpression.Helper.lt(version.incrementMinorVersion()));
        }
        else if (patch > 0) {
            return gte.and(CompositeExpression.Helper.lt(version.incrementPatchVersion()));
        }
        return CompositeExpression.Helper.eq(version);
    }

    /**
     * Parses the {@literal <comparison-range>} non-terminal.
     *
     * <pre>
     * {@literal
     * <comparison-range> ::= <comparison-op> <version> | <version>
     * }
     * </pre>
     *
     * @return the expression AST
     */
    private
    CompositeExpression parseComparisonRange() {
        Token token = tokens.lookahead();
        CompositeExpression expr;
        switch (token.type) {
            case EQUAL:
                tokens.consume();
                expr = CompositeExpression.Helper.eq(parseVersion());
                break;
            case NOT_EQUAL:
                tokens.consume();
                expr = CompositeExpression.Helper.neq(parseVersion());
                break;
            case GREATER:
                tokens.consume();
                expr = CompositeExpression.Helper.gt(parseVersion());
                break;
            case GREATER_EQUAL:
                tokens.consume();
                expr = CompositeExpression.Helper.gte(parseVersion());
                break;
            case LESS:
                tokens.consume();
                expr = CompositeExpression.Helper.lt(parseVersion());
                break;
            case LESS_EQUAL:
                tokens.consume();
                expr = CompositeExpression.Helper.lte(parseVersion());
                break;
            default:
                expr = CompositeExpression.Helper.eq(parseVersion());
        }
        return expr;
    }

    /**
     * Parses the {@literal <hyphen-range>} non-terminal.
     *
     * <pre>
     * {@literal
     * <hyphen-range> ::= <version> "-" <version>
     * }
     * </pre>
     *
     * @return the expression AST
     */
    private
    CompositeExpression parseHyphenRange() {
        CompositeExpression gte = CompositeExpression.Helper.gte(parseVersion());
        consumeNextToken(HYPHEN);
        return gte.and(CompositeExpression.Helper.lte(parseVersion()));
    }

    /**
     * Parses the {@literal <more-expr>} non-terminal.
     *
     * <pre>
     * {@literal
     * <more-expr> ::= <boolean-op> <semver-expr> | epsilon
     * }
     * </pre>
     *
     * @param expr the left-hand expression of the logical operators
     *
     * @return the expression AST
     */
    private
    CompositeExpression parseMoreExpressions(CompositeExpression expr) {
        if (tokens.positiveLookahead(AND)) {
            tokens.consume();
            expr = expr.and(parseSemVerExpression());
        }
        else if (tokens.positiveLookahead(OR)) {
            tokens.consume();
            expr = expr.or(parseSemVerExpression());
        }
        return expr;
    }

    /**
     * Parses the {@literal <partial-version-range>} non-terminal.
     *
     * <pre>
     * {@literal
     * <partial-version-range> ::= <major> | <major> "." <minor>
     * }
     * </pre>
     *
     * @return the expression AST
     */
    private
    CompositeExpression parsePartialVersionRange() {
        int major = intOf(consumeNextToken(NUMERIC).lexeme);
        if (!tokens.positiveLookahead(DOT)) {
            return CompositeExpression.Helper.gte(versionFor(major)).and(CompositeExpression.Helper.lt(versionFor(major + 1)));
        }
        consumeNextToken(DOT);
        int minor = intOf(consumeNextToken(NUMERIC).lexeme);
        return CompositeExpression.Helper.gte(versionFor(major, minor)).and(CompositeExpression.Helper.lt(versionFor(major, minor + 1)));
    }

    /**
     * Parses the {@literal <range>} non-terminal.
     *
     * <pre>
     * {@literal
     * <expr> ::= <comparison-range>
     *          | <wildcard-expr>
     *          | <tilde-range>
     *          | <caret-range>
     *          | <hyphen-range>
     *          | <partial-version-range>
     * }
     * </pre>
     *
     * @return the expression AST
     */
    private
    CompositeExpression parseRange() {
        if (tokens.positiveLookahead(TILDE)) {
            return parseTildeRange();
        }
        else if (tokens.positiveLookahead(CARET)) {
            return parseCaretRange();
        }
        else if (isWildcardRange()) {
            return parseWildcardRange();
        }
        else if (isHyphenRange()) {
            return parseHyphenRange();
        }
        else if (isPartialVersionRange()) {
            return parsePartialVersionRange();
        }
        return parseComparisonRange();
    }

    /**
     * Parses the {@literal <semver-expr>} non-terminal.
     *
     * <pre>
     * {@literal
     * <semver-expr> ::= "(" <semver-expr> ")"
     *                 | "!" "(" <semver-expr> ")"
     *                 | <semver-expr> <more-expr>
     *                 | <range>
     * }
     * </pre>
     *
     * @return the expression AST
     */
    private
    CompositeExpression parseSemVerExpression() {
        CompositeExpression expr;
        if (tokens.positiveLookahead(NOT)) {
            tokens.consume();
            consumeNextToken(LEFT_PAREN);
            expr = CompositeExpression.Helper.not(parseSemVerExpression());
            consumeNextToken(RIGHT_PAREN);
        }
        else if (tokens.positiveLookahead(LEFT_PAREN)) {
            consumeNextToken(LEFT_PAREN);
            expr = parseSemVerExpression();
            consumeNextToken(RIGHT_PAREN);
        }
        else {
            expr = parseRange();
        }
        return parseMoreExpressions(expr);
    }

    /**
     * Parses the {@literal <tilde-range>} non-terminal.
     *
     * <pre>
     * {@literal
     * <tilde-range> ::= "~" <version>
     * }
     * </pre>
     *
     * @return the expression AST
     */
    private
    CompositeExpression parseTildeRange() {
        consumeNextToken(TILDE);
        int major = intOf(consumeNextToken(NUMERIC).lexeme);
        if (!tokens.positiveLookahead(DOT)) {
            return CompositeExpression.Helper.gte(versionFor(major)).and(CompositeExpression.Helper.lt(versionFor(major + 1)));
        }
        consumeNextToken(DOT);
        int minor = intOf(consumeNextToken(NUMERIC).lexeme);
        if (!tokens.positiveLookahead(DOT)) {
            return CompositeExpression.Helper.gte(versionFor(major, minor)).and(CompositeExpression.Helper.lt(versionFor(major, minor + 1)));
        }
        consumeNextToken(DOT);
        int patch = intOf(consumeNextToken(NUMERIC).lexeme);
        return CompositeExpression.Helper.gte(versionFor(major, minor, patch)).and(CompositeExpression.Helper.lt(versionFor(major, minor + 1)));
    }

    /**
     * Parses the {@literal <version>} non-terminal.
     *
     * <pre>
     * {@literal
     * <version> ::= <major>
     *             | <major> "." <minor>
     *             | <major> "." <minor> "." <patch>
     * }
     * </pre>
     *
     * @return the parsed version
     */
    private
    Version parseVersion() {
        int major = intOf(consumeNextToken(NUMERIC).lexeme);
        int minor = 0;
        if (tokens.positiveLookahead(DOT)) {
            tokens.consume();
            minor = intOf(consumeNextToken(NUMERIC).lexeme);
        }
        int patch = 0;
        if (tokens.positiveLookahead(DOT)) {
            tokens.consume();
            patch = intOf(consumeNextToken(NUMERIC).lexeme);
        }
        return versionFor(major, minor, patch);
    }

    /**
     * Parses the {@literal <wildcard-range>} non-terminal.
     *
     * <pre>
     * {@literal
     * <wildcard-range> ::= <wildcard>
     *                    | <major> "." <wildcard>
     *                    | <major> "." <minor> "." <wildcard>
     *
     * <wildcard> ::= "*" | "x" | "X"
     * }
     * </pre>
     *
     * @return the expression AST
     */
    private
    CompositeExpression parseWildcardRange() {
        if (tokens.positiveLookahead(WILDCARD)) {
            tokens.consume();
            return CompositeExpression.Helper.gte(versionFor(0, 0, 0));
        }

        int major = intOf(consumeNextToken(NUMERIC).lexeme);
        consumeNextToken(DOT);
        if (tokens.positiveLookahead(WILDCARD)) {
            tokens.consume();
            return CompositeExpression.Helper.gte(versionFor(major)).and(CompositeExpression.Helper.lt(versionFor(major + 1)));
        }

        int minor = intOf(consumeNextToken(NUMERIC).lexeme);
        consumeNextToken(DOT);
        consumeNextToken(WILDCARD);
        return CompositeExpression.Helper.gte(versionFor(major, minor)).and(CompositeExpression.Helper.lt(versionFor(major, minor + 1)));
    }

    /**
     * Creates a {@code Version} instance for the specified major version.
     *
     * @param major the major version number
     *
     * @return the version for the specified major version
     */
    private
    Version versionFor(int major) {
        return versionFor(major, 0, 0);
    }

    /**
     * Creates a {@code Version} instance for
     * the specified major and minor versions.
     *
     * @param major the major version number
     * @param minor the minor version number
     *
     * @return the version for the specified major and minor versions
     */
    private
    Version versionFor(int major, int minor) {
        return versionFor(major, minor, 0);
    }

    /**
     * Creates a {@code Version} instance for the
     * specified major, minor and patch versions.
     *
     * @param major the major version number
     * @param minor the minor version number
     * @param patch the patch version number
     *
     * @return the version for the specified major, minor and patch versions
     */
    private
    Version versionFor(int major, int minor, int patch) {
        return Version.from(major, minor, patch);
    }
}
