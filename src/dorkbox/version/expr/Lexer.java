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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dorkbox.version.util.Stream;

/**
 * A lexer for the SemVer Expressions.
 *
 * @author Zafar Khaja <zafarkhaja@gmail.com>
 */
class Lexer {

    /**
     * This class holds the information about lexemes in the input stream.
     */
    static
    class Token {

        /**
         * Valid token types.
         */
        enum Type implements Stream.ElementType<Token> {

            NUMERIC("0|[1-9][0-9]*"),
            DOT("\\."),
            HYPHEN("-"),
            EQUAL("="),
            NOT_EQUAL("!="),
            GREATER(">(?!=)"),
            GREATER_EQUAL(">="),
            LESS("<(?!=)"),
            LESS_EQUAL("<="),
            TILDE("~"),
            WILDCARD("[\\*xX]"),
            CARET("\\^"),
            AND("&"),
            OR("\\|"),
            NOT("!(?!=)"),
            LEFT_PAREN("\\("),
            RIGHT_PAREN("\\)"),
            WHITESPACE("\\s+"),
            EOI("?!");

            /**
             * A pattern matching this type.
             */
            final Pattern pattern;

            /**
             * Constructs a token type with a regular
             * expression for the pattern.
             *
             * @param regexp the regular expression for the pattern
             *
             * @see #pattern
             */
            private
            Type(String regexp) {
                pattern = Pattern.compile("^(" + regexp + ")");
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public
            boolean isMatchedBy(Token token) {
                if (token == null) {
                    return false;
                }
                return this == token.type;
            }

            /**
             * Returns the string representation of this type.
             *
             * @return the string representation of this type
             */
            @Override
            public
            String toString() {
                return name() + "(" + pattern + ")";
            }
        }


        /**
         * The type of this token.
         */
        final Type type;

        /**
         * The lexeme of this token.
         */
        final String lexeme;

        /**
         * The position of this token.
         */
        final int position;

        /**
         * Constructs a {@code Token} instance
         * with the type, lexeme and position.
         *
         * @param type the type of this token
         * @param lexeme the lexeme of this token
         * @param position the position of this token
         */
        Token(Type type, String lexeme, int position) {
            this.type = type;
            this.lexeme = (lexeme == null) ? "" : lexeme;
            this.position = position;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public
        boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof Token)) {
                return false;
            }
            Token token = (Token) other;
            return type.equals(token.type) && lexeme.equals(token.lexeme) && position == token.position;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public
        int hashCode() {
            int hash = 5;
            hash = 71 * hash + type.hashCode();
            hash = 71 * hash + lexeme.hashCode();
            hash = 71 * hash + position;
            return hash;
        }

        /**
         * Returns the string representation of this token.
         *
         * @return the string representation of this token
         */
        @Override
        public
        String toString() {
            return String.format("%s(%s) at position %d", type.name(), lexeme, position);
        }
    }

    /**
     * Constructs a {@code Lexer} instance.
     */
    Lexer() {

    }

    /**
     * Tokenizes the specified input string.
     *
     * @param input the input string to tokenize
     *
     * @return a stream of tokens
     *
     * @throws LexerException when encounters an illegal character
     */
    Stream<Token> tokenize(String input) {
        List<Token> tokens = new ArrayList<Token>();
        int tokenPos = 0;
        while (!input.isEmpty()) {
            boolean matched = false;
            for (Token.Type tokenType : Token.Type.values()) {
                Matcher matcher = tokenType.pattern.matcher(input);
                if (matcher.find()) {
                    matched = true;
                    input = matcher.replaceFirst("");
                    if (tokenType != Token.Type.WHITESPACE) {
                        tokens.add(new Token(tokenType, matcher.group(), tokenPos));
                    }
                    tokenPos += matcher.end();
                    break;
                }
            }
            if (!matched) {
                throw new LexerException(input);
            }
        }
        tokens.add(new Token(Token.Type.EOI, null, tokenPos));
        return new Stream<Token>(tokens.toArray(new Token[tokens.size()]));
    }
}
