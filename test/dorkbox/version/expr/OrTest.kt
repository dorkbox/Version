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
class OrTest {
    @Test
    fun shouldCheckIfOneOfTwoExpressionsEvaluateToTrue() {
        val left: Expression = object : Expression {
            override fun interpret(version: Version?): Boolean {
                return false
            }
        }
        val right: Expression = object : Expression {
            override fun interpret(version: Version?): Boolean {
                return true
            }
        }
        val or = Or(left, right)
        Assert.assertTrue(or.interpret(null))
    }
}
