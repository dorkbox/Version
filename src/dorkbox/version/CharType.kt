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

package dorkbox.version

import dorkbox.version.util.Stream
import java.util.*

/**
 * Valid character types.
 */
enum class CharType : Stream.ElementType<Char> {
    DIGIT {
        /**
         * Checks if the specified element matches this type.
         *
         * @param char the element to be tested
         *
         * @return `true` if the element matches this type or `false` otherwise
         */
        override fun isMatchedBy(char: Char?): Boolean {
            return if (char == null) {
                false
            } else {
                char in '0'..'9'
            }
        }
    },
    LETTER {
        /**
         * Checks if the specified element matches this type.
         *
         * @param char the element to be tested
         *
         * @return `true` if the element matches this type or `false` otherwise
         */
        override fun isMatchedBy(char: Char?): Boolean {
            return if (char == null) {
                false
            } else {
                char in 'a'..'z' || char in 'A'..'Z'
            }
        }
    },
    SPACE {
        /**
         * Checks if the specified element matches this type.
         *
         * @param char the element to be tested
         *
         * @return `true` if the element matches this type or `false` otherwise
         */
        override fun isMatchedBy(char: Char?): Boolean {
            return if (char == null) {
                false
            } else {
                char == ' '
            }
        }
    },
    DOT {
        /**
         * Checks if the specified element matches this type.
         *
         * @param char the element to be tested
         *
         * @return `true` if the element matches this type or `false` otherwise
         */
        override fun isMatchedBy(char: Char?): Boolean {
            return if (char == null) {
                false
            } else {
                char == '.'
            }
        }
    },
    HYPHEN {
        /**
         * Checks if the specified element matches this type.
         *
         * @param char the element to be tested
         *
         * @return `true` if the element matches this type or `false` otherwise
         */
        override fun isMatchedBy(char: Char?): Boolean {
            return if (char == null) {
                false
            } else {
                char == '-'
            }
        }
    },
    PLUS {
        /**
         * Checks if the specified element matches this type.
         *
         * @param char the element to be tested
         *
         * @return `true` if the element matches this type or `false` otherwise
         */
        override fun isMatchedBy(char: Char?): Boolean {
            return if (char == null) {
                false
            } else {
                char == '+'
            }
        }
    },
    UNDER_SCORE {
        /**
         * Checks if the specified element matches this type.
         *
         * @param char the element to be tested
         *
         * @return `true` if the element matches this type or `false` otherwise
         */
        override fun isMatchedBy(char: Char?): Boolean {
            return if (char == null) {
                false
            } else {
                char == '_'
            }
        }
    },
    EOI {
        /**
         * Checks if the specified element matches this type.
         *
         * @param char the element to be tested
         *
         * @return `true` if the element matches this type or `false` otherwise
         */
        override fun isMatchedBy(char: Char?): Boolean {
            return char == null
        }
    },
    ILLEGAL {
        /**
         * Checks if the specified element matches this type.
         *
         * @param char the element to be tested
         *
         * @return `true` if the element matches this type or `false` otherwise
         */
        override fun isMatchedBy(char: Char?): Boolean {
            val itself = EnumSet.of(ILLEGAL)
            for (type in EnumSet.complementOf(itself)) {
                if (type.isMatchedBy(char)) {
                    return false
                }
            }
            return true
        }
    };

    companion object {
        /**
         * Gets the type for a given character.
         *
         * @param char the character to get the type for
         *
         * @return the type of the specified character
         */
        fun forCharacter(char: Char?): CharType? {
            for (type in values()) {
                if (type.isMatchedBy(char)) {
                    return type
                }
            }
            return null
        }
    }
}
