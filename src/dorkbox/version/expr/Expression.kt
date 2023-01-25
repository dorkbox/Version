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
 * The `Expression` interface is to be implemented by the nodes of the Abstract Syntax Tree produced by the `ExpressionParser` class.
 *
 * @author Zafar Khaja <zafarkhaja></zafarkhaja>@gmail.com>
 */
interface Expression {
    /**
     * Interprets the expression.
     *
     * @param version the version to interpret against
     *
     * @return the result of the expression interpretation
     */
    fun interpret(version: Version?): Boolean
}
