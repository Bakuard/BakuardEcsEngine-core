package com.bakuard.ecsEngine.core.utils;

import java.util.*;
import java.util.function.Consumer;

/**
 * Реализация динамического массива с несколькими дополнительными методами.
 * @param <T> тип объектов хранимых в массиве.
 */
public final class Array<T> implements Iterable<T> {

    private T[] values;
    private int length;
    private int actualModCount;
    private final int MIN_CAPACITY = 10;

    /**
     * Создвет пустой массив объектов указанной длины.
     * @param type тип объектов хранимых в массиве.
     * @param length длина массива.
     * @throws IllegalArgumentException если указанная длина меньше нуля.
     */
    @SuppressWarnings("unchecked")
    public Array(Class<T> type, int length){
        if(length < 0)
            throw new IllegalArgumentException("Длина массива не может быть отрицательной.");

        this.length = length;
        values = (T[]) java.lang.reflect.Array.newInstance(type, Math.max(calculateCapacity(length), MIN_CAPACITY));
    }

    /**
     * Возвращает элемент хранящийся в ячейке с указаным индексом.
     * @param index индекс ячейки массива.
     * @return элемент хранящийся в ячейке с указаным индексом.
     * @throws ArrayIndexOutOfBoundsException если не соблюдается условие index >= 0 && index < length
     */
    public T get(int index) {
        halfOpenIntervalCheck(index);
        return values[index];
    }

    /**
     * Записывает элемент в ячейку с указанным индексом и возвращает элемент, который находился в этой
     * ячейке до вызова этого метода. При вызове данного метода длина массива не изменяется.
     * @param index индекс ячейки массива куда будет записан элемент.
     * @param value добавляемое значение.
     * @throws ArrayIndexOutOfBoundsException если не соблюдается условие index >= 0 && index < length
     * @return элемент, который находился в массиве под указанным индексом до вызова этого метода.
     */
    public T set(int index, T value) {
        halfOpenIntervalCheck(index);

        ++actualModCount;

        T oldValue = values[index];
        values[index] = value;
        return oldValue;
    }

    /**
     * Записывает элемент в ячейку с указанным индексом и возвращает элемент, который находился в этой
     * ячейке до вызова этого метода. Если указанный индекс меньше длины массива - то вызов метода не
     * изменяет размер массива. Если указанный индекс больше или равен длине массива - то длина массива
     * станет равна index + 1.
     * @param index индекс ячейки массива куда будет записан элемент.
     * @param value добавляемое значение.
     * @throws ArrayIndexOutOfBoundsException если значение индекса меньше нуля.
     * @return элемент, который находился в массиве под указанным индексом до вызова этого метода.
     */
    public T setAndExpand(int index, T value) {
        ++actualModCount;

        expandTo(index + 1);
        T oldValue = values[index];
        values[index] = value;
        return oldValue;
    }

