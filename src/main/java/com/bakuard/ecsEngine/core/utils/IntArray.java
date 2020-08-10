package com.bakuard.ecsEngine.core.utils;

import java.util.*;

/**
 * Реализация динамического массива целых чисел с несколькими дополнительными методами.
 */
public final class IntArray {

    private int[] values;
    private int length;
    private int actualModCount;
    private final int MIN_CAPACITY = 10;

    /**
     * Создает динамический массив ля целых чисел указанной длины.
     * @param length длина массива.
     * @throws IllegalArgumentException если указанная длина меньше нуля.
     */
    public IntArray(int length) {
        if(length < 0)
            throw new IllegalArgumentException("Длина массива не может быть отрицательной.");

        this.length = length;
        values = new int[Math.max(calculateCapacity(length), MIN_CAPACITY)];
    }

    /**
     * Возвращает объект хранящийся в ячейке с указаным индексом.
     * @param index индекс ячейки массива.
     * @return объект хранящийся в ячейке с указаным индексом.
     * @throws IndexOutOfBoundsException если не соблюдается условие index >= 0 && index < length
     */
    public int get(int index) {
        halfOpenIntervalCheck(index);
        return values[index];
    }

    /**
     * Записывает элемент в ячейку с указанным индексом. Если под указанным индексом хранится другой элемент,
     * он будет перезаписан. При вызове данного метода длина массива не изменяется.
     * @param index индекс ячейки массива куда будет записан элемент.
     * @param value добавляемое значение.
     * @throws IndexOutOfBoundsException если не соблюдается условие index >= 0 && index < length
     */
    public int set(int index, int value) {
        halfOpenIntervalCheck(index);

        ++actualModCount;

        int oldValue = values[index];
        values[index] = value;
        return oldValue;
    }

    /**
     * Записывает элемент в ячейку с указанным индексом. Если под указанным индексом хранится другой элемент,
     * он будет перезаписан. Если указанный индекс меньше длины массива - то вызов метода не изменяет размер
     * массива. Если указанный индекс больше или равен длине массива - то длина массива станет равна index + 1.
     * @param index индекс ячейки массива куда будет записан элемент.
     * @param value добавляемое значение.
     * @throws ArrayIndexOutOfBoundsException если значение индекса меньше нуля.
     */
    public int setAndExpand(int index, int value) {
        ++actualModCount;

        expandTo(index + 1);
        int oldValue = values[index];
        values[index] = value;
        return oldValue;
    }

    /**
     * Увеличевает длину массива на единицу и затем записывает элемент в конец массива.
     * @param value добавляемое значение.
     */
    public void add(int value) {
        ++actualModCount;

        int index = length;
        expandTo(length + 1);
        values[index] = value;
    }

    /**
     * Добавляет все переданные элементы в конец массива увеличевая его длину на кол-во переданных элементов.
     * Порядок в котором элементы передаются методу сохраняется.
     * @param values добавляемые элементы.
     */
    public void addAll(int... values) {
        if(values.length > 0) {
            ++actualModCount;

            int lastIndex = length;
            expandTo(length + values.length);
            System.arraycopy(values, 0, this.values, lastIndex, values.length);
        }
    }

    /**
     * Добавляет все переданные элементы в конец массива увеличевая его длину на кол-во переданных элементов.
     * Порядок в котором элементы передаются методу сохраняется.
     * @param values добавляемые элементы.
     */
    public void addAll(IntArray values) {
        if(values.getLength() > 0) {
            ++actualModCount;

            int lastIndex = length;
            expandTo(length + values.getLength());
            System.arraycopy(values.values, 0, this.values, lastIndex, values.getLength());
        }
    }

    /**
     * Вставляет указанный элемент в указанную позицию. При этом - элемент, который ранее находился на данной
     * позиции и все элементы следующие за ним сдвигаются вверх на одну позицию.
     * @param index позиция, в которую будет добавлен элемент
     * @param value добавляемое значение
     * @throws IndexOutOfBoundsException если не соблюдается условие index >= 0 && index <= length
     */
    public void insert(int index, int value) {
        closedIntervalCheck(index);

        ++actualModCount;

        int oldLength = length;
        expandTo(length + 1);
        if(index < oldLength) {
            System.arraycopy(values, index, values, index + 1, oldLength - index);
        }
        values[index] = value;
    }

