package com.bakuard.ecsEngine.utils;

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
     * @throw NegativeArraySizeException если numberBits меньше нуля.
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
     * Устанавливает бит с указанным индексом в единицу.
     * @param index индекс бита устанавливаемого в единицу.
     * @throws IndexOutOfBoundsException если не выполняется условие index >= 0 && index < {@link #getSize()}
     */
    public void set(int index) throws IndexOutOfBoundsException {
        checkIndex(index);
        words[index >>> 6] |= 1L << index;
    }

    /**
     * Устаналивает значение для каждого бита, индекс которого указан в параметре indexes, в единицу.
     * Если хотя бы один из индексов не соответсвует условию - index >= 0 && index < {@link #getSize()},
     * выполнения метода будет прервано и ни один из указанных бит не будет изменен. Если метод вызывается
     * без аргументов - он ничего не делает.
     * @param indexes индексы бит устанавливаемых в единицу.
     * @throws IndexOutOfBoundsException если для одного из пере аднных индексов не выполняется
     *                                   условие index >= 0 && index < {@link #getSize()}
     */
    public void setAll(int... indexes) throws IndexOutOfBoundsException {
        for(int i = 0; i < indexes.length; i++) checkIndex(indexes[i]);
        for(int i = 0; i < indexes.length; i++) {
            int index = indexes[i];
            words[index >>> 6] |= 1L << index;
        }
    }

    /**
     * Устанавливает бит с указанным индексом в ноль.
     * @param index индекс бита устанавливаемого в ноль.
     * @throws IndexOutOfBoundsException если не выполняется условие index >= 0 && index < {@link #getSize()}
     */
    public void clear(int index) throws IndexOutOfBoundsException {
        checkIndex(index);
        words[index >>> 6] &= ~(1L << index);
    }

    /**
     * Устаналивает значение для каждого бита, индекс которого указан в параметре indexes, в ноль.
     * Если хотя бы один из индексов не соответсвует условию - index >= 0 && index < {@link #getSize()},
     * выполнения метода будет прервано и ни один из указанных бит не будет изменен. Если метод вызывается
     * без аргументов - он ничего не делает.
     * @param indexes индексы бит устанавливаемых в ноль.
     * @throws IndexOutOfBoundsException если для одного из пере аднных индексов не выполняется
     *                                   условие index >= 0 && index < {@link #getSize()}
     */
    public void clearAll(int... indexes) throws IndexOutOfBoundsException {
        for(int i = 0; i < indexes.length; i++) checkIndex(indexes[i]);
        for(int i = 0; i < indexes.length; i++) {
            int index = indexes[i];
            words[index >>> 6] &= ~(1L << index);
        }
    }

    /**
     * Устанавливает значение всех бит в ноль, при этом размер объекта Bits не изменяется.
     */
    public void clearAll() {
        Arrays.fill(words, 0L);
    }

    /**
     * Устанавливает для всех индексов в заданом диапозоне значение flag начиная с индекса fromIndex и заканчивая
     * toIndex не включая его. Иначе говоря, значения flag устанавливаются для всех индексов в диапозоне задаваемом
     * полуинтервалом [fromIndex; toIndex). В случае, если fromIndex == toIndex, метод не делает никаких изменений.
     * @param fromIndex индекс задающий начало заполняемого диапозона.
     * @param toIndex индекс задающий конец заполняемого диапозона.
     * @throws IndexOutOfBoundsException генерируется в одном из следующих случаев: если fromIndex > toIndex;
     *                                   если fromIndex < 0; если toIndex > getSize().
     */
    public void fill(int fromIndex, int toIndex, boolean flag) throws IndexOutOfBoundsException {
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
     * getSize() - 1 метод get() возвращает false.
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
     * Увеличевает емкость текущего объекта Bits до указанного кол-ва бит.
     * Если передаваемый аргумент меньше или равен текущему кол-ву бит - не оказывает никакого эффекта.
     * @param numberBits кол-во бит до которого нужно расширить текущий объект Bits.
     */
    public void expandTo(int numberBits) {
        if(numberBits > size) {
            size = numberBits;

            int numberWords = (numberBits >>> 6) + 1;
            if(numberWords > words.length) {
                long[] newWords = new long[numberWords];
                System.arraycopy(words, 0, newWords, 0, words.length);
                words = newWords;
            }
        }
    }

    /**
     * Уменьшает емкость текущего объекта Bits до указанного кол-ва бит и уменьшает объем занимаемой объектом памяти.
     * Если передаваемый аргумент больше или равен текущему кол-ву бит, или меньше нуля - не оказывает никакого эффекта.
     * @param numberBits кол-во бит до которого нужно сузить текущий объект Bits.
     */
    public void compressTo(int numberBits) {
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
    }

    /**
     * Выполняет операцию пересечения двух множеств. Вызов данного метода влияет на значение бит выходного параметра,
     * но не влияет на его размер.
     * @param other второй операнд для операции and.
     * @param out объект в который будет записан результат выполнения метода.
     * @throws IndexOutOfBoundsException в случае, если длина выходного параметра меньше длины наименьшего из операндов.
     */
    public void and(Bits other, Bits out)  throws IndexOutOfBoundsException {
        if(out.size < Math.min(other.size, size)) {
            throw new IndexOutOfBoundsException(
                    "Размер выходного параметра out недостаточен для вмещения результата.n\"" +
                            "Минимально необходимый размер = " + Math.min(other.size, size) + "\n" +
                            "Текущая длина out = " + out.size
            );
        }

        int commonWords = Math.min(other.words.length, words.length);
        if(this == other) System.arraycopy(words, 0, out.words, 0, words.length);
        else for(int i = 0; i < commonWords; i++) out.words[i] = words[i] & other.words[i];
        for(int i = commonWords; i < out.words.length; i++) out.words[i] = 0L;
    }

    /**
     * Выполняет операцию объединения двух множеств. Вызов данного метода влияет на значение бит выходного параметра,
     * но не влияет на его размер.
     * @param other второй операнд для операции or.
     * @param out объект в который будет записан результат выполнения метода.
     * @throws IndexOutOfBoundsException в случае, если длина выходного параметра меньше длины наибольшего из операндов.
     */
    public void or(Bits other, Bits out) throws IndexOutOfBoundsException {
        if(out.size < Math.max(other.size, size)) {
            throw new IndexOutOfBoundsException(
                    "Размер выходного параметра out недостаточен для вмещения результата.n\"" +
                            "Минимально необходимый размер = " + Math.max(other.size, size) + "\n" +
                            "Текущая длина out = " + out.size
            );
        }

        int commonWords = Math.min(other.words.length, words.length);
        if(this == other) System.arraycopy(words, 0, out.words, 0, words.length);
        else for(int i = 0; i < commonWords; i++) out.words[i] = words[i] | other.words[i];

        long[] longest = other.words.length > words.length ? other.words : words;
        System.arraycopy(longest, commonWords, out.words, commonWords, longest.length - commonWords);

        for(int i = longest.length; i < out.words.length; ++i) out.words[i] = 0L;
    }

    /**
     * Выполняе операцию симметричной разности для двух множеств. Вызов данного метода влияет на значение
     * бит выходного параметра, но не влияет на его размер.
     * @param other второй операнд для операции xor.
     * @param out объект в который будет записан результат выполнения метода.
     * @throws IndexOutOfBoundsException в случае, если длина выходного параметра меньше длины наибольшего из операндов.
     */
    public void xor(Bits other, Bits out) throws IndexOutOfBoundsException {
        if(out.size < Math.max(other.size, size)) {
            throw new IndexOutOfBoundsException(
                    "Размер выходного параметра out недостаточен для вмещения результата.n\"" +
                            "Минимально необходимый размер = " + Math.max(other.size, size) + "\n" +
                            "Текущая длина out = " + out.size
            );
        }

        int commonWords = Math.min(words.length, other.words.length);
        for(int i = 0; i < commonWords; ++i) out.words[i] = words[i] ^ other.words[i];

        long[] longest = other.words.length > words.length ? other.words : words;
        System.arraycopy(longest, commonWords, out.words, commonWords, longest.length - commonWords);

        for(int i = longest.length; i < out.words.length; ++i) out.words[i] = 0L;
    }

    /**
     * Инвертирует мнодество логических значений представленных объектом Bits. Вызов данного метода влияет на значение
     * бит выходного параметра, но не влияет на его размер.
     * @param out объект в который будет записан результат выполнения метода.
     * @throws IndexOutOfBoundsException в случае, если длина выходного параметра меньше длины операнда.
     */
    public void not(Bits out) throws IndexOutOfBoundsException {
        if(out.size < size) {
            throw new IndexOutOfBoundsException(
                    "Размер выходного параметра out недостаточен для вмещения результата.n\"" +
                            "Минимально необходимый размер = " + size + "\n" +
                            "Текущая длина out = " + out.size
            );
        }

        for(int i = 0; i < words.length; i++) out.words[i] = ~words[i];
        out.words[words.length - 1] &= -1L >>> (64 - size);
        for(int i = words.length; i < out.words.length; i++) out.words[i] = 0L;
    }

    /**
     * Выполняет операцию пересечения двух множеств и возвращает результат в виде нового объекта Bits.
     * В качестве первого операнда выступает объект у которого вызывается данный метод, а второго операнда - объект
     * передаваемый в качестве аргумента.
     * @param other второй операнд операции пересечения множеств.
     * @return результат операции пересечения множеств в виде нового объекта Bits.
     */
    public Bits and(Bits other) {
        Bits result = new Bits(Math.min(size, other.size));
        if(this == other) System.arraycopy(words, 0, result.words, 0, words.length);
        else for(int i = 0; i < result.words.length; i++) result.words[i] = words[i] & other.words[i];
        return result;
    }

    /**
     * Выполняет операцию объединения двух множеств и возвращает результат в виде нового объекта Bits.
     * В качестве первого операнда выступает объект у которого вызывается данный метод, а второго операнда - объект
     * передаваемый в качестве аргумента.
     * @param other второй операнд операции объединения множеств.
     * @return результат операции объединения множеств в виде нового объекта Bits.
     */
    public Bits or(Bits other) {
        Bits result = new Bits(Math.max(size, other.size));

        if(this == other) {
            System.arraycopy(words, 0, result.words, 0, words.length);
        } else {
            int commonWords = Math.min(other.words.length, words.length);
            for(int i = 0; i < commonWords; i++) result.words[i] = words[i] | other.words[i];
            long[] longest = other.words.length > words.length ? other.words : words;
            System.arraycopy(longest, commonWords, result.words, commonWords, longest.length - commonWords);
        }

        return result;
    }

    /**
     * Выполняе операцию симметричной разности для двух множеств и возвращает результат в виде нового объекта Bits.
     * В качестве первого операнда выступает объект у которого вызывается данный метод, а второго операнда - объект
     * передаваемый в качестве аргумента.
     * @param other второй операнд для операции xor.
     * @return результат операции симмтеричной разности двух множеств в виде нового объекта Bits.
     */
    public Bits xor(Bits other) {
        Bits result = new Bits(Math.max(size, other.size));

        if(this == other) {
            Arrays.fill(result.words, 0L);
        } else {
            int commonWords = Math.min(other.words.length, words.length);
            for (int i = 0; i < commonWords; i++) result.words[i] = words[i] ^ other.words[i];
            long[] longest = other.words.length > words.length ? other.words : words;
            System.arraycopy(longest, commonWords, result.words, commonWords, longest.length - commonWords);
        }

        return result;
    }

    /**
     * Выполняет операцию дополнения множества и возвращает результат в виде нового объекта Bits. Иначе говоря,
     * создает новый объект Bits равный по длине тому, у которого вызывается данный метод, при этом значения каждого
     * элемента нового объекта попарно противоположно с значением элемента объекта у которого вызывается данный метод.
     * @return результат операции дополнения множества в виде нового объекта Bits.
     */
    public Bits not() {
        Bits result = new Bits(size);
        for(int i = 0; i < result.words.length; i++) result.words[i] = ~words[i];
        result.words[words.length - 1] &= -1L >>> (64 - size);
        return result;
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
     * Два объекта Bits считаются одинаковыми если их размеры (значения возвращаемые методом getSize()) равны и значения
     * всех бит попарно равны.
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
     * Данный метод сравнивает два объекта Bits без учета из размеров (значения возвращаемые методом getSize()).
     * С точки зрения данного метода, два объекта Bits равны, если значения всех бит обоих объектов попарно равны,
     * а все биты для которых нет пары большего с точки зрения getSize() установлены в 0.
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
     * беззанковые числа, без учета их размеров.
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
