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
package dorkbox.version.util

/**
 * Thrown when attempting to consume a stream element of unexpected types.
 *
 * @author Zafar Khaja <zafarkhaja></zafarkhaja>@gmail.com>
 * @see Stream.consume
 */
class UnexpectedElementException internal constructor(
    /**
     * The unexpected element in the stream, can be null.
     */
    val unexpectedElement: Any?,

    /**
     * The position of the unexpected element in the stream.
     */
    val position: Int,

    /**
     * The array of the expected element types.
     */
    val expectedElementTypes: Array<Stream.ElementType<*>>
) : RuntimeException() {

    /**
     * Returns the string representation of this exception containing the information about the unexpected
     * element and, if available, about the expected types.
     *
     * @return the string representation of this exception
     */
    override fun toString(): String {
        var message = String.format("Unexpected element '%s' at position '%d'", unexpectedElement, position)

        if (expectedElementTypes.isNotEmpty()) {
            message += String.format(", expecting '%s'", expectedElementTypes.contentToString())
        }

        return message
    }
}
