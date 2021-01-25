package com.bakuard.ecsEngine.core.utils;

import java.util.Arrays;

/**
 * Класс Bits представляет собой КОНЕЧНОЕ упорядоченное множество логических значений. Позволяет выполнять
 * операции над множеством битов - and, or, not, xor, а также комбинировать эти операции. Также позволяет проверять
 * отношение между множествами такие как включение, строгое включение, пересечение, эквивалентность и
 * линейный порядок. Используется как аналог boolean массивов расходующий меньше памяти (на одно значение - один
 * бит). В отличие от массива может менять свой размер путем явного вызова методов expandTo() и compressTo().
 */
public final class Bits implements Comparable<Bits> {

    private long[] words = {0L};
    private int size;

    /**
     * Создвет пустой объект Bits.
     */
    public Bits() {}

    /**
     * Создает объект Bits зарезервированный для хранения указанного кол-ва бит. Значение любого бита в заданном
     * диапозоне, после вызова этого конструктора, будет равняться 0.
     * @param numberBits емкость создаваемого объекта Bits.
     * @throws NegativeArraySizeException если numberBits меньше нуля.
     */
    public Bits(int numberBits) {
        if(numberBits < 0)
            throw new NegativeArraySizeException("Параметр numberBits должен быть больше или равен 0. numberBits = " + numberBits);
        expandTo(numberBits);
    }

    /**
     * Создает точную копию переданного объекта Bits.
     * @param other объект bits, для которого создается копия.
     */
    public Bits(Bits other) {
        size = other.size;
        words = new long[other.words.length];
        System.arraycopy(other.words, 0, words, 0, other.words.length);
    }

    /**
     * Возвращает значение бита с указанным индексом. Возвращает true - если бит установлен в 1, false - в противном
     * случае.
     * @param index индекс считываемого бита.
     * @return true - если бит установлен в 1, false - в противном случае.
     * @throws IndexOutOfBoundsException если не выполняется условие index >= 0 && index < {@link #getSize()}
     */
    public boolean get(int index) throws IndexOutOfBoundsException {
        checkIndex(index);
        return (words[index >>> 6] & (1L << index)) != 0L;
    }

    /**
     * Устанавливает бит с указанным индексом в единицу и возвращает ссылку на тот же объект, у которого был вызван.
     * @param index индекс бита устанавливаемого в единицу.
     * @throws IndexOutOfBoundsException если не выполняется условие index >= 0 && index < {@link #getSize()}
     * @return объект, у которого был вызван данный метод.
     */
    public Bits set(int index) throws IndexOutOfBoundsException {
        checkIndex(index);
        words[index >>> 6] |= 1L << index;
        return this;
    }

    /**
     * Устаналивает значение для каждого бита, индекс которого указан в параметре indexes, в единицу и возвращает
     * ссылку на тот же объект, у которого был вызван. Если хотя бы один из индексов не соответсвует условию
     * index >= 0 && index < {@link #getSize()}, выполнения метода будет прервано и ни один из указанных бит не
     * будет изменен. Если метод вызывается без аргументов - он не вносит никаких изменений.
     * @param indexes индексы бит устанавливаемых в единицу.
     * @throws IndexOutOfBoundsException если для одного из указанных индексов не выполняется
     *                                   условие index >= 0 && index < {@link #getSize()}.
     * @return объект, у которого был вызван данный метод.
     */
    public Bits setAll(int... indexes) throws IndexOutOfBoundsException {
        for(int i = 0; i < indexes.length; i++) checkIndex(indexes[i]);
        for(int i = 0; i < indexes.length; i++) {
            int index = indexes[i];
            words[index >>> 6] |= 1L << index;
        }
        return this;
    }

    /**
     * Устанавливает бит с указанным индексом в ноль и возвращает ссылку на тот же объект, у которого был вызван.
     * @param index индекс бита устанавливаемого в ноль.
     * @throws IndexOutOfBoundsException если не выполняется условие index >= 0 && index < {@link #getSize()}.
     * @return объект, у которого был вызван данный метод.
     */
    public Bits clear(int index) throws IndexOutOfBoundsException {
        checkIndex(index);
        words[index >>> 6] &= ~(1L << index);
        return this;
    }

