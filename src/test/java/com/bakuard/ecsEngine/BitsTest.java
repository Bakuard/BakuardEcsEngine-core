package com.bakuard.ecsEngine;

import com.bakuard.ecsEngine.core.utils.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class BitsTest {

    @Test
    void set_get() {
        Bits bits = new Bits(100000).set(10).set(10007).set(512).set(52170);

        for(int i = 0; i < bits.getSize(); i++) {
            if(i == 10 || i == 10007 || i == 512 || i == 52170) {
                Assertions.assertTrue(bits.get(i),
                        "Метод get() должен возвращать true для бита установленного в единицу через " +
                                "метод set(). Индекс бита устанавливаемого в единицу = " + i);
            } else {
                Assertions.assertFalse(bits.get(i),
                        "Метод get() не должен возвращать true для бита, для которого не вызывался " +
                                "метод set(). Индекс бита с которым связана ошибка = " + i);
            }
        }

        for(int i = 0; i < 100; i++) bits.set(512);
        Assertions.assertTrue(bits.get(512),
                "Вызов метода set() для бита установленного в единицу не должен изменять его значение на ноль.");

        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> bits.set(100000),
                "При выходе индекса за границы размеров Bits, метод set() должен генерировать исключение.");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> bits.get(100000),
                "При выходе индекса за границы размеров Bits, метод get() должен генерировать исключение.");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> bits.set(-1),
                "При выходе индекса за границы размеров Bits, метод set() должен генерировать исключение.");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> bits.get(-1),
                "При выходе индекса за границы размеров Bits, метод get() должен генерировать исключение.");

        Assertions.assertSame(bits, bits.set(9000),
                "Метод set() должен возвращать ссылку на объект, у которого вызывался.");
    }

    @Test
    void setAll() {
        Assertions.assertThrows(IndexOutOfBoundsException.class,
                () -> new Bits(12000).setAll(-1, 0, 13, 100000),
                "Метод setAll должен генерировать исключение, если хотя бы один из переданых ему " +
                        "индексов не удовлетворяте условию index >= 0 && index < currentBits.getSize().");

        Bits bits = new Bits(12000).setAll(0,0,0, 12, 12, 12, 500, 2001);
        for(int i = 0; i < bits.getSize(); i++) {
            if(i == 0 || i == 12 || i == 500 || i == 2001) {
                Assertions.assertTrue(bits.get(i),
                        "Метод get() должен возвращать true для бит установленных в единицу через метод " +
                                "setAll(); Индекс бита = " + i);
            } else {
                Assertions.assertFalse(bits.get(i),
                        "Метод get() должен возвращать false для бита, для которого в данном случае " +
                                "не вызывался метод setAll(). Индекс бита = " + i);
            }
        }

        bits = new Bits(12000).
                fill(0, 12000, true).
                fill(1001, 9702, false).
                setAll();
        for(int i = 0; i < bits.getSize(); i++) {
            if(i >= 1001 && i < 9702) {
                Assertions.assertFalse(bits.get(i),
                        "Вызов метода setAll() без аргументов не должен изменять состояние Bits.");
            } else {
                Assertions.assertTrue(bits.get(i),
                        "Вызов метода setAll() без аргументов не должен изменять состояние Bits.");
            }
        }

        bits = new Bits(10000);
        Assertions.assertSame(bits, bits.setAll(0,0,0,1,3,5,5007),
                "Метод setAll() должен возвращать ссылку на объект, у которого он был вызван.");
    }

    @Test
    void clear_get() {
        final Bits bits = new Bits(100000);

        for(int i = 0; i < bits.getSize(); i++) {
            bits.clear(i);
            Assertions.assertFalse(bits.get(i),
                    "Метод clear(int index) вызываемый для индексов со значением false " +
                            "не должен менять их состояния на true.");
        }

        bits.fill(0, 100000, true);
        for(int i = 0; i < 100000; i++) {
            if((i < 12003 || i > 50001) && (i < 90702)) bits.clear(i);
        }
        for(int i = 0; i < 100000; i++) {
            if((i < 12003 || i > 50001) && (i < 90702)) {
                Assertions.assertFalse(bits.get(i),
                        "После вызова clear(int index) метод get(int index) должен возвращать " +
                                "false для всех индексов, для которых вызывался метод clear(int index).");
            } else {
                Assertions.assertTrue(bits.get(i),
                        "Метод get(int index) должен возвращать true для всех индексов, для которых " +
                                "в данном случае метод clear(int index) не вызывался.");
            }
        }

        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> bits.clear(100000),
                "При выходе индекса за границы размеров Bits2, метод clear() должен генерировать исключение.");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> bits.get(100000),
                "При выходе индекса за границы размеров Bits2, метод get() должен генерировать исключение.");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> bits.clear(-1),
                "При выходе индекса за границы размеров Bits2, метод clear() должен генерировать исключение.");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> bits.get(-1),
                "При выходе индекса за границы размеров Bits2, метод get() должен генерировать исключение.");

        Bits original = new Bits(100000);
        Assertions.assertSame(original, original.clear(9000),
                "Метод clear(int index) должен возвращать ссылку на объект, у которого он был вызван.");
    }

    @Test
    void clearAll() {
        Assertions.assertThrows(IndexOutOfBoundsException.class,
                () -> new Bits(12000).clearAll(-1, 0, 13, 100000),
                "Метод clearAll(int indexes) должен генерировать исключение, если хотя бы один из " +
                        "переданых ему индексов не удовлетворяте условию " +
                        "index >= 0 && index < currentBits.getSize().");

        final Bits bits = new Bits(12000);

        bits.fill(0, 12000, true).clearAll(0,0,0, 12, 12, 12, 500, 2001);
        for(int i = 0; i < bits.getSize(); i++) {
            if(i == 0 || i == 12 || i == 500 || i == 2001) {
                Assertions.assertFalse(bits.get(i),
                        "Метод get() должен возвращать false для бит установленных в ноль через метод " +
                                "clearAll(int indexes); Индекс бита = " + i);
            } else {
                Assertions.assertTrue(bits.get(i),
                        "Метод get() не должен возвращать false, если для указанного бита в данном случае " +
                                "не вызывался метод clearAll(int indexes). Индекс бита = " + i);
            }
        }

        bits.fill(0, 12000, true).fill(1001, 9702, false).clearAll(new int[0]);
        for(int i = 0; i < bits.getSize(); i++) {
            if(i >= 1001 && i < 9702) {
                Assertions.assertFalse(bits.get(i),
                        "Вызов метода clearAll(int indexes) с пустым массивом не должен изменять состояние Bits.");
            } else {
                Assertions.assertTrue(bits.get(i),
                        "Вызов метода clearAll(int indexes) с пустым массивом не должен изменять состояние Bits.");
            }
        }

        Bits original = new Bits(10000);
        Assertions.assertSame(original, original.clearAll(0,0,1,5,6),
                "Метод clearAll(int indexes) должен возвращать ссылку на объект, у которого он был вызван.");
    }

    @Test
    void Bits_copy() {
        Bits bits = new Bits(100000);
        for(int i = 0; i < bits.getSize(); i++) {
            if(i >= 12000 && i <= 24375 || i >= 80250 && i <= 99001) bits.set(i);
        }

        Bits copy = new Bits(bits);

        Assertions.assertEquals(100000, copy.getSize(),
                "Не верно работает конструктор копирования в классе Bits.");

        for(int i = 0; i < bits.getSize(); i++) {
            Assertions.assertEquals(bits.get(i), copy.get(i),
                    "Не верно работает конструктор копирования в классе Bits.");
        }
    }

    @Test
    void Bits_numberBits() {
        Bits bits = new Bits(97562);

        Assertions.assertDoesNotThrow(() -> bits.set(97561),
                "Не верно работает кнструктор Bits(int numberBits)");

        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> bits.set(97562),
                "Не верно работает кнструктор Bits(int numberBits)");

        Assertions.assertEquals(97562, bits.getSize(),
                "Не верно работает кнструктор Bits(int numberBits)");
    }

    @Test
    void clearAll_get() {
        Bits bits = new Bits(100000).
                fill(12000, 24376, true).
                fill(80250, 99002, true);

        bits.clearAll();

        Assertions.assertEquals(100000, bits.getSize(),
                "Вызов метода clearAll() не должен влиять на результат возвращаемый методом getSize().");

        for(int i = 0; i < 100000; i++) {
            Assertions.assertFalse(bits.get(i),
                    "После вызова метода clearAll() метод get() должен возвращать false для всех допустимых " +
                            "значений индексов.");
        }

        Bits original = new Bits(10000);
        Assertions.assertSame(original, original.clearAll(),
                "Метод clearAll() должен возвращать ссылку на объект, у которого он был вызван.");
    }

    @Test
    void fill_get() {
        Bits bits = new Bits(100000).
                fill(12000, 24376, true).
                fill(80250, 99002, true);

        for(int i = 0; i < bits.getSize(); i++) {
            if(i >= 12000 && i <= 24375 || i >= 80250 && i <= 99001) {
                Assertions.assertTrue(bits.get(i),
                        "Метод get() должен возвращать true для бит установленных в единицу с помощью " +
                                "метода fill(). Индекс бита = " + i);
            } else {
                Assertions.assertFalse(bits.get(i),
                        "Метод get() в данном случае должен возвращать false для бит не " +
                                "установленных в единицу с помощью метода fill(). Индекс бита = " + i);
            }
        }

        Assertions.assertEquals(100000, bits.getSize(),
                "Вызов метода fill() аргументом flag равным true не должен влиять на результат " +
                        "возвращаемый методом getSize().");

        Bits bits2 = new Bits(100000).
                fill(0, 100000, true).
                fill(28001, 56012, false);
        for(int i = 0; i < bits2.getSize(); i++) {
            if(i >= 28001 && i < 56012) {
                Assertions.assertFalse(bits2.get(i),
                        "Метод get() должен возвращать false для бит установленных в ноль с помощью " +
                                "метода fill(). Индекс бита = " + i);
            } else {
                Assertions.assertTrue(bits2.get(i),
                        "Метод get() в данном случае должен возвращать true для бит не " +
                                "установленных в ноль с помощью метода fill(). Индекс бита = " + i);
            }
        }

        Assertions.assertEquals(100000, bits2.getSize(),
                "Вызов метода fill() аргументом flag равным false не должен влиять на результат " +
                        "возвращаемый методом getSize().");

        Bits bits3 = new Bits(bits2).fill(200, 200, true);
        Assertions.assertEquals(bits2, bits3,
                "Если значение аргументов fromIndex и toIndex переданных методу fill() совпадают, " +
                        "метод не должен вносить никаких изменений.");

        Assertions.assertThrows(IndexOutOfBoundsException.class, ()-> bits.fill(12, 200000, true),
                "Если хотя бы для одного из индексов задающих интервал заполнения или очистки бит, " +
                        "не соблюдается условие index >= 0 && index < currentBits.getSize().");
        Assertions.assertThrows(IndexOutOfBoundsException.class, ()-> bits.fill(-1, 90000, true),
                "Если хотя бы для одного из индексов задающих интервал заполнения или очистки бит, " +
                        "не соблюдается условие index >= 0 && index < currentBits.getSize().");
        Assertions.assertThrows(IndexOutOfBoundsException.class, ()-> bits.fill(50000, 200, true),
                "Если fromIndex > toIndex - метод fill() должен генерировать исключение.");

        Bits original = new Bits(10000);
        Assertions.assertSame(original, original.fill(100, 700, true),
                "Метод fill() должен возвращать ссылку на объект, у которого он был вызван.");
    }

    @Test
    void cardinality_clearAll_clear_set() {
        Bits bits = new Bits(100000);
        Assertions.assertEquals(0, bits.cardinality(),
                "Метод cardinality() должен возвращать 0 для объекта Bits, у которого ни один бит " +
                        "не устанволен в единицу.");

        bits.fill(12000, 24376, true);
        bits.fill(80250, 99002, true);
        Assertions.assertEquals(31128, bits.cardinality(),
                "Метод cardinality() возвращает не верное число бит установленных  в единицу после вызова " +
                        "метода fill().");

        for(int i = 80250; i <= 99001; i++) {
            bits.clear(i);
        }
        Assertions.assertEquals(12376, bits.cardinality(),
                "Метод cardinality() возвращает не верное число бит установленных  в единицу после вызова " +
                        "метода clear().");

        bits.clearAll();
        Assertions.assertEquals(0, bits.cardinality(),
                "Метод cardinality() должен возвращать 0 после вызова метода clearAll().");
    }

    @Test
    void cardinality_singleParameter() {
        Bits bits = new Bits(100000);

        bits.set(70);
        Assertions.assertEquals(0, bits.cardinality(68));
        Assertions.assertEquals(0, bits.cardinality(70));
        Assertions.assertEquals(1, bits.cardinality(71));

        bits.clear(70);
        bits.set(63);
        Assertions.assertEquals(0, bits.cardinality(63));
        Assertions.assertEquals(1, bits.cardinality(64));

        bits.clear(63);
        bits.set(64);
        Assertions.assertEquals(0, bits.cardinality(64));
        Assertions.assertEquals(1, bits.cardinality(65));

        bits.setAll(0, 1, 22, 67, 89, 100, 15, 2);
        Assertions.assertEquals(9, bits.cardinality(101));
        Assertions.assertEquals(9, bits.cardinality(50020));
        Assertions.assertEquals(9, bits.cardinality(100000));

        bits.clearAll(22, 1);
        Assertions.assertEquals(7, bits.cardinality(101));
        Assertions.assertEquals(7, bits.cardinality(50020));
        Assertions.assertEquals(7, bits.cardinality(100000));

        bits.clearAll();
        Assertions.assertEquals(0, bits.cardinality(101));
        Assertions.assertEquals(0, bits.cardinality(50020));
        Assertions.assertEquals(0, bits.cardinality(100000));

        Assertions.assertDoesNotThrow(() -> bits.cardinality(bits.getSize()));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> bits.cardinality(bits.getSize() + 1));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> bits.cardinality(-1));
    }

    @Test
    void getHighBitIndex() {
        Bits bits = new Bits(100000);
        Assertions.assertEquals(-1, bits.getHighBitIndex(),
                "Если все биты Bits равны 0, метод getHighBitIndex() должен возвращать -1.");

        bits.set(0);
        Assertions.assertEquals(0, bits.getHighBitIndex());

        bits.set(63);
        Assertions.assertEquals(63, bits.getHighBitIndex());

        bits.setAll(12, 34, 56, 1200, 500, 50001, 28888, 17334);
        Assertions.assertEquals(50001, bits.getHighBitIndex());

        bits.set(bits.getSize() - 1);
        Assertions.assertEquals(bits.getSize() - 1, bits.getHighBitIndex());
    }

    @Test
    void isEmpty() {
        Bits bits = new Bits(100000);
        Assertions.assertTrue(bits.isEmpty(),
                "Для новго объекта Bits созданого через конструктор Bits(int numberBits) " +
                        "метод isEmpty() всегда должен возвращаеть true.");

        bits = new Bits();
        Assertions.assertTrue(bits.isEmpty(),
                "Для новго объекта Bits созданого через конструктор Bits() " +
                        "метод isEmpty() всегда должен возвращаеть true.");

        bits.expandTo(50000);
        Assertions.assertTrue(bits.isEmpty(),
                "Вызов метода expandTo() не должен отражаться на резульате работы метода isEmpty()");

        bits.set(12);
        Assertions.assertFalse(bits.isEmpty(),
                "Непосредственно после вызова set(), метод isEmpty() должен возвращать false.");

        bits.clear(12);
        Assertions.assertTrue(bits.isEmpty(),
                "Если все биты Bits были очищены с помощью clear() метод isEmpty() должен возвращать true.");

        bits.fill(12070, 30450, true);
        Assertions.assertFalse(bits.isEmpty(),
                "После вызова fill() со значеним флага равным true, " +
                        "метод isEmpty() должен возвращать false.");

        bits.fill(12070, 30450, false);
        Assertions.assertTrue(bits.isEmpty(),
                "Если все биты Bits были очищены с помощью fill() метод isEmpty() должен возвращать true.");

        bits.fill(12070, 30450, true).clearAll();
        Assertions.assertTrue(bits.isEmpty(),
                "После вызова clearAll() метод isEmpty() должен возвращать true.");
    }

    @Test
    void nextSetBit() {
        Bits bits = new Bits(100000);
        bits.setAll(15006, 21450, 21507, 31600, 31601, 70000, 99500);

        int index = -1;
        Assertions.assertEquals(15006, (index = bits.nextSetBit(index + 1)),
                "Не верно работает метод nextSetBit().");
        Assertions.assertEquals(21450, (index = bits.nextSetBit(index + 1)),
                "Не верно работает метод nextSetBit().");
        Assertions.assertEquals(21507, (index = bits.nextSetBit(index + 1)),
                "Не верно работает метод nextSetBit().");
        Assertions.assertEquals(31600, (index = bits.nextSetBit(index + 1)),
                "Не верно работает метод nextSetBit().");
        Assertions.assertEquals(31601, (index = bits.nextSetBit(index + 1)),
                "Не верно работает метод nextSetBit().");
        Assertions.assertEquals(70000, (index = bits.nextSetBit(index + 1)),
                "Не верно работает метод nextSetBit().");
        Assertions.assertEquals(99500, bits.nextSetBit(index + 1),
                "Не верно работает метод nextSetBit().");
    }

    @Test
    void nextClearBit() {
        Bits bits = new Bits(100000);
        bits.fill(0, 100000, true);
        bits.clearAll(0, 15006, 21450, 21507, 31600, 31601, 70000, 99500);

        int index = -1;
        Assertions.assertEquals(0, (index = bits.nextClearBit(index + 1)),
                "Не верно работает метод nextClearBit().");
        Assertions.assertEquals(15006, (index = bits.nextClearBit(index + 1)),
                "Не верно работает метод nextClearBit().");
        Assertions.assertEquals(21450, (index = bits.nextClearBit(index + 1)),
                "Не верно работает метод nextClearBit().");
        Assertions.assertEquals(21507, (index = bits.nextClearBit(index + 1)),
                "Не верно работает метод nextClearBit().");
        Assertions.assertEquals(31600, (index = bits.nextClearBit(index + 1)),
                "Не верно работает метод nextClearBit().");
        Assertions.assertEquals(31601, (index = bits.nextClearBit(index + 1)),
                "Не верно работает метод nextClearBit().");
        Assertions.assertEquals(70000, (index = bits.nextClearBit(index + 1)),
                "Не верно работает метод nextClearBit().");
        Assertions.assertEquals(99500, bits.nextClearBit(index + 1),
                "Не верно работает метод nextClearBit().");
    }

    @Test
    void expandTo() {
        Bits bits = new Bits(100000).
                fill(12000, 24376, true).
                fill(80250, 99002, true);

        bits.expandTo(20000);
        Assertions.assertEquals(100000, bits.getSize(),
                "При вызове expandTo() с аргументом, значение которого меньше значения возвращаемого\n" +
                        "методом getSize(), метод expandTo() не должен изменять состояние объекта Bits.");
        for(int i = 0; i < 100000; i++) {
            if(i >= 12000 && i <= 24375 || i >= 80250 && i <= 99001)
                Assertions.assertTrue(bits.get(i), "Не верно работает метод expandTo().");
            else
                Assertions.assertFalse(bits.get(i), "Не верно работает метод expandTo().");
        }

        bits.expandTo(200034).fill(150000, 200034, true);
        Assertions.assertEquals(200034, bits.getSize(),
                "При вызове expandTo() с аргументом, значение которого больше значения возвращаемого\n" +
                        "методом getSize(), метод expandTo() должен изменить состояние объекта Bits и \n" +
                        "метод getSize() должен возвращать значение переданное методу expandTo().");
        for(int i = 0; i < 200034; i++) {
            if(i >= 12000 && i <= 24375 || i >= 80250 && i <= 99001 || i >= 150000)
                Assertions.assertTrue(bits.get(i), "Не верно работает метод expandTo().");
            else
                Assertions.assertFalse(bits.get(i), "Не верно работает метод expandTo().");
        }

        Bits original = new Bits(10000);
        Assertions.assertEquals(original, original.expandTo(20000),
                "Метод expandTo(int numberBits) должен возвращать ссылку на объект, у которого он был вызван.");
    }

    @Test
    void compressTo_expandTo() {
        Bits bits = new Bits(100000).
                fill(12000, 24376, true).
                fill(80250, 99002, true);
        Bits expected = new Bits(100000).
                fill(12000, 24376, true).
                fill(80250, 99002, true);

        bits.compressTo(100001);
        Assertions.assertEquals(expected, bits,
                "Метод compressTo() не должен изменять состояние объекта Bits если значение аргумента " +
                        "больше или равно getSize().");

        bits.compressTo(24300);
        expected = new Bits(24300).fill(12000, 24300, true);
        Assertions.assertEquals(24300, bits.getSize(),
                "При вызове compressTo() с аргументом, значение которого меньше значения возвращаемого " +
                        "методом getSize(), метод compressTo() должен изменить состояние объекта Bits2 и " +
                        "метод getSize() должен возвращать значение переданное методу compressTo().");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> bits.get(24300),
                "Не верно работает метод compressTo().");
        Assertions.assertEquals(expected, bits);

        expected = new Bits(100000).fill(12000, 24300, true);
        Assertions.assertEquals(expected, bits.expandTo(100000),
                "После вызова метода compressTo(), биты во внутреннем хранилище, индекс которых " +
                        "больше или равен значению переданному compressTo(), должны быть установленны в ноль.");

        Bits empty = new Bits();
        bits.fill(0, 65, true).compressTo(0);
        Assertions.assertEquals(empty, bits);
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> bits.get(0),
                "Не верно работает метод compressTo().");
        Assertions.assertEquals(new Bits(100000), bits.expandTo(100000),
                "После вызова метода compressTo() уменьшающего емкость объекта до нуля, все биты во " +
                        "внутреннем хранилище, должны быть установленны в ноль.");

        Bits original = new Bits(10000);
        Assertions.assertEquals(original, original.compressTo(2000),
                "Метод compressTo(int numberBits) должен возвращать ссылку на объект, у которого он был вызван.");
    }

    @Test
    void copyState() {
        Bits original = new Bits(100000).fill(23078, 40200, true);

        Assertions.assertEquals(original, new Bits(100).setAll(1,23,45,46,98).copyState(original),
                "Метод copyState(Bits other) не верно работает, если размер копируемого объекта больше " +
                        "текущего.");

        Assertions.assertEquals(original,
                new Bits(150000).fill(56010, 120007, true).copyState(original),
                "Метод copyState(Bits other) не верно работает, если размер копируемого объекта меньше " +
                        "текущего.");

        Bits bits = new Bits(100).setAll(1,23,45,46,98);
        Assertions.assertSame(bits, bits.copyState(original),
                "Метод copyState(Bits other) должен возвращать ссылку на объект, у которого он был вызван.");
    }

    @Test
    void and_Operand() {
        Bits original = new Bits(100000).fill(20000, 50001, true);

        Bits operand1 = new Bits(original);
        Assertions.assertEquals(original, operand1.and(operand1),
                "Если метод and(Bits other) в качестве аргумента получает тот же объект, у которого " +
                        "и вызывается, то результатирующий объект должен быть равен исходному по методу equals()."
        );

        Bits operand2 = new Bits(40000).fill(10000, 30001, true);
        Bits expected = new Bits(100000).fill(20000, 30001, true);
        Assertions.assertEquals(expected, new Bits(original).and(operand2),
                "Не верно работает метод and(Bits other) в случае, если размер второго оперенда " +
                        "меньше первого.");

        Bits operand3 = new Bits(200000).fill(40000, 120000, true);
        expected = new Bits(100000).fill(40000, 50001, true);
        Assertions.assertEquals(expected, new Bits(original).and(operand3),
                "Не верно работает метод and(Bits other) в случае, если размер второго оперенда больше первого.");
    }

    @Test
    void and_Properties() {
        Bits bits1 = new Bits(100000).fill(20050, 30999, true);
        Bits bits2 = new Bits(100000).fill(20050, 30999, true);
        Bits bits3 = new Bits(100000).fill(30000, 90111, true);

        Assertions.assertEquals(new Bits(bits1).and(new Bits(bits2)), new Bits(bits2).and(new Bits(bits1)),
                "Не соблюдается свойство коммутативности для метода and(Bits other).");

        Assertions.assertEquals(bits1, new Bits(bits1).and(bits1),
                "Не соблюдается свойство идемпотентности для метода and(Bits other).");

        Assertions.assertEquals(new Bits(bits1).and(bits2).and(bits3), new Bits(bits2).and(bits3).and(bits1),
                "Не соблюдается свойство ассоциативности для метода and(Bits other).");

        Bits full = new Bits(100000).fill(0, 100000, true);
        Assertions.assertEquals(bits1, new Bits(bits1).and(full),
                "Не соблюдается свойство единицы для метода and(Bits other).");

        Bits empty = new Bits(100000);
        Assertions.assertEquals(empty, new Bits(bits1).and(empty),
                "Не соблюдается свойство нуля для метода and(Bits other).");

        Bits original = new Bits(bits2);
        Assertions.assertSame(original, original.and(bits3),
                "Метод and(Bits other) должен возвращать ссылку на объект, у которого он был вызван.");
    }

    @Test
    void or_Operand() {
        Bits original = new Bits(100000).fill(20000, 50001, true);

        Bits operand1 = new Bits(original);
        Assertions.assertEquals(original, operand1.or(operand1),
                "Если метод or() с одним параметром в качестве аргумента получает тот же объект, у " +
                        "которого и вызывается, то результатрирующий объект должен быть равен исходному по " +
                        "методу equals()."
        );

        Bits operand2 = new Bits(40000).fill(10000, 30001, true);
        Bits expected = new Bits(100000).fill(10000, 50001, true);
        Assertions.assertEquals(expected, new Bits(original).or(operand2),
                "Не верно работает метод or(Bits other) в случае, если размер второго оперенда " +
                        "меньше первого.");

        Bits operand3 = new Bits(200000).fill(40000, 120000, true);
        expected = new Bits(200000).fill(20000, 120000, true);
        Assertions.assertEquals(expected, new Bits(original).or(operand3),
                "Не верно работает метод or(Bits other) в случае, если размер второго оперенда больше первого.");
    }

    @Test
    void or_Properties() {
        Bits bits1 = new Bits(100000).fill(20050, 30999, true);
        Bits bits2 = new Bits(100000).fill(20050, 30999, true);
        Bits bits3 = new Bits(100000).fill(30000, 90111, true);

        Assertions.assertEquals(new Bits(bits1).or(new Bits(bits2)), new Bits(bits2).or(new Bits(bits1)),
                "Не соблюдается свойство коммутативности для метода or(Bits other).");

        Assertions.assertEquals(bits1, new Bits(bits1).or(bits1),
                "Не соблюдается свойство идемпотентности для метода or(Bits other).");

        Assertions.assertEquals(new Bits(bits1).or(bits2).or(bits3), new Bits(bits2).or(bits3).or(bits1),
                "Не соблюдается свойство ассоциативности для метода or(Bits other).");

        Bits full = new Bits(100000).fill(0, 100000, true);
        Assertions.assertEquals(full, new Bits(bits1).or(full),
                "Не соблюдается свойство единицы для метода or(Bits other).");

        Bits empty = new Bits(100000);
        Assertions.assertEquals(bits1, new Bits(bits1).or(empty),
                "Не соблюдается свойство нуля для метода or(Bits other).");

        Bits original = new Bits(bits2);
        Assertions.assertSame(original, original.or(bits3),
                "Метод or(Bits other) должен возвращать ссылку на объект, у которого он был вызван.");
    }

    @Test
    void xor_Operand() {
        Bits original = new Bits(1000).setAll(10, 100, 200, 219, 600, 601, 742, 326);

        Assertions.assertEquals(new Bits(1000), new Bits(original).xor(original),
                "При вызове метода xor(), где в качестве аргмента передается один и тот же объект, " +
                        "результатом должен являться пустой объект Bits.");

        Bits operand2 = new Bits(750).setAll(10, 100, 201, 220, 600, 601, 743, 326);
        Bits expected = new Bits(1000).setAll(200, 201, 219, 220, 742, 743);
        Assertions.assertEquals(expected, new Bits(original).xor(operand2),
                "Не верно работает метод xor() с одним параметром в случае, если второй операнд меньше " +
                        "по размеру первого.");

        Bits operand3 = new Bits(2000).setAll(10, 100, 201, 220, 600, 601, 743, 326, 1001, 1200, 1317, 1500, 1902);
        expected = new Bits(2000).setAll(200, 201, 219, 220, 742, 743, 1001, 1200, 1317, 1500, 1902);
        Assertions.assertEquals(expected, new Bits(original).xor(operand3),
                "Не верно работает метод xor() с одним параметром в случае, если второй операнд больше " +
                        "по размеру первого.");
    }

    @Test
    void xor_Properties() {
        Bits bits1 = new Bits(1000).setAll(10, 100, 200, 219, 600, 601, 742, 326);
        Bits bits2 = new Bits(1000).setAll(10, 100, 201, 220, 600, 601, 743, 326);
        Bits bits3 = new Bits(1000).setAll(200, 201, 219, 220, 744, 745);
        Bits empty = new Bits(1000);

        Assertions.assertEquals(new Bits(bits1).xor(new Bits(bits2)), new Bits(bits2).xor(new Bits(bits1)),
                "Не выполняеся свойство коммутативности для метода xor().");

        Assertions.assertEquals(new Bits(bits1).or(bits2).or(bits3), new Bits(bits2).or(bits3).or(bits1),
                "Не соблюдается свойство ассоциативности для метода or(Bits other).");

        Bits copy = new Bits(bits1);
        Assertions.assertEquals(empty, copy.xor(copy),
                "При симметричной разности любого объекта Bits с самим собой, " +
                        "должно получиться пустое множество.");

        Assertions.assertEquals(empty, new Bits(bits1).xor(bits1),
                "При симметричной разности любых равных объектов Bits, " +
                        "должно получиться пустое множество.");

        Assertions.assertEquals(bits1, new Bits(bits1).xor(empty),
                "пустое множество должно являться нейтральным элементом для метода xor().");

        Bits original = new Bits(bits2);
        Assertions.assertSame(original, original.xor(bits3),
                "Метод xor(Bits other) должен возвращать ссылку на объект, у которого он был вызван.");
    }

    @Test
    void not_Operand() {
        Bits original = new Bits(100000).fill(20000, 50001, true);

        Bits expected = new Bits(100000).
                fill(0, 20000, true).
                fill(50001, 100000, true);
        Assertions.assertEquals(expected, new Bits(original).not());
    }

    @Test
    void not_Properties() {
        Bits expected = new Bits(100000).fill(10000, 37601, true);

        Bits bits = new Bits(expected).not().not();

        Assertions.assertEquals(expected, bits,
                "Не выполняется свойство двойного отрицания для метода not().");

        Bits original = new Bits(expected);
        Assertions.assertSame(original, original.not(),
                "Метод not() должен возвращать ссылку на объект, у которого он был вызван.");
    }

    @Test
    void contains() {
        Bits bits = new Bits(10000);
        bits.fill(200, 5004, true);

        Bits bits2 = new Bits(5000);
        bits2.setAll(300, 500, 1000, 1001, 2217);
        Assertions.assertTrue(bits.contains(bits2));

        bits2.expandTo(100000);
        Assertions.assertTrue(bits.contains(bits2));

        bits2.set(100);
        Assertions.assertFalse(bits.contains(bits2));

        bits2.clear(100);
        bits2.set(90000);
        Assertions.assertFalse(bits.contains(bits2));

        Bits emptyBits = new Bits();
        Assertions.assertTrue(bits.contains(emptyBits),
                "Не соблюдается правило: пустое множество является подмножеством любого другого множества.");
        Assertions.assertTrue(emptyBits.contains(emptyBits),
                "Не соблюдается правило: пустое множество является подмножеством самого себя.");
    }

    @Test
    void strictlyContains() {
        Bits bits = new Bits(10000);
        bits.fill(2000, 7000, true);

        Bits bits2 = new Bits(10000);
        bits2.setAll(2001, 3012, 5000, 6016, 6999);
        Assertions.assertTrue(bits.strictlyContains(bits2));

        Bits bits3 = new Bits(5000);
        bits3.fill(3000, 4000, true);
        Assertions.assertTrue(bits.strictlyContains(bits3),
                "Результат работы метода strictlyContains не должен зависеть от разности в размерах " +
                        "объектов Bits.");

        Bits bits4 = new Bits(50000);
        bits4.fill(2050, 6950, true);
        Assertions.assertTrue(bits.strictlyContains(bits4),
                "Результат работы метода strictlyContains не должен зависеть от разности в размерах " +
                        "объектов Bits.");

        Bits bits5 = new Bits(10000);
        bits5.setAll(50, 2001, 3012, 5000, 6016, 6999);
        Assertions.assertFalse(bits.strictlyContains(bits5));

        bits5.clearAll();
        bits5.fill(2000, 7000, true);
        Assertions.assertFalse(bits.strictlyContains(bits5),
                "Метод strictlyContains() должен возвращать false, если два объекта Bits равны по" +
                        "equalsIgnoreSize().");

        Bits bits6 = new Bits(5000);
        bits6.fill(500, 4000, true);
        Assertions.assertFalse(bits.strictlyContains(bits6),
                "Результат работы метода strictlyContains не должен зависеть от разности в размерах " +
                        "объектов Bits.");

        Bits bits7 = new Bits(50000);
        bits7.fill(50, 6950, true);
        Assertions.assertFalse(bits.strictlyContains(bits7),
                "Результат работы метода strictlyContains не должен зависеть от разности в размерах " +
                        "объектов Bits.");

        bits7.clearAll();
        bits7.fill(2000, 7000, true);
        Assertions.assertFalse(bits.strictlyContains(bits7),
                "Метод strictlyContains() должен возвращать false, если два объекта Bits равны по" +
                        "equalsIgnoreSize().");

        bits7.fill(50, 40900, true);
        Assertions.assertFalse(bits.strictlyContains(bits7),
                "Результат работы метода strictlyContains() не должен зависеть от разности в размерах " +
                        "объектов Bits.");

        bits7.clearAll();
        bits7.fill(2000, 6999, true);
        bits7.set(32000);
        Assertions.assertFalse(bits.strictlyContains(bits7),
                "Результат работы метода strictlyContains() не должен зависеть от разности в размерах " +
                        "объектов Bits.");
    }

    @Test
    void intersect() {
        Bits bits = new Bits(100000);
        bits.fill(1000, 40000, true);

        Bits bits2 = new Bits(50000);
        bits2.fill(30000, 50000, true);
        Assertions.assertTrue(bits.intersect(bits2));
        Assertions.assertTrue(bits2.intersect(bits));

        Bits bits3 = new Bits(200000);
        bits3.fill(10, 950,true);
        bits3.fill(110000, 150050, true);
        Assertions.assertFalse(bits.intersect(bits3));
        Assertions.assertFalse(bits3.intersect(bits));
    }

    @Test
    void equals() {
        Bits bits1 = new Bits(10000);
        bits1.fill(2001, 7099, true);
        Bits bits2 = new Bits(10000);
        bits2.fill(2001, 7099, true);

        Assertions.assertEquals(bits1,bits2,
                "Не верно работает метод equals().");

        Assertions.assertEquals(bits1, bits1,
                "Не соблюдаетс свойство рефлексивности для метода equals().");
        Assertions.assertEquals(bits2, bits2,
                "Не соблюдаетс свойство рефлексивности для метода equals().");

        Assertions.assertEquals(bits1.equals(bits2), bits2.equals(bits1),
                "Не соблюдается свойство симметрисности для метода equals().");

        bits2.expandTo(100000);
        Assertions.assertNotEquals(bits1, bits2,
                "Не верно работает метод equals().");
        Assertions.assertEquals(bits2.equals(bits1), bits1.equals(bits2),
                "Не соблюдается свойство симметрисности для метода equals().");

        Bits bits3 = new Bits(10000);
        bits3.fill(10, 3000, true);
        Assertions.assertNotEquals(bits1, bits3,
                "Не верно работает метод equals().");
        Assertions.assertEquals(bits3.equals(bits1), bits1.equals(bits3),
                "Не соблюдается свойство симметрисности для метода equals().");

        bits2 = new Bits(10000);
        bits2.fill(2001, 7099, true);
        bits3 = new Bits(10000);
        bits3.fill(2001, 7099, true);
        Assertions.assertTrue(bits1.equals(bits2) && bits2.equals(bits3) && bits1.equals(bits3),
                "Не соблюдается свойство транзитивности для сетода equals().");
    }

    @Test
    void equalsIgnoreSize() {
        Assertions.assertTrue(new Bits(10000).equalsIgnoreSize(new Bits()));

        Bits bits1 = new Bits(8000);
        bits1.fill(2001, 7099, true);
        Bits bits2 = new Bits(1000000);
        bits2.fill(2001, 7099, true);
        Bits bits3 = new Bits(100000);
        bits3.fill(2001, 7099, true);
        bits3.fill(10001, 12030, true);

        Assertions.assertFalse(bits1.equalsIgnoreSize(bits3),
                "Не верно работает метод equalsIgnoreSize().");
        Assertions.assertTrue(bits1.equalsIgnoreSize(bits2),
                "Не верно работает метод equalsIgnoreSize().");

        Assertions.assertTrue(bits1.equalsIgnoreSize(bits1),
                "Не соблюдается свойство рефлексивности для метода equalsIgnoreSize().");
        Assertions.assertTrue(bits2.equalsIgnoreSize(bits2),
                "Не соблюдаетс свойство рефлексивности для метода equalsIgnoreSize().");
        Assertions.assertTrue(bits3.equalsIgnoreSize(bits3),
                "Не соблюдаетс свойство рефлексивности для метода equalsIgnoreSize().");

        Assertions.assertEquals(bits1.equalsIgnoreSize(bits2), bits2.equalsIgnoreSize(bits1),
                "Не соблюдается свойство симметрисности для метода equalsIgnoreSize().");
        Assertions.assertEquals(bits3.equalsIgnoreSize(bits1), bits1.equalsIgnoreSize(bits3),
                "Не соблюдается свойство симметрисности для метода equalsIgnoreSize().");

        bits2.fill(1000, 2000, true);
        Assertions.assertTrue(!bits1.equalsIgnoreSize(bits2) && !bits2.equalsIgnoreSize(bits3) &&
                        !bits1.equalsIgnoreSize(bits3),
                "Не соблюдается свойство транзитивности для метода equalsIgnoreSize().");
        bits2.clearAll();
        bits2.fill(2001, 7099, true);
        bits3.clearAll();
        bits3.fill(2001, 7099, true);
        Assertions.assertTrue(bits1.equalsIgnoreSize(bits2) && bits2.equalsIgnoreSize(bits3) &&
                        bits1.equalsIgnoreSize(bits3),
                "Не соблюдается свойство транзитивности для сетода equalsIgnoreSize().");


        bits1 = new Bits(8000);
        bits1.fill(2001, 7099, true);
        bits2 = new Bits(8000);
        bits2.fill(2001, 7099, true);
        bits3 = new Bits(8000);
        bits3.fill(1000, 5000, true);

        Assertions.assertFalse(bits1.equalsIgnoreSize(bits3),
                "Не верно работает метод equalsIgnoreSize().");
        Assertions.assertTrue(bits1.equalsIgnoreSize(bits2),
                "Не верно работает метод equalsIgnoreSize().");

        Assertions.assertTrue(bits1.equalsIgnoreSize(bits1),
                "Не соблюдается свойство рефлексивности для метода equalsIgnoreSize().");
        Assertions.assertTrue(bits2.equalsIgnoreSize(bits2),
                "Не соблюдаетс свойство рефлексивности для метода equalsIgnoreSize().");
        Assertions.assertTrue(bits3.equalsIgnoreSize(bits3),
                "Не соблюдаетс свойство рефлексивности для метода equalsIgnoreSize().");

        Assertions.assertEquals(bits1.equalsIgnoreSize(bits2), bits2.equalsIgnoreSize(bits1),
                "Не соблюдается свойство симметрисности для метода equalsIgnoreSize().");
        Assertions.assertEquals(bits3.equalsIgnoreSize(bits1), bits1.equalsIgnoreSize(bits3),
                "Не соблюдается свойство симметрисности для метода equalsIgnoreSize().");

        bits2.fill(1000, 2000, true);
        Assertions.assertTrue(!bits1.equalsIgnoreSize(bits2) && !bits2.equalsIgnoreSize(bits3) &&
                        !bits1.equalsIgnoreSize(bits3),
                "Не соблюдается свойство транзитивности для метода equalsIgnoreSize().");
        bits2.clearAll();
        bits2.fill(2001, 7099, true);
        bits3.clearAll();
        bits3.fill(2001, 7099, true);
        Assertions.assertTrue(bits1.equalsIgnoreSize(bits2) && bits2.equalsIgnoreSize(bits3) &&
                        bits1.equalsIgnoreSize(bits3),
                "Не соблюдается свойство транзитивности для сетода equalsIgnoreSize().");
    }

    @Test
    void compareTo() {
        Bits bits1 = new Bits(1000);
        Bits bits2 = new Bits(500);
        Bits bits3 = new Bits(1000);
        Bits bits4 = new Bits(1000);
        bits1.setAll(12,23,4,103,111,112,126,205);
        bits2.setAll(126,205,314,380,419,498);
        bits3.setAll(50,103,111,112,126,205);
        bits4.fill(0, 205, true);
        Bits bits5 = new Bits(bits1);
        Bits bits6 = new Bits(bits1);

        Assertions.assertTrue(bits1.compareTo(bits2) > 0,
                "Метод compareTo() должен определять объект Bits имеющий больший размер,\n " +
                        "как больший объект, несмотря на то, что объект с меньшим размером\n " +
                        "является больше как целоое беззнаковое число.");
        Assertions.assertTrue(bits3.compareTo(bits1) > 0,
                "Метод compareTo() должен сравнивать два объекта Bits имеющих одинаковый\n " +
                        "размер, как два беззнаковых целых числа.");
        Assertions.assertTrue(bits1.compareTo(bits4) > 0,
                "Метод compareTo() должен сравнивать два объекта Bits имеющих одинаковый\n " +
                        "размер, как два беззнаковых целых числа.");
        Assertions.assertEquals(0, bits1.compareTo(bits5),
                "Метод compareTo() должен возвращать 0, если два объекта Bits имеют\n " +
                        "одинаковый размер и одинаковый набор бит.");
        Assertions.assertTrue(bits2.compareTo(new Bits()) > 0,
                "Метод compareTo() при сравнении пустого объекта Bits нулевого размера\n " +
                        "с не пустым, должен определять непустой объект Bits как больший.");
        Assertions.assertEquals(0, new Bits().compareTo(new Bits()),
                "Метод compareTo() при сравнении двух пустых объектов Bits нулевого размера\n " +
                        "должен возвращать 0.");

        Assertions.assertEquals(bits1.compareTo(bits2), -bits2.compareTo(bits1),
                "Метод compareTo() должен обладать свойством антисимметричности:\n " +
                        "x.compareTo(y) == -y.compareTo(x).");

        Assertions.assertTrue(
                bits3.compareTo(bits1) > 0 &&
                        bits1.compareTo(bits2) > 0 &&
                        bits3.compareTo(bits2) > 0,
                "Метод compareTo() должен обладать свойством транзитивности.");
        Assertions.assertTrue(
                bits1.compareTo(bits5) == 0 &&
                        bits5.compareTo(bits6) == 0 &&
                        bits1.compareTo(bits6) == 0,
                "Метод compareTo() должен обладать свойством транзитивности.");

        Assertions.assertEquals(bits1.compareTo(bits2), bits5.compareTo(bits2),
                "Для метода compareTo() должно соблюдаться свойство:\n " +
                        "если x.compareTo(y) == 0, то x.compareTo(z) == y.compareTo(z).");

        Assertions.assertTrue(bits1.equals(bits5) && bits1.compareTo(bits5) == 0,
                "Для метода compareTo() должно соблюдаться свойство:\n " +
                        "если x.equals(y) == true, то x.compareTo(y) == 0.");
        Assertions.assertTrue(!bits1.equals(bits4) && bits1.compareTo(bits4) != 0,
                "Для метода compareTo() должно соблюдаться свойство:\n " +
                        "если x.equals(y) == false, то x.compareTo(y) != 0.");

        Assertions.assertThrows(NullPointerException.class, ()-> bits1.compareTo(null),
                "Метод compareTo() должен выбрасывать NullPointerException,\n " +
                        "если в качестве аргумента он получает null.");
    }

    @Test
    void compareIgnoreSize() {
        Bits bits1 = new Bits(1000);
        Bits bits2 = new Bits(500);
        Bits bits3 = new Bits(800);
        Bits bits4 = new Bits(2000);
        Bits bits5 = new Bits(700);
        Bits bits6 = new Bits(1100);
        bits1.setAll(12,23,4,103,111,112,126,205);
        bits2.setAll(126,205,314,380,419,498);
        bits3.setAll(50,103,111,112,126,205);
        bits4.fill(0, 205, true);
        bits5.setAll(12,23,4,103,111,112,126,205);
        bits6.setAll(12,23,4,103,111,112,126,205);

        Assertions.assertTrue(bits2.compareIgnoreSize(bits1) > 0,
                "Метод compareIgnoreSize() должен сравнивать два объекта Bits как целые\n " +
                        "беззнаковые числа, без учета размеров этих объектов.");
        Assertions.assertTrue(bits3.compareIgnoreSize(bits1) > 0,
                "Метод compareIgnoreSize() должен сравнивать два объекта Bits как целые\n " +
                        "беззнаковые числа, без учета размеров этих объектов.");
        Assertions.assertTrue(bits1.compareIgnoreSize(bits4) > 0,
                "Метод compareIgnoreSize() должен сравнивать два объекта Bits как целые\n " +
                        "беззнаковые числа, без учета размеров этих объектов.");
        Assertions.assertEquals(0, bits1.compareIgnoreSize(bits5),
                "Метод compareIgnoreSize() должен возвращать 0, несмотря на разные размеры\n " +
                        "двух объектов Bits, если те равны как целые беззнаковые числа.");
        Assertions.assertTrue(bits2.compareIgnoreSize(new Bits()) > 0,
                "Метод compareIgnoreSize() при сравнении пустого объекта Bits нулевого размера\n " +
                        "с не пустым, должен определять непустой объект Bits как больший.");
        Assertions.assertEquals(0, new Bits().compareIgnoreSize(new Bits()),
                "Метод compareIgnoreSize() при сравнении двух пустых объектов Bits нулевого размера\n " +
                        "должен возвращать 0.");

        Assertions.assertEquals(bits1.compareIgnoreSize(bits2), -bits2.compareIgnoreSize(bits1),
                "Метод compareIgnoreSize() должен обладать свойством антисимметричности:\n " +
                        "x.compareIgnoreSize(y) == -y.compareIgnoreSize(x).");

        Assertions.assertTrue(
                bits2.compareIgnoreSize(bits3) > 0 &&
                        bits3.compareIgnoreSize(bits1) > 0 &&
                        bits2.compareIgnoreSize(bits1) > 0,
                "Метод compareIgnoreSize() должен обладать свойством транзитивности.");
        Assertions.assertTrue(
                bits1.compareIgnoreSize(bits5) == 0 &&
                        bits5.compareIgnoreSize(bits6) == 0 &&
                        bits1.compareIgnoreSize(bits6) == 0,
                "Метод compareIgnoreSize() должен обладать свойством транзитивности.");

        Assertions.assertEquals(bits1.compareIgnoreSize(bits2), bits5.compareIgnoreSize(bits2),
                "Для метода compareIgnoreSize() должно соблюдаться свойство:\n " +
                        "если x.compareIgnoreSize(y) == 0, то x.compareIgnoreSize(z) == y.compareIgnoreSize(z).");

        Assertions.assertTrue(bits1.equalsIgnoreSize(bits5) && bits1.compareIgnoreSize(bits5) == 0,
                "Для метода compareIgnoreSize() должно соблюдаться свойство:\n " +
                        "если x.equalsIgnoreSize(y) == true, то x.compareIgnoreSize(y) == 0.");
        Assertions.assertTrue(!bits1.equalsIgnoreSize(bits4) && bits1.compareIgnoreSize(bits4) != 0,
                "Для метода compareIgnoreSize() должно соблюдаться свойство:\n " +
                        "если x.equalsIgnoreSize(y) == false, то x.compareIgnoreSize(y) != 0.");

        Assertions.assertThrows(NullPointerException.class, ()-> bits1.compareIgnoreSize(null),
                "Метод compareIgnoreSize() должен выбрасывать NullPointerException,\n " +
                        "если в качестве аргумента он получает null.");
    }

    @Test
    void hashCode_Properties() {
        Bits bits1 = new Bits(100000);
        bits1.fill(10000, 70400, true);
        Bits bits2 = new Bits(100000);
        bits2.fill(10000, 70400, true);
        Bits bits3 = new Bits(100000);
        bits3.fill(10001, 80099, true);

        Assertions.assertEquals(bits1.hashCode(), bits2.hashCode(),
                "Если объекты равны по equals(), то их хеш-коды тоже должны быть равны.");

        if(bits1.hashCode() != bits3.hashCode()) {
            Assertions.assertNotEquals(bits1, bits3,
                    "Если хеш коды объектов не равны, то объекты гарантированно отличаются по equals().");
        }

        int hashCode = bits1.hashCode();
        for(int i = 0; i < 100000; i++) {
            Assertions.assertEquals(hashCode, bits1.hashCode(),
                    "Для одного и того же состояния объекта hashCode() должен возвращать одно и тоже значение.");
        }
    }

}