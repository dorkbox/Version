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

import static dorkbox.version.VersionParser.CharType.DIGIT;
import static dorkbox.version.VersionParser.CharType.DOT;
import static dorkbox.version.VersionParser.CharType.EOI;
import static dorkbox.version.VersionParser.CharType.HYPHEN;
import static dorkbox.version.VersionParser.CharType.LETTER;
import static dorkbox.version.VersionParser.CharType.PLUS;
import static dorkbox.version.VersionParser.CharType.SPACE;
import static dorkbox.version.VersionParser.CharType.UNDER_SCORE;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import dorkbox.version.util.Stream;
import dorkbox.version.util.UnexpectedElementException;

/**
 * A parser for the SemVer Version.
 *
 * @author Zafar Khaja <zafarkhaja@gmail.com>
 */
class VersionParser implements Parser<Version> {

    /**
     * Valid character types.
     */
    enum CharType implements Stream.ElementType<Character> {

        DIGIT {
            /**
             * Checks if the specified element matches this type.
             *
             * @param chr the element to be tested
             *
             * @return {@code true} if the element matches this type
             *         or {@code false} otherwise
             */
            @Override
            public
            boolean isMatchedBy(Character chr) {
                if (chr == null) {
                    return false;
                }
                return chr >= '0' && chr <= '9';
            }
        }, LETTER {
            /**
             * Checks if the specified element matches this type.
             *
             * @param chr the element to be tested
             *
             * @return {@code true} if the element matches this type
             *         or {@code false} otherwise
             */
            @Override
            public
            boolean isMatchedBy(Character chr) {
                if (chr == null) {
                    return false;
                }
                return (chr >= 'a' && chr <= 'z') || (chr >= 'A' && chr <= 'Z');
            }
        }, SPACE {
            /**
             * Checks if the specified element matches this type.
             *
             * @param chr the element to be tested
             *
             * @return {@code true} if the element matches this type
             *         or {@code false} otherwise
             */
            @Override
            public
            boolean isMatchedBy(Character chr) {
                if (chr == null) {
                    return false;
                }
                return chr == ' ';
            }
        }, DOT {
            /**
             * Checks if the specified element matches this type.
             *
             * @param chr the element to be tested
             *
             * @return {@code true} if the element matches this type
             *         or {@code false} otherwise
             */
            @Override
            public
            boolean isMatchedBy(Character chr) {
                if (chr == null) {
                    return false;
                }
                return chr == '.';
            }
        }, HYPHEN {
            /**
             * Checks if the specified element matches this type.
             *
             * @param chr the element to be tested
             *
             * @return {@code true} if the element matches this type
             *         or {@code false} otherwise
             */
            @Override
            public
            boolean isMatchedBy(Character chr) {
                if (chr == null) {
                    return false;
                }
                return chr == '-';
            }
        }, PLUS {
            /**
             * Checks if the specified element matches this type.
             *
             * @param chr the element to be tested
             *
             * @return {@code true} if the element matches this type
             *         or {@code false} otherwise
             */
            @Override
            public
            boolean isMatchedBy(Character chr) {
                if (chr == null) {
                    return false;
                }
                return chr == '+';
            }
        }, UNDER_SCORE {
            /**
             * Checks if the specified element matches this type.
             *
             * @param chr the element to be tested
             *
             * @return {@code true} if the element matches this type
             *         or {@code false} otherwise
             */
            @Override
            public
            boolean isMatchedBy(Character chr) {
                if (chr == null) {
                    return false;
                }
                return chr == '_';
            }
        }, EOI {
            /**
             * Checks if the specified element matches this type.
             *
             * @param chr the element to be tested
             *
             * @return {@code true} if the element matches this type
             *         or {@code false} otherwise
             */
            @Override
            public
            boolean isMatchedBy(Character chr) {
                return chr == null;
            }
        }, ILLEGAL {
            /**
             * Checks if the specified element matches this type.
             *
             * @param chr the element to be tested
             *
             * @return {@code true} if the element matches this type
             *         or {@code false} otherwise
             */
            @Override
            public
            boolean isMatchedBy(Character chr) {
                EnumSet<CharType> itself = EnumSet.of(ILLEGAL);
                for (CharType type : EnumSet.complementOf(itself)) {
                    if (type.isMatchedBy(chr)) {
                        return false;
                    }
                }
                return true;
            }
        };

        /**
         * Gets the type for a given character.
         *
         * @param chr the character to get the type for
         *
         * @return the type of the specified character
         */
        static
        CharType forCharacter(Character chr) {
            for (CharType type : values()) {
                if (type.isMatchedBy(chr)) {
                    return type;
                }
            }
            return null;
        }
    }