    /**
     * Устаналивает значение для каждого бита, индекс которого указан в параметре indexes, в ноль и возвращает
     * ссылку на тот же объект, у которого был вызван. Если хотя бы один из индексов не соответсвует условию
     * index >= 0 && index < {@link #getSize()}, выполнения метода будет прервано и ни один из указанных бит не
     * будет изменен. Если метод вызывается без аргументов - он не вносит никаких изменений.
     * @param indexes индексы бит устанавливаемых в ноль.
     * @throws IndexOutOfBoundsException если для одного из пере аднных индексов не выполняется
     *                                   условие index >= 0 && index < {@link #getSize()}.
     * @return объект, у которого был вызван данный метод.
     */
    public Bits clearAll(int... indexes) throws IndexOutOfBoundsException {
        for(int i = 0; i < indexes.length; i++) checkIndex(indexes[i]);
        for(int i = 0; i < indexes.length; i++) {
            int index = indexes[i];
            words[index >>> 6] &= ~(1L << index);
        }
        return this;
    }

    /**
     * Устанавливает значение всех бит в ноль, при этом размер объекта Bits не изменяется. Метод возвращает ссылку
     * на тот же объект, у которого был вызван.
     * @return объект, у которого был вызван данный метод.
     */
    public Bits clearAll() {
        Arrays.fill(words, 0L);
        return this;
    }

    /**
     * Устанавливает для всех индексов в заданом диапозоне значение flag начиная с индекса fromIndex и заканчивая
     * toIndex не включая его. Иначе говоря, если значение flag равняется true, то метод устанавливаются все биты
     * с индексами в диапозоне задаваемом полуинтервалом [fromIndex; toIndex) в единицы, иначе - в нули.
     * В случае, если fromIndex == toIndex, метод не делает никаких изменений. Метод возвращает ссылку на тот же
     * объект, у которого был вызван.
     * @param fromIndex индекс задающий начало заполняемого диапозона.
     * @param toIndex индекс задающий конец заполняемого диапозона.
     * @param flag флаг, определяющий будут ли биты в указанном диапозоне установлены в единицы или нули.
     * @throws IndexOutOfBoundsException генерируется в одном из следующих случаев: <br/>
     *                                   1. Если fromIndex > toIndex; <br/>
     *                                   2. Если fromIndex < 0; <br/>
     *                                   3. Если toIndex > {@link #getSize()}.
     * @return объект, у которого был вызван данный метод.
     */
    public Bits fill(int fromIndex, int toIndex, boolean flag) throws IndexOutOfBoundsException {
        if(fromIndex > toIndex || fromIndex < 0 || toIndex > size) {
            throw new IndexOutOfBoundsException("Не верно задан полуинтервал: [fromIndex=" + fromIndex +
                    "; toIndex=" + toIndex + ")");
        }

        if(flag) {
            while (fromIndex < toIndex) {
                words[fromIndex >>> 6] |= 1L << fromIndex;
                ++fromIndex;
            }
        } else {
            while (fromIndex < toIndex) {
                words[fromIndex >>> 6] &= ~(1L << fromIndex);
                ++fromIndex;
            }
        }
        return this;
    }

    /**
     * Возвращает кол-во бит, установленных в единицу.
     * @return кол-во бит установленных в единицу.
     */
    public int cardinality() {
        int countBits = 0;
        for(int i = 0; i < words.length; ++i) countBits += Long.bitCount(words[i]);
        return countBits;
    }

    /**
     * Возвращает кол-во бит установленных в единицу. Подсчет ведется от бита с нулевым индексом
     * и до бита с указанным индексом toIndex, исключая его.
     * @param toIndex индекс бита до которого ведется подсчет единичных битов, исключая его.
     * @return кол-во бит установленных в единицу.
     * @throws IndexOutOfBoundsException если не выполняется условие toIndex >= 0 && toIndex <= size.
     */
    public int cardinality(int toIndex) {
        if(toIndex >= 0 && toIndex <= size) {
            int countBits = 0;
            int numberWords = toIndex >>> 6; //кол-во слов до слова содержащего бит с индексом toIndex.
            for (int i = 0; i < numberWords; ++i) countBits += Long.bitCount(words[i]);

            long word = words[numberWords] & ~(1L << toIndex);
            countBits += Long.bitCount(word & -1L >>> (64 - toIndex));

            return countBits;
        }
        throw new IndexOutOfBoundsException(
                "Параметр toIndex должен удовлетворять условию: toIndex >= 0 && toIndex <= size."
        );
    }

    /**
     * Возвращает индекс самого старшего бита установленного в единицу. Если все биты данного объекта Bits
     * имеют значение 0, то метод вернет -1.
     * @return индекс самого старшего бита установленного в единицу или -1.
     */
    public int getHighBitIndex() {
        int index = -1;
        for(int i = words.length - 1; i >= 0 && index == -1; --i) {
            if(words[i] != 0) {
                index = (i << 6) + (63 - Long.numberOfLeadingZeros(words[i]));
            }
        }
        return index;
    }

