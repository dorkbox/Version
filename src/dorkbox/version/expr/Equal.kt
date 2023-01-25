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
package dorkbox.version.expr

import dorkbox.version.Version

/**
 * Expression for the comparison "equal" operator.
 *
 * @author Zafar Khaja <zafarkhaja></zafarkhaja>@gmail.com>
 */
class Equal(
    /**
     * The parsed version, the right-hand operand of the "equal" operator.
     */
    private val parsedVersion: Version
) : Expression {
    /**
     * Checks if the current version equals the parsed version.
     *
     * @param version the version to compare to, the left-hand operand of the "equal" operator
     *
     * @return `true` if the version equals the parsed version or `false` otherwise
     */
    override fun interpret(version: Version?): Boolean {
        return version == parsedVersion
    }
}