    /**
     * Parses the whole version including pre-release version and build metadata.
     *
     * @param version the version string to parse
     *
     * @return a valid version object
     *
     * @throws IllegalArgumentException if the input string is {@code NULL} or empty
     * @throws ParseException when there is a grammar error
     * @throws UnexpectedCharacterException when encounters an unexpected character type
     */
    static
    Version parseValidSemVer(String version) {
        VersionParser parser = new VersionParser(version);
        return parser.parseValidSemVer();
    }

    /**
     * Parses the version core.
     *
     * @param versionCore the version core string to parse
     *
     * @return a valid normal version object
     *
     * @throws IllegalArgumentException if the input string is {@code NULL} or empty
     * @throws ParseException when there is a grammar error
     * @throws UnexpectedCharacterException when encounters an unexpected character type
     */
    static
    NormalVersion parseVersionCore(String versionCore) {
        VersionParser parser = new VersionParser(versionCore);
        return parser.parseVersionCore();
    }

    /**
     * Parses the pre-release version.
     *
     * @param preRelease the pre-release version string to parse
     *
     * @return a valid pre-release version object
     *
     * @throws IllegalArgumentException if the input string is {@code NULL} or empty
     * @throws ParseException when there is a grammar error
     * @throws UnexpectedCharacterException when encounters an unexpected character type
     */
    static
    MetadataVersion parsePreRelease(String preRelease) {
        VersionParser parser = new VersionParser(preRelease);
        return parser.parsePreRelease();
    }

    /**
     * Parses the build metadata.
     *
     * @param build the build metadata string to parse
     *
     * @return a valid build metadata object
     *
     * @throws IllegalArgumentException if the input string is {@code NULL} or empty
     * @throws ParseException when there is a grammar error
     * @throws UnexpectedCharacterException when encounters an unexpected character type
     */
    static
    MetadataVersion parseBuild(String build) {
        VersionParser parser = new VersionParser(build);
        return parser.parseBuild();
    }

    /**
     * The stream of characters.
     */
    private final Stream<Character> chars;