    /**
     * Возвращает true, если для всех значений начиная с индекса 0 и заканчивая индексом
     * {@link #getSize()} - 1 метод get() возвращает false.
     * @return true - если Bits не содержит ни одной единицы, false - в проивном случае.
     */
    public boolean isEmpty() {
        for(int i = 0; i < words.length; i++) {
            if(words[i] != 0L) return false;
        }
        return true;
    }

    /**
     * Возвращает размер Bits в битах доступных для изменения. Обратите внимание - данный метод
     * НЕ возвращает логический размер объекта Bits. Данный метод выполняет ту же функцию, что и переменная
     * length у массивов.
     * @return кол-во бит доступных для изменения.
     */
    public int getSize() {
        return size;
    }

    /**
     * Возвращает индекс превого встретевшегося бита установленного в единицу. Поиск ведется начиная с бита,
     * индекс которого указан в качестве аргумента, включая его. Биты перебираются в порядке возрастания их
     * индексов.
     * @param fromIndex индекс бита с которого начинается поиск.
     * @return индекс превого встретевшегося бита установленного в единицу.
     */
    public int nextSetBit(int fromIndex) {
        if(fromIndex >= 0 && fromIndex < size) {
            int indexWord = fromIndex >>> 6;
            long bitmap = words[indexWord] >>> fromIndex;
            if(bitmap != 0) return fromIndex + Long.numberOfTrailingZeros(bitmap);

            for (int i = indexWord + 1; i < words.length; ++i) {
                bitmap = words[i];
                if(bitmap != 0) return i * 64 + Long.numberOfTrailingZeros(bitmap);
            }
        }
        return -1;
    }

    /**
     * Возвращает индекс превого встретевшегося бита установленного в ноль. Поиск ведется начиная с бита,
     * индекс которого указан в качестве аргумента, включая его. Биты перебираются в порядке возрастания их
     * индексов.
     * @param fromIndex индекс бита с которого начинается поиск.
     * @return индекс превого встретевшегося бита установленного в ноль.
     */
    public int nextClearBit(int fromIndex) {
        if(fromIndex >= 0 && fromIndex < size) {
            int indexWord = fromIndex >>> 6;
            long bitmap = ~(words[indexWord] >> fromIndex);
            if (bitmap != 0) return fromIndex + Long.numberOfTrailingZeros(bitmap);

            for (int i = indexWord + 1; i < words.length; ++i) {
                bitmap = ~words[i];
                if (bitmap != 0) return (i << 6) + Long.numberOfTrailingZeros(bitmap);
            }
        }
        return -1;
    }

    /**
     * Увеличевает емкость текущего объекта Bits до указанного кол-ва бит и возвращает объект, у которого был вызван
     * данный метод. Если передаваемый аргумент меньше или равен текущему кол-ву бит - не оказывает никакого эффекта.
     * @param numberBits кол-во бит до которого нужно расширить текущий объект Bits.
     * @return объект, у которого был вызван данный метод.
     */
    public Bits expandTo(int numberBits) {
        if(numberBits > size) {
            size = numberBits;

            int numberWords = (numberBits >>> 6) + 1;
            if(numberWords > words.length) {
                long[] newWords = new long[numberWords];
                System.arraycopy(words, 0, newWords, 0, words.length);
                words = newWords;
            }
        }
        return this;
    }

    /**
     * Уменьшает емкость текущего объекта Bits до указанного кол-ва бит и возвращает объект, у которого был вызван
     * данный метод. Вызов данного метода может привести к уменьшению объема памяти, занимаемому данным объектом.
     * Если передаваемый аргумент больше или равен текущему кол-ву бит, или меньше нуля - не оказывает никакого эффекта.
     * @param numberBits кол-во бит до которого нужно сузить текущий объект Bits.
     * @return объект, у которого был вызван данный метод.
     */
    public Bits compressTo(int numberBits) {
        if(numberBits < size && numberBits >= 0) {
            size = numberBits;

            int numberWords = (numberBits >>> 6) + 1;
            if(numberWords < words.length) {
                long[] newWords = new long[numberWords];
                System.arraycopy(words, 0, newWords, 0, numberWords);
                words = newWords;
            }

            int indexBitInWord = (numberBits - 1) % 64;
            if(numberBits > 0) words[numberBits >>> 6] &= -1L >>> (63 - indexBitInWord);
            else words[0] = 0L;
        }
        return this;
    }

