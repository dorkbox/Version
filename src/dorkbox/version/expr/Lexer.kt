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

import dorkbox.version.util.Stream
import java.util.regex.Pattern

/**
 * A lexer for the SemVer Expressions.
 *
 * @author Zafar Khaja <zafarkhaja></zafarkhaja>@gmail.com>
 */
class Lexer
/**
 * Constructs a `Lexer` instance.
 */
{
    /**
     * This class holds the information about lexemes in the input stream.
     */
    class Token(
        /**
         * The type of this token.
         */
        val type: Type, lexeme: String? = "", position: Int
    ) {
        /**
         * The lexeme of this token.
         */
        val lexeme: String

        /**
         * The position of this token.
         */
        val position: Int

        init {
            this.lexeme = lexeme ?: ""
            this.position = position
        }

        /**
         * {@inheritDoc}
         */
        override fun equals(other: Any?): Boolean {
            if (this === other) {
                return true
            }
            if (other !is Token) {
                return false
            }

            return type == other.type && lexeme == other.lexeme && position == other.position
        }

        /**
         * {@inheritDoc}
         */
        override fun hashCode(): Int {
            var hash = 5
            hash = 71 * hash + type.hashCode()
            hash = 71 * hash + lexeme.hashCode()
            hash = 71 * hash + position
            return hash
        }

        /**
         * Returns the string representation of this token.
         *
         * @return the string representation of this token
         */
        override fun toString(): String {
            return String.format("%s(%s) at position %d", type.name, lexeme, position)
        }

        /**
         * Valid token types.
         *
         * @param regexp the regular expression for the pattern
         */
        enum class Type(regexp: String) : Stream.ElementType<Token> {
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
            val pattern: Pattern

            init {
                pattern = Pattern.compile("^($regexp)")
            }

            /**
             * {@inheritDoc}
             */
            override fun isMatchedBy(char: Token?): Boolean {
                return if (char == null) {
                    false
                } else {
                    this == char.type
                }
            }

            /**
             * Returns the string representation of this type.
             *
             * @return the string representation of this type
             */
            override fun toString(): String {
                return "$name($pattern)"
            }
        }
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
    fun tokenize(input: String): Stream<Token> {
        @Suppress("NAME_SHADOWING")
        var input = input
        val tokens: MutableList<Token> = ArrayList()
        var tokenPos = 0

        while (input.isNotEmpty()) {
            var matched = false
            for (tokenType in Token.Type.values()) {
                val matcher = tokenType.pattern.matcher(input)
                if (matcher.find()) {
                    matched = true
                    input = matcher.replaceFirst("")

                    if (tokenType != Token.Type.WHITESPACE) {
                        tokens.add(Token(tokenType, matcher.group(), tokenPos))
                    }

                    tokenPos += matcher.end()
                    break
                }
            }

            if (!matched) {
                throw LexerException(input)
            }
        }

        tokens.add(Token(Token.Type.EOI, null, tokenPos))
        return Stream(tokens.toTypedArray())
    }
}
