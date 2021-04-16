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
package dorkbox.version.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Iterator;

import org.junit.Test;

import dorkbox.version.util.Stream;
import dorkbox.version.util.Stream.ElementType;
import dorkbox.version.util.UnexpectedElementException;

/**
 * @author Zafar Khaja <zafarkhaja@gmail.com>
 */
public
class StreamTest {

    @Test
    public
    void shouldBeBackedByArray() {
        Character[] input = {'a', 'b', 'c'};
        Stream<Character> stream = new Stream<Character>(input);
        assertArrayEquals(input, stream.toArray());
    }

    @Test
    public
    void shouldCheckIfElementOfExpectedTypesExistBeforeGivenType() {
        Stream<Character> stream = new Stream<Character>(new Character[] {'1', '.', '0', '.', '0'});
        assertTrue(stream.positiveLookaheadBefore(new ElementType<Character>() {
            @Override
            public
            boolean isMatchedBy(Character element) {
                return element == '.';
            }
        }, new ElementType<Character>() {
            @Override
            public
            boolean isMatchedBy(Character element) {
                return element == '1';
            }
        }));
        assertFalse(stream.positiveLookaheadBefore(new ElementType<Character>() {
            @Override
            public
            boolean isMatchedBy(Character element) {
                return element == '1';
            }
        }, new ElementType<Character>() {
            @Override
            public
            boolean isMatchedBy(Character element) {
                return element == '.';
            }
        }));
    }

    @Test
    public
    void shouldCheckIfElementOfExpectedTypesExistUntilGivenPosition() {
        Stream<Character> stream = new Stream<Character>(new Character[] {'1', '.', '0', '.', '0'});
        assertTrue(stream.positiveLookaheadUntil(3, new ElementType<Character>() {
            @Override
            public
            boolean isMatchedBy(Character element) {
                return element == '0';
            }
        }));
        assertFalse(stream.positiveLookaheadUntil(3, new ElementType<Character>() {
            @Override
            public
            boolean isMatchedBy(Character element) {
                return element == 'a';
            }
        }));
    }

    @Test
    public
    void shouldCheckIfLookaheadIsOfExpectedTypes() {
        Stream<Character> stream = new Stream<Character>(new Character[] {'a', 'b', 'c'});
        assertTrue(stream.positiveLookahead(new ElementType<Character>() {
            @Override
            public
            boolean isMatchedBy(Character element) {
                return element == 'a';
            }
        }));
        assertFalse(stream.positiveLookahead(new ElementType<Character>() {
            @Override
            public
            boolean isMatchedBy(Character element) {
                return element == 'c';
            }
        }));
    }

    @Test
    public
    void shouldConsumeElementsOneByOne() {
        Stream<Character> stream = new Stream<Character>(new Character[] {'a', 'b', 'c'});
        assertEquals(Character.valueOf('a'), stream.consume());
        assertEquals(Character.valueOf('b'), stream.consume());
        assertEquals(Character.valueOf('c'), stream.consume());
    }

    @Test
    public
    void shouldImplementIterable() {
        Character[] input = {'a', 'b', 'c'};
        Stream<Character> stream = new Stream<Character>(input);
        Iterator<Character> it = stream.iterator();
        for (Character chr : input) {
            assertEquals(chr, it.next());
        }
        assertFalse(it.hasNext());
    }

    @Test
    public
    void shouldKeepTrackOfCurrentOffset() {
        Stream<Character> stream = new Stream<Character>(new Character[] {'a', 'b', 'c'});
        assertEquals(0, stream.currentOffset());
        stream.consume();
        assertEquals(1, stream.currentOffset());
        stream.consume();
        stream.consume();
        assertEquals(3, stream.currentOffset());
    }

    @Test
    public
    void shouldLookaheadArbitraryNumberOfElements() {
        Stream<Character> stream = new Stream<Character>(new Character[] {'a', 'b', 'c'});
        assertEquals(Character.valueOf('a'), stream.lookahead(1));
        assertEquals(Character.valueOf('b'), stream.lookahead(2));
        assertEquals(Character.valueOf('c'), stream.lookahead(3));
    }

    @Test
    public
    void shouldLookaheadWithoutConsuming() {
        Stream<Character> stream = new Stream<Character>(new Character[] {'a', 'b', 'c'});
        assertEquals(Character.valueOf('a'), stream.lookahead());
        assertEquals(Character.valueOf('a'), stream.lookahead());
    }

    @Test
    public
    void shouldNotReturnRealElementsArray() {
        Stream<Character> stream = new Stream<Character>(new Character[] {'a', 'b', 'c'});
        Character[] charArray = stream.toArray();
        charArray[0] = Character.valueOf('z');
        assertEquals(Character.valueOf('z'), charArray[0]);
        assertEquals(Character.valueOf('a'), stream.toArray()[0]);
    }

    @Test
    public
    void shouldPushBackOneElementAtATime() {
        Stream<Character> stream = new Stream<Character>(new Character[] {'a', 'b', 'c'});
        assertEquals(Character.valueOf('a'), stream.consume());
        stream.pushBack();
        assertEquals(Character.valueOf('a'), stream.consume());
    }

    @Test
    public
    void shouldRaiseErrorWhenUnexpectedElementConsumed() {
        Stream<Character> stream = new Stream<Character>(new Character[] {'a', 'b', 'c'});
        try {
            stream.consume(new ElementType<Character>() {
                @Override
                public
                boolean isMatchedBy(Character element) {
                    return false;
                }
            });
        } catch (UnexpectedElementException e) {
            return;
        }
        fail("Should raise error when unexpected element type is consumed");
    }

    @Test
    public
    void shouldReturnArrayOfElementsThatAreLeftInStream() {
        Stream<Character> stream = new Stream<Character>(new Character[] {'a', 'b', 'c'});
        stream.consume();
        stream.consume();
        assertEquals(1, stream.toArray().length);
        assertEquals(Character.valueOf('c'), stream.toArray()[0]);
    }

    @Test
    public
    void shouldStopPushingBackWhenThereAreNoElements() {
        Stream<Character> stream = new Stream<Character>(new Character[] {'a', 'b', 'c'});
        assertEquals(Character.valueOf('a'), stream.consume());
        assertEquals(Character.valueOf('b'), stream.consume());
        assertEquals(Character.valueOf('c'), stream.consume());
        stream.pushBack();
        stream.pushBack();
        stream.pushBack();
        stream.pushBack();
        assertEquals(Character.valueOf('a'), stream.consume());
    }
}
