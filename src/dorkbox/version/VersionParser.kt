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
import dorkbox.version.util.UnexpectedElementException

/**
 * A parser for the SemVer Version.
 *
 * @author Zafar Khaja <zafarkhaja@gmail.com>
 * @author dorkbox, llc <info></info>@dorkbox.com>
 */
internal class VersionParser
    /**
     * Constructs a `VersionParser` instance with the input string to parse.
     *
     * @param input the input string to parse
     *
     * @throws IllegalArgumentException if the input string is `NULL` or empty
     */
    (input: String) : Parser<Version> {


    /**
     * The stream of characters.
     */
    private val chars: Stream<Char>


    init {
        require(input.isNotEmpty()) { "Input string is empty" }

        val elements = input.map { it }.toTypedArray()
        chars = Stream(elements)
    }

    /**
     * ```
     * Parses the <alphanumeric identifier> non-terminal.
     *
     * <alphanumeric identifier> ::=
     * | <non-digit>
     * | <non-digit> <identifier characters>
     * | <identifier characters> <non-digit>
     * | <identifier characters> <non-digit> <identifier characters>
     *```
     *
     * @return a string representing the alphanumeric identifier
     */
    private fun alphanumericIdentifier(): String {
        val sb = StringBuilder()
        do {
            sb.append(consumeNextCharacter(CharType.DIGIT, CharType.LETTER, CharType.HYPHEN))
        } while (chars.positiveLookahead(CharType.DIGIT, CharType.LETTER, CharType.HYPHEN))
        return sb.toString()
    }

    /**
     * ```
     * Parses the <build identifier> non-terminal.
     *
     * <build identifier> ::=
     * | <alphanumeric identifier>
     * | <digits>
     * ```
     *
     * @return a single build identifier
     */
    private fun buildIdentifier(): String {
        checkForEmptyIdentifier()
        val boundary = nearestCharType(CharType.DOT, CharType.EOI)
        return if (chars.positiveLookaheadBefore(
                boundary,
                CharType.LETTER,
                CharType.HYPHEN
            )
        ) {
            alphanumericIdentifier()
        } else {
            digits()
        }
    }

    /**
     * Checks for empty identifiers in the pre-release version or build metadata.
     *
     * @throws ParseException if the pre-release version or build metadata have empty identifier(s)
     */
    private fun checkForEmptyIdentifier() {
        val la = chars.lookahead(1)
        if (CharType.DOT.isMatchedBy(la) || CharType.PLUS.isMatchedBy(la) || CharType.EOI.isMatchedBy(la)) {
            throw ParseException(
                "Identifiers MUST NOT be empty",
                UnexpectedCharacterException(la, chars.currentOffset(), CharType.DIGIT, CharType.LETTER, CharType.HYPHEN)
            )
        }
    }

    /**
     * Checks for leading zeroes in the numeric identifiers.
     *
     * @throws ParseException if a numeric identifier has leading zero(es)
     */
    private fun checkForLeadingZeroes() {
        val la1 = chars.lookahead(1)
        val la2 = chars.lookahead(2)
        if (la1 != null && la1 == '0' && CharType.DIGIT.isMatchedBy(la2)) {
            throw ParseException("Numeric identifier MUST NOT contain leading zeroes")
        }
    }

    /**
     * Tries to consume the next character in the stream.
     *
     * @param expected the expected types of the next character
     *
     * @return the next character in the stream
     *
     * @throws UnexpectedCharacterException when encounters an unexpected character type
     */
    private fun consumeNextCharacter(vararg expected: CharType): Char? {
        return try {
            chars.consume(*expected)
        } catch (e: UnexpectedElementException) {
            throw UnexpectedCharacterException(e)
        }
    }

    /**
     * Checks to see if the next character is in the stream.
     *
     * @param expected the expected types of the next character
     *
     * @return true if the next character is present in the stream
     */
    private fun checkNextCharacter(vararg expected: CharType): Boolean {
        return chars.contains(*expected)
    }

    /**
     * ```
     * Parses the <digits> non-terminal.
     *
     * <digits> ::=
     * | <digit>
     * | <digit> <digits>
     * ```
     *
     * @return a string representing the digits
     */
    private fun digits(): String {
        val sb = StringBuilder()
        do {
            sb.append(consumeNextCharacter(CharType.DIGIT))
        } while (chars.positiveLookahead(CharType.DIGIT))
        return sb.toString()
    }

    /**
     * Checks if the next character in the stream is valid.
     *
     * @param expected the expected types of the next character
     *
     * @throws UnexpectedCharacterException if the next character is not valid
     */
    private fun ensureValidLookahead(vararg expected: CharType) {
        if (!chars.positiveLookahead(*expected)) {
            throw UnexpectedCharacterException(chars.lookahead(1), chars.currentOffset(), *expected)
        }
    }

    /**
     * Finds the nearest character type.
     *
     * @param types the character types to choose from
     *
     * @return the nearest character type or `EOI`
     */
    private fun nearestCharType(vararg types: CharType): CharType {
        for (chr in chars) {
            for (type in types) {
                if (type.isMatchedBy(chr)) {
                    return type
                }
            }
        }
        return CharType.EOI
    }

    /**
     * ```
     * Parses the <numeric identifier> non-terminal.
     *
     * <numeric identifier> ::=
     * | 0
     * | <positive digit>
     * | <positive digit> <digits>
     * ```
     *
     * @return a string representing the numeric identifier
     */
    private fun numericIdentifier(): String {
        checkForLeadingZeroes()
        return digits()
    }

    /**
     * Parses the input string.
     *
     * @param input the input string to parse. Although `input` is not used in the method, it's present to override `Parser.parse(String)`
     *
     * @return a valid version object
     *
     * @throws ParseException when there is a grammar error
     * @throws UnexpectedCharacterException when encounters an unexpected character type
     */
    override fun parse(input: String): Version {
        return parseValidSemVer()
    }

    /**
     * ```
     * Parses the <build> non-terminal.
     *
     * <build> ::= <dot-separated build identifiers>
     *
     * <dot-separated build identifiers> ::=
     * | <build identifier>
     * | <build identifier>.<dot-separated build identifiers>
     * ```
     *
     * @return a valid build metadata object
     */
    private fun parseBuild(): MetadataVersion {
        ensureValidLookahead(CharType.DIGIT, CharType.LETTER, CharType.HYPHEN)
        val idents: MutableList<String> = ArrayList()
        do {
            idents.add(buildIdentifier())
            if (chars.positiveLookahead(CharType.DOT)) {
                consumeNextCharacter(CharType.DOT)
                continue
            }
            break
        } while (true)
        return MetadataVersion(idents.toTypedArray())
    }

    /**
     * ```
     * Parses the <pre-release> non-terminal.
     *
     * <pre-release> ::= <dot-separated pre-release identifiers>
     *
     * <dot-separated pre-release identifiers> ::=
     * | <pre-release identifier>
     * | <pre-release identifier>.<dot-separated pre-release identifiers>
     * ```
     *
     * @return a valid pre-release version object
     */
    private fun parsePreRelease(): MetadataVersion {
        ensureValidLookahead(CharType.DIGIT, CharType.LETTER, CharType.HYPHEN, CharType.UNDER_SCORE)
        val idents: MutableList<String> = ArrayList()
        do {
            idents.add(preReleaseIdentifier())
            if (chars.positiveLookahead(CharType.DOT)) {
                consumeNextCharacter(CharType.DOT)
                continue
            }
            break
        } while (true)
        return MetadataVersion(idents.toTypedArray())
    }

    /**
     * ```
     * Parses the <valid semver> non-terminal.
     *
     * <valid semver> ::=
     * | <version core>
     * | <version core>-<pre-release>
     * | <version core>+<build>
     * | <version core>-<pre-release>+<build>
     * ```
     *
     * @return a valid version object
     */
    private fun parseValidSemVer(): Version {
        val normal = parseVersionCore()
        var preRelease = MetadataVersion.NULL
        var build = MetadataVersion.NULL

        // EXCEPTION to SemVer??
        if (!(normal.minorSpecified && normal.patchSpecified)) {
            if (checkNextCharacter(CharType.LETTER, CharType.DIGIT)) {
                // NOTE: build INSTEAD OF minor/patch is not valid for semver, however when parsing we want to ALLOW parsing as much.
                // straight away to 4.1.Final
                // this is not valid semver, but we want to parse it anyways.
                // when writing this information, IT WILL NOT be in the format, as it will follow semver.
                build = parseBuild()
            }

            // we can have other info.
            if (!checkNextCharacter(CharType.HYPHEN, CharType.PLUS, CharType.UNDER_SCORE, CharType.EOI)) {
                if (!normal.minorSpecified && checkNextCharacter(CharType.SPACE)) {
                    // fail. but we want to be specific with the error
                    consumeNextCharacter(CharType.DOT)
                }

                // fail. but we want to be specific with the error
                consumeNextCharacter(CharType.DIGIT)
            }
        }

        // EXCEPTION TO SEMVER
        if (checkNextCharacter(CharType.DOT)) {
            // NOTE: DOT is not valid for semver, however when parsing we want to ALLOW parsing as much.
            consumeNextCharacter(CharType.DOT)
            // straight away to 4.1.50.Final
            // straight away to 4.5.4.201711221230-r
            // this is not valid semver, but we want to parse it anyways.
            // when writing this information, IT WILL NOT be in the format, as it will follow semver.
            build = parseBuild()
        }
        if (checkNextCharacter(CharType.SPACE)) {
            // fail. but we want to be specific with the error
            consumeNextCharacter(CharType.DIGIT)
        }
        var next = consumeNextCharacter(CharType.HYPHEN, CharType.PLUS, CharType.UNDER_SCORE, CharType.EOI)
        if (CharType.HYPHEN.isMatchedBy(next)) {
            preRelease = parsePreRelease()
            next = consumeNextCharacter(CharType.PLUS, CharType.EOI)
        }
        if (CharType.UNDER_SCORE.isMatchedBy(next)) {
            preRelease = parsePreRelease()
            next = consumeNextCharacter(CharType.PLUS, CharType.EOI)
        }
        if (CharType.PLUS.isMatchedBy(next)) {
            build = parseBuild()
        }
        consumeNextCharacter(CharType.EOI)
        return Version(normal, preRelease, build)
    }

    /**
     * ```
     * Parses the <version core> non-terminal.
     *
     * <version core> ::= <major>
     * <version core> ::= <major>.<minor>
     * <version core> ::= <major>.<minor>.<patch>
     * <version core> ::= <major>.<minor>.<build>
     * ```
     *
     * @return a valid normal version object
     */
    private fun parseVersionCore(): NormalVersion {
        val major = numericIdentifier().toLong()
        if (!checkNextCharacter(CharType.DOT)) {
            // only major!
            return NormalVersion(major)
        }
        consumeNextCharacter(CharType.DOT)
        val minor = numericIdentifier().toLong()
        if (checkNextCharacter(CharType.DOT)) {
            consumeNextCharacter(CharType.DOT)
            if (checkNextCharacter(CharType.DIGIT)) {
                val patch = numericIdentifier().toLong()
                return NormalVersion(major, minor, patch)
            } else if (checkNextCharacter(CharType.EOI)) {
                throw ParseException("Unexpected end of information")
            }
        }
        return NormalVersion(major, minor)
    }

    /**
     * ```
     * Parses the <pre-release identifier> non-terminal.
     *
     * <pre-release identifier> ::=
     * | <alphanumeric identifier>
     * | <numeric identifier>
     * ```
     *
     * @return a single pre-release identifier
     */
    private fun preReleaseIdentifier(): String {
        checkForEmptyIdentifier()
        val boundary = nearestCharType(CharType.DOT, CharType.PLUS, CharType.EOI)
        return if (chars.positiveLookaheadBefore(
                boundary,
                CharType.LETTER,
                CharType.HYPHEN
            )
        ) {
            alphanumericIdentifier()
        } else {
            numericIdentifier()
        }
    }

    companion object {
        /**
         * Parses the whole version including pre-release version and build metadata.
         *
         * @param majorAndMinor the major and minor version number, in double notation
         *
         * @return a valid version object
         *
         * @throws IllegalArgumentException if the input string is `NULL` or empty
         * @throws ParseException when there is a grammar error
         * @throws UnexpectedCharacterException when encounters an unexpected character type
         */
        fun parseValidSemVer(majorAndMinor: Double): Version {
            require(majorAndMinor >= 0.0) { "Major.minor number MUST be non-negative!" }
            val parser = VersionParser(java.lang.Double.toString(majorAndMinor))
            return parser.parseValidSemVer()
        }

        /**
         * Parses the whole version including pre-release version and build metadata.
         *
         * @param version the version string to parse
         *
         * @return a valid version object
         *
         * @throws IllegalArgumentException if the input string is `NULL` or empty
         * @throws ParseException when there is a grammar error
         * @throws UnexpectedCharacterException when encounters an unexpected character type
         */
        fun parseValidSemVer(version: String): Version {
            val parser = VersionParser(version)
            return parser.parseValidSemVer()
        }

        /**
         * Parses the version core.
         *
         * @param versionCore the version core string to parse
         *
         * @return a valid normal version object
         *
         * @throws IllegalArgumentException if the input string is `NULL` or empty
         * @throws ParseException when there is a grammar error
         * @throws UnexpectedCharacterException when encounters an unexpected character type
         */
        fun parseVersionCore(versionCore: String): NormalVersion {
            val parser = VersionParser(versionCore)
            return parser.parseVersionCore()
        }

        /**
         * Parses the pre-release version.
         *
         * @param preRelease the pre-release version string to parse
         *
         * @return a valid pre-release version object
         *
         * @throws IllegalArgumentException if the input string is `NULL` or empty
         * @throws ParseException when there is a grammar error
         * @throws UnexpectedCharacterException when encounters an unexpected character type
         */
        fun parsePreRelease(preRelease: String): MetadataVersion {
            val parser = VersionParser(preRelease)
            return parser.parsePreRelease()
        }

        /**
         * Parses the build metadata.
         *
         * @param build the build metadata string to parse
         *
         * @return a valid build metadata object
         *
         * @throws IllegalArgumentException if the input string is `NULL` or empty
         * @throws ParseException when there is a grammar error
         * @throws UnexpectedCharacterException when encounters an unexpected character type
         */
        fun parseBuild(build: String): MetadataVersion {
            val parser = VersionParser(build)
            return parser.parseBuild()
        }
    }
}
