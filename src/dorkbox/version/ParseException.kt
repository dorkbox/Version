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

/**
 * Thrown to indicate an error during the parsing.
 *
 * @author Zafar Khaja <zafarkhaja></zafarkhaja>@gmail.com>
 */
open class ParseException : RuntimeException {
    /**
     * Constructs a `ParseException` instance with no error message.
     */
    constructor() : super() {}

    /**
     * Constructs a `ParseException` instance with an error message.
     *
     * @param message the error message
     */
    constructor(message: String?) : super(message) {}

    /**
     * Constructs a `ParseException` instance with an error message
     * and the cause exception.
     *
     * @param message the error message
     * @param cause an exception that caused this exception
     */
    constructor(message: String?, cause: UnexpectedCharacterException?) : super(message) {
        initCause(cause)
    }

    /**
     * Returns the string representation of this exception.
     *
     * @return the string representation of this exception
     */
    override fun toString(): String {
        val cause = cause
        var msg = message
        if (msg != null) {
            msg += if (cause != null) " ($cause)" else ""
            return msg
        }

        return cause?.toString() ?: ""
    }
}
