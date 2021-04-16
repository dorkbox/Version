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

import static dorkbox.version.expr.CompositeExpression.Helper.eq;
import static dorkbox.version.expr.CompositeExpression.Helper.gt;
import static dorkbox.version.expr.CompositeExpression.Helper.gte;
import static dorkbox.version.expr.CompositeExpression.Helper.lt;
import static dorkbox.version.expr.CompositeExpression.Helper.lte;
import static dorkbox.version.expr.CompositeExpression.Helper.neq;
import static dorkbox.version.expr.CompositeExpression.Helper.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import dorkbox.version.expr.CompositeExpression;

/**
 * @author Zafar Khaja <zafarkhaja@gmail.com>
 */
public
class CompositeExpressionTest {

    @Test
    public
    void shouldSupportAndExpression() {
        assertTrue(gt("1.0.0").and(lt("2.0.0")).interpret("1.5.0"));
        assertFalse(gt("1.0.0").and(lt("2.0.0")).interpret("2.5.0"));
    }

    @Test
    public
    void shouldSupportComplexExpressions() {
        /* ((>=1.0.1 & <2) | (>=3.0 & <4)) & ((1-1.5) & (~1.5)) */
        CompositeExpression expr = gte("1.0.1").and(lt("2.0.0").or(gte("3.0.0").and(lt("4.0.0").and(gte("1.0.0").and(lte("1.5.0").and(gte("1.5.0").and(lt(
                "2.0.0"))))))));
        assertTrue(expr.interpret("1.5.0"));
        assertFalse(expr.interpret("2.5.0"));
    }

    @Test
    public
    void shouldSupportEqualExpression() {
        assertTrue(eq("1.0.0").interpret("1.0.0"));
        assertFalse(eq("1.0.0").interpret("2.0.0"));
    }

    @Test
    public
    void shouldSupportGreaterExpression() {
        assertTrue(gt("1.0.0").interpret("2.0.0"));
        assertFalse(gt("2.0.0").interpret("1.0.0"));
    }

    @Test
    public
    void shouldSupportGreaterOrEqualExpression() {
        assertTrue(gte("1.0.0").interpret("1.0.0"));
        assertTrue(gte("1.0.0").interpret("2.0.0"));
        assertFalse(gte("2.0.0").interpret("1.0.0"));
    }

    @Test
    public
    void shouldSupportLessExpression() {
        assertTrue(lt("2.0.0").interpret("1.0.0"));
        assertFalse(lt("1.0.0").interpret("2.0.0"));
    }

    @Test
    public
    void shouldSupportLessOrEqualExpression() {
        assertTrue(lte("1.0.0").interpret("1.0.0"));
        assertTrue(lte("2.0.0").interpret("1.0.0"));
        assertFalse(lte("1.0.0").interpret("2.0.0"));
    }

    @Test
    public
    void shouldSupportNotEqualExpression() {
        assertTrue(neq("1.0.0").interpret("2.0.0"));
    }

    @Test
    public
    void shouldSupportNotExpression() {
        assertTrue(not(eq("1.0.0")).interpret("2.0.0"));
        assertFalse(not(eq("1.0.0")).interpret("1.0.0"));
    }

    @Test
    public
    void shouldSupportOrExpression() {
        assertTrue(lt("1.0.0").or(gt("1.0.0")).interpret("1.5.0"));
        assertFalse(gt("1.0.0").or(gt("2.0.0")).interpret("0.5.0"));
    }
}