    /**
     * Добавляет указанный элемент в массив сохраняя заданный порядок элементов и возвращает индекс
     * вставки добавляемого элемента. Если массив содержит есколько элементов с тем же значением, что и добавляемый
     * элемент - метод не дает гарантий, куда будет вставлен элемент оносительно элементов с тем же значением.
     * Выполняет вставку элемента с использованием двоичного поиска.
     * Данный метод требует, чтобы массив был предварительно отсортирован и для сравнения использовался
     * Comparator задающий тот же линейный порядок, что и порядок отсортированного массива. Если это условие
     * не соблюдается - результ не определен.
     * @param value добавляемое значение.
     * @param comparator объект выполняющий упорядочивающее сравнение элементов массива.
     */
    public int binaryInsert(int value, IntComparator comparator) {
        ++actualModCount;

        int fromIndex = 0;
        int toIndex = length;
        int middle = 0;
        while (fromIndex < toIndex) {
            middle = (fromIndex + toIndex) >>> 1;
            int different = comparator.compare(value, values[middle]);

            if (different == 0) break;
            else if (different > 0) fromIndex = middle + 1;
            else toIndex = middle;
        }

        if(fromIndex == toIndex) {
            insert(fromIndex, value);
            return fromIndex;
        } else {
            insert(middle, value);
            return middle;
        }
    }

    /**
     * Удаляет элемент под указанным индексом и возвращает его. На место удаленного элемента будет записан
     * последний элемент массива и длина массива будет уменьшена на единицу. Данный метод не уменьшает емкость
     * внутреннего хранилища. Если вам необходимо уменьшить объекм памяти занимаемый данным объектом {@link IntArray},
     * используйте метод {@link #compressTo(int)}.
     * @param index индекс удаляемого элемента.
     * @return удаляемый элемент под указанным индексом.
     */
    public int quickRemove(int index) {
        halfOpenIntervalCheck(index);

        ++actualModCount;

        int removableItem = values[index];
        values[index] = values[--length];
        values[length] = 0;
        return removableItem;
    }

    /**
     * Удаляет элемент под указанным индексом и возвращает его. Все элементы, индекс которых больше указанного,
     * сдвигаются вниз на одну позицию. Иначе  говоря, данный метод выполняет удаление элемента с сохранением
     * порядка для оставшихся элементов. Длина массива будет уменьшена на единицу. Данный метод не уменьшает емкость
     * внутреннего хранилища. Если вам необходимо уменьшить объекм памяти занимаемый данным объектом {@link IntArray},
     * используйте метод {@link #compressTo(int)}.
     * @param index индекс удаляемого элемента.
     * @return удаляемый элемент под указанным индексом.
     * @throws IndexOutOfBoundsException если не соблюдается условие index >= 0 && index < length
     */
    public int orderedRemove(int index) {
        halfOpenIntervalCheck(index);

        ++actualModCount;

        int removableItem = values[index];
        if(--length > index) {
            System.arraycopy(values, index + 1, values, index, length - index);
        }
        values[length] = 0;
        return removableItem;
    }

    /**
     * Удаляет все элементы массива и уменьшает его длину до нуля. Данный метод не уменьшает емкость
     * внутреннего хранилища. Если вам необходимо уменьшить объекм памяти занимаемый данным объектом {@link IntArray},
     * используйте метод {@link #compressTo(int)}.
     */
    public void clear() {
        ++actualModCount;
        for(int to = length, i = length = 0; i < to; ++i) values[i] = 0;
    }

    /**
     * Сортирует массив в соответствии с заданным порядком.
     * @param comparator объект выполняющий упорядочивающее сравнение элементов массива.
     */
    public void sort(IntComparator comparator) {
        ++actualModCount;

        quickSort(values, 0, length - 1, comparator);
    }

    /**
     * Возвращает длину массива. Возвращаемое значение меньше фактической длины внутреннего массива.
     * Это сделано, чтобы избежать слишком частого создания новых внутренних массивов и перезаписи в них значений из
     * старых.
     * @return длина массива.
     */
    public int getLength() {
        return length;
    }

    /**
     * Возвращает индекс первого встретевшегося элемента с указанным значением или -1, если массив
     * не содержит элемент с указанным значением. Выполняет линейный поиск.
     * @param value значение элеменета, для которого осуществляется поиск.
     * @return индекс первого встретевшегося элемента с указанным значением.
     */
    public int linearSearch(int value) {
        return linearSearchInRange(value, 0, length);
    }

