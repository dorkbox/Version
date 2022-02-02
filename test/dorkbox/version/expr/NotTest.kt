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
import org.junit.Assert
import org.junit.Test

/**
 * @author Zafar Khaja <zafarkhaja></zafarkhaja>@gmail.com>
 */
class NotTest {
    @Test
    fun shouldRevertBooleanResultOfExpression() {
        val expr1: Expression = object : Expression {
            override fun interpret(version: Version?): Boolean {
                return false
            }
        }
        val expr2: Expression = object : Expression {
            override fun interpret(version: Version?): Boolean {
                return true
            }
        }
        var not: Not
        not = Not(expr1)
        Assert.assertTrue(not.interpret(null))
        not = Not(expr2)
        Assert.assertFalse(not.interpret(null))
    }
}
