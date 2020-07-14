package com.bakuard.ecsEngine;

import com.bakuard.ecsEngine.utils.IntArray;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;
import java.util.Random;

class IntArrayTest {

    @Test
    public void constructor() {
        IntArray array = new IntArray(100);
        for(int i = 0; i < array.getLength(); i++) {
            Assertions.assertEquals(0, array.get(i));
        }
        Assertions.assertEquals(100, array.getLength());
        Assertions.assertDoesNotThrow(() -> array.set(99, 1200));

        Assertions.assertThrows(IllegalArgumentException.class, () ->
                new IntArray(-1));

        IntArray array2 = new IntArray(0);
        Assertions.assertEquals(0, array2.getLength());
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> array2.set(0, 1200));
    }

    @Test
    public void get() {
        IntArray array = new IntArray(10);

        Assertions.assertEquals(0, array.get(0));
        array.set(0, 1200);
        Assertions.assertEquals(1200, array.get(0));
        array.set(0, 0);
        Assertions.assertEquals(0, array.get(0));

        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> array.get(-1));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> array.get(10));
    }

    @Test
    public void set() {
        IntArray array = new IntArray(10);

        Assertions.assertEquals(0, array.set(7, 1200));
        Assertions.assertEquals(1200, array.get(7));

        Assertions.assertEquals(1200, array.set(7, 0));
        Assertions.assertEquals(0, array.get(7));

        Assertions.assertEquals(0, array.set(7, 1300));
        Assertions.assertEquals(1300, array.get(7));

        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> array.set(-1, 1200));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> array.set(10, 1200));
    }

    @Test
    public void setAndExpand() {
        IntArray array = new IntArray(10);

        Assertions.assertEquals(0, array.setAndExpand(7, 1200));
        Assertions.assertEquals(1200, array.get(7));

        Assertions.assertEquals(1200, array.setAndExpand(7, 0));
        Assertions.assertEquals(0, array.get(7));

        Assertions.assertEquals(0, array.setAndExpand(7, 1300));
        Assertions.assertEquals(1300, array.get(7));

        Assertions.assertEquals(0, array.setAndExpand(112, 1400));
        Assertions.assertEquals(1400, array.get(112));
        for(int i = 10; i < 112; i++)
            Assertions.assertEquals(0, array.get(i),
                    "При расширении массива при применении метода putAndExpand, " +
                            "новые значения между указанным индексом и и инедксом равным предыдущей длине " +
                            "массива, должны быть равны null.");
        Assertions.assertEquals(1300, array.get(7),
                "При расширении массива при применении метода putAndExpand, " +
                        "старые занчения должны сохраняться.");
        Assertions.assertEquals(113, array.getLength());

        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> array.setAndExpand(-1, 1200));
    }

    @Test
    public void add() {
        IntArray array = new IntArray(10);

        for(int i = 0; i < 100; i++) array.add(i);
        for(int i = 0; i < 10; i++) Assertions.assertEquals(0, array.get(i));
        for(int i = 10; i < array.getLength(); i++) Assertions.assertEquals(i - 10, array.get(i));
        Assertions.assertEquals(110, array.getLength());

        IntArray array2 = new IntArray(0);
        for(int i = 0; i < 10; i++) array2.add(i);
        Assertions.assertEquals(10, array2.getLength());
    }

    @Test
    public void addAll() {
        IntArray array = new IntArray(0);
        array.addAll(1,2,3,4,5,6);
        IntArray pattern2 = new IntArray(6);
        pattern2.set(0, 1);
        pattern2.set(1, 2);
        pattern2.set(2, 3);
        pattern2.set(3, 4);
        pattern2.set(4, 5);
        pattern2.set(5, 6);
        Assertions.assertEquals(pattern2, array,
                "Не верно работает метод addAll() при добавлении элементов в пустой массив.");

        array.addAll(10,20,30,40,50);
        IntArray pattern3 = new IntArray(11);
        pattern3.set(0, 1);
        pattern3.set(1, 2);
        pattern3.set(2, 3);
        pattern3.set(3, 4);
        pattern3.set(4, 5);
        pattern3.set(5, 6);
        pattern3.set(6, 10);
        pattern3.set(7, 20);
        pattern3.set(8, 30);
        pattern3.set(9, 40);
        pattern3.set(10, 50);
        Assertions.assertEquals(pattern3, array,
                "Не верно работает метод addAll() при добавлении элементов в не пустой массив.");

        array.addAll(1000);
        IntArray pattern4 = new IntArray(12);
        pattern4.set(0, 1);
        pattern4.set(1, 2);
        pattern4.set(2, 3);
        pattern4.set(3, 4);
        pattern4.set(4, 5);
        pattern4.set(5, 6);
        pattern4.set(6, 10);
        pattern4.set(7, 20);
        pattern4.set(8, 30);
        pattern4.set(9, 40);
        pattern4.set(10, 50);
        pattern4.set(11, 1000);
        Assertions.assertEquals(pattern4, array,
                "Не верно работает метод addAll() при попытке добавить через него один элемент.");

        array.addAll();
        int[] emptyArray = new int[0];
        array.addAll(emptyArray);
        Assertions.assertEquals(pattern4, array,
                "Метод addAll() при вызове без аргументов или с пустым массивом не должен оказывать\n " +
                        "никакого эффекта.");

        IntArray array2 = new IntArray( 0);
        array2.addAll(1000);
        IntArray pattern5 = new IntArray(1);
        pattern5.set(0, 1000);
        Assertions.assertEquals(pattern5, array2,
                "Не верно работает метод addAll() при попытке добавить через него один элемент\n " +
                        "в пустой массив.");
    }

    @Test
    public void addAll_Array() {
        IntArray to = new IntArray(0);
        IntArray from = new IntArray(0);
        from.addAll(1,2,3,4,5,6,7,8,9,10);
        to.addAll(from);
        IntArray pattern1 = new IntArray(0);
        pattern1.addAll(1,2,3,4,5,6,7,8,9,10);
        Assertions.assertEquals(pattern1, to,
                "Не верно работает метод addAll(IntArray) при добавлении элементов в пустой массив.");

        IntArray to2 = new IntArray(0);
        to2.addAll(1,2,3,4,5,6,7,8,9,10);
        IntArray from2 = new IntArray(0);
        from2.addAll(11,12,13,14,15,16,17);
        to2.addAll(from2);
        IntArray pattern2 = new IntArray(0);
        pattern2.addAll(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17);
        Assertions.assertEquals(pattern2, to2,
                "Не верно работает метод addAll(IntArray) при добавлении элементов в не пустой массив.");

        IntArray to3 = new IntArray(0);
        to3.addAll(1,2,3,4,5,6,7,8,9,10);
        IntArray from3 = new IntArray(1);
        from3.set(0, 1000);
        to3.addAll(from3);
        IntArray pattern3 = new IntArray(0);
        pattern3.addAll(1,2,3,4,5,6,7,8,9,10,1000);
        Assertions.assertEquals(pattern3, to3,
                "Не верно работает метод addAll(IntArray) при попытке добавить через него один элемент.");

        IntArray to4 = new IntArray(0);
        to4.addAll(1,2,3,4,5,6,7,8,9,10);
        IntArray from4 = new IntArray(0);
        to4.addAll(from4);
        IntArray pattern4 = new IntArray(0);
        pattern4.addAll(1,2,3,4,5,6,7,8,9,10);
        Assertions.assertEquals(pattern4, to4,
                "Метод addAll(IntArray) при вызове с пустым массивом в качестве аргумента " +
                        "не должен оказывать никакого эффекта.");

        IntArray to5 = new IntArray(0);
        IntArray from5 = new IntArray(1);
        from5.set(0, 1000);
        to5.addAll(from5);
        IntArray pattern5 = new IntArray(1);
        pattern5.set(0, 1000);
        Assertions.assertEquals(pattern5, to5,
                "Не верно работает метод addAll(IntArray) при попытке добавить через него один элемент\n " +
                        "в пустой массив.");
    }

    @Test
    public void insert() {
        IntArray array = new IntArray(0);

        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> array.insert(-1, 10),
                "Метод add(T value, int index) должен проверять параметр index на\n " +
                        "принадлженость диапозону.");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> array.insert(1, 10),
                "Метод add(T value, int index) должен проверять параметр index на\n " +
                        "принадлженость диапозону.");
        array.insert(0, 10);
        Assertions.assertEquals(10, array.get(0),
                "После вызова метода Метод add(T value, int index), метод get() должен\n " +
                        "возвращать по индексу переданному методу add(T value, int index) значение,\n " +
                        "которое было передан этому методу.");

        array.addAll(20, 30, 40, 0, 60, 70);
        array.insert(0, 0);
        IntArray pattern = new IntArray(0);
        pattern.addAll(0, 10, 20, 30, 40, 0, 60, 70);
        Assertions.assertEquals(pattern, array,
                "Метод add(T value, int index) при добавлении элемента в начало массива должен\n " +
                        "сдвигать все элементы вверх с сохранением их взаимного расположения и увеличить\n " +
                        "длину массива на единицу.");

        array.insert(array.getLength(), 0);
        IntArray pattern2 = new IntArray(0);
        pattern2.addAll(0, 10, 20, 30, 40, 0, 60, 70, 0);
        Assertions.assertEquals(pattern2, array,
                "Метод add(T value, int index) при добавлении элемента в конец массива.");
    }

    @Test
    public void binaryInsert_ArrayIsEmptyOrContainsOneItem() {
        IntArray emptyArray = new IntArray(0);
        IntArray pattern = new IntArray(1);
        pattern.set(0, 100);
        emptyArray.binaryInsert(100, Integer::compare);
        Assertions.assertEquals(pattern, emptyArray,
                "Не верно работает метод binaryInsert в случае, когда исходный массив пуст.");

        IntArray arrayWithOneItem = new IntArray(1);
        arrayWithOneItem.set(0, 100);
        IntArray pattern2 = new IntArray(2);
        pattern2.set(0, 10);
        pattern2.set(1, 100);
        arrayWithOneItem.binaryInsert(10, Integer::compare);
        Assertions.assertEquals(pattern2, arrayWithOneItem,
                "Не верно работает метод binaryInsert в случае, когда исходный массив имеет один\n " +
                        "элемент и добавляемый элемент меньше его.");

        IntArray arrayWithOneItem2 = new IntArray(1);
        arrayWithOneItem2.set(0, 100);
        IntArray pattern3 = new IntArray(2);
        pattern3.set(0, 100);
        pattern3.set(1, 1000);
        arrayWithOneItem2.binaryInsert(1000, Integer::compare);
        Assertions.assertEquals(pattern3, arrayWithOneItem2,
                "Не верно работает метод binaryInsert в случае, когда исходный массив имеет один\n " +
                        "элемент и добавляемый элемент больше его.");

        IntArray arrayWithOneItem3 = new IntArray(1);
        arrayWithOneItem3.set(0, 100);
        IntArray pattern4 = new IntArray(2);
        pattern4.set(0, 100);
        pattern4.set(1, 100);
        arrayWithOneItem3.binaryInsert(100, Integer::compare);
        Assertions.assertEquals(pattern4, arrayWithOneItem3,
                "Не верно работает метод binaryInsert в случае, когда исходный массив имеет один\n " +
                        "элемент и добавляемый элемент равен ему.");
    }

    @Test
    public void binaryInsert_ArrayContainsTwoItem() {
        IntArray array = new IntArray(0);
        array.addAll(10, 100);
        IntArray pattern = new IntArray(0);
        pattern.addAll(2, 10, 100);
        array.binaryInsert(2, Integer::compare);
        Assertions.assertEquals(pattern , array,
                "Не верно работает метод binaryInsert(), когда кол-во элементов массива равно двум,\n " +
                        "порядок возрастающий, добавляемый элемент меньше наименьшего.");

        IntArray array2 = new IntArray(0);
        array2.addAll(10, 100);
        IntArray pattern2 = new IntArray(0);
        pattern2.addAll(10, 60, 100);
        array2.binaryInsert(60, Integer::compare);
        Assertions.assertEquals(pattern2 , array2,
                "Не верно работает метод binaryInsert(), когда кол-во элементов массива равно двум,\n " +
                        "порядок возрастающий, добавляемый элемент должен встать посередине.");

        IntArray array3 = new IntArray(0);
        array3.addAll(10, 100);
        IntArray pattern3 = new IntArray(0);
        pattern3.addAll(10, 100, 500);
        array3.binaryInsert(500, Integer::compare);
        Assertions.assertEquals(pattern3 , array3,
                "Не верно работает метод binaryInsert(), когда кол-во элементов массива равно двум,\n " +
                        "порядок возрастающий, добавляемый элемент больше наибольшего в массиве.");

        IntArray array4 = new IntArray(0);
        array4.addAll(10, 100);
        IntArray pattern4 = new IntArray(0);
        pattern4.addAll(10, 10, 100);
        array4.binaryInsert(10, Integer::compare);
        Assertions.assertEquals(pattern4, array4,
                "Не верно работает метод binaryInsert(), когда кол-во элементов массива равно двум,\n " +
                        "порядок возрастающий, добавляемый элемент уже присутсвует в начале массива один раз.");

        IntArray array5 = new IntArray(0);
        array5.addAll(10, 100);
        IntArray pattern5 = new IntArray(0);
        pattern5.addAll(10, 100, 100);
        array5.binaryInsert(100, Integer::compare);
        Assertions.assertEquals(pattern5, array5,
                "Не верно работает метод binaryInsert(), когда кол-во элементов массива равно двум,\n " +
                        "порядок возрастающий, добавляемый элемент уже присутсвует в конце массива один раз.");

        IntArray array6 = new IntArray( 0);
        array6.addAll(10, 10);
        IntArray pattern6 = new IntArray(0);
        pattern6.addAll(10, 10, 10);
        array6.binaryInsert(10, Integer::compare);
        Assertions.assertEquals(pattern6, array6,
                "Не верно работает метод binaryInsert(), когда кол-во элементов массива равно двум,\n " +
                        "порядок возрастающий, все элемнты массива одинаковы и добавляемый элемент присутсвует.");
    }

    @Test
    public void binaryInsert_NumberOfItemsIsEvenAndMoreThenTwo() {
        IntArray array = new IntArray(1000);
        for(int i = 0; i < array.getLength(); i++) array.set(i, i * 10);
        IntArray pattern = new IntArray(1001);
        for(int i = 0; i < pattern.getLength(); i++) pattern.set(i, (i - 1) * 10);
        array.binaryInsert(-10, Integer::compare);
        Assertions.assertEquals(pattern, array,
                "Не верно работает метод binaryInsert(), если кол-во элементов массива\n " +
                        "больше двух и четно, порядок возрастающий, добавляемый элемент отсутсвует\n " +
                        "и меньше наименьшего элемента в массиве.");

        IntArray array2 = new IntArray(1000);
        for(int i = 0; i < array2.getLength(); i++) array2.set(i, i * 10);
        IntArray pattern2 = new IntArray(1000);
        for(int i = 0; i < pattern2.getLength(); i++) pattern2.set(i, i * 10);
        pattern2.insert(556, 5557);
        array2.binaryInsert(5557, Integer::compare);
        Assertions.assertEquals(pattern2, array2,
                "Не верно работает метод binaryInsert(), если кол-во элементов массива\n " +
                        "больше двух и четно, порядок возрастающий, добавляемый элемент отсутсвует\n " +
                        "и должен быть добавлен где-то в средину массива.");

        IntArray array3 = new IntArray(1000);
        for(int i = 0; i < array3.getLength(); i++) array3.set(i, i * 10);
        IntArray pattern3 = new IntArray(1001);
        for(int i = 0; i < pattern3.getLength(); i++) pattern3.set(i, i * 10);
        array3.binaryInsert(10000, Integer::compare);
        Assertions.assertEquals(pattern3, array3,
                "Не верно работает метод binaryInsert(), если кол-во элементов массива\n " +
                        "больше двух и четно, порядок возрастающий, добавляемый элемент отсутсвует\n " +
                        "и больше наибольшего элемента в массиве.");

        IntArray array4 = new IntArray(1000);
        for(int i = 0; i < array4.getLength(); i++) array4.set(i, i * 10);
        IntArray pattern4 = new IntArray(1000);
        for(int i = 0; i < pattern4.getLength(); i++) pattern4.set(i, i * 10);
        pattern4.insert(0, 0);
        array4.binaryInsert(0, Integer::compare);
        Assertions.assertEquals(pattern4, array4,
                "Не верно работает метод binaryInsert(), если кол-во элементов массива\n " +
                        "больше двух и четно, порядок возрастающий, добавляемый элемент присутсвует\n " +
                        "один раз и находится в начале массива.");

        IntArray array5 = new IntArray(1000);
        for(int i = 0; i < array5.getLength(); i++) array5.set(i, i * 10);
        IntArray pattern5 = new IntArray(1000);
        for(int i = 0; i < pattern5.getLength(); i++) pattern5.set(i, i * 10);
        pattern5.insert(556, 5557);
        array5.binaryInsert(5557, Integer::compare);
        Assertions.assertEquals(pattern5, array5,
                "Не верно работает метод binaryInsert(), если кол-во элементов массива\n " +
                        "больше двух и четно, порядок возрастающий, добавляемый элемент присутсвует\n " +
                        "один раз и находится где-то в середине массива.");

        IntArray array6 = new IntArray(1000);
        for(int i = 0; i < array6.getLength(); i++) array6.set(i, i * 10);
        IntArray pattern6 = new IntArray(1000);
        for(int i = 0; i < pattern6.getLength(); i++) pattern6.set(i, i * 10);
        pattern6.insert(1000, 9990);
        array6.binaryInsert(9990, Integer::compare);
        Assertions.assertEquals(pattern6, array6,
                "Не верно работает метод binaryInsert(), если кол-во элементов массива\n " +
                        "больше двух и четно, порядок возрастающий, добавляемый элемент присутсвует\n " +
                        "один раз и находится в конце массива.");

        IntArray array7 = new IntArray(1000);
        for(int i = 0; i < array7.getLength(); i++) {
            if(i < 56) array7.set(i, 0);
            else array7.set(i, i * 10);
        }
        IntArray pattern7 = new IntArray( 1000);
        for(int i = 0; i < pattern7.getLength(); i++) {
            if(i < 56) pattern7.set(i, 0);
            else pattern7.set(i, i * 10);
        }
        pattern7.insert(0, 0);
        array7.binaryInsert(0, Integer::compare);
        Assertions.assertEquals(pattern7, array7,
                "Не верно работает метод binaryInsert(), если кол-во элементов массива\n " +
                        "больше двух и четно, порядок возрастающий, добавляемый элемент присутсвует\n " +
                        "несколько раз и последовательность находится в начале массива.");

        IntArray array8 = new IntArray(1000);
        for(int i = 0; i < array8.getLength(); i++) {
            if(i > 200 && i < 312) array8.set(i, 2000);
            else array8.set(i, i * 10);
        }
        IntArray pattern8 = new IntArray(1000);
        for(int i = 0; i < pattern8.getLength(); i++) {
            if(i > 200 && i < 312) pattern8.set(i, 2000);
            else pattern8.set(i, i * 10);
        }
        pattern8.insert(255, 2000);
        array8.binaryInsert(2000, Integer::compare);
        Assertions.assertEquals(pattern8, array8,
                "Не верно работает метод binaryInsert(), если кол-во элементов массива\n " +
                        "больше двух и четно, порядок возрастающий, добавляемый элемент присутсвует\n " +
                        "несколько раз и последовательность находится где-то в середине массива.");

        IntArray array9 = new IntArray(1000);
        for(int i = 0; i < array9.getLength(); i++) {
            if(i > 870) array9.set(i, 8710);
            else array9.set(i, i * 10);
        }
        IntArray pattern9 = new IntArray(1000);
        for(int i = 0; i < pattern9.getLength(); i++) {
            if(i > 870) pattern9.set(i, 8710);
            else pattern9.set(i, i * 10);
        }
        pattern9.insert(877, 8710);
        array9.binaryInsert(8710, Integer::compare);
        Assertions.assertEquals(pattern9, array9,
                "Не верно работает метод binaryInsert(), если кол-во элементов массива\n " +
                        "больше двух и четно, порядок возрастающий, добавляемый элемент присутсвует\n " +
                        "несколько раз и последовательность находится в конце массива.");

        IntArray array10 = new IntArray(1000);
        for(int i = 0; i < array10.getLength(); i++) array10.set(i, 10);
        IntArray pattern10 = new IntArray(1001);
        for(int i = 0; i < pattern10.getLength(); i++) pattern10.set(i, 10);
        array10.binaryInsert(10, Integer::compare);
        Assertions.assertEquals(pattern10, array10,
                "Не верно работает метод binaryInsert(), если кол-во элементов массива\n " +
                        "больше двух и четно, порядок возрастающий, добавляемый элемент присутсвует\n " +
                        "и все элементы массива одинаковы.");
    }

    @Test
    public void binaryInsert_NumberOfItemsIsOddAndMoreThenTwo() {
        IntArray array = new IntArray(1001);
        for(int i = 0; i < array.getLength(); i++) array.set(i, i * 10);
        IntArray pattern = new IntArray(1002);
        for(int i = 0; i < pattern.getLength(); i++) pattern.set(i, (i - 1) * 10);
        array.binaryInsert(-10, Integer::compare);
        Assertions.assertEquals(pattern, array,
                "Не верно работает метод binaryInsert(), если кол-во элементов массива\n " +
                        "больше двух и не четно, порядок возрастающий, добавляемый элемент отсутсвует\n " +
                        "и меньше наименьшего элемента в массиве.");

        IntArray array2 = new IntArray(1001);
        for(int i = 0; i < array2.getLength(); i++) array2.set(i, i * 10);
        IntArray pattern2 = new IntArray(1001);
        for(int i = 0; i < pattern2.getLength(); i++) pattern2.set(i, i * 10);
        pattern2.insert(556, 5557);
        array2.binaryInsert(5557, Integer::compare);
        Assertions.assertEquals(pattern2, array2,
                "Не верно работает метод binaryInsert(), если кол-во элементов массива\n " +
                        "больше двух и не четно, порядок возрастающий, добавляемый элемент отсутсвует\n " +
                        "и должен быть добавлен где-то в средину массива.");

        IntArray array3 = new IntArray(1001);
        for(int i = 0; i < array3.getLength(); i++) array3.set(i, i * 10);
        IntArray pattern3 = new IntArray(1002);
        for(int i = 0; i < pattern3.getLength(); i++) pattern3.set(i, i * 10);
        array3.binaryInsert(10010, Integer::compare);
        Assertions.assertEquals(pattern3, array3,
                "Не верно работает метод binaryInsert(), если кол-во элементов массива\n " +
                        "больше двух и не четно, порядок возрастающий, добавляемый элемент отсутсвует\n " +
                        "и больше наибольшего элемента в массиве.");

        IntArray array4 = new IntArray(1001);
        for(int i = 0; i < array4.getLength(); i++) array4.set(i, i * 10);
        IntArray pattern4 = new IntArray(1001);
        for(int i = 0; i < pattern4.getLength(); i++) pattern4.set(i, i * 10);
        pattern4.insert(0, 0);
        array4.binaryInsert(0, Integer::compare);
        Assertions.assertEquals(pattern4, array4,
                "Не верно работает метод binaryInsert(), если кол-во элементов массива\n " +
                        "больше двух и не четно, порядок возрастающий, добавляемый элемент присутсвует\n " +
                        "один раз и находится в начале массива.");

        IntArray array5 = new IntArray(1001);
        for(int i = 0; i < array5.getLength(); i++) array5.set(i, i * 10);
        IntArray pattern5 = new IntArray(1001);
        for(int i = 0; i < pattern5.getLength(); i++) pattern5.set(i, i * 10);
        pattern5.insert(556, 5557);
        array5.binaryInsert(5557, Integer::compare);
        Assertions.assertEquals(pattern5, array5,
                "Не верно работает метод binaryInsert(), если кол-во элементов массива\n " +
                        "больше двух и не четно, порядок возрастающий, добавляемый элемент присутсвует\n " +
                        "один раз и находится где-то в середине массива.");

        IntArray array6 = new IntArray(1001);
        for(int i = 0; i < array6.getLength(); i++) array6.set(i, i * 10);
        IntArray pattern6 = new IntArray(1001);
        for(int i = 0; i < pattern6.getLength(); i++) pattern6.set(i, i * 10);
        pattern6.insert(1001, 10000);
        array6.binaryInsert(10000, Integer::compare);
        Assertions.assertEquals(pattern6, array6,
                "Не верно работает метод binaryInsert(), если кол-во элементов массива\n " +
                        "больше двух и не четно, порядок возрастающий, добавляемый элемент присутсвует\n " +
                        "один раз и находится в конце массива.");

        IntArray array7 = new IntArray(1001);
        for(int i = 0; i < array7.getLength(); i++) {
            if(i < 56) array7.set(i, 0);
            else array7.set(i, i * 10);
        }
        IntArray pattern7 = new IntArray(1001);
        for(int i = 0; i < pattern7.getLength(); i++) {
            if(i < 56) pattern7.set(i, 0);
            else pattern7.set(i, i * 10);
        }
        pattern7.insert(0, 0);
        array7.binaryInsert(0, Integer::compare);
        Assertions.assertEquals(pattern7, array7,
                "Не верно работает метод binaryInsert(), если кол-во элементов массива\n " +
                        "больше двух и не четно, порядок возрастающий, добавляемый элемент присутсвует\n " +
                        "несколько раз и последовательность находится в начале массива.");

        IntArray array8 = new IntArray(1001);
        for(int i = 0; i < array8.getLength(); i++) {
            if(i > 200 && i < 312) array8.set(i, 2000);
            else array8.set(i, i * 10);
        }
        IntArray pattern8 = new IntArray(1001);
        for(int i = 0; i < pattern8.getLength(); i++) {
            if(i > 200 && i < 312) pattern8.set(i, 2000);
            else pattern8.set(i, i * 10);
        }
        pattern8.insert(255, 2000);
        array8.binaryInsert(2000, Integer::compare);
        Assertions.assertEquals(pattern8, array8,
                "Не верно работает метод binaryInsert(), если кол-во элементов массива\n " +
                        "больше двух и не четно, порядок возрастающий, добавляемый элемент присутсвует\n " +
                        "несколько раз и последовательность находится где-то в середине массива.");

        IntArray array9 = new IntArray(1001);
        for(int i = 0; i < array9.getLength(); i++) {
            if(i > 870) array9.set(i, 8710);
            else array9.set(i, i * 10);
        }
        IntArray pattern9 = new IntArray(1001);
        for(int i = 0; i < pattern9.getLength(); i++) {
            if(i > 870) pattern9.set(i, 8710);
            else pattern9.set(i, i * 10);
        }
        pattern9.insert(877, 8710);
        array9.binaryInsert(8710, Integer::compare);
        Assertions.assertEquals(pattern9, array9,
                "Не верно работает метод binaryInsert(), если кол-во элементов массива\n " +
                        "больше двух и не четно, порядок возрастающий, добавляемый элемент присутсвует\n " +
                        "несколько раз и последовательность находится в конце массива.");

        IntArray array10 = new IntArray(1001);
        for(int i = 0; i < array10.getLength(); i++) array10.set(i, 10);
        IntArray pattern10 = new IntArray(1002);
        for(int i = 0; i < pattern10.getLength(); i++) pattern10.set(i, 10);
        array10.binaryInsert(10, Integer::compare);
        Assertions.assertEquals(pattern10, array10,
                "Не верно работает метод binaryInsert(), если кол-во элементов массива\n " +
                        "больше двух и не четно, порядок возрастающий, добавляемый элемент присутсвует\n " +
                        "и все элементы массива одинаковы.");
    }

    @Test
    public void quickRemove() {
        IntArray array = new IntArray(10);
        for(int i = 0; i < array.getLength(); i++) array.setAndExpand(i, i);

        Assertions.assertThrows(IndexOutOfBoundsException.class, ()-> array.quickRemove(-1));
        Assertions.assertThrows(IndexOutOfBoundsException.class, ()-> array.quickRemove(10));

        Assertions.assertEquals(3, array.quickRemove(3));
        Assertions.assertEquals(9, array.get(3));
        Assertions.assertEquals(9, array.getLength());

        Assertions.assertEquals(8, array.quickRemove(8));
        Assertions.assertThrows(IndexOutOfBoundsException.class, ()-> array.quickRemove(8));
        Assertions.assertEquals(8, array.getLength());
        array.expandTo(100);
        Assertions.assertEquals(0, array.get(8));

        for(int i = 0; i < array.getLength(); i++) array.set(i, 1200);
        for(int i = 0, length = array.getLength(); i < length; i++) array.quickRemove(0);
        Assertions.assertEquals(0, array.getLength());
        array.expandTo(100);
        for(int i = 0; i < array.getLength(); i++) Assertions.assertEquals(0, array.get(i));
    }

    @Test
    public void orderedRemove() {
        IntArray array = new IntArray(0);
        array.add(0);
        array.add(1);
        array.add(2);
        array.add(3);

        Assertions.assertEquals(1, array.orderedRemove(1));
        Assertions.assertEquals(0, array.get(0));
        Assertions.assertEquals(2, array.get(1));
        Assertions.assertEquals(3, array.get(2));
        Assertions.assertEquals(3, array.getLength());

        Assertions.assertEquals(3, array.orderedRemove(2),
                "Метод remove() не верно работает при удалении последнего элемента.");
        Assertions.assertEquals(2, array.getLength(),
                "Метод remove() не верно работает при удалении последнего элемента.");

        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> array.orderedRemove(-1));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> array.orderedRemove(array.getLength()));
    }

    @Test
    public void clear() {
        IntArray array = new IntArray(100);
        for(int i = 0; i < 100; ++i) array.set(i,i);
        for(int i = 100; i < 200; ++i) array.add(i);

        array.clear();
        Assertions.assertEquals(0, array.getLength(),
                "После вызова метода clear() длина массива должна равняться 0.");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> array.get(0),
                "После вызова метода clear() при попытке обраттиться к любому индексу должно " +
                        "генерироваться исключение.");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> array.set(0, 1000),
                "После вызова метода clear() при попытке обраттиться к любому индексу должно " +
                        "генерироваться исключение.");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> array.quickRemove(0),
                "После вызова метода clear() при попытке обраттиться к любому индексу должно " +
                        "генерироваться исключение.");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> array.orderedRemove(0),
                "После вызова метода clear() при попытке обраттиться к любому индексу должно " +
                        "генерироваться исключение.");

        array.expandTo(1077);
        for(int i = 0; i < 1077; ++i) Assertions.assertEquals(0, array.get(i));

        for(int i = 500; i < 1077; ++i) array.set(i, i);

        array.clear();

        array.expandTo(2000);
        for(int i = 0; i < 2000; ++i) Assertions.assertEquals(0, array.get(i));
    }

    @Test
    public void sort() {
        IntArray array = new IntArray( 0);
        IntArray pattern = new IntArray(0);
        array.sort(Integer::compare);
        Assertions.assertEquals(pattern, array,
                "Не верно работает сортировка, если массив пуст.");

        IntArray array2 = new IntArray( 1);
        IntArray pattern2 = new IntArray(1);
        array2.set(0, 100);
        pattern2.set(0, 100);
        array2.sort(Integer::compare);
        Assertions.assertEquals(pattern2, array2,
                "Не верно работает метод sort(), если кол-во элементов равно одному.");

        IntArray array3 = new IntArray( 0);
        IntArray pattern3 = new IntArray(0);
        array3.addAll(100, 12);
        pattern3.addAll(12, 100);
        array3.sort(Integer::compare);
        Assertions.assertEquals(pattern3, array3,
                "Не верно работает метод sort(), если кол-во элементов равно днум, они уникальны,\n " +
                        "порядок обратен заданому порядку сортировки.");

        IntArray array4 = new IntArray( 0);
        IntArray pattern4 = new IntArray(0);
        array4.addAll(12, 100);
        pattern4.addAll(12, 100);
        array4.sort(Integer::compare);
        Assertions.assertEquals(pattern4, array4,
                "Не верно работает метод sort(), если кол-во элементов равно днум, они уникальны,\n " +
                        "порядок соответсвует заданому порядку сортировки.");

        IntArray array5 = new IntArray( 0);
        IntArray pattern5 = new IntArray(0);
        array5.addAll(100, 100);
        pattern5.addAll(100, 100);
        array5.sort(Integer::compare);
        Assertions.assertEquals(pattern5, array5,
                "Не верно работает метод sort(), если кол-во элементов равно днум, они одинаковы.");

        Random random = new Random(1000L);

        IntArray array6 = new IntArray(100000);
        int[] pattern6 = new int[100000];
        for(int i = 0; i < array6.getLength(); i++) pattern6[i] = i;
        mix(pattern6, 1000);
        for(int i = 0; i < array6.getLength(); i++) array6.set(i, pattern6[i]);
        Arrays.sort(pattern6);
        array6.sort(Integer::compare);
        Assertions.assertTrue(compare(array6, pattern6),
                "Не верно работает метод сорт, если кол-во элементов массива больше двух и четно,\n " +
                        "элементы уникальны, порядок случаен.");

        IntArray array7 = new IntArray(100000);
        int[] pattern7 = new int[100000];
        for(int i = 0; i < array7.getLength(); i++) {
            int value = (int)(random.nextDouble() * 1000);
            pattern7[i] = value;
            array7.set(i, value);
        }
        Arrays.sort(pattern7);
        array7.sort(Integer::compare);
        Assertions.assertTrue(compare(array7, pattern7),
                "Не верно работает метод сорт, если кол-во элементов массива больше двух и четно,\n " +
                        "есть повторяющиеся элементы, порядок случаен.");

        IntArray array8 = new IntArray(100000);
        int[] pattern8 = new int[100000];
        for(int i = 0; i < array8.getLength(); i++) {
            pattern8[i] = 100;
            array8.set(i, 100);
        }
        array8.sort(Integer::compare);
        Assertions.assertTrue(compare(array8, pattern8),
                "Не верно работает метод сорт, если кол-во элементов массива больше двух и четно,\n " +
                        "все элементы одинаковы.");

        IntArray array9 = new IntArray(100000);
        int[] pattern9 = new int[100000];
        for(int i = 0; i < array9.getLength(); i++) {
            pattern9[i] = i;
            array9.set(i, i);
        }
        array9.sort(Integer::compare);
        Assertions.assertTrue(compare(array9, pattern9),
                "Не верно работает метод сорт, если кол-во элементов массива больше двух и четно,\n " +
                        "элементы уникальны, порядок соответствует заданому.");

        IntArray array10 = new IntArray(100000);
        int[] pattern10 = new int[100000];
        for(int i = array10.getLength() - 1; i >= 0; --i) {
            pattern10[i] = i;
            array10.set(i, i);
        }
        array10.sort(Integer::compare);
        Assertions.assertTrue(compare(array10, pattern10),
                "Не верно работает метод сорт, если кол-во элементов массива больше двух и четно,\n " +
                        "элементы уникальны, порядок обратен заданому.");
    }

    @Test
    public void linearSearch() {
        IntArray array = new IntArray(0);
        for(int i = 0; i < 100; i++) array.add(i);

        for(int i = 0; i < 100; i++)
            Assertions.assertEquals(i, array.linearSearch(i));

        for(int i = 12; i < 24; i++) array.set(i, 1200);
        Assertions.assertEquals(12, array.linearSearch(1200));

        Assertions.assertEquals(-1, array.linearSearch(1000000));
    }

    @Test
    public void linearSearch_Interval() {
        IntArray array = new IntArray(0);
        for(int i = 0; i < 100; i++) array.add(i);

        for(int i = 12; i < 87; i++)
            Assertions.assertEquals(i, array.linearSearch(i, 12, 87));

        for(int i = 15; i < 24; i++) array.set(i, 1200);
        Assertions.assertEquals(15, array.linearSearch(1200, 12, 30));

        Assertions.assertEquals(-1, array.linearSearch(51, 0, 50));
        Assertions.assertEquals(-1, array.linearSearch(51, 51, 51));

        Assertions.assertThrows(IndexOutOfBoundsException.class,
                ()-> array.linearSearch(0, -1, 100));
        Assertions.assertThrows(IndexOutOfBoundsException.class,
                ()-> array.linearSearch(0, 0, 101));
        Assertions.assertThrows(IndexOutOfBoundsException.class,
                ()-> array.linearSearch(0, 100, 0));
    }

    @Test
    public void binarySearch_ArrayIsEmptyOrContainsOneItem() {
        IntArray array = new IntArray(0);

        Assertions.assertEquals(-1, array.binarySearch(0, Integer::compare),
                "Если массив пустой, метод binarySearch() должен возвращать -1 для любого значения.");

        array.add(0);
        Assertions.assertEquals(0, array.binarySearch(0, Integer::compare),
                "Метод binarySearch() должен возвращать 0, когда искомый элемент является\n " +
                        "единственным в массиве.");
        Assertions.assertEquals(-1, array.binarySearch(1, Integer::compare),
                "Метод binarySearch() должен возвращать -1, когда искомый элемент больше\n " +
                        "единсвтенного элемента в массиве.");
        Assertions.assertEquals(-1, array.binarySearch(-1, Integer::compare),
                "Метод binarySearch() должен возвращать -1, когда искомый элемент меньше\n " +
                        "единсвтенного элемента в массиве.");
    }

    @Test
    public void binarySearch_AscendingOrder() {
        IntArray array = new IntArray(0);

        array.add(0);
        array.add(10);
        Assertions.assertEquals(-1, array.binarySearch(-1, Integer::compare),
                "Метод binarySearch() должен возвращать -1, если искомый элемент меньше\n " +
                        "наименьшего элемента в массиве из двух элементов отсортирванных в\n " +
                        "возрастающем порядке.");
        Assertions.assertEquals(-1, array.binarySearch(11, Integer::compare),
                "Метод binarySearch() должен возвращать -1, если искомый элемент больше\n " +
                        "наибольшего элемента в массиве из двух элементов отсортирванных в\n " +
                        "возрастающем порядке.");
        Assertions.assertEquals(-1, array.binarySearch(5, Integer::compare),
                "Метод binarySearch() должен возвращать -1, если искомый элемент отсутсвует\n " +
                        "в массиве из двух элементов в возрастающем порядке, но мог бы поместиться," +
                        "где-то посередине.");
        Assertions.assertEquals(0, array.binarySearch(0, Integer::compare),
                "Метод binarySearch() должен возвращать 0, если искомый элемент находится\n " +
                        "в начеле массива из двух элементов, отсортированного в возрастающем порядке.");
        Assertions.assertEquals(1, array.binarySearch(10, Integer::compare),
                "Метод binarySearch() должен возвращать 1, если искомый элемент находится\n " +
                        "в конце массива из двух элементов, отсортированного в возрастающем порядке.");


        for(int i = 2; i < 1000; i++) array.add(i * 10);
        Assertions.assertEquals(-1, array.binarySearch(-10, Integer::compare),
                "Метод binarySearch() должен возвращать -1, если искомый элемент\n " +
                        "меньше наименьшего элемента в массиве имеющего больше двух элементов,\n " +
                        "при этом число элементов четное, а их порядок возрастающий.");
        Assertions.assertEquals(-1, array.binarySearch(1000000, Integer::compare),
                "Метод binarySearch() должен возвращать -1, если искомый элемент\n " +
                        "больше наибольшего элемента в массиве имеющего больше двух элементов,\n " +
                        "при этом число элементов четное, а их порядок возрастающий.");
        Assertions.assertEquals(-1, array.binarySearch(5007, Integer::compare),
                "Метод binarySearch() должен возвращать -1, если искомый элемент\n " +
                        "отсутсвует в массиве имеющем больше двух элементов(число\n " +
                        "элементов четное, а их порядок возрастающий), при этом искомый элемент\n " +
                        "мог бы расположиться где-то посередине.");
        Assertions.assertEquals(0, array.binarySearch(0, Integer::compare),
                "Метод binarySearch() должен возвращать 0, если искомый элемент\n " +
                        "присутствует в единственном экземпляре и находится в начале массива,\n " +
                        "имеющем больше двух элементов(число элементов четное, а их порядок\n" +
                        "возрастающий).");
        Assertions.assertEquals(999, array.binarySearch(9990, Integer::compare),
                "Метод binarySearch() должен возвращать последний индекс массива,\n " +
                        "если искомый элемент присутствует в единственном экземпляре и находится\n " +
                        "в конце массива, имеющем больше двух элементов(число элементов четное,\n " +
                        "а их порядок возрастающий).");
        Assertions.assertEquals(497, array.binarySearch(4970, Integer::compare),
                "Метод binarySearch() должен правильно находить индекс искомого элемента,\n " +
                        "если тот присутсвует в массиве в единтсвенном экземпляре имеющем больше\n " +
                        "двух элементов(число элементов четное, а их порядок возрастающий) и находится\n " +
                        "примерно где-то посередине.");
        for(int i = 0; i < array.getLength(); i++) {
            Assertions.assertEquals(i, array.binarySearch(i * 10, Integer::compare),
                    "Метод binarySearch должен находить индекс каждого элемента, присутсвующего\n " +
                            "в массиве один раз, с учетом того, что число элементов массива четно,\n " +
                            "порядок возрастающий. Элемент для которого данный метод работает неправильно,\n " +
                            "равен " + (i * 10));
        }
        for(int i = 0; i < 30; i++) array.set(i, 10);
        Assertions.assertNotEquals(-1, array.binarySearch(10, Integer::compare),
                "Метод binarySearch() не должен возвращать -1, если искомый элемент\n " +
                        "присутсвует в массиве имеющем больше двух элементов(число элементов\n " +
                        "четное, а их порядок возрастающий) несколько раз, и последовательность\n " +
                        "из элементов равных искомому начинается с начала массива.");
        Assertions.assertEquals(10, array.get(array.binarySearch(10, Integer::compare)),
                "Метод binarySearch() должен возвращать индекс элемента имеющего то же значение," +
                        "что и искомый элемент, в случае - если искомый элемент присутсвует в массиве\n " +
                        "имеющем больше двух элементов(число элементов четное, а их порядок возрастающий)\n " +
                        "несколько раз, и последовательность из элементов равных искомому начинается\n " +
                        "с начала массива.");
        for(int i = 490; i < 550; i++) array.set(i, 5000);
        Assertions.assertNotEquals(-1, array.binarySearch(5000, Integer::compare),
                "Метод binarySearch() не должен возвращать -1, если искомый элемент\n " +
                        "присутсвует в массиве имеющем больше двух элементов(число элементов\n " +
                        "четное, а их порядок возрастающий) несколько раз, и последовательность\n " +
                        "из элементов равных искомому начинается где-то с середины массива.");
        Assertions.assertEquals(5000, array.get(array.binarySearch(5000, Integer::compare)),
                "Метод binarySearch() должен возвращать индекс элемента имеющего то же значение," +
                        "что и искомый элемент, в случае - если искомый элемент присутсвует в массиве\n " +
                        "имеющем больше двух элементов(число элементов четное, а их порядок возрастающий)\n " +
                        "несколько раз, и последовательность из элементов равных искомому начинается\n " +
                        "где-то с середины массива.");
        for(int i = 910; i < 1000; i++) array.set(i, 10000);
        Assertions.assertNotEquals(-1, array.binarySearch(10000, Integer::compare),
                "Метод binarySearch() не должен возвращать -1, если искомый элемент\n " +
                        "присутсвует в массиве имеющем больше двух элементов(число элементов\n " +
                        "четное, а их порядок возрастающий) несколько раз, и последовательность\n " +
                        "из элементов равных искомому находится в конце массива.");
        Assertions.assertEquals(10000, array.get(array.binarySearch(10000, Integer::compare)),
                "Метод binarySearch() должен возвращать индекс элемента имеющего то же значение," +
                        "что и искомый элемент, в случае - если искомый элемент присутсвует в массиве\n " +
                        "имеющем больше двух элементов(число элементов четное, а их порядок возрастающий)\n " +
                        "несколько раз, и последовательность из элементов равных искомому находится\n " +
                        "в конце массива.");


        for(int i = 0; i < 1001; i++) array.setAndExpand(i, i * 10);
        Assertions.assertEquals(-1, array.binarySearch(-10, Integer::compare),
                "Метод binarySearch() должен возвращать -1, если искомый элемент\n " +
                        "меньше наименьшего элемента в массиве имеющего больше двух элементов,\n " +
                        "при этом число элементов не четное, а их порядок возрастающий.");
        Assertions.assertEquals(-1, array.binarySearch(1000000, Integer::compare),
                "Метод binarySearch() должен возвращать -1, если искомый элемент\n " +
                        "больше наибольшего элемента в массиве имеющего больше двух элементов,\n " +
                        "при этом число элементов не четное, а их порядок возрастающий.");
        Assertions.assertEquals(-1, array.binarySearch(5007, Integer::compare),
                "Метод binarySearch() должен возвращать -1, если искомый элемент\n " +
                        "отсутсвует в массиве имеющем больше двух элементов(число\n " +
                        "элементов не четное, а их порядок возрастающий), при этом искомый элемент\n " +
                        "мог бы расположиться где-то посередине.");
        Assertions.assertEquals(0, array.binarySearch(0, Integer::compare),
                "Метод binarySearch() должен возвращать 0, если искомый элемент\n " +
                        "присутствует в единственном экземпляре и находится в начале массива,\n " +
                        "имеющем больше двух элементов(число элементов не четное, а их порядок\n" +
                        "возрастающий).");
        Assertions.assertEquals(1000, array.binarySearch(10000, Integer::compare),
                "Метод binarySearch() должен возвращать последний индекс массива,\n " +
                        "если искомый элемент присутствует в единственном экземпляре и находится\n " +
                        "в конце массива, имеющем больше двух элементов(число элементов не четное,\n " +
                        "а их порядок возрастающий).");
        Assertions.assertEquals(497, array.binarySearch(4970, Integer::compare),
                "Метод binarySearch() должен правильно находить индекс искомого элемента,\n " +
                        "если тот присутсвует в единственном экземпляре в массиве имеющем больше\n " +
                        "двух элементов(число элементов не четное, а их порядок возрастающий) и\n " +
                        "находится примерно где-то посередине.");
        for(int i = 0; i < array.getLength(); i++) {
            Assertions.assertEquals(i, array.binarySearch(i * 10, Integer::compare),
                    "Метод binarySearch должен находить индекс каждого элемента, присутсвующего\n " +
                            "в массиве один раз, с учетом того, что число элементов массива не четно,\n " +
                            "порядок возрастающий. Элемент для которого данный метод работает неправильно,\n " +
                            "равен " + (i * 10));
        }
        for(int i = 0; i < 30; i++) array.set(i, 10);
        Assertions.assertNotEquals(-1, array.binarySearch(10, Integer::compare),
                "Метод binarySearch() не должен возвращать -1, если искомый элемент\n " +
                        "присутсвует в массиве имеющем больше двух элементов(число элементов\n " +
                        "не четное, а их порядок возрастающий) несколько раз, и последовательность\n " +
                        "из элементов равных искомому начинается с начала массива.");
        Assertions.assertEquals(10, array.get(array.binarySearch(10, Integer::compare)),
                "Метод binarySearch() должен возвращать индекс элемента имеющего то же значение," +
                        "что и искомый элемент, в случае - если искомый элемент присутсвует в массиве\n " +
                        "имеющем больше двух элементов(число элементов не четное, а их порядок возрастающий)\n " +
                        "несколько раз, и последовательность из элементов равных искомому начинается\n " +
                        "с начала массива.");
        for(int i = 490; i < 550; i++) array.set(i, 5000);
        Assertions.assertNotEquals(-1, array.binarySearch(5000, Integer::compare),
                "Метод binarySearch() не должен возвращать -1, если искомый элемент\n " +
                        "присутсвует в массиве имеющем больше двух элементов(число элементов\n " +
                        "не четное, а их порядок возрастающий) несколько раз, и последовательность\n " +
                        "из элементов равных искомому начинается где-то с середины массива.");
        Assertions.assertEquals(5000, array.get(array.binarySearch(5000, Integer::compare)),
                "Метод binarySearch() должен возвращать индекс элемента имеющего то же значение," +
                        "что и искомый элемент, в случае - если искомый элемент присутсвует в массиве\n " +
                        "имеющем больше двух элементов(число элементов не четное, а их порядок возрастающий)\n " +
                        "несколько раз, и последовательность из элементов равных искомому начинается\n " +
                        "где-то с середины массива.");
        for(int i = 910; i < 1001; i++) array.set(i, 10000);
        Assertions.assertNotEquals(-1, array.binarySearch(10000, Integer::compare),
                "Метод binarySearch() не должен возвращать -1, если искомый элемент\n " +
                        "присутсвует в массиве имеющем больше двух элементов(число элементов\n " +
                        "не четное, а их порядок возрастающий) несколько раз, и последовательность\n " +
                        "из элементов равных искомому находится в конце массива.");
        Assertions.assertEquals(10000, array.get(array.binarySearch(10000, Integer::compare)),
                "Метод binarySearch() должен возвращать индекс элемента имеющего то же значение," +
                        "что и искомый элемент, в случае - если искомый элемент присутсвует в массиве\n " +
                        "имеющем больше двух элементов(число элементов не четное, а их порядок возрастающий)\n " +
                        "несколько раз, и последовательность из элементов равных искомому находится\n " +
                        "в конце массива.");
    }

    @Test
    public void binarySearch_DescendingOrder() {
        IntArray array = new IntArray(0);


        array.add(10);
        array.add(0);
        Assertions.assertEquals(-1, array.binarySearch(-1, (int a, int b) -> b - a),
                "Метод binarySearch() должен возвращать -1, если искомый элемент меньше\n " +
                        "наименьшего элемента в массиве из двух элементов отсортирванных в\n " +
                        "убывающем порядке.");
        Assertions.assertEquals(-1, array.binarySearch(11, (int a, int b) -> b - a),
                "Метод binarySearch() должен возвращать -1, если искомый элемент больше\n " +
                        "наибольшего элемента в массиве из двух элементов отсортирванных в\n " +
                        "убывающем порядке.");
        Assertions.assertEquals(-1, array.binarySearch(5, (int a, int b) -> b - a),
                "Метод binarySearch() должен возвращать -1, если искомый элемент отсутсвует\n " +
                        "в массиве из двух элементов в убывающем порядке, но мог бы поместиться," +
                        "где-то посередине.");
        Assertions.assertEquals(0, array.binarySearch(10, (int a, int b) -> b - a),
                "Метод binarySearch() должен возвращать 0, если искомый элемент находится\n " +
                        "в начеле массива из двух элементов, отсортированного в убывающем порядке.");
        Assertions.assertEquals(1, array.binarySearch(0, (int a, int b) -> b - a),
                "Метод binarySearch() должен возвращать 1, если искомый элемент находится\n " +
                        "в конце массива из двух элементов, отсортированного в убывающем порядке.");


        array = new IntArray(0);
        for(int i = 999; i >= 0; i--) array.add(i * 10);
        Assertions.assertEquals(-1, array.binarySearch(-10, (int a, int b) -> b - a),
                "Метод binarySearch() должен возвращать -1, если искомый элемент\n " +
                        "меньше наименьшего элемента в массиве имеющего больше двух элементов,\n " +
                        "при этом число элементов четное, а их порядок убывающий.");
        Assertions.assertEquals(-1, array.binarySearch(1000000, (int a, int b) -> b - a),
                "Метод binarySearch() должен возвращать -1, если искомый элемент\n " +
                        "больше наибольшего элемента в массиве имеющего больше двух элементов,\n " +
                        "при этом число элементов четное, а их порядок убывающий.");
        Assertions.assertEquals(-1, array.binarySearch(5007, (int a, int b) -> b - a),
                "Метод binarySearch() должен возвращать -1, если искомый элемент\n " +
                        "отсутсвует в массиве имеющем больше двух элементов(число\n " +
                        "элементов четное, а их порядок убывающий), при этом искомый элемент\n " +
                        "мог бы расположиться где-то посередине.");
        Assertions.assertEquals(0, array.binarySearch(9990, (int a, int b) -> b - a),
                "Метод binarySearch() должен возвращать 0, если искомый элемент\n " +
                        "присутствует в единственном экземпляре и находится в начале массива,\n " +
                        "имеющем больше двух элементов(число элементов четное, а их порядок\n" +
                        "убывающий).");
        Assertions.assertEquals(999, array.binarySearch(0, (int a, int b) -> b - a),
                "Метод binarySearch() должен возвращать последний индекс массива,\n " +
                        "если искомый элемент присутствует в единственном экземпляре и находится\n " +
                        "в конце массива, имеющем больше двух элементов(число элементов четное,\n " +
                        "а их порядок убывающий).");
        Assertions.assertEquals(498, array.binarySearch(5010, (int a, int b) -> b - a),
                "Метод binarySearch() должен правильно находить индекс искомого элемента,\n " +
                        "если тот присутсвует в массиве в единтсвенном экземпляре имеющем больше\n " +
                        "двух элементов(число элементов четное, а их порядок убывающий) и находится\n " +
                        "примерно где-то посередине.");
        for(int i = array.getLength() - 1; i >= 0; i--) {
            Assertions.assertEquals(
                    array.getLength() - 1 - i, array.binarySearch(i * 10, (int a, int b) -> b - a),
                    "Метод binarySearch должен находить индекс каждого элемента, присутсвующего\n " +
                            "в массиве один раз, с учетом того, что число элементов массива четно,\n " +
                            "порядок убывающий. Элемент для которого данный метод работает неправильно,\n " +
                            "равен " + (i * 10));
        }
        for(int i = 0; i < 30; i++) array.set(i, 10000);
        Assertions.assertNotEquals(-1, array.binarySearch(10000, (int a, int b) -> b - a),
                "Метод binarySearch() не должен возвращать -1, если искомый элемент\n " +
                        "присутсвует в массиве имеющем больше двух элементов(число элементов\n " +
                        "четное, а их порядок убывающий) несколько раз, и последовательность\n " +
                        "из элементов равных искомому начинается с начала массива.");
        Assertions.assertEquals(10000, array.get(array.binarySearch(10000, (int a, int b) -> b - a)),
                "Метод binarySearch() должен возвращать индекс элемента имеющего то же значение," +
                        "что и искомый элемент, в случае - если искомый элемент присутсвует в массиве\n " +
                        "имеющем больше двух элементов(число элементов четное, а их порядок убывающий)\n " +
                        "несколько раз, и последовательность из элементов равных искомому начинается\n " +
                        "с начала массива.");
        for(int i = 498; i < 550; i++) array.set(i, 4990);
        Assertions.assertNotEquals(-1, array.binarySearch(4990, (int a, int b) -> b - a),
                "Метод binarySearch() не должен возвращать -1, если искомый элемент\n " +
                        "присутсвует в массиве имеющем больше двух элементов(число элементов\n " +
                        "четное, а их порядок убывающий) несколько раз, и последовательность\n " +
                        "из элементов равных искомому начинается где-то с середины массива.");
        Assertions.assertEquals(4990, array.get(array.binarySearch(4990, (int a, int b) -> b - a)),
                "Метод binarySearch() должен возвращать индекс элемента имеющего то же значение," +
                        "что и искомый элемент, в случае - если искомый элемент присутсвует в массиве\n " +
                        "имеющем больше двух элементов(число элементов четное, а их порядок убывающий)\n " +
                        "несколько раз, и последовательность из элементов равных искомому начинается\n " +
                        "где-то с середины массива.");
        for(int i = 910; i < 1000; i++) array.set(i, 0);
        Assertions.assertNotEquals(-1, array.binarySearch(0, (int a, int b) -> b - a),
                "Метод binarySearch() не должен возвращать -1, если искомый элемент\n " +
                        "присутсвует в массиве имеющем больше двух элементов(число элементов\n " +
                        "четное, а их порядок убывающий) несколько раз, и последовательность\n " +
                        "из элементов равных искомому находится в конце массива.");
        Assertions.assertEquals(0, array.get(array.binarySearch(0, (int a, int b) -> b - a)),
                "Метод binarySearch() должен возвращать индекс элемента имеющего то же значение," +
                        "что и искомый элемент, в случае - если искомый элемент присутсвует в массиве\n " +
                        "имеющем больше двух элементов(число элементов четное, а их порядок убывающий)\n " +
                        "несколько раз, и последовательность из элементов равных искомому находится\n " +
                        "в конце массива.");


        array = new IntArray(0);
        for(int i = 1000; i >= 0; i--) array.add(i * 10);
        Assertions.assertEquals(-1, array.binarySearch(-10, (int a, int b) -> b - a),
                "Метод binarySearch() должен возвращать -1, если искомый элемент\n " +
                        "меньше наименьшего элемента в массиве имеющего больше двух элементов,\n " +
                        "при этом число элементов не четное, а их порядок убывающий.");
        Assertions.assertEquals(-1, array.binarySearch(1000000, (int a, int b) -> b - a),
                "Метод binarySearch() должен возвращать -1, если искомый элемент\n " +
                        "больше наибольшего элемента в массиве имеющего больше двух элементов,\n " +
                        "при этом число элементов не четное, а их порядок убывающий.");
        Assertions.assertEquals(-1, array.binarySearch(5007, (int a, int b) -> b - a),
                "Метод binarySearch() должен возвращать -1, если искомый элемент\n " +
                        "отсутсвует в массиве имеющем больше двух элементов(число\n " +
                        "элементов не четное, а их порядок убывающий), при этом искомый элемент\n " +
                        "мог бы расположиться где-то посередине.");
        Assertions.assertEquals(0, array.binarySearch(10000, (int a, int b) -> b - a),
                "Метод binarySearch() должен возвращать 0, если искомый элемент\n " +
                        "присутствует в единственном экземпляре и находится в начале массива,\n " +
                        "имеющем больше двух элементов(число элементов не четное, а их порядок\n" +
                        "убывающий).");
        Assertions.assertEquals(1000, array.binarySearch(0, (int a, int b) -> b - a),
                "Метод binarySearch() должен возвращать последний индекс массива,\n " +
                        "если искомый элемент присутствует в единственном экземпляре и находится\n " +
                        "в конце массива, имеющем больше двух элементов(число элементов не четное,\n " +
                        "а их порядок убывающий).");
        Assertions.assertEquals(500, array.binarySearch(5000, (int a, int b) -> b - a),
                "Метод binarySearch() должен правильно находить индекс искомого элемента,\n " +
                        "если тот присутсвует в единтсвенном экземпляре в массиве имеющем больше\n " +
                        "двух элементов(число элементов не четное, а их порядок убывающий) и находится\n " +
                        "примерно где-то посередине.");
        for(int i = array.getLength() - 1; i >= 0; i--) {
            Assertions.assertEquals(
                    array.getLength() - 1 - i, array.binarySearch(i * 10, (int a, int b) -> b - a),
                    "Метод binarySearch должен находить индекс каждого элемента, присутсвующего\n " +
                            "в массиве один раз, с учетом того, что число элементов массива не четно,\n " +
                            "порядок убывающий. Элемент для которого данный метод работает неправильно,\n " +
                            "равен " + (i * 10));
        }
        for(int i = 0; i < 30; i++) array.set(i, 10000);
        Assertions.assertNotEquals(-1, array.binarySearch(10000, (int a, int b) -> b - a),
                "Метод binarySearch() не должен возвращать -1, если искомый элемент\n " +
                        "присутсвует в массиве имеющем больше двух элементов(число элементов\n " +
                        "не четное, а их порядок убывающий) несколько раз, и последовательность\n " +
                        "из элементов равных искомому начинается с начала массива.");
        Assertions.assertEquals(10000, array.get(array.binarySearch(10000, (int a, int b) -> b - a)),
                "Метод binarySearch() должен возвращать индекс элемента имеющего то же значение," +
                        "что и искомый элемент, в случае - если искомый элемент присутсвует в массиве\n " +
                        "имеющем больше двух элементов(число элементов не четное, а их порядок убывающий)\n " +
                        "несколько раз, и последовательность из элементов равных искомому начинается\n " +
                        "с начала массива.");
        for(int i = 500; i < 550; i++) array.set(i, 5000);
        Assertions.assertNotEquals(-1, array.binarySearch(5000, (int a, int b) -> b - a),
                "Метод binarySearch() не должен возвращать -1, если искомый элемент\n " +
                        "присутсвует в массиве имеющем больше двух элементов(число элементов\n " +
                        "не четное, а их порядок убывающий) несколько раз, и последовательность\n " +
                        "из элементов равных искомому начинается где-то с середины массива.");
        Assertions.assertEquals(5000, array.get(array.binarySearch(5000, (int a, int b) -> b - a)),
                "Метод binarySearch() должен возвращать индекс элемента имеющего то же значение," +
                        "что и искомый элемент, в случае - если искомый элемент присутсвует в массиве\n " +
                        "имеющем больше двух элементов(число элементов не четное, а их порядок убывающий)\n " +
                        "несколько раз, и последовательность из элементов равных искомому начинается\n " +
                        "где-то с середины массива.");
        for(int i = 910; i <= 1000; i++) array.set(i, 0);
        Assertions.assertNotEquals(-1, array.binarySearch(0, (int a, int b) -> b - a),
                "Метод binarySearch() не должен возвращать -1, если искомый элемент\n " +
                        "присутсвует в массиве имеющем больше двух элементов(число элементов\n " +
                        "не четное, а их порядок убывающий) несколько раз, и последовательность\n " +
                        "из элементов равных искомому находится в конце массива.");
        Assertions.assertEquals(0, array.get(array.binarySearch(0, (int a, int b) -> b - a)),
                "Метод binarySearch() должен возвращать индекс элемента имеющего то же значение," +
                        "что и искомый элемент, в случае - если искомый элемент присутсвует в массиве\n " +
                        "имеющем больше двух элементов(число элементов не четное, а их порядок убывающий)\n " +
                        "несколько раз, и последовательность из элементов равных искомому находится\n " +
                        "в конце массива.");
    }

    @Test
    public void binarySearch_AllItemsAreTheSame() {
        IntArray array = new IntArray(0);
        array.addAll(100,100);
        Assertions.assertNotEquals(-1, array.binarySearch(100, Integer::compare),
                "Метод binarySearch() не должен возвращать -1, если искомый элемент\n " +
                        "совпадает с каждым элементом в массиве из двух элементов.");
        Assertions.assertEquals(100, array.get(array.binarySearch(100, Integer::compare)),
                "Метод binarySearch() должен возвращать индекс элемента имеющего то же значение,\n " +
                        "что и искомый элемент, в случае - если искомый элемент совпадает с каждым\n " +
                        "элементом в массиве из двух элементов.");

        for(int i = 0; i < 98; i++) array.add(100);
        Assertions.assertNotEquals(-1, array.binarySearch(100, Integer::compare),
                "Метод binarySearch() не должен возвращать -1, если искомый элемент\n " +
                        "присутсвует в массиве имеющем больше двух элементов(число элементов\n " +
                        "четное), при этом все элеметы массива равны искомому.");
        Assertions.assertEquals(100, array.get(array.binarySearch(100, Integer::compare)),
                "Метод binarySearch() должен возвращать индекс элемента имеющего то же значение," +
                        "что и искомый элемент, в случае - если искомый элемент присутсвует в массиве\n " +
                        "имеющем больше двух элементов(число элементов четное), при этом все элеметы\n " +
                        "массива равны искомому.");

        array.add(100);
        Assertions.assertNotEquals(-1, array.binarySearch(100, Integer::compare),
                "Метод binarySearch() не должен возвращать -1, если искомый элемент\n " +
                        "присутсвует в массиве имеющем больше двух элементов(число элементов\n " +
                        "не четное, при этом все элеметы массива равны искомому.");
        Assertions.assertEquals(100, array.get(array.binarySearch(100, Integer::compare)),
                "Метод binarySearch() должен возвращать индекс элемента имеющего то же значение," +
                        "что и искомый элемент, в случае - если искомый элемент присутсвует в массиве\n " +
                        "имеющем больше двух элементов(число элементов не четное), при этом все элеметы\n " +
                        "массива равны искомому.");
    }

    @Test
    public void expandTo() {
        IntArray array = new IntArray(10);
        for(int i = 0; i < array.getLength(); i++) array.set(i, i);

        array.expandTo(8);
        Assertions.assertEquals(10, array.getLength());
        for(int i = 0; i < array.getLength(); i++) Assertions.assertEquals(i, array.get(i));

        array.expandTo(-1);
        Assertions.assertEquals(10, array.getLength());
        for(int i = 0; i < array.getLength(); i++) Assertions.assertEquals(i, array.get(i));

        array.expandTo(100);
        Assertions.assertEquals(100, array.getLength());
        for(int i = 0; i < 10; i++) Assertions.assertEquals(i, array.get(i));
        for(int i = 10; i < array.getLength(); i++) Assertions.assertEquals(0, array.get(i));
    }

    @Test
    public void compressTo() {
        IntArray array = new IntArray(10);
        for(int i = 0; i < array.getLength(); i++) array.set(i, i);

        array.compressTo(500);
        Assertions.assertEquals(10, array.getLength());
        for(int i = 0; i < array.getLength(); i++) Assertions.assertEquals(i, array.get(i));

        array.compressTo(10);
        Assertions.assertEquals(10, array.getLength());
        for(int i = 0; i < array.getLength(); i++) Assertions.assertEquals(i, array.get(i));

        array.compressTo(5);
        Assertions.assertEquals(5, array.getLength());
        for(int i = 0; i < array.getLength(); i++) Assertions.assertEquals(i, array.get(i));

        array.expandTo(20);
        for(int i = 5; i < array.getLength(); i++) Assertions.assertEquals(0, array.get(i));

        for(int i = 0; i < array.getLength(); i++) array.set(i, 1200);
        array.compressTo(0);
        Assertions.assertEquals(0, array.getLength());
        array.expandTo(100);
        for(int i = 0; i < array.getLength(); i++) Assertions.assertEquals(0, array.get(i));
    }

    @Test
    public void increment() {
        IntArray array = new IntArray(10);
        for(int i = 1; i < 100; i++) {
            Assertions.assertEquals(i, array.increment(8));
        }

        array.set(5, Integer.MAX_VALUE);
        Assertions.assertEquals(Integer.MIN_VALUE, array.increment(5));
    }

    @Test
    public void decrement() {
        IntArray array = new IntArray(10);
        for(int i = 1; i < 100; i++) {
            Assertions.assertEquals(-i, array.decrement(8));
        }

        array.set(5, Integer.MIN_VALUE);
        Assertions.assertEquals(Integer.MAX_VALUE, array.decrement(5));
    }

    @Test
    public void iterator_hasNext() {
        IntArray array = new IntArray(10);
        Assertions.assertTrue(array.iterator().hasNext());

        array.set(0, 1200);
        Assertions.assertTrue(array.iterator().hasNext());

        array.quickRemove(0);
        Assertions.assertTrue(array.iterator().hasNext());

        for(int i = 0, length = array.getLength(); i < length; i++) array.quickRemove(0);
        Assertions.assertFalse(array.iterator().hasNext());

        array.expandTo(10);
        Assertions.assertTrue(array.iterator().hasNext());

        array.compressTo(0);
        Assertions.assertFalse(array.iterator().hasNext());
    }

    @Test
    public void iterator_next() {
        IntArray array = new IntArray(10);
        final IntArray.IntIterator iterator = array.iterator();
        array.set(0, 1200);
        Assertions.assertThrows(ConcurrentModificationException.class, iterator::next,
                "При любой модификации объекта Array, метод next() итератора полученного до этой" +
                        "модификации должен генерировать исключение.");

        IntArray.IntIterator iterator2 = array.iterator();
        while(iterator2.hasNext()) iterator2.next();
        Assertions.assertThrows(NoSuchElementException.class, iterator2::next);

        array.compressTo(0);
        IntArray.IntIterator iterator3 = array.iterator();
        Assertions.assertThrows(NoSuchElementException.class, iterator3::next);

        for(int i = 0; i < 100; i++) array.add(i);
        array.set(12, 0);
        array.set(20, 0);
        array.set(36, 0);
        IntArray matcher = new IntArray(0);
        IntArray.IntIterator iterator4 = array.iterator();
        while(iterator4.hasNext()) matcher.add(iterator4.next());
        Assertions.assertEquals(array, matcher);
    }

    @Test
    public void forEach() {
        IntArray array = new IntArray(100);
        final int[] countIterations = new int[1];
        array.forEach((int value) -> ++countIterations[0]);
        Assertions.assertEquals(100, countIterations[0],
                "Кол-во итераций метода forEach() должно равняться длине массива(getLength()).");

        for(int i = 0; i < 100; i++) array.set(i, 1200);
        for(int i = 0; i < 100; i++) array.quickRemove(0);
        countIterations[0] = 0;
        array.forEach((int value) -> ++countIterations[0]);
        Assertions.assertEquals(0, countIterations[0],
                "Если объект Array имеет нулевую длину, то метод forEach() не должен " +
                        "выполнять ни одной итерации.");

        for(int i = 0; i < 100; i++) array.add(i);
        IntArray matcher = new IntArray(0);
        array.forEach(matcher::add);
        Assertions.assertEquals(array, matcher);

        Assertions.assertThrows(ConcurrentModificationException.class,
                () -> array.forEach((int value)->array.add(1000000)));
    }

    @Test
    public void equals() {
        IntArray array = new IntArray(1000);
        for(int i = 0; i < array.getLength(); i++) array.set(i, i);
        IntArray array2 = new IntArray(1000);
        for(int i = 0; i < array2.getLength(); i++) array2.set(i, i);

        Assertions.assertEquals(array, array2,
                "Не верно работает метод equals().");

        Assertions.assertEquals(array, array,
                "Не соблюдаетс свойство рефлексивности для метода equals().");
        Assertions.assertEquals(array2, array2,
                "Не соблюдаетс свойство рефлексивности для метода equals().");

        Assertions.assertEquals(array.equals(array2), array2.equals(array),
                "Не соблюдается свойство симметрисности для метода equals().");

        array2.expandTo(10000);
        Assertions.assertNotEquals(array, array2,
                "Не верно работает метод equals().");
        Assertions.assertEquals(array2.equals(array), array.equals(array2),
                "Не соблюдается свойство симметрисности для метода equals().");

        IntArray array3 = new IntArray(2000);
        for(int i = 0; i < array3.getLength(); i++) array3.set(i, i);
        Assertions.assertNotEquals(array, array3,
                "Не верно работает метод equals().");
        Assertions.assertEquals(array3.equals(array), array.equals(array3),
                "Не соблюдается свойство симметрисности для метода equals().");

        Assertions.assertTrue(!array.equals(array2) && !array2.equals(array3) && !array.equals(array3),
                "Не соблюдается свойство транзитивности для сетода equals().");

        array2 = new IntArray(1000);
        for(int i = 0; i < array2.getLength(); i++) array2.set(i, i);
        array3 = new IntArray(1000);
        for(int i = 0; i < array3.getLength(); i++) array3.set(i, i);
        Assertions.assertTrue(array.equals(array2) && array2.equals(array3) && array.equals(array3),
                "Не соблюдается свойство транзитивности для сетода equals().");
    }

    @Test
    public void hashCode_Properties() {
        IntArray array = new IntArray(1000);
        for(int i = 0; i < array.getLength(); i++) array.set(i, i);
        IntArray array2 = new IntArray(1000);
        for(int i = 0; i < array2.getLength(); i++) array2.set(i, i);
        IntArray array3 = new IntArray(1000);
        for(int i = 0; i < array3.getLength(); i++) array3.set(i, i*2);

        Assertions.assertEquals(array.hashCode(), array2.hashCode(),
                "Если объекты равны по equals(), то их хеш-коды тоже должны быть равны.");

        if(array.hashCode() != array3.hashCode()) {
            Assertions.assertNotEquals(array, array3,
                    "Если хеш коды объектов не равны, то объекты гарантированно отличаются по equals().");
        }

        int hashCode = array.hashCode();
        for(int i = 0; i < 100000; i++) {
            Assertions.assertEquals(hashCode, array.hashCode(),
                    "Для одного и того же состояния объекта hashCode() должен возвращать одно и тоже значение.");
        }
    }

    private void mix(int[] array, long key) {
        Random random = new Random(key);
        for(int i = 0; i < array.length; i++) {
            int randomIndex = (int)(random.nextDouble() * array.length);
            int temp = array[i];
            array[i] = array[randomIndex];
            array[randomIndex] = temp;
        }
    }

    private boolean compare(IntArray array, int[] array2) {
        boolean isEqual = true;
        for(int i = 0; i < array.getLength() && isEqual; i++) {
            isEqual = array.get(i) == array2[i];
        }
        return isEqual;
    }

}