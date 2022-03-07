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

/**
 * A simple stream class used to represent a stream of characters or tokens.
 *
 * @param <E> the type of elements held in this stream
 * @param elements the elements to be streamed
 *
 * @author Zafar Khaja <zafarkhaja></zafarkhaja>@gmail.com>
 */
class Stream<E>(elements: Array<E>) : Iterable<E> {
    /**
     * The array holding all the elements of this stream.
     */
    private val elements: Array<E>

    /**
     * The current offset which is incremented when an element is consumed.
     *
     * @see .consume
     */
    private var offset = 0

    init {
        this.elements = elements.clone()
    }

    /**
     * Consumes the next element in this stream.
     *
     * @return the next element in this stream
     * or `null` if no more elements left
     */
    fun consume(): E? {
        return if (offset >= elements.size) {
            null
        } else {
            elements[offset++]
        }
    }

    /**
     * Consumes the next element in this stream
     * only if it is of the expected types.
     *
     * @param <T> represents the element type of this stream
     *
     * @param expected the types which are expected
     *
     * @return the next element in this stream or `null` if no more elements left
     *
     * @throws UnexpectedElementException if the next element is of an unexpected type
     */
    @SafeVarargs
    fun <T : ElementType<E>> consume(vararg expected: T?): E? {
        val lookahead: E? = lookahead(1)
        for (type in expected) {
            if (type?.isMatchedBy(lookahead) == true) {
                return consume()
            }
        }

        @Suppress("UNCHECKED_CAST")
        throw UnexpectedElementException(lookahead as Any?, offset, expected as Array<ElementType<*>>)
    }

    /**
     * Checks to see if the next element is contained in this stream.
     *
     * @param <T> represents the element type of this stream, removes the
     * "unchecked generic array creation for varargs parameter"
     * warnings
     *
     * @param expected the types which are expected
     *
     * @return true if the expected elements are in the stream
     */
    @SafeVarargs
    fun <T : ElementType<E>?> contains(vararg expected: T): Boolean {
        val lookahead = lookahead(1)
        for (type in expected) {
            if (type?.isMatchedBy(lookahead) == true) {
                return true
            }
        }
        return false
    }

    /**
     * Returns the current offset of this stream.
     *
     * @return the current offset of this stream
     */
    fun currentOffset(): Int {
        return offset
    }

    /**
     * Returns the element at the specified position in this stream without consuming it.
     *
     * @param position the position of the element to return
     *
     * @return the element at the specified position
     * or `null` if no more elements left
     */
    fun lookahead(position: Int = 1): E? {
        val idx = offset + position - 1
        return if (idx < elements.size) {
            elements[idx]
        } else {
            null
        }
    }

    /**
     * Checks if the next element in this stream is of the expected types.
     *
     * @param <T> represents the element type of this stream
     *
     * @param expected the expected types
     *
     * @return `true` if the next element is of the expected types
     * or `false` otherwise
     */
    @SafeVarargs
    fun <T : ElementType<E>> positiveLookahead(vararg expected: T): Boolean {
        for (type in expected) {
            if (type.isMatchedBy(lookahead(1))) {
                return true
            }
        }
        return false
    }

    /**
     * Checks if there exists an element in this stream of
     * the expected types before the specified type.
     *
     * @param <T> represents the element type of this stream
     *
     * @param before the type before which to search
     * @param expected the expected types
     *
     * @return `true` if there is an element of the expected types
     * before the specified type or `false` otherwise
     */
    @SafeVarargs
    fun <T : ElementType<E>?> positiveLookaheadBefore(before: ElementType<E>, vararg expected: T): Boolean {
        var lookahead: E?
        for (i in 1..elements.size) {
            lookahead = lookahead(i)
            if (before.isMatchedBy(lookahead)) {
                break
            }

            for (type in expected) {
                if (type?.isMatchedBy(lookahead) == true) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * Checks if there is an element in this stream of
     * the expected types until the specified position.
     *
     * @param <T> represents the element type of this stream
     *
     * @param until the position until which to search
     * @param expected the expected types
     *
     * @return `true` if there is an element of the expected types until the specified position or `false` otherwise
     */
    @SafeVarargs
    fun <T : ElementType<E>?> positiveLookaheadUntil(until: Int, vararg expected: T): Boolean {
        for (i in 1..until) {
            for (type in expected) {
                if (type?.isMatchedBy(lookahead(i)) == true) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * Pushes back one element at a time.
     */
    fun pushBack() {
        if (offset > 0) {
            offset--
        }
    }

    /**
     * Returns an array containing all the
     * chars that are left in this stream.
     *
     *
     * The returned array is a safe copy.
     *
     * @return an array containing all the chars in this stream
     */
    fun toArray(): Array<E> {
        return elements.copyOfRange(offset, elements.size)
    }

    /**
     * The `ElementType` interface represents types of the elements
     * held by this stream and can be used for stream filtering.
     *
     * @param <E> type of elements held by this stream
     */
    interface ElementType<E> {
        /**
         * Checks if the specified element matches this type.
         *
         * @param char the element to be tested
         *
         * @return `true` if the element matches this type
         * or `false` otherwise
         */
        fun isMatchedBy(char: E?): Boolean
    }

    /**
     * Returns an iterator over elements that are left in this stream.
     *
     * @return an iterator of the remaining elements in this stream
     */
    override fun iterator(): MutableIterator<E> {
        return object : MutableIterator<E> {
            /**
             * The index to indicate the current position
             * of this iterator.
             *
             * The starting point is set to the current
             * value of this stream's offset, so that it
             * doesn't iterate over consumed elements.
             */
            private var index = offset

            /**
             * Returns `true` if the iteration has more elements.
             * (In other words, returns `true` if [.next] would
             * return an element rather than throwing an exception.)
             *
             * @return `true` if the iteration has more elements
             */
            override fun hasNext(): Boolean {
                return index < elements.size
            }

            /**
             * Returns the next element in the iteration.
             *
             * @return the next element in the iteration
             *
             * @throws NoSuchElementException if the iteration has no more elements
             */
            override fun next(): E {
                if (index >= elements.size) {
                    throw NoSuchElementException()
                }
                return elements[index++]
            }

            /**
             * Removes from the underlying collection the last element returned
             * by this iterator (optional operation).  This method can be called
             * only once per call to [.next].  The behavior of an iterator
             * is unspecified if the underlying collection is modified while the
             * iteration is in progress in any way other than by calling this
             * method.
             *
             * @throws UnsupportedOperationException if the `remove`
             * operation is not supported by this iterator
             *
             * @throws IllegalStateException if the `next` method has not
             * yet been called, or the `remove` method has already
             * been called after the last call to the `next`
             * method
             *
             * @implSpec The default implementation throws an instance of
             * [UnsupportedOperationException] and performs no other action.
             */
            override fun remove() {
                throw UnsupportedOperationException()
            }
        }
    }
}
