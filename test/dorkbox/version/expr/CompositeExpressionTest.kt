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

import dorkbox.version.expr.CompositeExpression.Helper.eq
import dorkbox.version.expr.CompositeExpression.Helper.gt
import dorkbox.version.expr.CompositeExpression.Helper.gte
import dorkbox.version.expr.CompositeExpression.Helper.lt
import dorkbox.version.expr.CompositeExpression.Helper.lte
import dorkbox.version.expr.CompositeExpression.Helper.neq
import dorkbox.version.expr.CompositeExpression.Helper.not
import org.junit.Assert
import org.junit.Test

/**
 * @author Zafar Khaja <zafarkhaja></zafarkhaja>@gmail.com>
 */
class CompositeExpressionTest {
    @Test
    fun shouldSupportAndExpression() {
        Assert.assertTrue(gt("1.0.0").and(lt("2.0.0")).interpret("1.5.0"))
        Assert.assertFalse(gt("1.0.0").and(lt("2.0.0")).interpret("2.5.0"))
    }

    @Test
    fun shouldSupportComplexExpressions() {
        /* ((>=1.0.1 & <2) | (>=3.0 & <4)) & ((1-1.5) & (~1.5)) */
        val expr: CompositeExpression = gte("1.0.1").and(
            lt("2.0.0").or(
                gte("3.0.0").and(
                    lt("4.0.0").and(
                        gte("1.0.0").and(
                            lte("1.5.0").and(
                                gte("1.5.0").and(
                                    lt(
                                        "2.0.0"
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )
        Assert.assertTrue(expr.interpret("1.5.0"))
        Assert.assertFalse(expr.interpret("2.5.0"))
    }

    @Test
    fun shouldSupportEqualExpression() {
        Assert.assertTrue(eq("1.0.0").interpret("1.0.0"))
        Assert.assertFalse(eq("1.0.0").interpret("2.0.0"))
    }

    @Test
    fun shouldSupportGreaterExpression() {
        Assert.assertTrue(gt("1.0.0").interpret("2.0.0"))
        Assert.assertFalse(gt("2.0.0").interpret("1.0.0"))
    }

    @Test
    fun shouldSupportGreaterOrEqualExpression() {
        Assert.assertTrue(gte("1.0.0").interpret("1.0.0"))
        Assert.assertTrue(gte("1.0.0").interpret("2.0.0"))
        Assert.assertFalse(gte("2.0.0").interpret("1.0.0"))
    }

    @Test
    fun shouldSupportLessExpression() {
        Assert.assertTrue(lt("2.0.0").interpret("1.0.0"))
        Assert.assertFalse(lt("1.0.0").interpret("2.0.0"))
    }

    @Test
    fun shouldSupportLessOrEqualExpression() {
        Assert.assertTrue(lte("1.0.0").interpret("1.0.0"))
        Assert.assertTrue(lte("2.0.0").interpret("1.0.0"))
        Assert.assertFalse(lte("1.0.0").interpret("2.0.0"))
    }

    @Test
    fun shouldSupportNotEqualExpression() {
        Assert.assertTrue(neq("1.0.0").interpret("2.0.0"))
    }

    @Test
    fun shouldSupportNotExpression() {
        Assert.assertTrue(not(eq("1.0.0")).interpret("2.0.0"))
        Assert.assertFalse(not(eq("1.0.0")).interpret("1.0.0"))
    }

    @Test
    fun shouldSupportOrExpression() {
        Assert.assertTrue(lt("1.0.0").or(gt("1.0.0")).interpret("1.5.0"))
        Assert.assertFalse(gt("1.0.0").or(gt("2.0.0")).interpret("0.5.0"))
    }
}
