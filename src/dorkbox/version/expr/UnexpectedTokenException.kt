/*
 * Copyright 2023 dorkbox, llc
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

import dorkbox.version.ParseException
import dorkbox.version.util.UnexpectedElementException

/**
 * Thrown when a token of unexpected types is encountered during the parsing.
 *
 * @author Zafar Khaja <zafarkhaja></zafarkhaja>@gmail.com>
 */
@Suppress("UNCHECKED_CAST")
class UnexpectedTokenException : ParseException {
    /**
     * The unexpected token.
     */
    val unexpectedToken: Lexer.Token

    /**
     * The array of the expected token types.
     */
    val expectedTokenTypes: Array<Lexer.Token.Type>

    /**
     * Constructs a `UnexpectedTokenException` instance with the wrapped `UnexpectedElementException` exception.
     *
     * @param cause the wrapped exception
     */
    internal constructor(cause: UnexpectedElementException) {
        unexpectedToken = cause.unexpectedElement as Lexer.Token
        expectedTokenTypes = cause.expectedElementTypes as Array<Lexer.Token.Type>
    }

    /**
     * Constructs a `UnexpectedTokenException` instance with the unexpected token and the expected types.
     *
     * @param token the unexpected token
     * @param expected an array of the expected token types
     */
    internal constructor(token: Lexer.Token, vararg expected: Lexer.Token.Type) {
        unexpectedToken = token
        expectedTokenTypes = expected as Array<Lexer.Token.Type>
    }

    /**
     * Returns the string representation of this exception containing the information about the unexpected
     * token and, if available, about the expected types.
     *
     * @return the string representation of this exception
     */
    override fun toString(): String {
        var message = String.format("Unexpected token '%s'", unexpectedToken)
        if (expectedTokenTypes.isNotEmpty()) {
            message += String.format(", expecting '%s'", expectedTokenTypes.contentToString())
        }
        return message
    }
}