    /**
     * Constructs a {@code VersionParser} instance
     * with the input string to parse.
     *
     * @param input the input string to parse
     *
     * @throws IllegalArgumentException if the input string is {@code NULL} or empty
     */
    VersionParser(String input) {
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("Input string is NULL or empty");
        }
        Character[] elements = new Character[input.length()];
        for (int i = 0; i < input.length(); i++) {
            elements[i] = input.charAt(i);
        }
        chars = new Stream<Character>(elements);
    }

    /**
     * Parses the {@literal <alphanumeric identifier>} non-terminal.
     *
     * <pre>
     * {@literal
     * <alphanumeric identifier> ::= <non-digit>
     *             | <non-digit> <identifier characters>
     *             | <identifier characters> <non-digit>
     *             | <identifier characters> <non-digit> <identifier characters>
     * }
     * </pre>
     *
     * @return a string representing the alphanumeric identifier
     */
    private
    String alphanumericIdentifier() {
        StringBuilder sb = new StringBuilder();
        do {
            sb.append(consumeNextCharacter(DIGIT, LETTER, HYPHEN));
        } while (chars.positiveLookahead(DIGIT, LETTER, HYPHEN));
        return sb.toString();
    }

    /**
     * Parses the {@literal <build identifier>} non-terminal.
     *
     * <pre>
     * {@literal
     * <build identifier> ::= <alphanumeric identifier>
     *                      | <digits>
     * }
     * </pre>
     *
     * @return a single build identifier
     */
    private
    String buildIdentifier() {
        checkForEmptyIdentifier();
        CharType boundary = nearestCharType(DOT, EOI);
        if (chars.positiveLookaheadBefore(boundary, LETTER, HYPHEN)) {
            return alphanumericIdentifier();
        }
        else {
            return digits();
        }
    }

    /**
     * Checks for empty identifiers in the pre-release version or build metadata.
     *
     * @throws ParseException if the pre-release version or build
     *         metadata have empty identifier(s)
     */
    private
    void checkForEmptyIdentifier() {
        Character la = chars.lookahead(1);
        if (DOT.isMatchedBy(la) || PLUS.isMatchedBy(la) || EOI.isMatchedBy(la)) {
            throw new ParseException("Identifiers MUST NOT be empty", new UnexpectedCharacterException(la, chars.currentOffset(), DIGIT, LETTER, HYPHEN));
        }
    }

    /**
     * Checks for leading zeroes in the numeric identifiers.
     *
     * @throws ParseException if a numeric identifier has leading zero(es)
     */
    private
    void checkForLeadingZeroes() {
        Character la1 = chars.lookahead(1);
        Character la2 = chars.lookahead(2);
        if (la1 != null && la1 == '0' && DIGIT.isMatchedBy(la2)) {
            throw new ParseException("Numeric identifier MUST NOT contain leading zeroes");
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
    private
    Character consumeNextCharacter(CharType... expected) {
        try {
            return chars.consume(expected);
        } catch (UnexpectedElementException e) {
            throw new UnexpectedCharacterException(e);
        }
    }

    /**
     * Checks to see if the next character is in the stream.
     *
     * @param expected the expected types of the next character
     *
     * @return true if the next character is present in the stream
     */
    private
    boolean checkNextCharacter(CharType... expected) {
        return chars.contains(expected);
    }

    /**
     * Parses the {@literal <digits>} non-terminal.
     *
     * <pre>
     * {@literal
     * <digits> ::= <digit>
     *            | <digit> <digits>
     * }
     * </pre>
     *
     * @return a string representing the digits
     */
    private
    String digits() {
        StringBuilder sb = new StringBuilder();
        do {
            sb.append(consumeNextCharacter(DIGIT));
        } while (chars.positiveLookahead(DIGIT));
        return sb.toString();
    }

    /**
     * Checks if the next character in the stream is valid.
     *
     * @param expected the expected types of the next character
     *
     * @throws UnexpectedCharacterException if the next character is not valid
     */
    private
    void ensureValidLookahead(CharType... expected) {
        if (!chars.positiveLookahead(expected)) {
            throw new UnexpectedCharacterException(chars.lookahead(1), chars.currentOffset(), expected);
        }
    }

    /**
     * Finds the nearest character type.
     *
     * @param types the character types to choose from
     *
     * @return the nearest character type or {@code EOI}
     */
    private
    CharType nearestCharType(CharType... types) {
        for (Character chr : chars) {
            for (CharType type : types) {
                if (type.isMatchedBy(chr)) {
                    return type;
                }
            }
        }
        return EOI;
    }

    /**
     * Parses the {@literal <numeric identifier>} non-terminal.
     *
     * <pre>
     * {@literal
     * <numeric identifier> ::= "0"
     *                        | <positive digit>
     *                        | <positive digit> <digits>
     * }
     * </pre>
     *
     * @return a string representing the numeric identifier
     */
    private
    String numericIdentifier() {
        checkForLeadingZeroes();
        return digits();
    }

    /**
     * Parses the input string.
     *
     * @param input the input string to parse. Although {@code input} is not used
     *         in the method, it's present to override {@code Parser.parse(String)}
     *
     * @return a valid version object
     *
     * @throws ParseException when there is a grammar error
     * @throws UnexpectedCharacterException when encounters an unexpected character type
     */
    @Override
    public
    Version parse(String input) {
        return parseValidSemVer();
    }

    /**
     * Parses the {@literal <build>} non-terminal.
     *
     * <pre>
     * {@literal
     * <build> ::= <dot-separated build identifiers>
     *
     * <dot-separated build identifiers> ::= <build identifier>
     *                | <build identifier> "." <dot-separated build identifiers>
     * }
     * </pre>
     *
     * @return a valid build metadata object
     */
    private
    MetadataVersion parseBuild() {
        ensureValidLookahead(DIGIT, LETTER, HYPHEN);
        List<String> idents = new ArrayList<String>();
        do {
            idents.add(buildIdentifier());
            if (chars.positiveLookahead(DOT)) {
                consumeNextCharacter(DOT);
                continue;
            }
            break;
        } while (true);
        //noinspection ToArrayCallWithZeroLengthArrayArgument
        return new MetadataVersion(idents.toArray(new String[idents.size()]));
    }

    /**
     * Parses the {@literal <pre-release>} non-terminal.
     *
     * <pre>
     * {@literal
     * <pre-release> ::= <dot-separated pre-release identifiers>
     *
     * <dot-separated pre-release identifiers> ::= <pre-release identifier>
     *    | <pre-release identifier> "." <dot-separated pre-release identifiers>
     * }
     * </pre>
     *
     * @return a valid pre-release version object
     */
    private
    MetadataVersion parsePreRelease() {
        ensureValidLookahead(DIGIT, LETTER, HYPHEN, UNDER_SCORE);
        List<String> idents = new ArrayList<String>();
        do {
            idents.add(preReleaseIdentifier());
            if (chars.positiveLookahead(DOT)) {
                consumeNextCharacter(DOT);
                continue;
            }
            break;
        } while (true);
        //noinspection ToArrayCallWithZeroLengthArrayArgument
        return new MetadataVersion(idents.toArray(new String[idents.size()]));
    }

    /**
     * Parses the {@literal <valid semver>} non-terminal.
     *
     * <pre>
     * {@literal
     * <valid semver> ::= <version core>
     *                  | <version core> "-" <pre-release>
     *                  | <version core> "+" <build>
     *                  | <version core> "-" <pre-release> "+" <build>
     * }
     * </pre>
     *
     * @return a valid version object
     */
    private
    Version parseValidSemVer() {
        NormalVersion normal = parseVersionCore();
        MetadataVersion preRelease = MetadataVersion.NULL;
        MetadataVersion build = MetadataVersion.NULL;

        // EXCEPTION to SemVer??
        if (!(normal.minorSpecified && normal.patchSpecified)) {
            if (checkNextCharacter(LETTER, DIGIT)) {
                // NOTE: build INSTEAD OF minor/patch is not valid for semver, however when parsing we want to ALLOW parsing as much.
                // straight away to 4.1.Final
                // this is not valid semver, but we want to parse it anyways.
                // when writing this information, IT WILL NOT be in the format, as it will follow semver.
                build = parseBuild();
            }

            // we can have other info.
            if (!checkNextCharacter(HYPHEN, PLUS, UNDER_SCORE, EOI)) {
                if (!normal.minorSpecified && checkNextCharacter(SPACE)) {
                    // fail. but we want to be specific with the error
                    consumeNextCharacter(DOT);
                }

                // fail. but we want to be specific with the error
                consumeNextCharacter(DIGIT);
            }
        }

        // EXCEPTION TO SEMVER
        if (checkNextCharacter(DOT)) {
            // NOTE: DOT is not valid for semver, however when parsing we want to ALLOW parsing as much.
            consumeNextCharacter(DOT);
            // straight away to 4.1.50.Final
            // straight away to 4.5.4.201711221230-r
            // this is not valid semver, but we want to parse it anyways.
            // when writing this information, IT WILL NOT be in the format, as it will follow semver.
            build = parseBuild();
        }

        if (checkNextCharacter(SPACE)) {
            // fail. but we want to be specific with the error
            consumeNextCharacter(DIGIT);
        }

        Character next = consumeNextCharacter(HYPHEN, PLUS, UNDER_SCORE, EOI);
        if (HYPHEN.isMatchedBy(next)) {
            preRelease = parsePreRelease();
            next = consumeNextCharacter(PLUS, EOI);
        }
        if (UNDER_SCORE.isMatchedBy(next)) {
            preRelease = parsePreRelease();
            next = consumeNextCharacter(PLUS, EOI);
        }

        if (PLUS.isMatchedBy(next)) {
            build = parseBuild();
        }

        consumeNextCharacter(EOI);
        return new Version(normal, preRelease, build);
    }

    /**
     * Parses the {@literal <version core>} non-terminal.
     *
     * <pre>
     * {@literal
     * <version core> ::= <major>
     * <version core> ::= <major> "." <minor>
     * <version core> ::= <major> "." <minor> "." <patch>
     * <version core> ::= <major> "." <minor> "." <build>
     * }
     * </pre>
     *
     * @return a valid normal version object
     */
    private
    NormalVersion parseVersionCore() {
        long major = Long.parseLong(numericIdentifier());
        if (!checkNextCharacter(DOT)) {
            // only major!
            return new NormalVersion(major);
        }

        consumeNextCharacter(DOT);

        long minor = Long.parseLong(numericIdentifier());

        if (checkNextCharacter(DOT)) {
            consumeNextCharacter(DOT);

            if (checkNextCharacter(DIGIT)) {
                long patch = Long.parseLong(numericIdentifier());
                return new NormalVersion(major, minor, patch);
            } else if (checkNextCharacter(EOI)) {
                throw new ParseException("Unexpected end of information");
            }
        }

        return new NormalVersion(major, minor);
    }

    /**
     * Parses the {@literal <pre-release identifier>} non-terminal.
     *
     * <pre>
     * {@literal
     * <pre-release identifier> ::= <alphanumeric identifier>
     *                            | <numeric identifier>
     * }
     * </pre>
     *
     * @return a single pre-release identifier
     */
    private
    String preReleaseIdentifier() {
        checkForEmptyIdentifier();
        CharType boundary = nearestCharType(DOT, PLUS, EOI);
        if (chars.positiveLookaheadBefore(boundary, LETTER, HYPHEN)) {
            return alphanumericIdentifier();
        }
        else {
            return numericIdentifier();
        }
    }
}
