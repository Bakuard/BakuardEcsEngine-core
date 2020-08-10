package com.bakuard.ecsEngine.core.utils;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Ассоциативный массив на основне хеш-таблицы с разрешением коллизий методом цепочек. В качестве ключей
 * выступают целые числа типа int.
 * @param <T> тип значений.
 */
public final class IntMap<T> implements Iterable<IntMap.Node<T>> {

    private int size;
    private Node<T>[] table;
    private final float LOAD_FACTOR = 0.75F;
    private final int MAX_CAPACITY = 1073741824;
    private int actualModCount;

    /**
     *  Создает пустой ассоциативный массив.
     */
    public IntMap() {
        table = new Node[16];
    }

    /**
     * Добавляет или перезаписывает элемент по указанному ключу и возвращает значение, которое хранилось
     * по указаному ключу перед вызовом этого метода или null, если по даному ключу не хранилось
     * ни одного значения.
     * @param key ключ.
     * @param value добавляемое или перезаписываемое значение.
     * @return  значение, которое хранилось по указаному ключу перед вызовом этого метода или null,
     *          если по даному ключу не хранилось ни одного значения.
     */
    public T put(int key, T value) {
        if((float)size/table.length >= LOAD_FACTOR && table.length < MAX_CAPACITY) resize();

        T oldValue = null;
        Node<T> duplicate = getNode(key);
        if(duplicate != null) {
            oldValue = duplicate.value;
            duplicate.value = value;
        } else {
            int bucketIndex = keyToBucketIndex(key, table.length);
            Node<T> newNode = new Node<>(key, value, table[bucketIndex]);
            table[bucketIndex] = newNode;
            ++size;
        }

        ++actualModCount;

        return oldValue;
    }

    /**
     * Возвращает значение хранящееся по указаному ключу или null, если по указаному ключу не хранится
     * на одного значения.
     * @param key ключ.
     * @return значение хранящееся по указаному ключу или null, если по указаному ключу не хранится
     *         на одного значения.
     */
    public T get(int key) {
        Node<T> result = getNode(key);
        return result != null ? result.value : null;
    }

    /**
     * Удаляет значение хранящееся по указаному ключу и возвращает его. Если по указаному ключу не хранится
     * ни одного значение - возвращает null.
     * @param key ключ.
     * @return удаляемое значение или null, если по указаном ключу ничего не хранилось.
     */
    public T remove(int key) {
        int bucketIndex = keyToBucketIndex(key, table.length);

        Node<T> currentNode = table[bucketIndex];
        Node<T> previousNode = currentNode;
        while(currentNode != null && currentNode.KEY != key) {
            previousNode = currentNode;
            currentNode = currentNode.next;
        }

        if(currentNode != null) {
            if(previousNode == table[bucketIndex]) {
                table[bucketIndex] = currentNode.next;
            } else {
                previousNode.next = currentNode.next;
            }
            --size;
            ++actualModCount;
            return currentNode.value;
        }
        return null;
    }

    /**
     * Очищает ассоциативный массив. После вызово данного метода размер ассоциативного массива будет равен 0.
     */
    public void clear() {
        size = 0;
        for(int i = 0; i < table.length; ++i) table[i] = null;
        ++actualModCount;
    }

    /**
     * Возвращает кол-во пар ключ-значение хранящихся в ассоциативном массиве на момент вызова данного метода.
     * @return кол-во пар ключ-значение хранящихся в ассоциативном массиве на момент вызова данного метода.
     */
    public int getSize() {
        return size;
    }

    /**
     * Возвращает true - если ассоциативный массив пуст, иначе - false.
     * @return true - если ассоциативный массив пуст, иначе - false.
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Возвращает true, если ассоциативный массив хранит значение по указаному ключу, иначе - false.
     * @param key ключ.
     * @return true, если ассоциативный массив хранит значение по указаному ключу, иначе - false.
     */
    public boolean containsKey(int key) {
        return getNode(key) != null;
    }

    /**
     * Заполняет переданный массив всеми значениями хранящимеся в ассоциативном массиве. Каждое значение
     * ассоциативного массива будет записано в передаваемый массив столько раз, сколько оно встречается в
     * ассоциативном массиве. Переданный массив будет заполняться с элемента под индексом 0. Если длина
     * переданного массива больше размера ассоциативного массива, то все элементы переданного массива,
     * начиная с индекса равного размеру ассоциативного массива, сохранят теже значения, что и до вызова этого метода.
     * @param array заполняемый массив значениями из ассоциативного массива.
     * @throws IllegalArgumentException если длина переданного массива меньше размера ассоциативного массива.
     */
    public void fillArrayWithValues(T[] array) throws IllegalArgumentException {
        if(array.length < size) {
            throw new IllegalArgumentException("Размер передаваемого массива не может быть меньше значения возвращаемого " +
                    "getSize(). Значение getSize() = " + size + ", array.length = " + array.length);
        }

        int indexArray = 0;
        int indexTable = 0;
        while(indexArray < size) {
            Node<T> currentNode = table[indexTable++];
            while(currentNode != null) {
                array[indexArray++] = currentNode.value;
                currentNode = currentNode.next;
            }
        }
    }