    /**
     * Увеличевает длину массива на единицу и затем записывает элемент в конец массива.
     * @param value добавляемое значение.
     */
    public void add(T value) {
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
    public void addAll(T... values) {
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
    public void addAll(Array<T> values) {
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
     * @throws ArrayIndexOutOfBoundsException если не соблюдается условие index >= 0 && index <= length
     */
    public void insert(int index, T value) {
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
     * вставки добавляемого элемента. Если массив содержит несколько элементов с тем же значением, что и добавляемый
     * элемент - метод не дает гарантий, куда будет вставлен элемент оносительно элементов с тем же значением.
     * Выполняет вставку элемента с использованием двоичного поиска.
     * Данный метод требует, чтобы массив был предварительно отсортирован и для сравнения использовался
     * Comparator задающий тот же линейный порядок, что и порядок отсортированного массива. Если это условие
     * не соблюдается - результ не определен.
     * @param value добавляемое значение.
     * @param comparator объект выполняющий упорядочивающее сравнение элементов массива.
     */
    public int binaryInsert(T value, Comparator<T> comparator) {
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
     * внутреннего хранилища. Если вам необходимо уменьшить объекм памяти занимаемый данным объектом {@link Array},
     * используйте метод {@link #compressTo(int)}.
     * @param index индекс удаляемого элемента.
     * @return удаляемый элемент под указанным индексом.
     * @throws ArrayIndexOutOfBoundsException если не соблюдается условие index >= 0 && index < length
     */
    public T quickRemove(int index) {
        halfOpenIntervalCheck(index);

        ++actualModCount;

        T removableItem = values[index];
        values[index] = values[--length];
        values[length] = null;
        return removableItem;
    }

    /**
     * Удаляет элемент под указанным индексом и возвращает его. Все элементы, индекс которых больше указанного,
     * сдвигаются вниз на одну позицию. Иначе  говоря, данный метод выполняет удаление элемента с сохранением
     * порядка для оставшихся элементов. Длина массива будет уменьшена на единицу. Данный метод не уменьшает емкость
     * внутреннего хранилища. Если вам необходимо уменьшить объекм памяти занимаемый данным объектом {@link Array},
     * используйте метод {@link #compressTo(int)}.
     * @param index индекс удаляемого элемента.
     * @return удаляемый элемент под указанным индексом.
     * @throws ArrayIndexOutOfBoundsException если не соблюдается условие index >= 0 && index < length
     */
    public T orderedRemove(int index) {
        halfOpenIntervalCheck(index);

        ++actualModCount;

        T removableItem = values[index];
        if(--length > index) {
            System.arraycopy(values, index + 1, values, index, length - index);
        }
        values[length] = null;
        return removableItem;
    }

    /**
     * Удаляет все элементы массива и уменьшает его длину до нуля. Данный метод не уменьшает емкость
     * внутреннего хранилища. Если вам необходимо уменьшить объекм памяти занимаемый данным объектом {@link Array},
     * используйте метод {@link #compressTo(int)}.
     */
    public void clear() {
        ++actualModCount;
        for(int to = length, i = length = 0; i < to; ++i) values[i] = null;
    }

    /**
     * Сортирует массив в соответствии с заданным порядком.
     * @param comparator объект выполняющий упорядочивающее сравнение элементов массива.
     */
    public void sort(Comparator<T> comparator) {
        ++actualModCount;

        Arrays.sort(values, 0, length, comparator);
    }

    /**
     * Возвращает длину массива. Возвращаемое значение меньше фактической длины внутреннего массива.
     * Это сделано, чтобы избежать слишком частого создания новых внутренних массивов и перезаписи
     * в них значений из старых.
     * @return длина массива.
     */
    public int getLength() {
        return length;
    }

    /**
     * Возвращает индекс первого(с начала массива) встретевшегося элемента с указанным значением или -1,
     * если массив не содержит элемент с указанным значением. Выполняет линейный поиск.
     * @param value значение элеменета, для которого осуществляется поиск.
     * @return индекс первого встретевшегося элемента с указанным значением.
     */
    public int linearSearch(T value) {
        return linearSearchInRange(value, 0, length);
    }

    /**
     * Возвращает индекс первого(с начала интервала) встретевшегося элемента с указанным значением или -1,
     * если массив в указанном диапозоне не содержит элемент с заданным значением. Выполняет линейный поиск
     * начиная с элемента под индексом fromIndex(включая) и до элемента с индексом toIndex(исключая).
     * Если fromIndex == toIndex, метод возвращает -1.
     * @param value значение элеменета, для которого осуществляется поиск.
     * @param fromIndex индекс первого элемента с которого начнется поиск (включая).
     * @param toIndex индекс элемента, до которого ведется поиск (исключая).
     * @return индекс первого(с начала интервала) встретевшегося элемента с указанным значением.
     * @throws ArrayIndexOutOfBoundsException если fromIndex < 0 || toIndex > length || fromIndex > toIndex
     */
    public int linearSearch(T value, int fromIndex, int toIndex) {
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
     * @param value значение элемента, для которого осуществляется поиск.
     * @param comparator объект выполняющий упорядочивающее сравнение элементов массива.
     * @return индекс элемента с указанным значением или -1, если таковой не был найден.
     */
    public int binarySearch(T value, Comparator<T> comparator) {
        return binarySearchInRange(value, 0, length, comparator);
    }

    /**
     * Возвращает индекс первого встретевшегося элемента часть полей которого имеет нужные значение или -1,
     * если массив не содержит такого элемента. Если массив содержит несколько подходящих элементов - метод
     * не дает гарантий, индекс какого именно из этих элементов будет возвращен. Выполняет двоичный поиск.
     * Данный метод требует, чтобы массив был предварительно отсортирован и для сравнения использовался
     * Comparator задающий линейный порядок по тем же полям объекта и сравнивающий их тем же образом, что
     * и данный PropertyComparator. Если это условие не соблюдается - результ не определен.
     * @param comparator объект выполняющий упорядочивающее сравнение элементов массива.
     * @return индекс элемента часть полей которого имеет нужные значение или -1, если таковой не был найден.
     */
    public int binarySearch(PropertyComparator<T> comparator) {
        return binarySearchByPropertyInRange(0, length, comparator);
    }

    /**
     * Увеличевает длину массива. Фактическая длина внутреннего массива будет больше указанной длины.
     * Это сделано, чтобы избежать слишком частого выделения памяти. Если указанная новая длина массива меньше
     * или равна текущей - метод ничего не делает.
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
            Arrays.fill(values, newLength, values.length, null);
        }
    }

    /**
     * Выполняет переданную операцию реализованную объектом типа Consumer для каждого элемента
     * хранящегося в массиве. Порядок перебора элементов соответсвует порядку их следования в массиве.
     * @param action действие выполняемое для каждого элемента хранящегося в данном массиве.
     */
    @Override
    public void forEach(Consumer<? super T> action) {
        final int EXPECTED_COUNT_MOD = actualModCount;

        for(int i = 0; i < length; i++) {
            action.accept(values[i]);
            if(EXPECTED_COUNT_MOD != actualModCount) {
                throw new ConcurrentModificationException(
                        "Нельзя модифицировать объект Array во время работы метода forEach()."
                );
            }
        }
    }

    /**
     * Возвращает итератор для одностороннего перебора элементов данного массива. Порядок перебора соответсвует
     * порядку элементов в массиве.
     * @return итератор для одностороннего перебора элементов данного массива.
     */
    @Override
    public Iterator<T> iterator() {

        return new Iterator<>() {

            private final int EXPECTED_COUNT_MOD = actualModCount;
            private int currentIndex;

            @Override
            public boolean hasNext() {
                return currentIndex < length;
            }

            @Override
            public T next() {
                if(EXPECTED_COUNT_MOD != actualModCount) {
                    throw new ConcurrentModificationException(
                            "Нельзя вызвать метод next() после модификации объекта Array, " +
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
        Array<?> array = (Array<?>) o;

        if(array.length != length) return false;
        for(int i = 0; i < length; i++) {
            if(!Objects.equals(array.values[i], values[i])) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = length;
        for(int i = 0; i < length; i++) result = result * 31 + Objects.hashCode(values[i]);
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

        return "Array{" +
                "length=" + length +
                ", values=" + valuesToString.toString() +
                '}';
    }

    private int calculateCapacity(int length) {
        return length + (length >>> 1);
    }

    private void rangeCheck(int fromIndex, int toIndex) {
        if(fromIndex < 0 || toIndex > length || fromIndex > toIndex)
            throw new ArrayIndexOutOfBoundsException("fromIndex = " + fromIndex + " | toIndex = " + toIndex);
    }

    private void halfOpenIntervalCheck(int index) {
        if(index < 0 || index >= length) {
            throw new ArrayIndexOutOfBoundsException(
                    "Для параметра index не соблюдается условие: " +
                            "index >= 0 && index < length, где length=" + length + ", index=" + index);
        }
    }

    private void closedIntervalCheck(int index) {
        if(index < 0 || index > length) {
            throw new ArrayIndexOutOfBoundsException(
                    "Для параметра index не соблюдается условие: " +
                            "index >= 0 && index <= length, где length=" + length + ", index=" + index);
        }
    }

    private int linearSearchInRange(T value, int fromIndex, int toIndex) {
        Object[] vs = values;
        if(value == null) {
            for(int i = fromIndex; i < toIndex; ++i) if(vs[i] == null) return i;
        } else {
            for(int i = fromIndex; i < toIndex; ++i) if(value.equals(vs[i])) return i;
        }
        return -1;
    }

    private int binarySearchInRange(T value, int fromIndex, int toIndex, Comparator<T> comparator) {
        while(fromIndex < toIndex) {
            int middle = (fromIndex + toIndex) >>> 1;
            int different = comparator.compare(value, values[middle]);

            if(different == 0) return middle;
            else if(different > 0) fromIndex = middle + 1;
            else toIndex = middle;
        }
        return -1;
    }

    private int binarySearchByPropertyInRange(int fromIndex, int toIndex, PropertyComparator<T> comparator) {
        while(fromIndex < toIndex) {
            int middle = (fromIndex + toIndex) >>> 1;
            int different = comparator.compare(values[middle]);

            if(different == 0) return middle;
            else if(different > 0) fromIndex = middle + 1;
            else toIndex = middle;
        }
        return -1;
    }


    /**
     * Назначение данного интерфейса сходно назначению интерфейса Comparator из стандартной бибилиотеки java.
     * Отличие - данный интерфейс используется для бинарного поиска в тех случаях, когда необходимо осущестить
     * бинарный поиск по некоторому одному или нескольким полям объекта, по кокторым для типа данного объекта
     * задан линейный порядок. При этом у вызывающего кода есть данные поля в виде самостоятельных объектов
     * или примитивов, но нет "цельного" объекта с которым можно было бы осуществить двоичный поиск.
     * @param <T> тип объекта для которого выполняется упорядочевающее сравнение.
     */
    public interface PropertyComparator<T> {

        /**
         * Выполняет упорядочевающее сравнение объекта по некоторым его полям.
         * @param value объект для которого выполняется упорядоченное сравнение.
         * @return отрицательное число, ноль или положительное число, если некоторые поля объекта для которого
         *         вызывается данный метод меньше, равны или больше свойства с которыми происходит сравнение.
         */
        public int compare(T value);

    }

}
