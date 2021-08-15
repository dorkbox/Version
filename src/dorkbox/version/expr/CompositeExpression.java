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

import dorkbox.version.ParseException;
import dorkbox.version.UnexpectedCharacterException;
import dorkbox.version.Version;

/**
 * This class implements internal DSL for the
 * SemVer Expressions using fluent interface.
 *
 * @author Zafar Khaja <zafarkhaja@gmail.com>
 */
public
class CompositeExpression implements Expression {

    /**
     * A class with static helper methods.
     */
    public static
    class Helper {

        /**
         * Creates a {@code CompositeExpression} with
         * an underlying {@code Not} expression.
         *
         * @param expr an {@code Expression} to negate
         *
         * @return a newly created {@code CompositeExpression}
         */
        public static
        CompositeExpression not(Expression expr) {
            return new CompositeExpression(new Not(expr));
        }

        /**
         * Creates a {@code CompositeExpression} with
         * an underlying {@code Equal} expression.
         *
         * @param version a {@code Version} to check for equality
         *
         * @return a newly created {@code CompositeExpression}
         */
        public static
        CompositeExpression eq(Version version) {
            return new CompositeExpression(new Equal(version));
        }

        /**
         * Creates a {@code CompositeExpression} with
         * an underlying {@code Equal} expression.
         *
         * @param version a {@code Version} string to check for equality
         *
         * @return a newly created {@code CompositeExpression}
         *
         * @throws IllegalArgumentException if the input string is {@code NULL} or empty
         * @throws ParseException when invalid version string is provided
         * @throws UnexpectedCharacterException is a special case of {@code ParseException}
         */
        public static
        CompositeExpression eq(String version) {
            return eq(new Version(version));
        }

        /**
         * Creates a {@code CompositeExpression} with
         * an underlying {@code NotEqual} expression.
         *
         * @param version a {@code Version} to check for non-equality
         *
         * @return a newly created {@code CompositeExpression}
         */
        public static
        CompositeExpression neq(Version version) {
            return new CompositeExpression(new NotEqual(version));
        }

        /**
         * Creates a {@code CompositeExpression} with
         * an underlying {@code NotEqual} expression.
         *
         * @param version a {@code Version} string to check for non-equality
         *
         * @return a newly created {@code CompositeExpression}
         *
         * @throws IllegalArgumentException if the input string is {@code NULL} or empty
         * @throws ParseException when invalid version string is provided
         * @throws UnexpectedCharacterException is a special case of {@code ParseException}
         */
        public static
        CompositeExpression neq(String version) {
            return neq(new Version(version));
        }

        /**
         * Creates a {@code CompositeExpression} with
         * an underlying {@code Greater} expression.
         *
         * @param version a {@code Version} to compare with
         *
         * @return a newly created {@code CompositeExpression}
         */
        public static
        CompositeExpression gt(Version version) {
            return new CompositeExpression(new Greater(version));
        }

        /**
         * Creates a {@code CompositeExpression} with
         * an underlying {@code Greater} expression.
         *
         * @param version a {@code Version} string to compare with
         *
         * @return a newly created {@code CompositeExpression}
         *
         * @throws IllegalArgumentException if the input string is {@code NULL} or empty
         * @throws ParseException when invalid version string is provided
         * @throws UnexpectedCharacterException is a special case of {@code ParseException}
         */
        public static
        CompositeExpression gt(String version) {
            return gt(new Version(version));
        }

        /**
         * Creates a {@code CompositeExpression} with an
         * underlying {@code GreaterOrEqual} expression.
         *
         * @param version a {@code Version} to compare with
         *
         * @return a newly created {@code CompositeExpression}
         */
        public static
        CompositeExpression gte(Version version) {
            return new CompositeExpression(new GreaterOrEqual(version));
        }

        /**
         * Creates a {@code CompositeExpression} with an
         * underlying {@code GreaterOrEqual} expression.
         *
         * @param version a {@code Version} string to compare with
         *
         * @return a newly created {@code CompositeExpression}
         *
         * @throws IllegalArgumentException if the input string is {@code NULL} or empty
         * @throws ParseException when invalid version string is provided
         * @throws UnexpectedCharacterException is a special case of {@code ParseException}
         */
        public static
        CompositeExpression gte(String version) {
            return gte(new Version(version));
        }

        /**
         * Creates a {@code CompositeExpression} with
         * an underlying {@code Less} expression.
         *
         * @param version a {@code Version} to compare with
         *
         * @return a newly created {@code CompositeExpression}
         */
        public static
        CompositeExpression lt(Version version) {
            return new CompositeExpression(new Less(version));
        }

        /**
         * Creates a {@code CompositeExpression} with
         * an underlying {@code Less} expression.
         *
         * @param version a {@code Version} string to compare with
         *
         * @return a newly created {@code CompositeExpression}
         *
         * @throws IllegalArgumentException if the input string is {@code NULL} or empty
         * @throws ParseException when invalid version string is provided
         * @throws UnexpectedCharacterException is a special case of {@code ParseException}
         */
        public static
        CompositeExpression lt(String version) {
            return lt(new Version(version));
        }

        /**
         * Creates a {@code CompositeExpression} with an
         * underlying {@code LessOrEqual} expression.
         *
         * @param version a {@code Version} to compare with
         *
         * @return a newly created {@code CompositeExpression}
         */
        public static
        CompositeExpression lte(Version version) {
            return new CompositeExpression(new LessOrEqual(version));
        }

        /**
         * Creates a {@code CompositeExpression} with an
         * underlying {@code LessOrEqual} expression.
         *
         * @param version a {@code Version} string to compare with
         *
         * @return a newly created {@code CompositeExpression}
         *
         * @throws IllegalArgumentException if the input string is {@code NULL} or empty
         * @throws ParseException when invalid version string is provided
         * @throws UnexpectedCharacterException is a special case of {@code ParseException}
         */
        public static
        CompositeExpression lte(String version) {
            return lte(new Version(version));
        }
    }


    /**
     * The underlying expression tree.
     */
    private Expression exprTree;

    /**
     * Constructs a {@code CompositeExpression}
     * with an underlying {@code Expression}.
     *
     * @param expr the underlying expression
     */
    public
    CompositeExpression(Expression expr) {
        exprTree = expr;
    }

    /**
     * Adds another {@code Expression} to {@code CompositeExpression}
     * using {@code And} logical expression.
     *
     * @param expr an expression to add
     *
     * @return this {@code CompositeExpression}
     */
    public
    CompositeExpression and(Expression expr) {
        exprTree = new And(exprTree, expr);
        return this;
    }

    /**
     * Interprets the expression.
     *
     * @param version a {@code Version} string to interpret against
     *
     * @return the result of the expression interpretation
     *
     * @throws IllegalArgumentException if the input string is {@code NULL} or empty
     * @throws ParseException when invalid version string is provided
     * @throws UnexpectedCharacterException is a special case of {@code ParseException}
     */
    public
    boolean interpret(String version) {
        return interpret(new Version(version));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public
    boolean interpret(Version version) {
        return exprTree.interpret(version);
    }

    /**
     * Adds another {@code Expression} to {@code CompositeExpression}
     * using {@code Or} logical expression.
     *
     * @param expr an expression to add
     *
     * @return this {@code CompositeExpression}
     */
    public
    CompositeExpression or(Expression expr) {
        exprTree = new Or(exprTree, expr);
        return this;
    }
}
