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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import dorkbox.version.Version;
import dorkbox.version.expr.GreaterOrEqual;

/**
 * @author Zafar Khaja <zafarkhaja@gmail.com>
 */
public
class GreaterOrEqualTest {

    @Test
    public
    void shouldCheckIfVersionIsGreaterThanOrEqualToParsedVersion() {
        Version parsed = Version.from("2.0.0");
        GreaterOrEqual ge = new GreaterOrEqual(parsed);
        assertTrue(ge.interpret(Version.from("3.2.1")));
        assertTrue(ge.interpret(Version.from("2.0.0")));
        assertFalse(ge.interpret(Version.from("1.2.3")));
    }
}
