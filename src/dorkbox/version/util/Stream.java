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

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

import dorkbox.version.expr.ExpressionParser;

/**
 * A simple stream class used to represent a stream of characters or tokens.
 *
 * @param <E> the type of elements held in this stream
 *
 * @author Zafar Khaja <zafarkhaja@gmail.com>
 * @see com.dorkbox.version.VersionParser
 * @see ExpressionParser
 */
public
class Stream<E> implements Iterable<E> {

    /**
     * The {@code ElementType} interface represents types of the elements
     * held by this stream and can be used for stream filtering.
     *
     * @param <E> type of elements held by this stream
     */
    public
    interface ElementType<E> {

        /**
         * Checks if the specified element matches this type.
         *
         * @param element the element to be tested
         *
         * @return {@code true} if the element matches this type
         *         or {@code false} otherwise
         */
        boolean isMatchedBy(E element);
    }


    /**
     * The array holding all the elements of this stream.
     */
    private final E[] elements;

    /**
     * The current offset which is incremented when an element is consumed.
     *
     * @see #consume()
     */
    private int offset = 0;

    /**
     * Constructs a stream containing the specified elements.
     * <p>
     * The stream does not store the real elements but the defensive copy.
     *
     * @param elements the elements to be streamed
     */
    public
    Stream(E[] elements) {
        this.elements = elements.clone();
    }

    /**
     * Consumes the next element in this stream.
     *
     * @return the next element in this stream
     *         or {@code null} if no more elements left
     */
    public
    E consume() {
        if (offset >= elements.length) {
            return null;
        }
        return elements[offset++];
    }

    /**
     * Consumes the next element in this stream
     * only if it is of the expected types.
     *
     * @param <T> represents the element type of this stream, removes the
     *         "unchecked generic array creation for varargs parameter"
     *         warnings
     * @param expected the types which are expected
     *
     * @return the next element in this stream
     *
     * @throws UnexpectedElementException if the next element is of an unexpected type
     */
    @SafeVarargs
    public final
    <T extends ElementType<E>> E consume(T... expected) {
        E lookahead = lookahead(1);
        for (ElementType<E> type : expected) {
            if (type.isMatchedBy(lookahead)) {
                return consume();
            }
        }
        throw new UnexpectedElementException(lookahead, offset, expected);
    }

    /**
     * Checks to see if the next element is contained in this stream.
     *
     * @param <T> represents the element type of this stream, removes the
     *         "unchecked generic array creation for varargs parameter"
     *         warnings
     * @param expected the types which are expected
     *
     * @return true if the expected elements are in the stream
     */
    @SafeVarargs
    public final
    <T extends ElementType<E>> boolean contains(T... expected) {
        E lookahead = lookahead(1);
        for (ElementType<E> type : expected) {
            if (type.isMatchedBy(lookahead)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns the current offset of this stream.
     *
     * @return the current offset of this stream
     */
    public
    int currentOffset() {
        return offset;
    }

    /**
     * Returns an iterator over elements that are left in this stream.
     *
     * @return an iterator of the remaining elements in this stream
     */
    @Override
    public
    Iterator<E> iterator() {
        return new Iterator<E>() {

            /**
             * The index to indicate the current position
             * of this iterator.
             *
             * The starting point is set to the current
             * value of this stream's offset, so that it
             * doesn't iterate over consumed elements.
             */
            private int index = offset;

            /**
             * Returns {@code true} if the iteration has more elements.
             * (In other words, returns {@code true} if {@link #next} would
             * return an element rather than throwing an exception.)
             *
             * @return {@code true} if the iteration has more elements
             */
            @Override
            public
            boolean hasNext() {
                return index < elements.length;
            }

            /**
             * Returns the next element in the iteration.
             *
             * @return the next element in the iteration
             *
             * @throws NoSuchElementException if the iteration has no more elements
             */
            @Override
            public
            E next() {
                if (index >= elements.length) {
                    throw new NoSuchElementException();
                }
                return elements[index++];
            }

            /**
             * Removes from the underlying collection the last element returned
             * by this iterator (optional operation).  This method can be called
             * only once per call to {@link #next}.  The behavior of an iterator
             * is unspecified if the underlying collection is modified while the
             * iteration is in progress in any way other than by calling this
             * method.
             *
             * @throws UnsupportedOperationException if the {@code remove}
             *         operation is not supported by this iterator
             * @throws IllegalStateException if the {@code next} method has not
             *         yet been called, or the {@code remove} method has already
             *         been called after the last call to the {@code next}
             *         method
             * @implSpec The default implementation throws an instance of
             *         {@link UnsupportedOperationException} and performs no other action.
             */
            @Override
            public
            void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    /**
     * Returns the next element in this stream without consuming it.
     *
     * @return the next element in this stream
     */
    public
    E lookahead() {
        return lookahead(1);
    }

    /**
     * Returns the element at the specified position
     * in this stream without consuming it.
     *
     * @param position the position of the element to return
     *
     * @return the element at the specified position
     *         or {@code null} if no more elements left
     */
    public
    E lookahead(int position) {
        int idx = offset + position - 1;
        if (idx < elements.length) {
            return elements[idx];
        }
        return null;
    }

    /**
     * Checks if the next element in this stream is of the expected types.
     *
     * @param <T> represents the element type of this stream, removes the
     *         "unchecked generic array creation for varargs parameter"
     *         warnings
     * @param expected the expected types
     *
     * @return {@code true} if the next element is of the expected types
     *         or {@code false} otherwise
     */
    @SafeVarargs
    public final
    <T extends ElementType<E>> boolean positiveLookahead(T... expected) {
        for (ElementType<E> type : expected) {
            if (type.isMatchedBy(lookahead(1))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if there exists an element in this stream of
     * the expected types before the specified type.
     *
     * @param <T> represents the element type of this stream, removes the
     *         "unchecked generic array creation for varargs parameter"
     *         warnings
     * @param before the type before which to search
     * @param expected the expected types
     *
     * @return {@code true} if there is an element of the expected types
     *         before the specified type or {@code false} otherwise
     */
    @SafeVarargs
    public final
    <T extends ElementType<E>> boolean positiveLookaheadBefore(ElementType<E> before, T... expected) {
        E lookahead;
        for (int i = 1; i <= elements.length; i++) {
            lookahead = lookahead(i);
            if (before.isMatchedBy(lookahead)) {
                break;
            }
            for (ElementType<E> type : expected) {
                if (type.isMatchedBy(lookahead)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if there is an element in this stream of
     * the expected types until the specified position.
     *
     * @param <T> represents the element type of this stream, removes the
     *         "unchecked generic array creation for varargs parameter"
     *         warnings
     * @param until the position until which to search
     * @param expected the expected types
     *
     * @return {@code true} if there is an element of the expected types
     *         until the specified position or {@code false} otherwise
     */
    @SafeVarargs
    public final
    <T extends ElementType<E>> boolean positiveLookaheadUntil(int until, T... expected) {
        for (int i = 1; i <= until; i++) {
            for (ElementType<E> type : expected) {
                if (type.isMatchedBy(lookahead(i))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Pushes back one element at a time.
     */
    public
    void pushBack() {
        if (offset > 0) {
            offset--;
        }
    }

    /**
     * Returns an array containing all of the
     * elements that are left in this stream.
     * <p>
     * The returned array is a safe copy.
     *
     * @return an array containing all of elements in this stream
     */
    public
    E[] toArray() {
        return Arrays.copyOfRange(elements, offset, elements.length);
    }
}
