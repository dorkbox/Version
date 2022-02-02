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

import dorkbox.version.Version

/**
 * Expression for the logical "and" operator.
 *
 * @author Zafar Khaja <zafarkhaja></zafarkhaja>@gmail.com>
 */
internal class And(
    /**
     * The left-hand operand of expression.
     */
    private val left: Expression,
    /**
     * The right-hand operand of expression.
     */
    private val right: Expression
) : Expression {
    /**
     * Checks if both operands evaluate to `true`.
     *
     * @param version the version to interpret against
     *
     * @return `true` if both operands evaluate to `true`
     * or `false` otherwise
     */
    override fun interpret(version: Version?): Boolean {
        return left.interpret(version) && right.interpret(version)
    }
}
