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
 * This class implements internal DSL for the SemVer Expressions using fluent interface.
 *
 * @author Zafar Khaja <zafarkhaja></zafarkhaja>@gmail.com>
 */
class CompositeExpression
    /**
     * Constructs a `CompositeExpression` with an underlying `Expression`.
     *
     * @param exprTree the underlying expression tree.
     */
    (private var exprTree: Expression) : Expression {

    /**
     * A class with static helper methods.
     */
    object Helper {

        /**
         * Creates a `CompositeExpression` with an underlying `Not` expression.
         *
         * @param expr an `Expression` to negate
         *
         * @return a newly created `CompositeExpression`
         */
        fun not(expr: Expression): CompositeExpression {
            return CompositeExpression(Not(expr))
        }

        /**
         * Creates a `CompositeExpression` with an underlying `Equal` expression.
         *
         * @param version a `Version` to check for equality
         *
         * @return a newly created `CompositeExpression`
         */
        fun eq(version: Version): CompositeExpression {
            return CompositeExpression(Equal(version))
        }

        /**
         * Creates a `CompositeExpression` with an underlying `Equal` expression.
         *
         * @param version a `Version` string to check for equality
         *
         * @return a newly created `CompositeExpression`
         *
         * @throws IllegalArgumentException if the input string is `NULL` or empty
         * @throws ParseException when invalid version string is provided
         * @throws UnexpectedCharacterException is a special case of `ParseException`
         */
        fun eq(version: String): CompositeExpression {
            return eq(Version(version))
        }

        /**
         * Creates a `CompositeExpression` with an underlying `NotEqual` expression.
         *
         * @param version a `Version` to check for non-equality
         *
         * @return a newly created `CompositeExpression`
         */
        fun neq(version: Version): CompositeExpression {
            return CompositeExpression(NotEqual(version))
        }

        /**
         * Creates a `CompositeExpression` with an underlying `NotEqual` expression.
         *
         * @param version a `Version` string to check for non-equality
         *
         * @return a newly created `CompositeExpression`
         *
         * @throws IllegalArgumentException if the input string is `NULL` or empty
         * @throws ParseException when invalid version string is provided
         * @throws UnexpectedCharacterException is a special case of `ParseException`
         */
        fun neq(version: String): CompositeExpression {
            return neq(Version(version))
        }

        /**
         * Creates a `CompositeExpression` with an underlying `Greater` expression.
         *
         * @param version a `Version` to compare with
         *
         * @return a newly created `CompositeExpression`
         */
        fun gt(version: Version): CompositeExpression {
            return CompositeExpression(Greater(version))
        }

        /**
         * Creates a `CompositeExpression` with an underlying `Greater` expression.
         *
         * @param version a `Version` string to compare with
         *
         * @return a newly created `CompositeExpression`
         *
         * @throws IllegalArgumentException if the input string is `NULL` or empty
         * @throws ParseException when invalid version string is provided
         * @throws UnexpectedCharacterException is a special case of `ParseException`
         */
        fun gt(version: String): CompositeExpression {
            return gt(Version(version))
        }

        /**
         * Creates a `CompositeExpression` with an underlying `GreaterOrEqual` expression.
         *
         * @param version a `Version` to compare with
         *
         * @return a newly created `CompositeExpression`
         */
        fun gte(version: Version): CompositeExpression {
            return CompositeExpression(GreaterOrEqual(version))
        }

        /**
         * Creates a `CompositeExpression` with an underlying `GreaterOrEqual` expression.
         *
         * @param version a `Version` string to compare with
         *
         * @return a newly created `CompositeExpression`
         *
         * @throws IllegalArgumentException if the input string is `NULL` or empty
         * @throws ParseException when invalid version string is provided
         * @throws UnexpectedCharacterException is a special case of `ParseException`
         */
        fun gte(version: String): CompositeExpression {
            return gte(Version(version))
        }

        /**
         * Creates a `CompositeExpression` with an underlying `Less` expression.
         *
         * @param version a `Version` to compare with
         *
         * @return a newly created `CompositeExpression`
         */
        fun lt(version: Version): CompositeExpression {
            return CompositeExpression(Less(version))
        }

        /**
         * Creates a `CompositeExpression` with an underlying `Less` expression.
         *
         * @param version a `Version` string to compare with
         *
         * @return a newly created `CompositeExpression`
         *
         * @throws IllegalArgumentException if the input string is `NULL` or empty
         * @throws ParseException when invalid version string is provided
         * @throws UnexpectedCharacterException is a special case of `ParseException`
         */
        fun lt(version: String): CompositeExpression {
            return lt(Version(version))
        }

        /**
         * Creates a `CompositeExpression` with an underlying `LessOrEqual` expression.
         *
         * @param version a `Version` to compare with
         *
         * @return a newly created `CompositeExpression`
         */
        fun lte(version: Version): CompositeExpression {
            return CompositeExpression(LessOrEqual(version))
        }

        /**
         * Creates a `CompositeExpression` with an underlying `LessOrEqual` expression.
         *
         * @param version a `Version` string to compare with
         *
         * @return a newly created `CompositeExpression`
         *
         * @throws IllegalArgumentException if the input string is `NULL` or empty
         * @throws ParseException when invalid version string is provided
         * @throws UnexpectedCharacterException is a special case of `ParseException`
         */
        fun lte(version: String): CompositeExpression {
            return lte(Version(version))
        }
    }

    /**
     * Adds another `Expression` to `CompositeExpression` using `And` logical expression.
     *
     * @param expr an expression to add
     *
     * @return this `CompositeExpression`
     */
    fun and(expr: Expression): CompositeExpression {
        exprTree = And(exprTree, expr)
        return this
    }

    /**
     * Interprets the expression.
     *
     * @param version a `Version` string to interpret against
     *
     * @return the result of the expression interpretation
     *
     * @throws IllegalArgumentException if the input string is `NULL` or empty
     * @throws ParseException when invalid version string is provided
     * @throws UnexpectedCharacterException is a special case of `ParseException`
     */
    fun interpret(version: String): Boolean {
        return interpret(Version(version))
    }

    /**
     * {@inheritDoc}
     */
    override fun interpret(version: Version?): Boolean {
        return exprTree.interpret(version)
    }

    /**
     * Adds another `Expression` to `CompositeExpression` using `Or` logical expression.
     *
     * @param expr an expression to add
     *
     * @return this `CompositeExpression`
     */
    fun or(expr: Expression): CompositeExpression {
        exprTree = Or(exprTree, expr)
        return this
    }
}