    /**
     * Возвращает индекс первого встретевшегося элемента с указанным значением или -1, если массив
     * в указанном диапозоне не содержит элемент с заданным значением. Выполняет линейный поиск.
     * Если fromIndex == toIndex, метод возвращает -1.
     * @param value значение элеменета, для которого осуществляется поиск.
     * @param fromIndex индекс первого элемента с которого начнется поиск (включая).
     * @param toIndex индекс элемента, до которого ведется поиск (исключая).
     * @return индекс первого встретевшегося элемента с указанным значением.
     * @throws IndexOutOfBoundsException если fromIndex < 0 || toIndex >= length || fromIndex > toIndex
     */
    public int linearSearch(int value, int fromIndex, int toIndex) {
        rangeCheck(fromIndex, toIndex);
        return linearSearchInRange(value, fromIndex, toIndex);
    }

    /**
     * Возвращает индекс первого встретевшегося элемента с указанным значением или -1,
     * если массив не содержит элемент с указанным значением. Если массив содержит несколько
     * подходящих элементов - метод не дает гарантий, индекс какого именно из этих элементов
     * будет возвращен. Выполняет двоичный поиск.
     * Данный метод требует, чтобы массив был предварительно отсортирован и для сравнения использовался
     * Comparator задающий тот же линейный порядок, что и порядок отсортированного массива. Если это условие
     * не соблюдается - результ не определен.
     * @param value значение элеменета, для которого осуществляется поиск.
     * @param comparator объект выполняющий упорядочивающее сравнение элементов массива.
     * @return индекс элемента с указанным значением или -1, если таковой не был найден.
     */
    public int binarySearch(int value, IntComparator comparator) {
        return binarySearchInRange(value, 0, length, comparator);
    }

    /**
     * Увеличевает длину массива. Фактическая длина внутреннего массива будет больше указанной длины.
     * Это сделано, чтобы избежать слишком частого выделения памяти. Если указанная новая длина массива
     * меньше или равна текущей - метод ничего не делает.
     * @param newLength новая длина массива.
     */
    public void expandTo(int newLength) {
        if(newLength > length) {
            ++actualModCount;

            length = newLength;
            if(newLength > values.length) {
                values = Arrays.copyOf(values, calculateCapacity(newLength));
            }
        }
    }

    /**
     * Уменьшает длину массива. Фактическая длина внутреннего массива будет больше указанной длины.
     * Это сделано, чтобы избежать слишком частого создания новых внутренних массивов и перезаписи в них значений из
     * старых. Если указанная новая длина массива не соответствует условию newLength < length && newLength >= 0,
     * то метод ничего не делает.
     * @param newLength новая длина массива.
     */
    public void compressTo(int newLength) {
        if(newLength < length && newLength >= 0) {
            ++actualModCount;

            length = newLength;
            int capacity = Math.max(calculateCapacity(newLength), MIN_CAPACITY);
            if(capacity < values.length) {
                values = Arrays.copyOf(values, capacity);
            }
            Arrays.fill(values, newLength, values.length, 0);
        }
    }

    /**
     * Увеличевает указанный элемент на единицу и возвращает его новое значение. При вызове этого
     * метода для элемента со значением {@link Integer#MAX_VALUE} - произойдет переполнение.
     * @param index индекс элемента.
     * @return значение указанного элемента увеличенного на единицу.
     */
    public int increment(int index) {
        halfOpenIntervalCheck(index);
        ++actualModCount;
        return ++values[index];
    }

    /**
     * Уменьшает указанный элемент на единицу и возвращает его новое значение. При вызове этого
     * метода для элемента со значением {@link Integer#MIN_VALUE} - произойдет переполнение.
     * @param index индекс элемента.
     * @return значение указанного элемента уменьшенное на единицу.
     */
    public int decrement(int index) {
        halfOpenIntervalCheck(index);
        ++actualModCount;
        return --values[index];
    }

    /**
     * Выполняет переданную операцию реализованную объектом типа IntConsumer для каждого элемента
     * хранящегося в массиве. Порядок перебора элементов соответсвует порядку их следования в массиве.
     * @param action действие выполняемое для каждого элемента хранящегося в данном массиве.
     */
    public void forEach(IntConsumer action) {
        final int EXPECTED_COUNT_MOD = actualModCount;

        for(int i = 0; i < length; i++) {
            action.accept(values[i]);
            if(EXPECTED_COUNT_MOD != actualModCount) {
                throw new ConcurrentModificationException(
                        "Нельзя модифицировать объект IntArray во время работы метода forEach()."
                );
            }
        }
    }

