package com.bakuard.ecsEngine;

import com.bakuard.ecsEngine.utils.Bits;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class BitsTest {

    @Test
    void set_get() {
        Bits bits = new Bits(100000);
        bits.set(10);
        bits.set(10007);
        bits.set(512);
        bits.set(52170);

        for(int i = 0; i < 100000; i++) {
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
        Assertions.assertTrue(bits.get(512), "Вызов  метода set() для бита установленного в единицу " +
                "не должен изменять его значение на ноль.");

        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> bits.set(100000),
                "При выходе индекса за границы размеров Bits, метод set() должен генерировать исключение.");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> bits.get(100000),
                "При выходе индекса за границы размеров Bits, метод get() должен генерировать исключение.");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> bits.set(-1),
                "При выходе индекса за границы размеров Bits, метод set() должен генерировать исключение.");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> bits.get(-1),
                "При выходе индекса за границы размеров Bits, метод get() должен генерировать исключение.");
    }

    @Test
    void setAll() {
        final Bits bits = new Bits(12000);

        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> bits.setAll(-1, 0, 13, 100000),
                "Метод setAll должен генерировать исключение, если хотя бы один из переданых ему " +
                        "индексов не удовлетворяте условию index >= 0 && index < currentBits.getSize().");

        bits.setAll(0,0,0, 12, 12, 12, 500, 2001);
        for(int i = 0; i < bits.getSize(); i++) {
            if(i == 0 || i == 12 || i == 500 || i == 2001) {
                Assertions.assertTrue(bits.get(i),
                        "Метод get() должен возвращать true для бит установленных в  единицу через метод " +
                                "setAll(); Индекс бита = " + i);
            } else {
                Assertions.assertFalse(bits.get(i),
                        "Метод get() должен возвращать false для бита, для которого в данном случае " +
                                "не вызывался метод setAll().  Индекс бита = " + i);
            }
        }

        bits.fill(0, 12000, true);
        bits.fill(1001, 9702, false);
        int[] emptyArray = new int[0];
        bits.setAll(emptyArray);
        for(int i = 0; i < bits.getSize(); i++) {
            if(i >= 1001 && i < 9702) {
                Assertions.assertFalse(bits.get(i),
                        "Вызов метода setAll() с пустым массивом не должен изменять состояние Bits.");
            } else {
                Assertions.assertTrue(bits.get(i),
                        "Вызов метода setAll() с пустым массивом не должен изменять состояние Bits.");
            }
        }
    }

    @Test
    void clear_get() {
        Bits bits = new Bits(100000);

        for(int i = 0; i < 100000; i++) bits.clear(i);

        for(int i = 0; i < 100000; i++) {
            Assertions.assertFalse(bits.get(i),
                    "Метод Bits::clear(int) вызываемый для индексов со значением false " +
                            "не должен менять их состояния на true.");
        }

        bits.fill(0, 100000, true);

        for(int i = 0; i < 100000; i++) {
            if((i < 12003 || i > 50001) && (i < 90702)) bits.clear(i);
        }

        for(int i = 0; i < 100000; i++) {
            if((i >= 12003 && i <= 50001) || (i >= 90702)) {
                Assertions.assertTrue(bits.get(i),
                        "После вызова Bits::clear(int) метод Bits::get(int) должен возвращать " +
                                "false для всех индексов, для которых вызывался метод Bits::clear(int).");
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
    }

    @Test
    void clearAll() {
        final Bits bits = new Bits(12000);

        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> bits.clearAll(-1, 0, 13, 100000),
                "Метод clearAll должен генерировать исключение, если хотя бы один из переданых ему " +
                        "индексов не удовлетворяте условию index >= 0 && index < currentBits.getSize().");

        bits.fill(0, 12000, true);
        bits.clearAll(0,0,0, 12, 12, 12, 500, 2001);
        for(int i = 0; i < bits.getSize(); i++) {
            if(i == 0 || i == 12 || i == 500 || i == 2001) {
                Assertions.assertFalse(bits.get(i),
                        "Метод get() должен возвращать false для бит установленных в ноль через метод " +
                                "clearAll(); Индекс бита = " + i);
            } else {
                Assertions.assertTrue(bits.get(i),
                        "Метод get() не должен возвращать false, если для указанного бита в данном случае " +
                                "не вызывался метод clearAll(). Индекс бита = " + i);
            }
        }

        bits.fill(0, 12000, true);
        bits.fill(1001, 9702, false);
        int[] emptyArray = new int[0];
        bits.clearAll(emptyArray);
        for(int i = 0; i < bits.getSize(); i++) {
            if(i >= 1001 && i < 9702) {
                Assertions.assertFalse(bits.get(i),
                        "Вызов метода clearAll() с пустым массивом не должен изменять состояние Bits.");
            } else {
                Assertions.assertTrue(bits.get(i),
                        "Вызов метода clearAll() с пустым массивом не должен изменять состояние Bits.");
            }
        }
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
        Bits bits = new Bits(100000);
        for(int i = 0; i < bits.getSize(); i++) {
            if(i >= 12000 && i <= 24375 || i >= 80250 && i <= 99001) bits.set(i);
        }

        bits.clearAll();

        Assertions.assertEquals(100000, bits.getSize(),
                "Вызов метода clearAll() не должен влиять на результат возвращаемый методом getSize().");

        for(int i = 0; i < 100000; i++) {
            Assertions.assertFalse(bits.get(i),
                    "После вызова метода clearAll() метод get() должен возвращать false для всех допустимых " +
                            "значений индексов.");
        }
    }

    @Test
    void fill_get() {
        Bits bits = new Bits(100000);
        bits.fill(12000, 24376, true);
        bits.fill(80250, 99002, true);

        Assertions.assertEquals(100000, bits.getSize(),
                "Вызов метода fill() не должен влиять на результат возвращаемый методом getSize().");
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

        Bits bits2 = new Bits(100000);
        bits2.fill(0, 100000, true);
        bits2.fill(28001, 56012, false);
        Assertions.assertEquals(100000, bits.getSize(),
                "Вызов метода fill() не должен влиять на результат возвращаемый методом getSize().");
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

        Bits bits3 = new Bits(bits2);
        bits3.fill(200, 200, true);
        Assertions.assertEquals(bits2, bits3);

        Assertions.assertThrows(IndexOutOfBoundsException.class, ()-> bits.fill(12, 200000, true),
                "Если хотя бы для одного из индексов задающих интервал заполнения или очистки бит, " +
                        "не соблюдается условие index >= 0 && index < currentBits.getSize().");
        Assertions.assertThrows(IndexOutOfBoundsException.class, ()-> bits.fill(-1, 90000, true),
                "Если хотя бы для одного из индексов задающих интервал заполнения или очистки бит, " +
                        "не соблюдается условие index >= 0 && index < currentBits.getSize().");
        Assertions.assertThrows(IndexOutOfBoundsException.class, ()-> bits.fill(50000, 200, true),
                "Если fromIndex > toIndex - метод fill() должен генерировать исключение.");
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
                "Непосредственно после вызова fill() со значеним флага равным true, " +
                        "метод isEmpty() должен возвращать false.");

        bits.fill(12070, 30450, false);
        Assertions.assertTrue(bits.isEmpty(),
                "Если все биты Bits были очищены с помощью fill() метод isEmpty() должен возвращать true.");
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
        Bits bits = new Bits(100000);
        bits.fill(12000, 24376, true);
        bits.fill(80250, 99002, true);

        bits.expandTo(20000);
        Assertions.assertEquals(100000, bits.getSize(),
                "При вызове expandTo() с аргументом, значение которого меньше значения возвращаемого\n" +
                        "методом getSize(), метод expandTo() не должен изменять состояние объекта Bits2.");
        for(int i = 0; i < 100000; i++) {
            if(i >= 12000 && i <= 24375 || i >= 80250 && i <= 99001)
                Assertions.assertTrue(bits.get(i), "Не верно работает метод expandTo().");
            else
                Assertions.assertFalse(bits.get(i), "Не верно работает метод expandTo().");
        }

        bits.expandTo(200034);
        bits.fill(150000, 200034, true);
        Assertions.assertEquals(200034, bits.getSize(),
                "При вызове expandTo() с аргументом, значение которого больше значения возвращаемого\n" +
                        "методом getSize(), метод expandTo() должен изменить состояние объекта Bits2 и \n" +
                        "метод getSize() должен возвращать значение переданное методу expandTo().");
        for(int i = 0; i < 200034; i++) {
            if(i >= 12000 && i <= 24375 || i >= 80250 && i <= 99001 || i >= 150000)
                Assertions.assertTrue(bits.get(i), "Не верно работает метод expandTo().");
            else
                Assertions.assertFalse(bits.get(i), "Не верно работает метод expandTo().");
        }
    }

    @Test
    void compressTo_expandTo() {
        Bits bits = new Bits(100000);
        bits.fill(12000, 24376, true);
        bits.fill(80250, 99002, true);
        Bits answer = new Bits(100000);
        answer.fill(12000, 24376, true);
        answer.fill(80250, 99002, true);

        bits.compressTo(100001);
        Assertions.assertEquals(100000, bits.getSize(),
                "При вызове compressTo() с аргументом, значение которого больше значения возвращаемого\n" +
                        "методом getSize(), метод compressTo() не должен изменять состояние объекта Bits2.");
        Assertions.assertEquals(answer, bits, "Метод compressTo() не должен изменять состояние объекта Bits2,\n" +
                "если значение аргумента больше или равно getSize().");

        bits.compressTo(24300);
        answer = new Bits(24300);
        answer.fill(12000, 24300, true);
        Assertions.assertEquals(24300, bits.getSize(),
                "При вызове compressTo() с аргументом, значение которого меньше значения возвращаемого\n" +
                        "методом getSize(), метод compressTo() должен изменить состояние объекта Bits2 и \n" +
                        "метод getSize() должен возвращать значение переданное методу compressTo().");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> bits.get(24300),
                "Не верно работает метод compressTo().");
        Assertions.assertEquals(answer, bits, "Не верно работает метод compressTo().");

        bits.expandTo(100000);
        answer = new Bits(100000);
        answer.fill(12000, 24300, true);
        Assertions.assertEquals(answer, bits, "Не верно работает метод compressTo().");

        bits.fill(0, 65, true);
        bits.compressTo(0);
        Assertions.assertEquals(0, bits.getSize(),
                "Не верно работает метод compressTo().");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> bits.get(0),
                "Не верно работает метод compressTo().");
        bits.expandTo(100000);
        answer = new Bits(100000);
        Assertions.assertEquals(answer, bits, "Не верно работает метод compressTo().");
    }

    @Test
    void and_Operands() {
        Bits operand1 = new Bits(100000);

        operand1.fill(20000, 50001, true);
        operand1.and(operand1, operand1);
        Assertions.assertEquals(100000, operand1.getSize(),
                "Если в качестве первого и второго операнда, а также выходного значения передается\n" +
                        "один и тот же объект, то после выполнения метода он не должен изменить своего состояния.");
        for(int i = 0; i < operand1.getSize(); i++) {
            if(i >= 20000 && i <= 50000) {
                Assertions.assertTrue(operand1.get(i),
                        "Если в качестве первого и второгго операнад, а также выходного значения передается\n" +
                                "один и тот же объект, то после выполнения метода он не должен изменить своего состояния.");
            } else {
                Assertions.assertFalse(operand1.get(i),
                        "Если в качестве первого и второгго операнад, а также выходного значения передается\n" +
                                "один и тот же объект, то после выполнения метода он не должен изменить своего состояния.");
            }
        }


        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> operand1.and(operand1, new Bits(20000)),
                "Если размер выходного параметра меньше размера наименьшенго из двух операндов\n" +
                        "метод должен генерировать исключение.");

        Bits out2 = new Bits(200000);
        out2.fill(54000, 199700, true);
        operand1.fill(20000, 50001, true);
        operand1.and(operand1, out2);
        Assertions.assertEquals(200000, out2.getSize(),
                "Вызов метода and() не должен изменять размер выходного параметра.");
        for(int i = 0; i < out2.getSize(); i++) {
            if(i >= 20000 && i <= 50000) {
                Assertions.assertTrue(out2.get(i),"Не верно работает метод and().");
            } else {
                Assertions.assertFalse(out2.get(i),"Не верно работает метод and().");
            }
        }
        out2 = new Bits(200000);
        out2.fill(54000, 199700, true);


        Bits operand2 = new Bits(40000);
        operand2.fill(10000, 30001, true);
        operand1.fill(20000, 50001, true);
        operand1.and(operand2, operand1);
        Assertions.assertEquals(100000, operand1.getSize(),
                "Вызов метода and() не должен изменять размер выходного параметра.");
        for(int i = 0; i < operand1.getSize(); i++) {
            if(i >= 20000 && i <= 30000) {
                Assertions.assertTrue(operand1.get(i),"Не верно работает метод and().");
            } else {
                Assertions.assertFalse(operand1.get(i),"Не верно работает метод and().");
            }
        }

        operand2 = new Bits(200000);
        operand2.fill(10000, 30001, true);
        operand2.fill(110000, 150761, true);
        operand1.fill(20000, 50001, true);
        operand1.and(operand2, operand1);
        Assertions.assertEquals(100000, operand1.getSize(),
                "Вызов метода and() не должен изменять размер выходного параметра.");
        for(int i = 0; i < operand1.getSize(); i++) {
            if(i >= 20000 && i <= 30000) {
                Assertions.assertTrue(operand1.get(i),"Не верно работает метод and().");
            } else {
                Assertions.assertFalse(operand1.get(i),"Не верно работает метод and().");
            }
        }


        operand2 = new Bits(40000);
        operand2.fill(10000, 30001, true);
        operand1.fill(20000, 50001, true);
        operand1.and(operand2, operand2);
        Assertions.assertEquals(40000, operand2.getSize(),
                "Вызов метода and() не должен изменять размер выходного параметра.");
        for(int i = 0; i < operand2.getSize(); i++) {
            if(i >= 20000 && i <= 30000) {
                Assertions.assertTrue(operand2.get(i),"Не верно работает метод and().");
            } else {
                Assertions.assertFalse(operand2.get(i),"Не верно работает метод and().");
            }
        }

        operand2 = new Bits(200000);
        operand2.fill(10000, 30001, true);
        operand2.fill(110000, 150761, true);
        operand1.fill(20000, 50001, true);
        operand1.and(operand2, operand2);
        Assertions.assertEquals(200000, operand2.getSize(),
                "Вызов метода and() не должен изменять размер выходного параметра.");
        for(int i = 0; i < operand2.getSize(); i++) {
            if(i >= 20000 && i <= 30000) {
                Assertions.assertTrue(operand2.get(i),"Не верно работает метод and().");
            } else {
                Assertions.assertFalse(operand2.get(i),"Не верно работает метод and().");
            }
        }


        final Bits operand3 = new Bits(40000);
        final Bits out3 = new Bits(10000);
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> operand1.and(operand3, out3),
                "Если размер выходного параметра меньше размера наименьшенго из двух операндов\n" +
                        "метод должен генерировать исключение.");
        operand2 = new Bits(200000);
        operand2.fill(10000, 30001, true);
        operand2.fill(110000, 150761, true);
        Bits out4 = new Bits(100000);
        out4.fill(0, 100000, true);
        operand1.fill(20000, 50001, true);
        operand1.and(operand2, out4);
        Assertions.assertEquals(100000, out4.getSize(),
                "Вызов метода and() не должен изменять размер выходного параметра.");
        for(int i = 0; i < out4.getSize(); i++) {
            if(i >= 20000 && i <= 30000) {
                Assertions.assertTrue(out4.get(i),"Не верно работает метод and().");
            } else {
                Assertions.assertFalse(out4.get(i),"Не верно работает метод and().");
            }
        }
        out4 = new Bits(300000);
        out4.fill(0, 300000, true);
        operand1.and(operand2, out4);
        for(int i = 0; i < out4.getSize(); i++) {
            if(i >= 20000 && i <= 30000) {
                Assertions.assertTrue(out4.get(i),"Не верно работает метод and().");
            } else {
                Assertions.assertFalse(out4.get(i),"Не верно работает метод and().");
            }
        }
    }

    @Test
    void and_Properties() {
        Bits bits1 = new Bits(100000);
        bits1.fill(20050, 30999, true);
        Bits bits2 = new Bits(100000);
        bits2.fill(20050, 30999, true);
        Bits bits3 = new Bits(100000);
        bits3.fill(30000, 90111, true);
        Bits out = new Bits(100000);
        Bits out2 = new Bits(100000);
        Bits out3 = new Bits(100000);

        bits1.and(bits2, out);
        bits2.and(bits1, out2);
        Assertions.assertEquals(out, out2, "Не соблюдается свойство коммутативности для метода and().");

        bits1.and(bits1, out);
        Assertions.assertEquals(bits1, out, "Не соблюдается свойство идемпотентности для метода and().");

        bits1.and(bits2, out);
        out.and(bits3, out);
        bits1.and(bits3, out2);
        out2.and(bits2, out2);
        bits1.and(bits3, out3);
        out3.and(bits2, out3);
        Assertions.assertTrue(out.equals(out2) && out2.equals(out3) && out.equals(out3),
                "Не соблюдается свойство ассоциативности для метода and().");

        bits3.fill(0, bits3.getSize(), true);
        bits1.and(bits3, out);
        Assertions.assertEquals(bits1, out, "Не соблюдается свойство единицы для метода and().");

        bits3.clearAll();
        bits1.and(bits3, out);
        Assertions.assertEquals(bits3, out, "Не соблюдается свойство нуля для метода and().");
    }

    @Test
    void or_Operands() {
        Bits answer = new Bits(100000);
        answer.fill(20000, 50001, true);
        Bits operand1 = new Bits(100000);
        operand1.fill(20000, 50001, true);
        operand1.or(operand1, operand1);
        Assertions.assertEquals(100000, operand1.getSize(),
                "Если в качестве первого и второго операнда, а также выходного значения передается\n" +
                        "один и тот же объект, то после выполнения метода он не должен изменить своего состояния.");
        Assertions.assertEquals(answer, operand1, "Не верно работает метод or().");


        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> {
                Bits operand = new Bits(100000);
                operand.fill(20000, 50001, true);
                operand.and(operand, new Bits(20000));
            },
            "Если размер выходного параметра меньше размера наибольшего из двух операндов\n" +
                     "метод должен генерировать исключение."
        );


        Bits out = new Bits(200000);
        out.fill(54000, 199700, true);
        operand1 = new Bits(100000);
        operand1.fill(20000, 50001, true);
        answer = new Bits(200000);
        answer.fill(20000, 50001, true);
        operand1.or(operand1, out);
        Assertions.assertEquals(200000, out.getSize(),
                "Вызов метода or() не должен изменять размер выходного параметра.");
        Assertions.assertEquals(answer, out, "Не верно работает метод or().");


        Bits operand2 = new Bits(40000);
        operand2.fill(10000, 30001, true);
        operand1 = new Bits(100000);
        operand1.fill(20000, 50001, true);
        answer = new Bits(100000);
        answer.fill(10000, 50001, true);
        operand1.or(operand2, operand1);
        Assertions.assertEquals(100000, operand1.getSize(),
                "Вызов метода or() не должен изменять размер выходного параметра.");
        Assertions.assertEquals(answer, operand1, "Не верно работает метод or().");


        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> {
                Bits a = new Bits(200000);
                a.fill(110000, 150761, true);
                Bits b = new Bits(100000);
                b.fill(20000, 50001, true);
                b.or(a, b);
            },
            "Если размер выходного параметра меньше размера наибольшего из двух операндов\n" +
                     "метод должен генерировать исключение."
        );


        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> {
                    Bits a = new Bits(40000);
                    a.fill(110000, 150761, true);
                    Bits b = new Bits(100000);
                    b.fill(20000, 50001, true);
                    b.or(a, a);
                },
                "Если размер выходного параметра меньше размера наибольшего из двух операндов\n" +
                        "метод должен генерировать исключение."
        );


        operand2 = new Bits(200000);
        operand2.fill(110000, 150761, true);
        operand1 = new Bits(100000);
        operand1.fill(20000, 50001, true);
        answer = new Bits(200000);
        answer.fill(20000, 50001, true);
        answer.fill(110000, 150761, true);
        operand1.or(operand2, operand2);
        Assertions.assertEquals(200000, operand2.getSize(),
                "Вызов метода or() не должен изменять размер выходного параметра.");
        Assertions.assertEquals(answer, operand2, "Не верно работает метод or().");



        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> {
                Bits a = new Bits(100000);
                Bits b = new Bits(200000);
                Bits c = new Bits(120000);
                a.or(b, c);
            },
            "Если размер выходного параметра меньше размера наибольшего из двух операндов\n" +
                        "метод должен генерировать исключение."
        );


        operand1 = new Bits(100000);
        operand1.fill(20000, 50001, true);
        operand2 = new Bits(200000);
        operand2.fill(10000, 30001, true);
        operand2.fill(110000, 150761, true);
        out = new Bits(300000);
        out.fill(0, 300000, true);
        answer = new Bits(300000);
        answer.fill(10000, 50001, true);
        answer.fill(110000, 150761, true);
        operand1.or(operand2, out);
        Assertions.assertEquals(300000, out.getSize(),
                "Вызов метода or() не должен изменять размер выходного параметра.");
        Assertions.assertEquals(answer, out, "Не верно работает метод or().");
    }

    @Test
    void or_Properties() {
        Bits bits1 = new Bits(100000);
        bits1.fill(20050, 30999, true);
        Bits bits2 = new Bits(100000);
        bits2.fill(20050, 30999, true);
        Bits bits3 = new Bits(100000);
        bits3.fill(30000, 90111, true);
        Bits out = new Bits(100000);
        Bits out2 = new Bits(100000);
        Bits out3 = new Bits(100000);

        bits1.or(bits2, out);
        bits2.or(bits1, out2);
        Assertions.assertEquals(out, out2, "Не соблюдается свойство коммутативности для метода or().");

        bits1.and(bits1, out);
        Assertions.assertEquals(bits1, out, "Не соблюдается свойство идемпотентности для метода or().");

        bits1.or(bits2, out);
        out.or(bits3, out);
        bits1.or(bits3, out2);
        out2.or(bits2, out2);
        bits1.or(bits3, out3);
        out3.or(bits2, out3);
        Assertions.assertTrue(out.equals(out2) && out2.equals(out3) && out.equals(out3),
                "Не соблюдается свойство ассоциативности для метода or().");

        bits3.fill(0, bits3.getSize(), true);
        bits1.or(bits3, out);
        Assertions.assertEquals(bits3, out, "Не соблюдается свойство единицы для метода and().");

        bits3.clearAll();
        bits1.or(bits3, out);
        Assertions.assertEquals(bits1, out, "Не соблюдается свойство нуля для метода and().");
    }

    @Test
    void xor_Operands() {
        Bits bits = new Bits(1000);
        bits.setAll(10, 100, 200, 219, 600, 601, 742, 326);
        Bits expected = new Bits(1000);
        bits.xor(bits, bits);
        Assertions.assertEquals(1000, bits.getSize(),
                "Вызов метода xor() не должен изменять размер выходного параметра.");
        Assertions.assertEquals(expected, bits,
                "Если в качестве первого и второго операнда, а также выходного значения передается\n" +
                        "один и тот же объект, то после выполнения метода должно получиться пустое множество.");

        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> bits.and(bits, new Bits(10)),
                "Если размер выходного параметра меньше размера наибольшего из двух операндов\n" +
                        "метод должен генерировать исключение.");


        Bits singleOperand = new Bits(1000);
        singleOperand.setAll(10, 100, 200, 219, 600, 601, 742, 326);
        Bits out = new Bits(10000);
        out.fill(900, 7020, true);
        Bits expected2 = new Bits(10000);
        singleOperand.xor(singleOperand, out);
        Assertions.assertEquals(10000, out.getSize(),
                "Вызов метода xor() не должен изменять размер выходного параметра.");
        Assertions.assertEquals(expected2, out,
                "Не верно работает метод xor() в случае, если длина выходного параметра больше длины " +
                        "первого и второго аргумента, а вкачестве аргументов выступает один и тот же объект.");


        Bits firstOperand = new Bits(1000);
        firstOperand.setAll(10, 100, 200, 219, 600, 601, 742, 326);
        Bits secondOperand = new Bits(750);
        secondOperand.setAll(10, 100, 201, 220, 600, 601, 743, 326);
        Bits expected3 = new Bits(1000);
        expected3.setAll(200, 201, 219, 220, 742, 743);
        firstOperand.xor(secondOperand, firstOperand);
        Assertions.assertEquals(1000, firstOperand.getSize(),
                "Вызов метода xor() не должен изменять размер выходного параметра.");
        Assertions.assertEquals(expected3, firstOperand,
                "Не верно работает метод xor() в случае, если в качестве операндов вустпыпают разные " +
                        "объекты Bits, а в качестве выходного параметра используется первый операнд.\n" +
                        "первый опернад: " + firstOperand + '\n' +
                        "второй операнд: " + secondOperand);


        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> {
                    Bits operandA = new Bits(100000);
                    operandA.fill(20000, 50001, true);
                    Bits operandB = new Bits(200000);
                    operandB.fill(90000, 180650, true);
                    operandA.xor(operandB, operandA);
                },
                "Если размер выходного параметра меньше размера наибольшего из двух операндов\n" +
                        "метод xor() должен генерировать исключение."
        );


        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> {
                    Bits operandA = new Bits(100000);
                    operandA.fill(20000, 50001, true);
                    Bits operandB = new Bits(50000);
                    operandB.fill(9000, 42000, true);
                    operandA.xor(operandB, operandB);
                },
                "Если размер выходного параметра меньше размера наибольшего из двух операндов\n" +
                        "метод xor() должен генерировать исключение."
        );


        Bits firstOperand2 = new Bits(1000);
        firstOperand2.setAll(10, 100, 200, 219, 600, 601, 742, 326);
        Bits secondOperand2 = new Bits(2000);
        secondOperand2.setAll(10, 100, 201, 220, 600, 601, 743, 326, 1012, 1970);
        Bits expected4 = new Bits(2000);
        expected4.setAll(200, 201, 219, 220, 742, 743, 1012, 1970);
        firstOperand2.xor(secondOperand2, secondOperand2);
        Assertions.assertEquals(2000, secondOperand2.getSize(),
                "Вызов метода xor() не должен изменять размер выходного параметра.");
        Assertions.assertEquals(expected4, secondOperand2,
                "Не верно работает метод xor() в случае, если в качетве опернадов выступают разные объекты " +
                        "Bits, а в качестве выходного параметра выступает вторйо операнд.");


        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> {
                    Bits operandA = new Bits(100000);
                    Bits operandB = new Bits(200000);
                    Bits operandC = new Bits(120000);
                    operandA.xor(operandB, operandC);
                },
                "Если размер выходного параметра меньше размера наибольшего из двух операндов\n" +
                        "метод xor() должен генерировать исключение."
        );


        Bits firstOperand3 = new Bits(1000);
        firstOperand3.setAll(10, 100, 200, 219, 600, 601, 742, 326);
        Bits secondOperand3 = new Bits(2000);
        secondOperand3.setAll(10, 100, 201, 220, 600, 601, 743, 326, 1012, 1970);
        Bits out2 = new Bits(3000);
        out2.fill(0, 3000, true);
        Bits expected5 = new Bits(3000);
        expected5.setAll(200, 201, 219, 220, 742, 743, 1012, 1970);
        firstOperand3.xor(secondOperand3, out2);
        Assertions.assertEquals(3000, out2.getSize(),
                "Вызов метода xor() не должен изменять размер выходного параметра.");
        Assertions.assertEquals(expected5, out2,
                "Не верно работает метод xor() в случае, если в качетстве операндов и выходного " +
                        "параметра выступают разные объекты Bits.");
    }

    @Test
    void xor_Properties() {
        Bits bits = new Bits(1000);
        bits.setAll(10, 100, 200, 219, 600, 601, 742, 326);
        Bits bits2 = new Bits(1000);
        bits2.setAll(10, 100, 201, 220, 600, 601, 743, 326);

        Bits out1 = new Bits(1000);
        Bits out2 = new Bits(1000);
        bits.xor(bits2, out1);
        bits2.xor(bits, out2);
        Assertions.assertEquals(out1, out2,
                "Не выполняеся свойство коммутативности для метода xor().");

        Bits bits3 = new Bits(1000);
        bits3.setAll(200, 201, 219, 220, 744, 745);
        Bits out3 = new Bits(1000);
        Bits out4 = new Bits(1000);
        bits.xor(bits2, out3);
        out3.xor(bits3, out3);
        bits2.xor(bits3, out4);
        out4.xor(bits, out4);
        Assertions.assertEquals(out3, out4,
                "Не выполняется свойство ассоциативности для метода xor().");

        Bits out5 = new Bits(1000);
        bits.xor(bits, out5);
        Assertions.assertEquals(new Bits(1000), out5,
                "При симметричной разности любого объекта Bits с самим собой, " +
                        "должно получиться пустое множество.");
        Bits bits4 = new Bits(bits);
        bits.xor(bits4, out5);
        Assertions.assertEquals(new Bits(1000), out5,
                "При симметричной разности любых равных объектов Bits, " +
                        "должно получиться пустое множество.");

        Bits bits5 = new Bits(1000);
        Bits out6 = new Bits(1000);
        bits.xor(bits5, out6);
        Assertions.assertEquals(bits, out6,
                "пустое множество должно являться нейтральным элементом для метода xor().");
    }

    @Test
    void not_Operands() {
        Bits operand = new Bits(100000);
        operand.fill(20050, 70300, true);
        Bits answer = new Bits(100000);
        answer.fill(0, 20050, true);
        answer.fill(70300, 100000, true);
        operand.not(operand);
        Assertions.assertEquals(answer, operand, "Не верно работает метод not().");

        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> {
                Bits a = new Bits(100000);
                a.fill(20050, 70300, true);
                Bits out = new Bits(500);
                out.fill(0, 500, true);
                a.not(out);
            },
            "Если размер выходного параметра меньше размера операнда, то\n" +
                "метод должен генерировать исключение."
        );

        operand = new Bits(100000);
        operand.fill(20050, 70300, true);
        Bits out = new Bits(200000);
        out.fill(70800, 160404, true);
        answer = new Bits(200000);
        answer.fill(0, 20050, true);
        answer.fill(70300, 100000, true);
        operand.not(out);
        Assertions.assertEquals(answer, out, "Не верно работает метод not().");
    }

    @Test
    void not_Properties() {
        Bits operand = new Bits(100000);
        operand.fill(10000, 37601, true);
        Bits answer = new Bits(100000);
        answer.fill(10000, 37601, true);

        for(int i = 0; i < 100000; i++) {
            operand.not(operand);
        }

        Assertions.assertEquals(answer, operand, "Не выполняется свойство двойного отрицания для метода not().");
    }

    @Test
    void and_singleParameter_Operand() {
        Bits operand1 = new Bits(100000);
        operand1.fill(20000, 50001, true);

        Bits result = operand1.and(operand1);
        Assertions.assertNotSame(result, operand1,
                "Метод and() с одним параметром должен возвращать НОВЫЙ объект в результате выполнения.");
        Assertions.assertEquals(operand1, result,
                "Если метод and() с одним параметром в качестве аргумента получает тот же объект, у " +
                        "которого и вызывается, то результатрирующий объект должен быть равен исходному по " +
                        "методу equals().");

        Bits operand2 = new Bits(40000);
        operand2.fill(10000, 30001, true);
        result = operand1.and(operand2);
        Assertions.assertNotSame(result, operand1,
                "Метод and() с одним параметром должен возвращать НОВЫЙ объект в результате выполнения.");
        Assertions.assertNotSame(result, operand2,
                "Метод and() с одним параметром должен возвращать НОВЫЙ объект в результате выполнения.");
        Bits checkObject = new Bits(40000);
        checkObject.fill(20000, 30001, true);
        Assertions.assertEquals(checkObject, result,
                "Не верно работает метод and() с одним параметром.");

        Bits operand3 = new Bits(200000);
        operand3.fill(40000, 120000, true);
        Bits result2 = operand1.and(operand3);
        Assertions.assertNotSame(result2, operand1,
                "Метод and() с одним параметром должен возвращать НОВЫЙ объект в результате выполнения.");
        Assertions.assertNotSame(result2, operand3,
                "Метод and() с одним параметром должен возвращать НОВЫЙ объект в результате выполнения.");
        Bits expected = new Bits(100000);
        expected.fill(40000, 50001, true);
        Assertions.assertEquals(expected, result2,
                "Не верно работает метод and() с одним параметром в случае, если операнды это разные\n " +
                        "объекты Bits и размер второго операнда больше первого.");
    }

    @Test
    void and_singleParameter_Properties() {
        Bits bits1 = new Bits(100000);
        bits1.fill(20050, 30999, true);
        Bits bits2 = new Bits(100000);
        bits2.fill(20050, 30999, true);
        Bits bits3 = new Bits(100000);
        bits3.fill(30000, 90111, true);

        Bits out = bits1.and(bits2);
        Bits out2 = bits2.and(bits1);
        Assertions.assertEquals(out, out2,
                "Не соблюдается свойство коммутативности для метода and() с одним параметром.");

        out = bits1.and(bits1);
        Assertions.assertEquals(bits1, out,
                "Не соблюдается свойство идемпотентности для метода and() с одним параметром.");

        out = bits1.and(bits2);
        out = out.and(bits3);
        out2 = bits1.and(bits3);
        out2 = out2.and(bits2);
        Bits out3 = bits1.and(bits3);
        out3 = out3.and(bits2);
        Assertions.assertTrue(out.equals(out2) && out2.equals(out3) && out.equals(out3),
                "Не соблюдается свойство ассоциативности для метода and()  с одним параметром.");

        bits3.fill(0, bits3.getSize(), true);
        out = bits1.and(bits3);
        Assertions.assertEquals(bits1, out,
                "Не соблюдается свойство единицы для метода and() с одним параметром.");

        bits3.clearAll();
        out = bits1.and(bits3);
        Assertions.assertEquals(bits3, out,
                "Не соблюдается свойство нуля для метода and() с одним параметром.");
    }

    @Test
    void or_singleParameter_Operand() {
        Bits operand1 = new Bits(100000);
        operand1.fill(20000, 50001, true);

        Bits result = operand1.or(operand1);
        Assertions.assertNotSame(result, operand1,
                "Метод or() с одним параметром должен возвращать НОВЫЙ объект в результате выполнения.");
        Assertions.assertEquals(operand1, result,
                "Если метод or() с одним параметром в качестве аргумента получает тот же объект, у " +
                        "которого и вызывается, то результатрирующий объект должен быть равен исходному по " +
                        "методу equals().");

        Bits operand2 = new Bits(40000);
        operand2.fill(10000, 30001, true);
        result = operand1.or(operand2);
        Assertions.assertNotSame(result, operand1,
                "Метод or() с одним параметром должен возвращать НОВЫЙ объект в результате выполнения.");
        Assertions.assertNotSame(result, operand2,
                "Метод or() с одним параметром должен возвращать НОВЫЙ объект в результате выполнения.");
        Bits checkObject = new Bits(100000);
        checkObject.fill(10000, 50001, true);
        Assertions.assertEquals(checkObject, result,
                "Не верно работает метод or() с одним параметром в случае, если операнды это разные " +
                        "объекты Bits и размер второго операнда меньше первого.");

        Bits operand3 = new Bits(200000);
        operand3.fill(40000, 120000, true);
        Bits result2 = operand1.or(operand3);
        Assertions.assertNotSame(result2, operand1,
                "Метод or() с одним параметром должен возвращать НОВЫЙ объект в результате выполнения.");
        Assertions.assertNotSame(result2, operand3,
                "Метод or() с одним параметром должен возвращать НОВЫЙ объект в результате выполнения.");
        Bits expected = new Bits(200000);
        expected.fill(20000, 120000, true);
        Assertions.assertEquals(expected, result2,
                "Не верно работает метод or() с одним параметром в случае, если операнды это разные " +
                        "объекты Bits и размер второго операнда больше первого.");
    }

    @Test
    void or_singleParameter_Properties() {
        Bits bits1 = new Bits(100000);
        bits1.fill(20050, 30999, true);
        Bits bits2 = new Bits(100000);
        bits2.fill(20050, 30999, true);
        Bits bits3 = new Bits(100000);
        bits3.fill(30000, 90111, true);

        Bits out = bits1.or(bits2);
        Bits out2 = bits2.or(bits1);
        Assertions.assertEquals(out, out2,
                "Не соблюдается свойство коммутативности для метода or() с одним параметром.");

        out = bits1.and(bits1);
        Assertions.assertEquals(bits1, out,
                "Не соблюдается свойство идемпотентности для метода or() с одним параметром.");

        out = bits1.or(bits2);
        out = out.or(bits3);
        out2 = bits1.or(bits3);
        out2 = out2.or(bits2);
        Bits out3 = bits1.or(bits3);
        out3 = out3.or(bits2);
        Assertions.assertTrue(out.equals(out2) && out2.equals(out3) && out.equals(out3),
                "Не соблюдается свойство ассоциативности для метода or() с одним параметром.");

        bits3.fill(0, bits3.getSize(), true);
        out = bits1.or(bits3);
        Assertions.assertEquals(bits3, out,
                "Не соблюдается свойство единицы для метода or() с одним параметром.");

        bits3.clearAll();
        out = bits1.or(bits3);
        Assertions.assertEquals(bits1, out,
                "Не соблюдается свойство нуля для метода or() с одним параметром.");
    }

    @Test
    void xor_singleParameter_Operand() {
        Bits operand1 = new Bits(1000);
        operand1.setAll(10, 100, 200, 219, 600, 601, 742, 326);

        Bits result = operand1.xor(operand1);
        Assertions.assertNotSame(result, operand1,
                "Метод xor() с одним параметром должен возвращать НОВЫЙ объект Bits.");
        Assertions.assertEquals(new Bits(1000), result,
                "При вызове метода xor(), где в качестве аргмента передается один и тот же объект, " +
                        "метод должен вернуть пустой объект Bits того же размера, что и объект у которого " +
                        "он вызывается.");

        Bits operand2 = new Bits(750);
        operand2.setAll(10, 100, 201, 220, 600, 601, 743, 326);
        Bits expected = new Bits(1000);
        expected.setAll(200, 201, 219, 220, 742, 743);
        Bits result2 = operand1.xor(operand2);
        Assertions.assertNotSame(result2, operand1,
                "Метод xor() с одним параметром должен возвращать НОВЫЙ объект Bits.");
        Assertions.assertNotSame(result2, operand2,
                "Метод xor() с одним параметром должен возвращать НОВЫЙ объект Bits.");
        Assertions.assertEquals(expected, result2,
                "Не верно работает метод xor() с одним параметром в случае, если в качестве поерандов " +
                        "выступают разные объекты Bits и второй операнд меньше по размеру первого.");

        Bits operand3 = new Bits(2000);
        operand3.setAll(10, 100, 201, 220, 600, 601, 743, 326, 1001, 1200, 1317, 1500, 1902);
        Bits expected2 = new Bits(2000);
        expected2.setAll(200, 201, 219, 220, 742, 743, 1001, 1200, 1317, 1500, 1902);
        Bits result3 = operand1.xor(operand3);
        Assertions.assertNotSame(result3, operand1,
                "Метод xor() с одним параметром должен возвращать НОВЫЙ объект Bits.");
        Assertions.assertNotSame(result3, operand3,
                "Метод xor() с одним параметром должен возвращать НОВЫЙ объект Bits.");
        Assertions.assertEquals(expected2, result3,
                "Не верно работает метод xor() с одним параметром в случае, если в качестве операндов " +
                        "выступают разные объекты Bits и второй операнд больше по размеру первого.");
    }

    @Test
    void xor_singleParameter_Properties() {
        Bits bits = new Bits(1000);
        bits.setAll(10, 100, 200, 219, 600, 601, 742, 326);
        Bits bits2 = new Bits(1000);
        bits2.setAll(10, 100, 201, 220, 600, 601, 743, 326);

        Bits out1 = bits.xor(bits2);
        Bits out2 = bits2.xor(bits);
        Assertions.assertEquals(out1, out2,
                "Не выполняеся свойство коммутативности для метода xor().");

        Bits bits3 = new Bits(1000);
        bits3.setAll(200, 201, 219, 220, 744, 745);
        Bits out3 = bits.xor(bits2);
        out3 = out3.xor(bits3);
        Bits out4 = bits2.xor(bits3);
        out4 = out4.xor(bits);
        Assertions.assertEquals(out3, out4,
                "Не выполняется свойство ассоциативности для метода xor().");

        Bits out5 = bits.xor(bits);
        Assertions.assertEquals(new Bits(1000), out5,
                "При симметричной разности любого объекта Bits с самим собой, " +
                        "должно получиться пустое множество.");
        Bits bits4 = new Bits(bits);
        out5 = bits.xor(bits4);
        Assertions.assertEquals(new Bits(1000), out5,
                "При симметричной разности любых равных объектов Bits, " +
                        "должно получиться пустое множество.");

        Bits bits5 = new Bits(1000);
        Bits out6 = bits.xor(bits5);
        Assertions.assertEquals(bits, out6,
                "пустое множество должно являться нейтральным элементом для метода xor().");
    }

    @Test
    void notWithSingleParameter_Operand() {
        Bits operand = new Bits(100000);
        operand.fill(20000, 50001, true);
        Bits result = operand.not();

        Assertions.assertNotSame(operand, result,
                "Метод not() без параметров должен возвращать НОВЫЙ объект в результате выполнения.");

        Bits checkObject = new Bits(100000);
        checkObject.fill(0, 20000, true);
        checkObject.fill(50001, 100000, true);
        Assertions.assertEquals(checkObject, result,
                "Не верно работает метод not() без параметров.");
    }

    @Test
    void notWithParameter_Properties() {
        Bits operand = new Bits(100000);
        operand.fill(10000, 37601, true);
        Bits answer = new Bits(100000);
        answer.fill(10000, 37601, true);

        for(int i = 0; i < 100000; i++) operand = operand.not();

        Assertions.assertEquals(answer, operand, "Не выполняется свойство двойного отрицания для метода not().");
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

        Assertions.assertTrue(!bits1.equals(bits2) && !bits2.equals(bits3) && !bits1.equals(bits3),
                "Не соблюдается свойство транзитивности для сетода equals().");

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