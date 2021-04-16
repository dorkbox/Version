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
package dorkbox.version.util;

import java.util.Arrays;

import dorkbox.version.util.Stream.ElementType;

/**
 * Thrown when attempting to consume a stream element of unexpected types.
 *
 * @author Zafar Khaja <zafarkhaja@gmail.com>
 * @see Stream#consume(Stream.ElementType...)
 */
public
class UnexpectedElementException extends RuntimeException {

    /**
     * The unexpected element in the stream.
     */
    private final Object unexpected;

    /**
     * The position of the unexpected element in the stream.
     */
    private final int position;

    /**
     * The array of the expected element types.
     */
    private final ElementType<?>[] expected;

    /**
     * Constructs a {@code UnexpectedElementException} instance
     * with the unexpected element and the expected types.
     *
     * @param element the unexpected element in the stream
     * @param position the position of the unexpected element
     * @param expected an array of the expected element types
     */
    UnexpectedElementException(Object element, int position, ElementType<?>... expected) {
        unexpected = element;
        this.position = position;
        this.expected = expected;
    }

    /**
     * Gets the expected element types.
     *
     * @return an array of expected element types
     */
    public
    ElementType<?>[] getExpectedElementTypes() {
        return expected;
    }

    /**
     * Gets the position of the unexpected element.
     *
     * @return the position of the unexpected element
     */
    public
    int getPosition() {
        return position;
    }

    /**
     * Gets the unexpected element.
     *
     * @return the unexpected element
     */
    public
    Object getUnexpectedElement() {
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
        String message = String.format("Unexpected element '%s' at position '%d'", unexpected, position);
        if (expected.length > 0) {
            message += String.format(", expecting '%s'", Arrays.toString(expected));
        }
        return message;
    }
}
