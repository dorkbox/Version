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

import dorkbox.version.Version;

/**
 * Expression for the comparison "not equal" operator.
 *
 * @author Zafar Khaja <zafarkhaja@gmail.com>
 */
class NotEqual implements Expression {

    /**
     * The parsed version, the right-hand operand of the "not equal" operator.
     */
    private final Version parsedVersion;

    /**
     * Constructs a {@code NotEqual} expression with the parsed version.
     *
     * @param parsedVersion the parsed version
     */
    NotEqual(Version parsedVersion) {
        this.parsedVersion = parsedVersion;
    }

    /**
     * Checks if the current version does not equal the parsed version.
     *
     * @param version the version to compare with, the left-hand
     *         operand of the "not equal" operator
     *
     * @return {@code true} if the version does not equal the
     *         parsed version or {@code false} otherwise
     */
    @Override
    public
    boolean interpret(Version version) {
        return !version.equals(parsedVersion);
    }
}