    /**
     * Возвращает итератор для одностороннего перебора элементов данного массива. Порядок перебора соответсвует
     * порядку элементов в массиве.
     * @return итератор для одностороннего перебора элементов данного массива.
     */
    public IntIterator iterator() {

        return new IntIterator() {

            private final int EXPECTED_COUNT_MOD = actualModCount;
            private int currentIndex;

            @Override
            public boolean hasNext() {
                return currentIndex < length;
            }

            @Override
            public int next() {
                if(EXPECTED_COUNT_MOD != actualModCount) {
                    throw new ConcurrentModificationException(
                            "Нельзя вызвать метод next() после модификации объекта IntArray, " +
                                    "с которым связан данный итератор.");
                } else if(currentIndex >= length) {
                    throw new NoSuchElementException();
                } else {
                    return values[currentIndex++];
                }
            }

        };

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IntArray array = (IntArray) o;
        if(array.length != length) return false;
        for(int i = 0; i < length; i++) {
            if(array.values[i] != values[i]) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = length;
        for(int i = 0; i < length; i++) result = result * 31 + values[i];
        return result;
    }

    @Override
    public String toString() {
        StringBuilder valuesToString = new StringBuilder("[");
        for(int i = 0; i < length; ++i) {
            valuesToString.append(values[i]);
            if(i < length - 1) valuesToString.append(',');
        }
        valuesToString.append(']');

        return "IntArray{" +
                "length=" + length +
                ", values=" + valuesToString.toString() +
                '}';
    }

    private int calculateCapacity(int length) {
        return length + (length >>> 1);
    }

    private void rangeCheck(int fromIndex, int toIndex) {
        if(fromIndex < 0 || toIndex >= length || fromIndex > toIndex)
            throw new IndexOutOfBoundsException("fromIndex = " + fromIndex + " | toIndex = " + toIndex);
    }

    private void halfOpenIntervalCheck(int index) {
        if(index < 0 || index >= length) {
            throw new IndexOutOfBoundsException(
                    "Для параметра index=" + index + " не соблюдается условие: " +
                            "index >= 0 && index < length, где length=" + length);
        }
    }

    private void closedIntervalCheck(int index) {
        if(index < 0 || index > length) {
            throw new IndexOutOfBoundsException(
                    "Для параметра index=" + index + " не соблюдается условие: " +
                            "index >= 0 && index <= length, где length=" + length);
        }
    }

    private int linearSearchInRange(int value, int fromIndex, int toIndex) {
        int[] vs = values;
        for(int i = fromIndex; i < toIndex; ++i) if(vs[i] == value) return i;
        return -1;
    }

    private int binarySearchInRange(int value, int fromIndex, int toIndex, IntComparator comparator) {
        while(fromIndex < toIndex) {
            int middle = (fromIndex + toIndex) >>> 1;

            if(comparator.compare(value, values[middle]) == 0) return middle;
            else if(comparator.compare(value, values[middle]) > 0) fromIndex = middle + 1;
            else toIndex = middle;
        }
        return -1;
    }

    private void quickSort(int[] array, int start, int end, IntComparator comparator) {
        int left = start;
        int right = end;
        int pivot = array[(start + end) / 2];

        while (left <= right) {
            while(true) {
                if(comparator.compare(array[left], pivot) < 0) left++;
                else break;
            }

            while(true) {
                if(comparator.compare(array[right], pivot) > 0) right--;
                else break;
            }

            if (left <= right) {
                int temp = array[left];
                array[left] = array[right];
                array[right] = temp;
                left++;
                right--;
            }
        }

        if (start < right) quickSort(array, start, right, comparator);
        if (left < end) quickSort(array, left, end, comparator);
    }


    /**
     * Итератор для одностороннего перебора целых чисел хранящихся в {@link IntArray}.
     */
    public interface IntIterator {

        public boolean hasNext();

        public int next();

    }

    /**
     * Представляет операцию принимающую один элемент и не возвращающую результат. Данный интерфейс
     * является функциональным.
     */
    @FunctionalInterface
    public interface IntConsumer {

        public void accept(int value);

    }

    /**
     * Представляет функцию попарного сравнения целых чисел хранящихся в {@link IntArray}. Задает линейный порядок
     * для всех элементов {@link IntArray}. Данный интерфейс является функциональным.
     */
    @FunctionalInterface
    public interface IntComparator {

        public int compare(int a, int b);

    }

}
