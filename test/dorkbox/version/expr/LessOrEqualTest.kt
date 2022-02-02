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
class LessOrEqualTest {
    @Test
    fun shouldCheckIfVersionIsLessThanOrEqualToParsedVersion() {
        val parsed = Version("2.0.0")
        val le = LessOrEqual(parsed)
        Assert.assertTrue(le.interpret(Version("1.2.3")))
        Assert.assertTrue(le.interpret(Version("2.0.0")))
        Assert.assertFalse(le.interpret(Version("3.2.1")))
    }
}