    /**
     * Перезаписывает состояние текущего объекта копируя состояние переданного объекта other. Метод возвращает
     * ссылку на тот же объект, у которого он был вызван.
     * @param other объект Bits, состояние которого копируется.
     * @return объект, у которого он был вызван данный метод.
     */
    public Bits copyState(Bits other) {
        if(other.words.length != words.length) words = new long[other.words.length];
        System.arraycopy(other.words, 0, words, 0, words.length);
        size = other.size;
        return this;
    }

    /**
     * Выполняет операцию пересечения двух множеств. Метод записывает результат операции в объект Bits, у которого
     * был вызван данный метод и возвращает ссылку на этот же объект. В качестве первого операнда выступает объект
     * у которого вызывается данный метод, а второго операнда - объект передаваемый в качестве аргумента.
     * @param other второй операнд операции пересечения множеств.
     * @return ссылка на объект Bits, у которого был вызван данный метод.
     */
    public Bits and(Bits other) {
        int commonWords = Math.min(words.length, other.words.length);
        for(int i = words.length - 1; i >= commonWords; --i) words[i] = 0L;
        for(int i = 0; i < commonWords; ++i) words[i] &= other.words[i];
        return this;
    }

    /**
     * Выполняет операцию объединения двух множеств. Метод записывает результат операции в объект Bits, у которого
     * был вызван данный метод и возвращает ссылку на этот же объект. В качестве первого операнда выступает объект
     * у которого вызывается данный метод, а второго операнда - объект передаваемый в качестве аргумента. Если размер
     * объекта (см. {@link #getSize()}), у которого вызван метод, меньше чем other, то его размер увеличевается до
     * размера other.
     * @param other второй операнд операции объединения множеств.
     * @return ссылка на объект Bits, у которого был вызван данный метод.
     */
    public Bits or(Bits other) {
        expandTo(other.size);
        int commonWords = Math.min(words.length, other.words.length);
        for(int i = 0; i < commonWords; ++i) words[i] |= other.words[i];
        return this;
    }

    /**
     * Выполняет операцию симметричной разности двух множеств. Метод записывает результат операции в объект Bits,
     * у которого был вызван данный метод и возвращает ссылку на этот же объект. В качестве первого операнда
     * выступает объект у которого вызывается данный метод, а второго операнда - объект передаваемый в качестве
     * аргумента.
     * @param other второй операнд для операции xor.
     * @return ссылка на объект Bits, у которого был вызван данный метод.
     */
    public Bits xor(Bits other) {
        expandTo(other.size);
        int commonWords = Math.min(words.length, other.words.length);
        for(int i = 0; i < commonWords; ++i) words[i] ^= other.words[i];
        return this;
    }

    /**
     * Выполняет операцию дополенния множества. Метод записывает результат операции в объект Bits, у которого
     * был вызван данный метод и возвращает ссылку на этот же объект.
     * @return ссылка на объект Bits, у которого был вызван данный метод.
     */
    public Bits not() {
        for(int i = 0; i < words.length; ++i) words[i] = ~words[i];
        words[words.length - 1] &= -1L >>> (64 - size);
        return this;
    }

    /**
     * Проверяет - является ли множество бит представляемых объектом other не строгим подмножеством множества
     * бит представляемых текущим объектом Bits. Если это так, возвращает true, иначе - false.
     * @param other объект Bits для которого проверяется - является ли он не строгим подмножеством текущего объекта.
     * @return true, если other является не строгим подмножеством текущего объекта, иначе возвращает false.
     */
    public boolean contains(Bits other) {
        int commonWords = Math.min(words.length, other.words.length);
        for(int i = 0; i < commonWords; i++) {
            if((words[i] & other.words[i]) != other.words[i]) return false;
        }

        for(int i = words.length; i < other.words.length; i++) {
            if(other.words[i] != 0L) return false;
        }

        return true;
    }

    /**
     * Проверяет - является ли множество бит представленных объектом other строгим подмножеством множества
     * бит представляемых текущим объектом Bits. Если это так, возвращает true, иначе - false.
     * @param other объект Bits для которого проверяется - является ли он строгим подмножеством текущего объекта.
     * @return true, если other является строгим подмножеством текущего объекта, иначе возвращает false.
     */
    public boolean strictlyContains(Bits other) {
        for(int i = words.length; i < other.words.length; i++) {
            if(other.words[i] != 0L) return false;
        }

        int commonWords = Math.min(words.length, other.words.length);
        boolean hasMoreItems = false;
        for(int i = 0; i < commonWords; i++) {
            if(words[i] != other.words[i]) {
                hasMoreItems = true;
                if((words[i] & other.words[i]) != other.words[i]) return false;
            }
        }

        for(int i = commonWords; i < words.length && !hasMoreItems; i++) {
            hasMoreItems = words[i] != 0L;
        }

        return hasMoreItems;
    }