    /**
     * Возвращает итератор для одностороннего перебора всех пар ключ-значение хранящихся в данном
     * ассоциативном массиве. Порядок перебора может отличаться для каждого итераторв полученного через
     * данный метод.
     * @return итератор для одностороннего перебора всех пар ключ-значение хранящихся в данном ассоциативном массиве.
     */
    @Override
    public Iterator<Node<T>> iterator() {
        return new Iterator<>() {

            private int currentIndex;
            private Node<T> currentNode;
            private final int EXPECTED_MOD_COUNT;

            {
                EXPECTED_MOD_COUNT = actualModCount;

                while(currentNode == null && currentIndex < table.length) {
                    currentNode = table[currentIndex++];
                }
            }

            @Override
            public boolean hasNext() {
                return currentNode != null;
            }

            @Override
            public Node<T> next() {
                if(EXPECTED_MOD_COUNT != actualModCount) {
                    throw new ConcurrentModificationException(
                            "Нельзя вызвать метод next() после модификации объекта IntMap, с которым связан данный итератор."
                    );
                } else if(currentNode == null) {
                    throw new NoSuchElementException();
                } else {
                    Node<T> result = currentNode;
                    currentNode = currentNode.next;
                    while(currentNode == null && currentIndex < table.length) {
                        currentNode = table[currentIndex++];
                    }
                    return result;
                }
            }
        };
    }

    /**
     * Выполняет переданную операцию реализованную объектом типа Consumer для каждой пары ключ-значение
     * хранящейся в ассоциативном массиве. Порядок перебора пар ключ-значение может отличаться для каждого
     * вызова данного метода.
     * @param action действие выполняемое для каждой пары ключ-значение.
     */
    @Override
    public void forEach(Consumer<? super Node<T>> action) {
        Node<T> currentNode = null;
        final int EXPECTED_COUNT_MOD = actualModCount;

        for(int i = 0; i < table.length; i++) {
            currentNode = table[i];
            while(currentNode != null) {
                action.accept(currentNode);
                if(EXPECTED_COUNT_MOD != actualModCount) {
                    throw new ConcurrentModificationException(
                            "Нельзя модифицировать объект IntMap во время работы метода forEach()."
                    );
                }
                currentNode = currentNode.next;
            }
        }
    }

    private Node<T> getNode(int key) {
        Node<T> currentNode = table[keyToBucketIndex(key, table.length)];
        while(currentNode != null && currentNode.KEY != key) currentNode = currentNode.next;
        return currentNode;
    }

    private void resize() {
        Node<T>[] newTable = new Node[table.length << 1];

        for(int i = 0; i < table.length; ++i) {
            Node<T> currentNode = null;
            while((currentNode = table[i]) != null) {
                int bucketIndex = keyToBucketIndex(currentNode.KEY, newTable.length);
                table[i] = currentNode.next;
                currentNode.next = newTable[bucketIndex];
                newTable[bucketIndex] = currentNode;
            }
        }

        table = newTable;

        ++actualModCount;
    }

    private int keyToBucketIndex(int key, int lengthTable) {
        return (lengthTable - 1) & hash(key);
    }

    private int hash(int key) {
        return key ^ key >>> 16;
    }


    /**
     * Данный клас пердназначен для хранения пары ключ-значение хранящихся в ассоцитаивном массиве.
     * @param <T> тип значения.
     */
    public static final class Node<T> {
        final int KEY;
        T value;
        Node<T> next;

        Node(int key, T value, Node<T> next) {
            KEY = key;
            this.value = value;
            this.next = next;
        }

        /**
         * Возвращает ключ из пары ключ-значение хранящейся в данном объекте Node.
         * @return ключ из пары ключ-значение хранящейся в данном объекте Node.
         */
        public int getKey() {
            return KEY;
        }

        /**
         * Возвращает значение из пары ключ-значение хранящейся в данном объекте Node.
         * @return значение из пары ключ-значение хранящейся в данном объекте Node.
         */
        public T getValue() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node<?> node = (Node<?>) o;
            return KEY == node.KEY && Objects.equals(value, node.value);
        }

        @Override
        public int hashCode() {
            return KEY ^ Objects.hashCode(value);
        }

        @Override
        public String toString() {
            return KEY + "=" + value;
        }

    }

}
