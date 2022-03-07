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
package dorkbox.version

import dorkbox.version.util.UnexpectedElementException
import java.util.*

/**
 * Thrown when attempting to consume a character of unexpected types.
 *
 *
 * This exception is a wrapper exception extending `ParseException`.
 *
 * @author Zafar Khaja <zafarkhaja></zafarkhaja>@gmail.com>
 */
class UnexpectedCharacterException : ParseException {
    /**
     * The unexpected character, can be null
     */
    val unexpectedCharacter: Char?

    /**
     * The position of the unexpected character.
     */
    val position: Int

    /**
     * The array of expected character types.
     */
    val expectedCharTypes: Array<CharType>

    /**
     * Constructs a `UnexpectedCharacterException` instance with
     * the wrapped `UnexpectedElementException` exception.
     *
     * @param cause the wrapped exception
     */
    internal constructor(cause: UnexpectedElementException) {
        position = cause.position
        unexpectedCharacter = cause.unexpectedElement as Char?
        @Suppress("UNCHECKED_CAST")
        expectedCharTypes = cause.expectedElementTypes as Array<CharType>
    }

    /**
     * Constructs a `UnexpectedCharacterException` instance
     * with the unexpected character, its position and the expected types.
     *
     * @param unexpected the unexpected character, can be null
     * @param position the position of the unexpected character
     * @param expected an array of the expected character types
     */
    internal constructor(unexpected: Char?, position: Int, vararg expected: CharType) {
        unexpectedCharacter = unexpected
        this.position = position
        @Suppress("UNCHECKED_CAST")
        expectedCharTypes = expected as Array<CharType>
    }

    /**
     * Returns the string representation of this exception
     * containing the information about the unexpected
     * element and, if available, about the expected types.
     *
     * @return the string representation of this exception
     */
    override fun toString(): String {
        var message = String.format(
            "Unexpected character '%s(%s)' at position '%d'", CharType.forCharacter(
                unexpectedCharacter
            ), unexpectedCharacter, position
        )

        if (expectedCharTypes.isNotEmpty()) {
            message += String.format(", expecting '%s'", expectedCharTypes.contentToString())
        }

        return message
    }
}