    /**
     * Проверяют - пересекаются ли два множества бит представленых объектами other и текущим объектом Bits.
     * Если да - возвращает true, иначе возвращает false.
     * @param other объект битс представляющий множество бит, с которым проверяется наличие пересечения.
     * @return true, если оба объекта bits имеют биты установленные в единицу на совпадающих позициях.
     */
    public boolean intersect(Bits other) {
        int commonWords = Math.min(words.length, other.words.length);
        for(int i = 0; i < commonWords; i++) {
            if((words[i] & other.words[i]) != 0L) return true;
        }
        return false;
    }

    /**
     * Два объекта Bits считаются одинаковыми если их размеры (значения возвращаемые методом {@link #getSize()})
     * равны и значения всех бит попарно равны.
     * @param o объект типа Bits с которым производится сравнение.
     * @return true - если объекты равны, false - в противном случае.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bits bits = (Bits) o;

        boolean isEqual = size == bits.size;
        for(int i = 0; i < words.length && isEqual; i++) {
            isEqual = words[i] == bits.words[i];
        }
        return isEqual;
    }

    /**
     * Данный метод сравнивает два объекта Bits без учета из размеров (значения возвращаемые методом {@link #getSize()}).
     * С точки зрения данного метода, два объекта Bits равны, если значения всех бит обоих объектов попарно равны,
     * а все биты для которых нет пары установлены в 0.
     * @param o объект типа Bits с которым производится сравнение.
     * @return true - если логические значения объектов Bits равны, false - в противном случае.
     */
    public boolean equalsIgnoreSize(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bits bits = (Bits) o;

        boolean isEqual = true;
        int commonWords = Math.min(words.length, bits.words.length);
        for(int i = 0; i < commonWords && isEqual; i++) {
            isEqual = words[i] == bits.words[i];
        }

        long[] biggest = words.length > bits.words.length ? words : bits.words;
        for(int i = commonWords; i < biggest.length && isEqual; i++) {
            isEqual = biggest[i] == 0;
        }

        return isEqual;
    }

    /**
     * Выполняет упорядочивающее сравнение двух объектов Bits. Сперва сравниваются размеры обоих объектов Bits,
     * и если их размеры равны, тогда два объекта сравниваются как два беззнаковых целых числа.
     * @param o объект Bits с которым производится сравнение.
     * @return отрицательное число, ноль или положительное число, если объект у которого вызывается
     *         данный метод меньше, равен или больше указанного соответственно.
     */
    @Override
    public int compareTo(Bits o) {
        int result = size - o.size;
        for(int i = words.length - 1; i >= 0 && result == 0; --i) {
            result = Long.compareUnsigned(words[i], o.words[i]);
        }
        return result;
    }

    /**
     * Выполняет упорядочивающее сравнение двух объектов Bits. Два объекта Bits сравниваются как целые
     * беззнаковые числа, без учета их размеров.
     * @param o объект Bits с которым производится сравнение.
     * @return отрицательное число, ноль или положительное число, если объект у которого вызывается
     *         данный метод меньше, равен или больше указанного соответственно.
     */
    public int compareIgnoreSize(Bits o) {
        int result = 0;

        for(int i = words.length - 1; i >= o.words.length && result == 0; --i) {
            result = Long.compareUnsigned(words[i], 0L);
        }

        for(int i = o.words.length - 1; i >= words.length && result == 0; --i) {
            result = Long.compareUnsigned(0L, o.words[i]);
        }

        int commonWords = Math.min(words.length, o.words.length);
        for(int i = commonWords - 1; i >= 0 && result == 0; --i) {
            result = Long.compareUnsigned(words[i], o.words[i]);
        }

        return result;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = result * 31 + Arrays.hashCode(words);
        result = result * 31 + size;
        return result;
    }

    @Override
    public String toString() {
        StringBuilder array = new StringBuilder();
        for(int i = words.length-1; i >= 0; --i) array.append(Long.toBinaryString(words[i])).append(',');
        return "Bits {size=" + size + ", count words=" + words.length + ", words=" + array.toString() + '}';
    }


    private void checkIndex(int index) {
        if(index < 0 || index >= size)
            throw new IndexOutOfBoundsException(
                    "Указанное значение index=" + index + ", не соответсвет условию: index >= 0 && index < size, " +
                            "где size=" + size);
    }

}
