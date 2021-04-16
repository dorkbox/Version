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
 * Expression for the logical "and" operator.
 *
 * @author Zafar Khaja <zafarkhaja@gmail.com>
 */
class And implements Expression {

    /**
     * The left-hand operand of expression.
     */
    private final Expression left;

    /**
     * The right-hand operand of expression.
     */
    private final Expression right;

    /**
     * Constructs a {@code And} expression with
     * the left-hand and right-hand operands.
     *
     * @param left the left-hand operand of expression
     * @param right the right-hand operand of expression
     */
    And(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    /**
     * Checks if both operands evaluate to {@code true}.
     *
     * @param version the version to interpret against
     *
     * @return {@code true} if both operands evaluate to {@code true}
     *         or {@code false} otherwise
     */
    @Override
    public
    boolean interpret(Version version) {
        return left.interpret(version) && right.interpret(version);
    }
}
