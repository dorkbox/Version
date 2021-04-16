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

import java.util.Arrays;

import dorkbox.version.ParseException;
import dorkbox.version.util.UnexpectedElementException;

/**
 * Thrown when a token of unexpected types is encountered during the parsing.
 *
 * @author Zafar Khaja <zafarkhaja@gmail.com>
 */
public
class UnexpectedTokenException extends ParseException {

    /**
     * The unexpected token.
     */
    private final Lexer.Token unexpected;

    /**
     * The array of the expected token types.
     */
    private final Lexer.Token.Type[] expected;

    /**
     * Constructs a {@code UnexpectedTokenException} instance with
     * the wrapped {@code UnexpectedElementException} exception.
     *
     * @param cause the wrapped exception
     */
    UnexpectedTokenException(UnexpectedElementException cause) {
        unexpected = (Lexer.Token) cause.getUnexpectedElement();
        expected = (Lexer.Token.Type[]) cause.getExpectedElementTypes();
    }

    /**
     * Constructs a {@code UnexpectedTokenException} instance
     * with the unexpected token and the expected types.
     *
     * @param token the unexpected token
     * @param expected an array of the expected token types
     */
    UnexpectedTokenException(Lexer.Token token, Lexer.Token.Type... expected) {
        unexpected = token;
        this.expected = expected;
    }

    /**
     * Gets the expected token types.
     *
     * @return an array of expected token types
     */
    Lexer.Token.Type[] getExpectedTokenTypes() {
        return expected;
    }

    /**
     * Gets the unexpected token.
     *
     * @return the unexpected token
     */
    Lexer.Token getUnexpectedToken() {
        return unexpected;
    }

    /**
     * Returns the string representation of this exception
     * containing the information about the unexpected
     * token and, if available, about the expected types.
     *
     * @return the string representation of this exception
     */
    @Override
    public
    String toString() {
        String message = String.format("Unexpected token '%s'", unexpected);
        if (expected.length > 0) {
            message += String.format(", expecting '%s'", Arrays.toString(expected));
        }
        return message;
    }
}
