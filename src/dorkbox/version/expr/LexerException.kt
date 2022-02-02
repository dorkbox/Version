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

import dorkbox.version.ParseException

/**
 * Thrown during the lexical analysis when
 * an illegal character is encountered.
 *
 * @author Zafar Khaja <zafarkhaja></zafarkhaja>@gmail.com>
 */
class LexerException

/**
 * Constructs a `LexerException` instance with
 * a string starting from an illegal character.
 *
 * @param expr the string starting from an illegal character
 */
internal constructor(
    /**
     * The string being analyzed starting from an illegal character.
     */
    private val expr: String
) : ParseException() {
    /**
     * Returns the string representation of this exception.
     *
     * @return the string representation of this exception
     */
    override fun toString(): String {
        return "Illegal character near '$expr'"
    }
}
