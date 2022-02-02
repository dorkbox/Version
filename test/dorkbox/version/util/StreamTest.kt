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
package dorkbox.version.util

import org.junit.Assert
import org.junit.Test

/**
 * @author Zafar Khaja <zafarkhaja></zafarkhaja>@gmail.com>
 */
class StreamTest {
    @Test
    fun shouldBeBackedByArray() {
        val input = arrayOf('a', 'b', 'c')
        val stream = Stream(input)
        Assert.assertArrayEquals(input, stream.toArray())
    }

    @Test
    fun shouldCheckIfElementOfExpectedTypesExistBeforeGivenType() {
        val stream = Stream(arrayOf('1', '.', '0', '.', '0'))
        Assert.assertTrue(stream.positiveLookaheadBefore(object : Stream.ElementType<Char> {
            override fun isMatchedBy(element: Char?): Boolean {
                return element == '.'
            }
        }, object : Stream.ElementType<Char> {
            override fun isMatchedBy(element: Char?): Boolean {
                return element == '1'
            }
        }))
        Assert.assertFalse(stream.positiveLookaheadBefore(object : Stream.ElementType<Char> {
            override fun isMatchedBy(element: Char?): Boolean {
                return element == '1'
            }
        }, object : Stream.ElementType<Char> {
            override fun isMatchedBy(element: Char?): Boolean {
                return element == '.'
            }
        }))
    }

    @Test
    fun shouldCheckIfElementOfExpectedTypesExistUntilGivenPosition() {
        val stream = Stream(arrayOf('1', '.', '0', '.', '0'))
        Assert.assertTrue(stream.positiveLookaheadUntil(3, object : Stream.ElementType<Char> {
            override fun isMatchedBy(element: Char?): Boolean {
                return element == '0'
            }
        }))
        Assert.assertFalse(stream.positiveLookaheadUntil(3, object : Stream.ElementType<Char> {
            override fun isMatchedBy(element: Char?): Boolean {
                return element == 'a'
            }
        }))
    }

    @Test
    fun shouldCheckIfLookaheadIsOfExpectedTypes() {
        val stream = Stream(arrayOf('a', 'b', 'c'))
        Assert.assertTrue(stream.positiveLookahead(object : Stream.ElementType<Char> {
            override fun isMatchedBy(element: Char?): Boolean {
                return element == 'a'
            }
        }))
        Assert.assertFalse(stream.positiveLookahead(object : Stream.ElementType<Char> {
            override fun isMatchedBy(element: Char?): Boolean {
                return element == 'c'
            }
        }))
    }

    @Test
    fun shouldConsumeElementsOneByOne() {
        val stream = Stream(arrayOf('a', 'b', 'c'))
        Assert.assertEquals(Character.valueOf('a'), stream.consume())
        Assert.assertEquals(Character.valueOf('b'), stream.consume())
        Assert.assertEquals(Character.valueOf('c'), stream.consume())
    }

    @Test
    fun shouldImplementIterable() {
        val input = arrayOf('a', 'b', 'c')
        val stream = Stream(input)
        val it: Iterator<Char> = stream.iterator()
        for (chr in input) {
            Assert.assertEquals(chr, it.next())
        }
        Assert.assertFalse(it.hasNext())
    }

    @Test
    fun shouldKeepTrackOfCurrentOffset() {
        val stream = Stream(arrayOf('a', 'b', 'c'))
        Assert.assertEquals(0, stream.currentOffset().toLong())
        stream.consume()
        Assert.assertEquals(1, stream.currentOffset().toLong())
        stream.consume()
        stream.consume()
        Assert.assertEquals(3, stream.currentOffset().toLong())
    }

    @Test
    fun shouldLookaheadArbitraryNumberOfElements() {
        val stream = Stream(arrayOf('a', 'b', 'c'))
        Assert.assertEquals(Character.valueOf('a'), stream.lookahead(1))
        Assert.assertEquals(Character.valueOf('b'), stream.lookahead(2))
        Assert.assertEquals(Character.valueOf('c'), stream.lookahead(3))
    }

    @Test
    fun shouldLookaheadWithoutConsuming() {
        val stream = Stream(arrayOf('a', 'b', 'c'))
        Assert.assertEquals(Character.valueOf('a'), stream.lookahead())
        Assert.assertEquals(Character.valueOf('a'), stream.lookahead())
    }

    @Test
    fun shouldNotReturnRealElementsArray() {
        val stream = Stream(arrayOf('a', 'b', 'c'))
        val charArray = stream.toArray()
        charArray[0] = Character.valueOf('z')
        Assert.assertEquals(Character.valueOf('z'), charArray[0])
        Assert.assertEquals(Character.valueOf('a'), stream.toArray()[0])
    }

    @Test
    fun shouldPushBackOneElementAtATime() {
        val stream = Stream(arrayOf('a', 'b', 'c'))
        Assert.assertEquals(Character.valueOf('a'), stream.consume())
        stream.pushBack()
        Assert.assertEquals(Character.valueOf('a'), stream.consume())
    }

    @Test
    fun shouldRaiseErrorWhenUnexpectedElementConsumed() {
        val stream: Stream<Char> = Stream(arrayOf('a', 'b', 'c'))
        try {
            val expected: Stream.ElementType<Char> = object : Stream.ElementType<Char> {
                override fun isMatchedBy(element: Char?): Boolean {
                    return false
                }
            }
            stream.consume(expected)
        } catch (e: UnexpectedElementException) {
            return
        }
        Assert.fail("Should raise error when unexpected element type is consumed")
    }

    @Test
    fun shouldReturnArrayOfElementsThatAreLeftInStream() {
        val stream = Stream(arrayOf('a', 'b', 'c'))
        stream.consume()
        stream.consume()
        Assert.assertEquals(1, stream.toArray().size.toLong())
        Assert.assertEquals(Character.valueOf('c'), stream.toArray()[0])
    }

    @Test
    fun shouldStopPushingBackWhenThereAreNoElements() {
        val stream = Stream(arrayOf('a', 'b', 'c'))
        Assert.assertEquals(Character.valueOf('a'), stream.consume())
        Assert.assertEquals(Character.valueOf('b'), stream.consume())
        Assert.assertEquals(Character.valueOf('c'), stream.consume())
        stream.pushBack()
        stream.pushBack()
        stream.pushBack()
        stream.pushBack()
        Assert.assertEquals(Character.valueOf('a'), stream.consume())
    }
}
