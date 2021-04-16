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
package dorkbox.version;

import java.util.Arrays;

import dorkbox.version.util.UnexpectedElementException;

/**
 * Thrown when attempting to consume a character of unexpected types.
 * <p>
 * This exception is a wrapper exception extending {@code ParseException}.
 *
 * @author Zafar Khaja <zafarkhaja@gmail.com>
 */
public
class UnexpectedCharacterException extends ParseException {

    /**
     * The unexpected character.
     */
    private final Character unexpected;

    /**
     * The position of the unexpected character.
     */
    private final int position;

    /**
     * The array of expected character types.
     */
    private final VersionParser.CharType[] expected;

    /**
     * Constructs a {@code UnexpectedCharacterException} instance with
     * the wrapped {@code UnexpectedElementException} exception.
     *
     * @param cause the wrapped exception
     */
    UnexpectedCharacterException(UnexpectedElementException cause) {
        position = cause.getPosition();
        unexpected = (Character) cause.getUnexpectedElement();
        expected = (VersionParser.CharType[]) cause.getExpectedElementTypes();
    }

    /**
     * Constructs a {@code UnexpectedCharacterException} instance
     * with the unexpected character, its position and the expected types.
     *
     * @param unexpected the unexpected character
     * @param position the position of the unexpected character
     * @param expected an array of the expected character types
     */
    UnexpectedCharacterException(Character unexpected, int position, VersionParser.CharType... expected) {
        this.unexpected = unexpected;
        this.position = position;
        this.expected = expected;
    }

    /**
     * Gets the expected character types.
     *
     * @return an array of expected character types
     */
    VersionParser.CharType[] getExpectedCharTypes() {
        return expected;
    }

    /**
     * Gets the position of the unexpected character.
     *
     * @return the position of the unexpected character
     */
    int getPosition() {
        return position;
    }

    /**
     * Gets the unexpected character.
     *
     * @return the unexpected character
     */
    Character getUnexpectedCharacter() {
        return unexpected;
    }

    /**
     * Returns the string representation of this exception
     * containing the information about the unexpected
     * element and, if available, about the expected types.
     *
     * @return the string representation of this exception
     */
    @Override
    public
    String toString() {
        String message = String.format("Unexpected character '%s(%s)' at position '%d'", VersionParser.CharType.forCharacter(unexpected), unexpected, position);
        if (expected.length > 0) {
            message += String.format(", expecting '%s'", Arrays.toString(expected));
        }
        return message;
    }
}
