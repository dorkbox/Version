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
 * Expression for the logical "negation" operator.
 *
 * @author Zafar Khaja <zafarkhaja@gmail.com>
 */
class Not implements Expression {

    /**
     * The expression to negate.
     */
    private final Expression expr;

    /**
     * Constructs a {@code Not} expression with an expression to negate.
     *
     * @param expr the expression to negate
     */
    Not(Expression expr) {
        this.expr = expr;
    }

    /**
     * Negates the given expression.
     *
     * @param version the version to interpret against
     *
     * @return {@code true} if the given expression evaluates to
     *         {@code false} and {@code false} otherwise
     */
    @Override
    public
    boolean interpret(Version version) {
        return !expr.interpret(version);
    }
}
