package com.bakuard.ecsEngine;

import com.bakuard.ecsEngine.core.utils.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

class ArrayTest {

    @Test
    public void constructor() {
        Array<String> array = new Array<>(String.class, 3);

        Assertions.assertNull(array.get(0),
                "После создания массива не нулевого размера, все его элементы должны иметь значение null.");
        Assertions.assertNull(array.get(2),
                "После создания массива не нулевого размера, все его элементы должны иметь значение null.");

        Assertions.assertEquals(3, array.getLength(),
                "После создания массива не нулевого размера, метод getLength() должен возвращать значение, " +
                        "равное значению переданному в качестве аргумента конструктора.");

        Assertions.assertThrows(IllegalArgumentException.class, () -> new Array<>(String.class, -1),
                "При попытке создать массив с отрицательной длиной должно генерироваться исключение.");

        Array<String> emptyArray = new Array<>(String.class, 0);
        Assertions.assertEquals(0, emptyArray.getLength(),
                "После создании массива с нулевой длиной метрод getLength() должен возвращать 0.");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> emptyArray.get(0),
                "При попытке получить доступ к элементам пустого массива должно генерироваться исключение " +
                        "в независимости от переданного индекса.");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> emptyArray.get(1000000),
                "При попытке получить доступ к элементам пустого массива должно генерироваться исключение " +
                        "в независимости от переданного индекса.");
    }

    @Test
    public void get() {
        Array<String> emptyArray = new Array<>(String.class, 0);
        Array<String> array = new Array<>(String.class, 0);
        array.addAll("cat", "dog", "ping");

        Assertions.assertThrows(ArrayIndexOutOfBoundsException.class, () -> array.get(-1),
                "При попыте получить значение за нижней границей массива должно генерироваться исключение.");
        Assertions.assertThrows(ArrayIndexOutOfBoundsException.class, () -> array.get(3),
                "При попыте получить значение за верхней границей массива должно генерироваться исключение.");
        Assertions.assertThrows(ArrayIndexOutOfBoundsException.class, () -> emptyArray.get(0),
                "При попытке получить доступ к элементам пустого массива должно генерироваться исключение " +
                        "в независимости от переданного индекса.");
        Assertions.assertThrows(ArrayIndexOutOfBoundsException.class, () -> emptyArray.get(1000000),
                "При попытке получить доступ к элементам пустого массива должно генерироваться исключение " +
                        "в независимости от переданного индекса.");

        Assertions.assertEquals("cat", array.get(0));
        Assertions.assertEquals("ping", array.get(2));
    }

    @Test
    public void set() {
        Array<String> array = new Array<>(String.class, 0);
        array.addAll(null, "ping", "dog");

        Assertions.assertNull(array.set(0, "cat"),
                "Метод set(int index, T value) должен возвращать элемент хранящийся до его вызова " +
                        "в ячейке с указанным индексом. Для пустой ячейки должно возвращаться значение null.");
        Assertions.assertEquals("cat", array.get(0));
        Assertions.assertEquals("dog", array.set(2, "crocodile"),
                "Метод set(int index, T value) должен возвращать элемент хранящийся до его вызова " +
                        "в ячейке с указанным индексом.");
        Assertions.assertEquals("crocodile", array.get(2));
        Assertions.assertEquals("crocodile", array.set(2, null),
                "Метод set(int index, T value) должен позволять записать значение null.");
        Assertions.assertNull(array.get(2));

        Assertions.assertThrows(ArrayIndexOutOfBoundsException.class, () -> array.set(-1, "Cat"),
                "При попытке записать значение за границами массива с помощью set(int index, T value) " +
                        "должно генерироваться исключение.");
        Assertions.assertThrows(ArrayIndexOutOfBoundsException.class, () -> array.set(3, "Cat"),
                "При попытке записать значение за границами массива с помощью set(int index, T value) " +
                        "должно генерироваться исключение.");
    }

    @Test
    public void setAndExpand() {
        Array<String> array = new Array<>(String.class, 0);
        array.addAll("cat", "dog", "ping");

        Assertions.assertEquals("cat", array.setAndExpand(0, "Java"),
                "Метод setAndExpand(int index, T value) должен возвращать элемент хранящийся до его вызова " +
                        "в ячейке с указанным индексом.");
        Assertions.assertEquals("Java", array.get(0));

        Assertions.assertEquals("ping", array.setAndExpand(2, null),
                "Метод setAndExpand(int index, T value) должен позволять записать значение null.");
        Assertions.assertNull(array.get(2));

        Assertions.assertNull(array.setAndExpand(5, "C++"),
                "Если индекс переданый методу setAndExpand(int index, T value) больше длины массива, " +
                        "то возвращаемое значение метода должно иметь значение null.");
        Assertions.assertEquals("C++", array.get(5));
        Assertions.assertNull(array.get(3),
                "При расширении массива при применении метода setAndExpand(int index, T value), " +
                        "новые значения между указанным индексом и индексом равным предыдущей длине " +
                        "массива(включая его), должны быть равны null.");
        Assertions.assertNull(array.get(4),
                "При расширении массива при применении метода setAndExpand(int index, T value), " +
                        "новые значения между указанным индексом и индексом равным предыдущей длине " +
                        "массива(включая его), должны быть равны null.");
        Assertions.assertEquals("Java", array.get(0),
                "При расширении массива при применении метода setAndExpand(int index, T value), " +
                        "старые значение должны сохраняться.");
        Assertions.assertEquals("dog", array.get(1),
                "При расширении массива при применении метода setAndExpand(int index, T value), " +
                        "старые значение должны сохраняться.");
        Assertions.assertNull(array.get(2),
                "При расширении массива при применении метода setAndExpand(int index, T value), " +
                        "старые значение должны сохраняться.");

        Assertions.assertEquals(6, array.getLength(),
                "При расширении массива при применении метода setAndExpand(int index, T value), " +
                        "метод getLength() должен возвращать значение равное переданному индексу + 1.");

        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> array.setAndExpand(-1, "Cat"),
                "При вызове метода setAndExpand(int index, T value) с отрицательным индексом должно " +
                        "генерироваться исключение.");
    }

    @Test
    public void add() {
        Array<String> array = new Array<>(String.class, 3);
        array.set(0, "Java");
        array.set(1, "Candy");
        array.set(2, "C");

        array.add("cat");

        Assertions.assertEquals(4, array.getLength(),
                "Метод add(T value) должен увеличевать размер массива на единицу.");
        Assertions.assertEquals("cat", array.get(3));
        Assertions.assertEquals("Java", array.get(0),
                "После расширения массива в результате применения метода add(T value), элементы массива " +
                        "под индексами [0, oldLength) должны сохраниться.");
        Assertions.assertEquals("C", array.get(2),
                "После расширения массива в результате применения метода add(T value), элементы массива " +
                        "под индексами [0, oldLength) должны сохраниться.");
    }

    @Test
    public void addAll() {
        Array<Integer> actualArray = new Array<>(Integer.class, 0);
        actualArray.addAll(1,2,3);
        Array<Integer> expectedArray = new Array<>(Integer.class, 3);
        expectedArray.set(0, 1);
        expectedArray.set(1, 2);
        expectedArray.set(2, 3);
        Assertions.assertEquals(expectedArray, actualArray,
                "Не верно работает метод addAll() при добавлении элементов в пустой массив.");

        actualArray.addAll(10,20,30);
        Array<Integer> expectedArray2 = new Array<>(Integer.class, 6);
        expectedArray2.set(0, 1);
        expectedArray2.set(1, 2);
        expectedArray2.set(2, 3);
        expectedArray2.set(3, 10);
        expectedArray2.set(4, 20);
        expectedArray2.set(5, 30);
        Assertions.assertEquals(expectedArray2, actualArray,
                "Не верно работает метод addAll() при добавлении элементов в не пустой массив.");

        actualArray.addAll(1000);
        Array<Integer> expectedArray3 = new Array<>(Integer.class, 7);
        expectedArray3.set(0, 1);
        expectedArray3.set(1, 2);
        expectedArray3.set(2, 3);
        expectedArray3.set(3, 10);
        expectedArray3.set(4, 20);
        expectedArray3.set(5, 30);
        expectedArray3.set(6, 1000);
        Assertions.assertEquals(expectedArray3, actualArray,
                "Не верно работает метод addAll() при попытке добавить через него один элемент.");

        actualArray.addAll();
        actualArray.addAll(new Integer[0]);
        Assertions.assertEquals(expectedArray3, actualArray,
                "Метод addAll() при вызове без аргументов или с пустым массивом не должен оказывать\n " +
                        "никакого эффекта.");

        Array<Integer> array2 = new Array<>(Integer.class, 0);
        array2.addAll(1000);
        Array<Integer> expectedArray4 = new Array<>(Integer.class, 1);
        expectedArray4.set(0, 1000);
        Assertions.assertEquals(expectedArray4, array2,
                "Не верно работает метод addAll() при попытке добавить через него один элемент\n " +
                        "в пустой массив.");

        Array<Integer> array3 = new Array<>(Integer.class, 0);
        array3.addAll(null, 1,2,3,null,4,5,6,null);
        Array<Integer> expectedArray5 = new Array<>(Integer.class, 9);
        expectedArray5.set(0, null);
        expectedArray5.set(1, 1);
        expectedArray5.set(2, 2);
        expectedArray5.set(3, 3);
        expectedArray5.set(4, null);
        expectedArray5.set(5, 4);
        expectedArray5.set(6, 5);
        expectedArray5.set(7, 6);
        expectedArray5.set(8, null);
        Assertions.assertEquals(expectedArray5, array3,
                "Не верно работает метод addAll() при попытке добавить null элементы");
    }

    @Test
    public void addAll_Array() {
        Array<Integer> to = new Array<>(Integer.class, 0);
        Array<Integer> from = new Array<>(Integer.class, 0);
        from.addAll(1,2,3,4,5,6,7,8,9,10);
        to.addAll(from);
        Array<Integer> expectedArray = new Array<>(Integer.class, 0);
        expectedArray.addAll(1,2,3,4,5,6,7,8,9,10);
        Assertions.assertEquals(expectedArray, to,
                "Не верно работает метод addAll(Array) при добавлении элементов в пустой массив.");

        Array<Integer> to2 = new Array<>(Integer.class, 0);
        to2.addAll(1,2,3,4,5,6,7,8,9,10);
        Array<Integer> from2 = new Array<>(Integer.class, 0);
        from2.addAll(11,12,13,14,15,16,17);
        to2.addAll(from2);
        Array<Integer> expectedArray2 = new Array<>(Integer.class, 0);
        expectedArray2.addAll(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17);
        Assertions.assertEquals(expectedArray2, to2,
                "Не верно работает метод addAll(Array) при добавлении элементов в не пустой массив.");

        Array<Integer> to3 = new Array<>(Integer.class, 0);
        to3.addAll(1,2,3,4,5,6,7,8,9,10);
        Array<Integer> from3 = new Array<>(Integer.class, 1);
        from3.set(0, 1000);
        to3.addAll(from3);
        Array<Integer> expectedArray3 = new Array<>(Integer.class, 0);
        expectedArray3.addAll(1,2,3,4,5,6,7,8,9,10,1000);
        Assertions.assertEquals(expectedArray3, to3,
                "Не верно работает метод addAll(Array) при попытке добавить через него массив с " +
                        "одним элементом.");

        Array<Integer> to4 = new Array<>(Integer.class, 0);
        to4.addAll(1,2,3,4,5,6,7,8,9,10);
        Array<Integer> from4 = new Array<>(Integer.class, 0);
        to4.addAll(from4);
        Array<Integer> expectedArray4 = new Array<>(Integer.class, 0);
        expectedArray4.addAll(1,2,3,4,5,6,7,8,9,10);
        Assertions.assertEquals(expectedArray4, to4,
                "Метод addAll(Array) при вызове с пустым массивом в качестве аргумента " +
                        "не должен оказывать никакого эффекта.");

        Array<Integer> to5 = new Array<>(Integer.class, 0);
        Array<Integer> from5 = new Array<>(Integer.class, 1);
        from5.set(0, 1000);
        to5.addAll(from5);
        Array<Integer> expectedArray5 = new Array<>(Integer.class, 1);
        expectedArray5.set(0, 1000);
        Assertions.assertEquals(expectedArray5, to5,
                "Не верно работает метод addAll(Array) при попытке добавить через него один элемент\n " +
                        "в пустой массив.");

        Array<Integer> to6 = new Array<>(Integer.class, 0);
        to6.addAll(1,2,3,4,5,6,7,8,9,10);
        Array<Integer> from6 = new Array<>(Integer.class, 0);
        from6.addAll(11,null,12,null,13,null,null);
        to6.addAll(from6);
        Array<Integer> expectedArray6 = new Array<>(Integer.class, 0);
        expectedArray6.addAll(1,2,3,4,5,6,7,8,9,10,11,null,12,null,13,null,null);
        Assertions.assertEquals(expectedArray6, to6,
                "Не верно работает метод addAll(Array) если передаваемый массив содержит null элементы.");
    }

    @Test
    public void insert() {
        Array<Integer> emptyArray = new Array<>(Integer.class, 0);

        Assertions.assertThrows(ArrayIndexOutOfBoundsException.class, () -> emptyArray.insert(-1, 10),
                "Метод insert(T value, int index) должен проверять параметр index на\n " +
                        "принадлженость границам массива.");
        Assertions.assertThrows(ArrayIndexOutOfBoundsException.class, () -> emptyArray.insert(1, 10),
                "Метод insert(T value, int index) должен проверять параметр index на\n " +
                        "принадлженость границам массива.");

        emptyArray.insert(0, 10);
        Assertions.assertEquals(10, emptyArray.get(0));

        Array<Integer> array = new Array<>(Integer.class, 0);
        array.addAll(20, 30, 40, null, 60, 70);
        array.insert(0, 0);
        Array<Integer> expectedArray = new Array<>(Integer.class, 0);
        expectedArray.addAll(0, 20, 30, 40, null, 60, 70);
        Assertions.assertEquals(expectedArray, array,
                "Метод insert(T value, int index) при добавлении элемента в начало массива должен\n " +
                        "сдвигать все элементы вверх с сохранением их взаимного расположения.");

        Assertions.assertEquals(7, array.getLength(),
                "Метод insert(T value, int index) должен увеличеть длину массива на единицу.");

        Array<Integer> array2 = new Array<>(Integer.class, 0);
        array2.addAll(20, 30, 40, null, 60, 70);
        array2.insert(6, null);
        Array<Integer> expectedArray2 = new Array<>(Integer.class, 0);
        expectedArray2.addAll(20, 30, 40, null, 60, 70, null);
        Assertions.assertNull(array2.get(6),
                "Метод insert(T value, int index) должен позволять добавлять значение null.");
        Assertions.assertEquals(expectedArray2, array2,
                "Метод insert(T value, int index) при добавлении элемента в конец массива " +
                        "должен оставлять предыдущие элементы на своих местах.");
    }

    @Test
    public void binaryInsert_ArrayIsEmptyOrContainsOneItem() {
        Array<Integer> emptyArray = new Array<>(Integer.class, 0);
        Array<Integer> expectedArray = new Array<>(Integer.class, 1);
        expectedArray.set(0, 100);
        int insertIndex = emptyArray.binaryInsert(100, Integer::compare);
        Assertions.assertEquals(expectedArray, emptyArray,
                "Не верно работает метод binaryInsert в случае, когда исходный массив пуст.");
        Assertions.assertEquals(0, insertIndex,
                "Не верно работает метод binaryInsert в случае, когда исходный массив пуст.");

        Array<Integer> arrayWithOneItem = new Array<>(Integer.class, 1);
        arrayWithOneItem.set(0, 100);
        Array<Integer> expectedArray2 = new Array<>(Integer.class, 2);
        expectedArray2.set(0, 10);
        expectedArray2.set(1, 100);
        insertIndex = arrayWithOneItem.binaryInsert(10, Integer::compare);
        Assertions.assertEquals(expectedArray2, arrayWithOneItem,
                "Не верно работает метод binaryInsert в случае, когда исходный массив имеет один\n " +
                        "элемент и добавляемый элемент меньше его.");
        Assertions.assertEquals(0, insertIndex,
                "Не верно работает метод binaryInsert в случае, когда исходный массив имеет один\n " +
                        "элемент и добавляемый элемент меньше его.");

        Array<Integer> arrayWithOneItem2 = new Array<>(Integer.class, 1);
        arrayWithOneItem2.set(0, 100);
        Array<Integer> expectedArray3 = new Array<>(Integer.class, 2);
        expectedArray3.set(0, 100);
        expectedArray3.set(1, 1000);
        insertIndex = arrayWithOneItem2.binaryInsert(1000, Integer::compare);
        Assertions.assertEquals(expectedArray3, arrayWithOneItem2,
                "Не верно работает метод binaryInsert в случае, когда исходный массив имеет один\n " +
                        "элемент и добавляемый элемент больше его.");
        Assertions.assertEquals(1, insertIndex ,
                "Не верно работает метод binaryInsert в случае, когда исходный массив имеет один\n " +
                        "элемент и добавляемый элемент больше его.");

        Array<Integer> arrayWithOneItem3 = new Array<>(Integer.class, 1);
        arrayWithOneItem3.set(0, 100);
        Array<Integer> expectedArray4 = new Array<>(Integer.class, 2);
        expectedArray4.set(0, 100);
        expectedArray4.set(1, 100);
        insertIndex = arrayWithOneItem3.binaryInsert(100, Integer::compare);
        Assertions.assertEquals(expectedArray4, arrayWithOneItem3,
                "Не верно работает метод binaryInsert в случае, когда исходный массив имеет один\n " +
                        "элемент и добавляемый элемент равен ему.");
        Assertions.assertTrue(insertIndex == 0 || insertIndex == 1,
                "Не верно работает метод binaryInsert в случае, когда исходный массив имеет один\n " +
                        "элемент и добавляемый элемент равен ему.");
    }

    @Test
    public void binaryInsert_ArrayContainsTwoItem() {
        Array<Integer> array = new Array<>(Integer.class, 0);
        array.addAll(10, 100);
        Array<Integer> expectedArray = new Array<>(Integer.class, 0);
        expectedArray.addAll(2, 10, 100);
        int insertIndex = array.binaryInsert(2, Integer::compare);
        Assertions.assertEquals(expectedArray , array,
                "Не верно работает метод binaryInsert(), когда кол-во элементов массива равно двум,\n " +
                        "порядок возрастающий, добавляемый элемент меньше наименьшего.");
        Assertions.assertEquals(0, insertIndex,
                "Не верно работает метод binaryInsert(), когда кол-во элементов массива равно двум,\n " +
                        "порядок возрастающий, добавляемый элемент меньше наименьшего.");

        Array<Integer> array2 = new Array<>(Integer.class, 0);
        array2.addAll(10, 100);
        Array<Integer> expectedArray2 = new Array<>(Integer.class, 0);
        expectedArray2.addAll(10, 60, 100);
        insertIndex = array2.binaryInsert(60, Integer::compare);
        Assertions.assertEquals(expectedArray2 , array2,
                "Не верно работает метод binaryInsert(), когда кол-во элементов массива равно двум,\n " +
                        "порядок возрастающий, добавляемый элемент должен встать посередине.");
        Assertions.assertEquals(1, insertIndex,
                "Не верно работает метод binaryInsert(), когда кол-во элементов массива равно двум,\n " +
                        "порядок возрастающий, добавляемый элемент должен встать посередине.");

        Array<Integer> array3 = new Array<>(Integer.class, 0);
        array3.addAll(10, 100);
        Array<Integer> expectedArray3 = new Array<>(Integer.class, 0);
        expectedArray3.addAll(10, 100, 500);
        insertIndex = array3.binaryInsert(500, Integer::compare);
        Assertions.assertEquals(expectedArray3 , array3,
                "Не верно работает метод binaryInsert(), когда кол-во элементов массива равно двум,\n " +
                        "порядок возрастающий, добавляемый элемент больше наибольшего в массиве.");
        Assertions.assertEquals(2, insertIndex,
                "Не верно работает метод binaryInsert(), когда кол-во элементов массива равно двум,\n " +
                        "порядок возрастающий, добавляемый элемент больше наибольшего в массиве.");

        Array<Integer> array4 = new Array<>(Integer.class, 0);
        array4.addAll(10, 100);
        Array<Integer> expectedArray4 = new Array<>(Integer.class, 0);
        expectedArray4.addAll(10, 10, 100);
        insertIndex = array4.binaryInsert(10, Integer::compare);
        Assertions.assertEquals(expectedArray4, array4,
                "Не верно работает метод binaryInsert(), когда кол-во элементов массива равно двум,\n " +
                "порядок возрастающий, добавляемый элемент уже присутсвует в начале массива один раз.");
        Assertions.assertTrue(insertIndex == 0 || insertIndex == 1,
                "Не верно работает метод binaryInsert(), когда кол-во элементов массива равно двум,\n " +
                        "порядок возрастающий, добавляемый элемент уже присутсвует в начале массива один раз.");

        Array<Integer> array5 = new Array<>(Integer.class, 0);
        array5.addAll(10, 100);
        Array<Integer> expectedArray5 = new Array<>(Integer.class, 0);
        expectedArray5.addAll(10, 100, 100);
        insertIndex = array5.binaryInsert(100, Integer::compare);
        Assertions.assertEquals(expectedArray5, array5,
                "Не верно работает метод binaryInsert(), когда кол-во элементов массива равно двум,\n " +
                        "порядок возрастающий, добавляемый элемент уже присутсвует в конце массива один раз.");
        Assertions.assertTrue(insertIndex == 1 ||insertIndex == 2,
                "Не верно работает метод binaryInsert(), когда кол-во элементов массива равно двум,\n " +
                        "порядок возрастающий, добавляемый элемент уже присутсвует в конце массива один раз.");

        Array<Integer> array6 = new Array<>(Integer.class, 0);
        array6.addAll(10, 10);
        Array<Integer> expectedArray6 = new Array<>(Integer.class, 0);
        expectedArray6.addAll(10, 10, 10);
        insertIndex = array6.binaryInsert(10, Integer::compare);
        Assertions.assertEquals(expectedArray6, array6,
                "Не верно работает метод binaryInsert(), когда кол-во элементов массива равно двум,\n " +
                        "порядок возрастающий, все элемнты массива одинаковы и добавляемый элемент присутсвует.");
        Assertions.assertTrue(insertIndex >= 0 && insertIndex <= 2,
                "Не верно работает метод binaryInsert(), когда кол-во элементов массива равно двум,\n " +
                        "порядок возрастающий, все элемнты массива одинаковы и добавляемый элемент присутсвует.");
    }

    @Test
    public void binaryInsert_NumberOfItemsIsEvenAndMoreThenTwo() {
        Array<Integer> array = new Array<>(Integer.class, 0);
        array.addAll(1,2,3,4,5,6,7,8);
        Array<Integer> expectedArray = new Array<>(Integer.class, 0);
        expectedArray.addAll(-10,1,2,3,4,5,6,7,8);
        int insertIndex = array.binaryInsert(-10, Integer::compare);
        Assertions.assertEquals(expectedArray, array,
                "Не верно работает метод binaryInsert(), если кол-во элементов массива\n " +
                        "больше двух и четно, порядок возрастающий, добавляемый элемент отсутсвует\n " +
                        "и меньше наименьшего элемента в массиве.");
        Assertions.assertEquals(0, insertIndex,
                "Не верно работает метод binaryInsert(), если кол-во элементов массива\n " +
                        "больше двух и четно, порядок возрастающий, добавляемый элемент отсутсвует\n " +
                        "и меньше наименьшего элемента в массиве.");

        Array<Integer> array2 = new Array<>(Integer.class, 0);
        array2.addAll(1,2,3,4,6,7,8,9);
        Array<Integer> expectedArray2 = new Array<>(Integer.class, 0);
        expectedArray2.addAll(1,2,3,4,5,6,7,8,9);
        insertIndex = array2.binaryInsert(5, Integer::compare);
        Assertions.assertEquals(expectedArray2, array2,
                "Не верно работает метод binaryInsert(), если кол-во элементов массива\n " +
                        "больше двух и четно, порядок возрастающий, добавляемый элемент отсутсвует\n " +
                        "и должен быть добавлен где-то в средину массива.");
        Assertions.assertEquals(4, insertIndex,
                "Не верно работает метод binaryInsert(), если кол-во элементов массива\n " +
                        "больше двух и четно, порядок возрастающий, добавляемый элемент отсутсвует\n " +
                        "и должен быть добавлен где-то в средину массива.");

        Array<Integer> array3 = new Array<>(Integer.class, 0);
        array3.addAll(1,2,3,4,5,6,7,8);
        Array<Integer> expectedArray3 = new Array<>(Integer.class, 0);
        expectedArray3.addAll(1,2,3,4,5,6,7,8,9);
        insertIndex = array3.binaryInsert(9, Integer::compare);
        Assertions.assertEquals(expectedArray3, array3,
                "Не верно работает метод binaryInsert(), если кол-во элементов массива\n " +
                        "больше двух и четно, порядок возрастающий, добавляемый элемент отсутсвует\n " +
                        "и больше наибольшего элемента в массиве.");
        Assertions.assertEquals(8, insertIndex,
                "Не верно работает метод binaryInsert(), если кол-во элементов массива\n " +
                        "больше двух и четно, порядок возрастающий, добавляемый элемент отсутсвует\n " +
                        "и больше наибольшего элемента в массиве.");

        Array<Integer> array4 = new Array<>(Integer.class, 0);
        array4.addAll(1,2,3,4,5,6,7,8);
        Array<Integer> expectedArray4 = new Array<>(Integer.class, 0);
        expectedArray4.addAll(1,1,2,3,4,5,6,7,8);
        insertIndex = array4.binaryInsert(1, Integer::compare);
        Assertions.assertEquals(expectedArray4, array4,
                "Не верно работает метод binaryInsert(), если кол-во элементов массива\n " +
                        "больше двух и четно, порядок возрастающий, добавляемый элемент присутсвует\n " +
                        "один раз и находится в начале массива.");
        Assertions.assertTrue(insertIndex == 0 || insertIndex == 1,
                "Не верно работает метод binaryInsert(), если кол-во элементов массива\n " +
                        "больше двух и четно, порядок возрастающий, добавляемый элемент присутсвует\n " +
                        "один раз и находится в начале массива.");

        Array<Integer> array5 = new Array<>(Integer.class, 0);
        array5.addAll(1,2,3,4,5,6,7,8);
        Array<Integer> expectedArray5 = new Array<>(Integer.class, 0);
        expectedArray5.addAll(1,2,3,4,5,5,6,7,8);
        insertIndex = array5.binaryInsert(5, Integer::compare);
        Assertions.assertEquals(expectedArray5, array5,
                "Не верно работает метод binaryInsert(), если кол-во элементов массива\n " +
                        "больше двух и четно, порядок возрастающий, добавляемый элемент присутсвует\n " +
                        "один раз и находится где-то в середине массива.");
        Assertions.assertTrue(insertIndex == 4 || insertIndex == 5,
                "Не верно работает метод binaryInsert(), если кол-во элементов массива\n " +
                        "больше двух и четно, порядок возрастающий, добавляемый элемент присутсвует\n " +
                        "один раз и находится где-то в середине массива.");

        Array<Integer> array6 = new Array<>(Integer.class, 0);
        array6.addAll(1,2,3,4,5,6,7,8);
        Array<Integer> expectedArray6 = new Array<>(Integer.class, 0);
        expectedArray6.addAll(1,2,3,4,5,6,7,8,8);
        insertIndex = array6.binaryInsert(8, Integer::compare);
        Assertions.assertEquals(expectedArray6, array6,
                "Не верно работает метод binaryInsert(), если кол-во элементов массива\n " +
                        "больше двух и четно, порядок возрастающий, добавляемый элемент присутсвует\n " +
                        "один раз и находится в конце массива.");
        Assertions.assertTrue(insertIndex == 7 || insertIndex == 8,
                "Не верно работает метод binaryInsert(), если кол-во элементов массива\n " +
                        "больше двух и четно, порядок возрастающий, добавляемый элемент присутсвует\n " +
                        "один раз и находится в конце массива.");

        Array<Integer> array7 = new Array<>(Integer.class, 0);
        array7.addAll(1,1,1,2,3,4,5,6,7,8);
        Array<Integer> expectedArray7 = new Array<>(Integer.class, 0);
        expectedArray7.addAll(1,1,1,1,2,3,4,5,6,7,8);
        insertIndex = array7.binaryInsert(1, Integer::compare);
        Assertions.assertEquals(expectedArray7, array7,
                "Не верно работает метод binaryInsert(), если кол-во элементов массива\n " +
                        "больше двух и четно, порядок возрастающий, добавляемый элемент присутсвует\n " +
                        "несколько раз и последовательность находится в начале массива.");
        Assertions.assertTrue(insertIndex >= 0 && insertIndex <=3,
                "Не верно работает метод binaryInsert(), если кол-во элементов массива\n " +
                        "больше двух и четно, порядок возрастающий, добавляемый элемент присутсвует\n " +
                        "несколько раз и последовательность находится в начале массива.");

        Array<Integer> array8 = new Array<>(Integer.class, 0);
        array8.addAll(1,2,3,4,5,5,5,6,7,8);
        Array<Integer> expectedArray8 = new Array<>(Integer.class, 0);
        expectedArray8.addAll(1,2,3,4,5,5,5,5,6,7,8);
        insertIndex = array8.binaryInsert(5, Integer::compare);
        Assertions.assertEquals(expectedArray8, array8,
                "Не верно работает метод binaryInsert(), если кол-во элементов массива\n " +
                        "больше двух и четно, порядок возрастающий, добавляемый элемент присутсвует\n " +
                        "несколько раз и последовательность находится где-то в середине массива.");
        Assertions.assertTrue(insertIndex >= 4 && insertIndex <= 7,
                "Не верно работает метод binaryInsert(), если кол-во элементов массива\n " +
                        "больше двух и четно, порядок возрастающий, добавляемый элемент присутсвует\n " +
                        "несколько раз и последовательность находится где-то в середине массива.");

        Array<Integer> array9 = new Array<>(Integer.class, 0);
        array9.addAll(1,2,3,4,5,6,7,8,8,8);
        Array<Integer> expectedArray9 = new Array<>(Integer.class, 0);
        expectedArray9.addAll(1,2,3,4,5,6,7,8,8,8,8);
        insertIndex = array9.binaryInsert(8, Integer::compare);
        Assertions.assertEquals(expectedArray9, array9,
                "Не верно работает метод binaryInsert(), если кол-во элементов массива\n " +
                        "больше двух и четно, порядок возрастающий, добавляемый элемент присутсвует\n " +
                        "несколько раз и последовательность находится в конце массива.");
        Assertions.assertTrue(insertIndex >= 7 && insertIndex <= 10,
                "Не верно работает метод binaryInsert(), если кол-во элементов массива\n " +
                        "больше двух и четно, порядок возрастающий, добавляемый элемент присутсвует\n " +
                        "несколько раз и последовательность находится в конце массива.");

        Array<Integer> array10 = new Array<>(Integer.class, 0);
        array10.addAll(1,1,1,1,1,1,1,1);
        Array<Integer> expectedArray10 = new Array<>(Integer.class, 0);
        expectedArray10.addAll(1,1,1,1,1,1,1,1,1);
        insertIndex = array10.binaryInsert(1, Integer::compare);
        Assertions.assertEquals(expectedArray10, array10,
                "Не верно работает метод binaryInsert(), если кол-во элементов массива\n " +
                        "больше двух и четно, порядок возрастающий, добавляемый элемент присутсвует\n " +
                        "и все элементы массива одинаковы.");
        Assertions.assertTrue(insertIndex >= 0 && insertIndex <= 8,
                "Не верно работает метод binaryInsert(), если кол-во элементов массива\n " +
                        "больше двух и четно, порядок возрастающий, добавляемый элемент присутсвует\n " +
                        "и все элементы массива одинаковы.");
    }

    @Test
    public void binaryInsert_NumberOfItemsIsOddAndMoreThenTwo() {
        Array<Integer> array = new Array<>(Integer.class, 0);
        array.addAll(1,2,3,4,5,6,7,8,9);
        Array<Integer> expectedArray = new Array<>(Integer.class, 0);
        expectedArray.addAll(-10,1,2,3,4,5,6,7,8,9);
        int insertIndex = array.binaryInsert(-10, Integer::compare);
        Assertions.assertEquals(expectedArray, array,
                "Не верно работает метод binaryInsert(), если кол-во элементов массива\n " +
                        "больше двух и не четно, порядок возрастающий, добавляемый элемент отсутсвует\n " +
                        "и меньше наименьшего элемента в массиве.");
        Assertions.assertEquals(0, insertIndex,
                "Не верно работает метод binaryInsert(), если кол-во элементов массива\n " +
                        "больше двух и не четно, порядок возрастающий, добавляемый элемент отсутсвует\n " +
                        "и меньше наименьшего элемента в массиве.");

        Array<Integer> array2 = new Array<>(Integer.class, 0);
        array2.addAll(1,2,3,4,5,7,8,9,10);
        Array<Integer> expectedArray2 = new Array<>(Integer.class, 0);
        expectedArray2.addAll(1,2,3,4,5,6,7,8,9,10);
        insertIndex = array2.binaryInsert(6, Integer::compare);
        Assertions.assertEquals(expectedArray2, array2,
                "Не верно работает метод binaryInsert(), если кол-во элементов массива\n " +
                        "больше двух и не четно, порядок возрастающий, добавляемый элемент отсутсвует\n " +
                        "и должен быть добавлен где-то в средину массива.");
        Assertions.assertEquals(5, insertIndex,
                "Не верно работает метод binaryInsert(), если кол-во элементов массива\n " +
                        "больше двух и не четно, порядок возрастающий, добавляемый элемент отсутсвует\n " +
                        "и должен быть добавлен где-то в средину массива.");

        Array<Integer> array3 = new Array<>(Integer.class, 0);
        array3.addAll(1,2,3,4,5,6,7,8,9);
        Array<Integer> expectedArray3 = new Array<>(Integer.class, 0);
        expectedArray3.addAll(1,2,3,4,5,6,7,8,9,10);
        insertIndex = array3.binaryInsert(10, Integer::compare);
        Assertions.assertEquals(expectedArray3, array3,
                "Не верно работает метод binaryInsert(), если кол-во элементов массива\n " +
                        "больше двух и не четно, порядок возрастающий, добавляемый элемент отсутсвует\n " +
                        "и больше наибольшего элемента в массиве.");
        Assertions.assertEquals(9, insertIndex,
                "Не верно работает метод binaryInsert(), если кол-во элементов массива\n " +
                        "больше двух и не четно, порядок возрастающий, добавляемый элемент отсутсвует\n " +
                        "и больше наибольшего элемента в массиве.");

        Array<Integer> array4 = new Array<>(Integer.class, 0);
        array4.addAll(1,2,3,4,5,6,7,8,9);
        Array<Integer> expectedArray4 = new Array<>(Integer.class, 0);
        expectedArray4.addAll(1,1,2,3,4,5,6,7,8,9);
        insertIndex = array4.binaryInsert(1, Integer::compare);
        Assertions.assertEquals(expectedArray4, array4,
                "Не верно работает метод binaryInsert(), если кол-во элементов массива\n " +
                        "больше двух и не четно, порядок возрастающий, добавляемый элемент присутсвует\n " +
                        "один раз и находится в начале массива.");
        Assertions.assertTrue(insertIndex == 0 || insertIndex == 1,
                "Не верно работает метод binaryInsert(), если кол-во элементов массива\n " +
                        "больше двух и не четно, порядок возрастающий, добавляемый элемент присутсвует\n " +
                        "один раз и находится в начале массива.");

        Array<Integer> array5 = new Array<>(Integer.class, 0);
        array5.addAll(1,2,3,4,5,6,7,8,9);
        Array<Integer> expectedArray5 = new Array<>(Integer.class, 0);
        expectedArray5.addAll(1,2,3,4,5,5,6,7,8,9);
        insertIndex = array5.binaryInsert(5, Integer::compare);
        Assertions.assertEquals(expectedArray5, array5,
                "Не верно работает метод binaryInsert(), если кол-во элементов массива\n " +
                        "больше двух и не четно, порядок возрастающий, добавляемый элемент присутсвует\n " +
                        "один раз и находится где-то в середине массива.");
        Assertions.assertTrue(insertIndex == 4 || insertIndex == 5,
                "Не верно работает метод binaryInsert(), если кол-во элементов массива\n " +
                        "больше двух и не четно, порядок возрастающий, добавляемый элемент присутсвует\n " +
                        "один раз и находится где-то в середине массива.");

        Array<Integer> array6 = new Array<>(Integer.class, 0);
        array6.addAll(1,2,3,4,5,6,7,8,9);
        Array<Integer> expectedArray6 = new Array<>(Integer.class, 0);
        expectedArray6.addAll(1,2,3,4,5,6,7,8,9,9);
        insertIndex = array6.binaryInsert(9, Integer::compare);
        Assertions.assertEquals(expectedArray6, array6,
                "Не верно работает метод binaryInsert(), если кол-во элементов массива\n " +
                        "больше двух и не четно, порядок возрастающий, добавляемый элемент присутсвует\n " +
                        "один раз и находится в конце массива.");
        Assertions.assertTrue(insertIndex == 8 || insertIndex == 9,
                "Не верно работает метод binaryInsert(), если кол-во элементов массива\n " +
                        "больше двух и не четно, порядок возрастающий, добавляемый элемент присутсвует\n " +
                        "один раз и находится в конце массива.");

        Array<Integer> array7 = new Array<>(Integer.class, 0);
        array7.addAll(1,1,1,2,3,4,5,6,7,8,9);
        Array<Integer> expectedArray7 = new Array<>(Integer.class, 0);
        expectedArray7.addAll(1,1,1,1,2,3,4,5,6,7,8,9);
        insertIndex = array7.binaryInsert(1, Integer::compare);
        Assertions.assertEquals(expectedArray7, array7,
                "Не верно работает метод binaryInsert(), если кол-во элементов массива\n " +
                        "больше двух и не четно, порядок возрастающий, добавляемый элемент присутсвует\n " +
                        "несколько раз и последовательность находится в начале массива.");
        Assertions.assertTrue(insertIndex >= 0 && insertIndex <= 3,
                "Не верно работает метод binaryInsert(), если кол-во элементов массива\n " +
                        "больше двух и не четно, порядок возрастающий, добавляемый элемент присутсвует\n " +
                        "несколько раз и последовательность находится в начале массива.");

        Array<Integer> array8 = new Array<>(Integer.class, 0);
        array8.addAll(1,2,3,4,5,5,5,6,7,8,9);
        Array<Integer> expectedArray8 = new Array<>(Integer.class, 0);
        expectedArray8.addAll(1,2,3,4,5,5,5,5,6,7,8,9);
        insertIndex = array8.binaryInsert(5, Integer::compare);
        Assertions.assertEquals(expectedArray8, array8,
                "Не верно работает метод binaryInsert(), если кол-во элементов массива\n " +
                        "больше двух и не четно, порядок возрастающий, добавляемый элемент присутсвует\n " +
                        "несколько раз и последовательность находится где-то в середине массива.");
        Assertions.assertTrue(insertIndex >= 4 && insertIndex <= 7,
                "Не верно работает метод binaryInsert(), если кол-во элементов массива\n " +
                        "больше двух и не четно, порядок возрастающий, добавляемый элемент присутсвует\n " +
                        "несколько раз и последовательность находится где-то в середине массива.");

        Array<Integer> array9 = new Array<>(Integer.class, 0);
        array9.addAll(1,2,3,4,5,6,7,8,9,9,9);
        Array<Integer> expectedArray9 = new Array<>(Integer.class, 0);
        expectedArray9.addAll(1,2,3,4,5,6,7,8,9,9,9,9);
        insertIndex = array9.binaryInsert(9, Integer::compare);
        Assertions.assertEquals(expectedArray9, array9,
                "Не верно работает метод binaryInsert(), если кол-во элементов массива\n " +
                        "больше двух и не четно, порядок возрастающий, добавляемый элемент присутсвует\n " +
                        "несколько раз и последовательность находится в конце массива.");
        Assertions.assertTrue(insertIndex >= 8 && insertIndex <= 11,
                "Не верно работает метод binaryInsert(), если кол-во элементов массива\n " +
                        "больше двух и не четно, порядок возрастающий, добавляемый элемент присутсвует\n " +
                        "несколько раз и последовательность находится в конце массива.");

        Array<Integer> array10 = new Array<>(Integer.class, 0);
        array10.addAll(1,1,1,1,1,1,1,1,1);
        Array<Integer> expectedArray10 = new Array<>(Integer.class, 0);
        expectedArray10.addAll(1,1,1,1,1,1,1,1,1,1);
        insertIndex = array10.binaryInsert(1, Integer::compare);
        Assertions.assertEquals(expectedArray10, array10,
                "Не верно работает метод binaryInsert(), если кол-во элементов массива\n " +
                        "больше двух и не четно, порядок возрастающий, добавляемый элемент присутсвует\n " +
                        "и все элементы массива одинаковы.");
        Assertions.assertTrue(insertIndex >= 0 && insertIndex <= 9,
                "Не верно работает метод binaryInsert(), если кол-во элементов массива\n " +
                        "больше двух и не четно, порядок возрастающий, добавляемый элемент присутсвует\n " +
                        "и все элементы массива одинаковы.");
    }

    @Test
    public void quickRemove() {
        Array<Integer> array = new Array<>(Integer.class, 0);
        array.addAll(0,1,2,3,4,5,6,7,8,9);

        Assertions.assertThrows(ArrayIndexOutOfBoundsException.class, ()-> array.quickRemove(-1),
                "Метод quickRemove(int index) должен проверять индекс на принадлежность границам массива.");
        Assertions.assertThrows(ArrayIndexOutOfBoundsException.class, ()-> array.quickRemove(10),
                "Метод quickRemove(int index) должен проверять индекс на принадлежность границам массива.");

        Assertions.assertEquals(3, array.quickRemove(3),
                "Метод quickRemove(int index) должен возвращать удаляемый элемент.");
        Assertions.assertEquals(9, array.get(3),
                "После удаления элемента с помощью quickRemove(int index), на его место должен встать " +
                        "последний элемент.");
        Assertions.assertEquals(9, array.getLength(),
                "После вызова quickRemove(int index) длина массива должна уменьшится на единицу.");

        Array<Integer> array2 = new Array<>(Integer.class, 0);
        array2.addAll(0,1,2,3,4,5,6,7,8,9);
        Assertions.assertEquals(9, array2.quickRemove(9),
                "Метод quickRemove(int index) должен возвращать удаляемый элемент, также, когда он " +
                        "является последним элементом.");
        Assertions.assertThrows(ArrayIndexOutOfBoundsException.class, ()-> array2.get(9),
                "После удаления последнего элемента с помощью quickRemove(int index) при обращении к " +
                        "элементу по индексу используемого в вызове метода quickRemove(int index) должно " +
                        "генерироваться исключение.");
        Assertions.assertEquals(9, array2.getLength(),
                "После удаления последнего элемента с помощью quickRemove(int index) длина массива должна " +
                        "уменьшится на единицу.");
        array2.expandTo(10);
        Assertions.assertNull(array2.get(9),
                "После удаления последнего элемента с помощью quickRemove(int index) во внутренем массиве " +
                        "по указанному индексу должно быть записано значение null.");

        Array<Integer> array3 = new Array<>(Integer.class, 0);
        array3.addAll(1,2,3);
        array3.quickRemove(0);
        array3.quickRemove(0);
        array3.quickRemove(0);
        Assertions.assertEquals(0, array3.getLength(),
                "С помощью метода quickRemove(int index) должно быть возможно удалить все элементы массива.");
        array3.expandTo(3);
        Assertions.assertNull(array3.get(0),
                "После удаления всех элементов массива с помощью quickRemove(int index) во внутренем массиве " +
                        "для каждого из них должно быть записано значение null.");
        Assertions.assertNull(array3.get(1),
                "После удаления всех элементов массива с помощью quickRemove(int index) во внутренем массиве " +
                        "для каждого из них должно быть записано значение null.");
        Assertions.assertNull(array3.get(2),
                "После удаления всех элементов массива с помощью quickRemove(int index) во внутренем массиве " +
                        "для каждого из них должно быть записано значение null.");
    }

    @Test
    public void orderedRemove() {
        Array<String> array = new Array<>(String.class, 0);
        array.addAll("0","1","2","3");

        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> array.orderedRemove(-1));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> array.orderedRemove(4));

        Assertions.assertEquals("1", array.orderedRemove(1),
                "Метод orderedRemove(int index) должен возвращать удаляемое значение.");
        Assertions.assertEquals("0", array.get(0));
        Assertions.assertEquals("2", array.get(1));
        Assertions.assertEquals("3", array.get(2));
        Assertions.assertEquals(3, array.getLength(),
                "После вызова orderedRemove(int index) длина массива должна уменьшится на единицу.");

        Assertions.assertEquals("3", array.orderedRemove(2),
                "Метод orderedRemove(int index) не верно работает при удалении последнего элемента.");
        Assertions.assertEquals(2, array.getLength(),
                "Метод orderedRemove(int index) не верно работает при удалении последнего элемента.");
    }

    @Test
    public void clear() {
        Array<Integer> array = new Array<>(Integer.class, 0);
        array.addAll(1,2,3,4,5,6,7,8,9,10);

        array.clear();

        Assertions.assertEquals(0, array.getLength(),
                "После вызова метода clear() длина массива должна равняться 0.");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> array.get(0),
                "После вызова метода clear() при попытке обратиться к любому индексу должно " +
                        "генерироваться исключение.");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> array.set(9, 1000),
                "После вызова метода clear() при попытке обраттиться к любому индексу должно " +
                        "генерироваться исключение.");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> array.quickRemove(0),
                "После вызова метода clear() при попытке обраттиться к любому индексу должно " +
                        "генерироваться исключение.");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> array.orderedRemove(9),
                "После вызова метода clear() при попытке обраттиться к любому индексу должно " +
                        "генерироваться исключение.");

        array.expandTo(3);
        Assertions.assertNull(array.get(0),
                "После вызова метода clear() все элементы внутреннего массива должны иметь значение null.");
        Assertions.assertNull(array.get(1),
                "После вызова метода clear() все элементы внутреннего массива должны иметь значение null.");
        Assertions.assertNull(array.get(2),
                "После вызова метода clear() все элементы внутреннего массива должны иметь значение null.");
    }

    @Test
    public void sort() {
        Array<Integer> array = new Array<>(Integer.class, 0);
        Array<Integer> expectedArray = new Array<>(Integer.class, 0);
        array.sort(Integer::compare);
        Assertions.assertEquals(expectedArray, array,
                "Не верно работает сортировка, если массив пуст.");

        Array<Integer> array2 = new Array<>(Integer.class, 1);
        array2.set(0, 100);
        Array<Integer> expectedArray2 = new Array<>(Integer.class, 1);
        expectedArray2.set(0, 100);
        array2.sort(Integer::compare);
        Assertions.assertEquals(expectedArray2, array2,
                "Не верно работает метод sort(), если кол-во элементов равно одному.");

        Array<Integer> array3 = new Array<>(Integer.class, 0);
        array3.addAll(100, 12);
        Array<Integer> expectedArray3 = new Array<>(Integer.class, 0);
        expectedArray3.addAll(12, 100);
        array3.sort(Integer::compare);
        Assertions.assertEquals(expectedArray3, array3,
                "Не верно работает метод sort(), если кол-во элементов равно днум, они уникальны,\n " +
                        "порядок обратен заданому порядку сортировки.");

        Array<Integer> array4 = new Array<>(Integer.class, 0);
        Array<Integer> expectedArray4 = new Array<>(Integer.class, 0);
        array4.addAll(12, 100);
        expectedArray4.addAll(12, 100);
        array4.sort(Integer::compare);
        Assertions.assertEquals(expectedArray4, array4,
                "Не верно работает метод sort(), если кол-во элементов равно днум, они уникальны,\n " +
                        "порядок соответсвует заданому порядку сортировки.");

        Array<Integer> array5 = new Array<>(Integer.class, 0);
        Array<Integer> expectedArray5 = new Array<>(Integer.class, 0);
        array5.addAll(100, 100);
        expectedArray5.addAll(100, 100);
        array5.sort(Integer::compare);
        Assertions.assertEquals(expectedArray5, array5,
                "Не верно работает метод sort(), если кол-во элементов равно днум, они одинаковы.");

        Array<Integer> array6 = new Array<>(Integer.class, 0);
        array6.addAll(0,-10,3,4,1,120,99,76,75,1000);
        Array<Integer> expectedArray6 = new Array<>(Integer.class, 0);
        expectedArray6.addAll(-10,0,1,3,4,75,76,99,120,1000);
        array6.sort(Integer::compare);
        Assertions.assertEquals(expectedArray6, array6,
                "Не верно работает метод сорт, если кол-во элементов массива больше двух и четно,\n " +
                        "элементы уникальны, порядок случаен.");

        Array<Integer> array7 = new Array<>(Integer.class, 0);
        array7.addAll(0,-10,99,4,1,99,99,76,75,1000);
        Array<Integer> expectedArray7 = new Array<>(Integer.class, 0);
        expectedArray7.addAll(-10,0,1,4,75,76,99,99,99,1000);
        array7.sort(Integer::compare);
        Assertions.assertEquals(expectedArray7, array7,
                "Не верно работает метод сорт, если кол-во элементов массива больше двух и четно,\n " +
                        "есть повторяющиеся элементы, порядок случаен.");

        Array<Integer> array8 = new Array<>(Integer.class, 0);
        array8.addAll(1,1,1,1,1,1,1,1);
        Array<Integer> expectedArray8 = new Array<>(Integer.class, 0);
        expectedArray8.addAll(1,1,1,1,1,1,1,1);
        array8.sort(Integer::compare);
        Assertions.assertEquals(expectedArray8, array8,
                "Не верно работает метод сорт, если кол-во элементов массива больше двух и четно,\n " +
                        "все элементы одинаковы.");

        Array<Integer> array9 = new Array<>(Integer.class, 0);
        array9.addAll(1,2,3,4,5,6,7,8,9,10);
        Array<Integer> expectedArray9 = new Array<>(Integer.class, 0);
        expectedArray9.addAll(1,2,3,4,5,6,7,8,9,10);
        array9.sort(Integer::compare);
        Assertions.assertEquals(expectedArray9, array9,
                "Не верно работает метод сорт, если кол-во элементов массива больше двух и четно,\n " +
                        "элементы уникальны, порядок соответствует заданому.");

        Array<Integer> array10 = new Array<>(Integer.class, 0);
        array10.addAll(10,9,8,7,6,5,4,3,2,1);
        Array<Integer> expectedArray10 = new Array<>(Integer.class, 0);
        expectedArray10.addAll(1,2,3,4,5,6,7,8,9,10);
        array10.sort(Integer::compare);
        Assertions.assertEquals(expectedArray10, array10,
                "Не верно работает метод сорт, если кол-во элементов массива больше двух и четно,\n " +
                        "элементы уникальны, порядок обратен заданому.");
    }

    @Test
    public void linearSearch() {
        Array<Integer> array = new Array<>(Integer.class, 0);
        array.addAll(0,1,2,3);

        Assertions.assertEquals(0, array.linearSearch(0));
        Assertions.assertEquals(1, array.linearSearch(1));
        Assertions.assertEquals(2, array.linearSearch(2));
        Assertions.assertEquals(3, array.linearSearch(3));
        Assertions.assertEquals(-1, array.linearSearch(100));

        Array<Integer> array2 = new Array<>(Integer.class, 0);
        array2.addAll(0,null,null,3);
        Assertions.assertEquals(1, array2.linearSearch(null),
                "Метод linearSearch(T value) должен уметь находить индекс для null значений, если они " +
                        "присутсвуют в массиве.");

        Array<Integer> array3 = new Array<>(Integer.class, 0);
        array3.addAll(0,1,1,1,1,2,3,4,4,4,4,5);
        Assertions.assertEquals(1, array3.linearSearch(1),
                "Если исклмый элемент присутсвует несколько раз в массиве, linearSearch(T value) должен " +
                        "возвращать индекс первого встретевшегося элемента с таким значением.");
        Assertions.assertEquals(7, array3.linearSearch(4),
                "Если исклмый элемент присутсвует несколько раз в массиве, linearSearch(T value) должен " +
                        "возвращать индекс первого встретевшегося элемента с таким значением.");
    }

    @Test
    public void linearSearch_Interval() {
        Array<Integer> array = new Array<>(Integer.class, 0);
        array.addAll(0,1,2,3,null,5,6,7,null,9);

        Assertions.assertEquals(4, array.linearSearch(null, 2, 10),
                "Метод linearSearch(T value, int fromIndex, int toIndex) должен возвращать индекс " +
                        "первого встретевшегося элемента с начала интервала поиска, значение которого равно " +
                        "значению искомого элемента.");
        Assertions.assertEquals(2, array.linearSearch(2, 2, 10),
                "Если искомый элемент на промежутке [fromIndex, toIndex) встречается впервые под индексом " +
                        "fromIndex, метод linearSearch(T value, int fromIndex, int toIndex) должен возвращать " +
                        "индекс равный fromIndex.");
        Assertions.assertEquals(-1, array.linearSearch(2, 3, 10),
                "Метод linearSearch(T value, int fromIndex, int toIndex) должен возвращать -1, если " +
                        "искомый элемент присутвует в массиве, но отсутсует в заданном интервале поиска.");
        Assertions.assertEquals(-1, array.linearSearch(12, 0, 10),
                "Метод linearSearch(T value, int fromIndex, int toIndex) должен возвращать -1, если искомый " +
                        "элемент отсутвует в массиве.");
        Assertions.assertEquals(-1, array.linearSearch(1, 1, 1),
                "Если fromIndex == toIndex, метод linearSearch(T value, int fromIndex, int toIndex) " +
                        "должен возвращать -1.");

        Assertions.assertThrows(ArrayIndexOutOfBoundsException.class,
                ()-> array.linearSearch(0, -1, 11));
        Assertions.assertThrows(ArrayIndexOutOfBoundsException.class,
                ()-> array.linearSearch(0, 0, 11));
        Assertions.assertThrows(ArrayIndexOutOfBoundsException.class,
                ()-> array.linearSearch(0, 10, 0));
    }

    @Test
    public void binarySearch_ArrayIsEmptyOrContainsOneItem() {
        Array<Integer> emptyArray = new Array<>(Integer.class, 0);
        Assertions.assertEquals(-1, emptyArray.binarySearch(0, Integer::compare),
                "Если массив пустой, метод binarySearch() должен возвращать -1 для любого значения.");

        Array<Integer> array = new Array<>(Integer.class, 0);
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
        Array<Integer> array = new Array<>(Integer.class, 0);
        array.addAll(0, 10);

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


        Array<Integer> array2 = new Array<>(Integer.class, 0);
        array2.addAll(0,10,20,30,40,50,60,70,80,90);
        Assertions.assertEquals(-1, array2.binarySearch(-10, Integer::compare),
                "Метод binarySearch() должен возвращать -1, если искомый элемент\n " +
                        "меньше наименьшего элемента в массиве имеющего больше двух элементов,\n " +
                        "при этом число элементов четное, а их порядок возрастающий.");
        Assertions.assertEquals(-1, array2.binarySearch(100, Integer::compare),
                "Метод binarySearch() должен возвращать -1, если искомый элемент\n " +
                        "больше наибольшего элемента в массиве имеющего больше двух элементов,\n " +
                        "при этом число элементов четное, а их порядок возрастающий.");
        Assertions.assertEquals(-1, array2.binarySearch(57, Integer::compare),
                "Метод binarySearch() должен возвращать -1, если искомый элемент\n " +
                        "отсутсвует в массиве имеющем больше двух элементов(число\n " +
                        "элементов четное, а их порядок возрастающий), при этом искомый элемент\n " +
                        "мог бы расположиться где-то посередине.");
        Assertions.assertEquals(0, array2.binarySearch(0, Integer::compare),
                "Метод binarySearch() должен возвращать 0, если искомый элемент\n " +
                        "присутствует в единственном экземпляре и находится в начале массива,\n " +
                        "имеющем больше двух элементов(число элементов четное, а их порядок\n" +
                        "возрастающий).");
        Assertions.assertEquals(9, array2.binarySearch(90, Integer::compare),
                "Метод binarySearch() должен возвращать последний индекс массива,\n " +
                        "если искомый элемент присутствует в единственном экземпляре и находится\n " +
                        "в конце массива, имеющем больше двух элементов(число элементов четное,\n " +
                        "а их порядок возрастающий).");
        Assertions.assertEquals(5, array2.binarySearch(50, Integer::compare),
                "Метод binarySearch() должен правильно находить индекс искомого элемента,\n " +
                        "если тот присутсвует в массиве в единтсвенном экземпляре имеющем больше\n " +
                        "двух элементов(число элементов четное, а их порядок возрастающий) и находится\n " +
                        "примерно где-то посередине.");
        for(int i = 0; i < array2.getLength(); i++) {
            Assertions.assertEquals(i, array2.binarySearch(i * 10, Integer::compare),
                    "Метод binarySearch должен находить индекс каждого элемента, присутсвующего\n " +
                            "в массиве один раз, с учетом того, что число элементов массива четно,\n " +
                            "порядок возрастающий. Элемент для которого данный метод работает неправильно,\n " +
                            "равен " + (i * 10));
        }

        Array<Integer> array3 = new Array<>(Integer.class, 0);
        array3.addAll(10,10,10,30,40,50,60,70,80,90);
        Assertions.assertNotEquals(-1, array3.binarySearch(10, Integer::compare),
                "Метод binarySearch() не должен возвращать -1, если искомый элемент\n " +
                        "присутсвует в массиве имеющем больше двух элементов(число элементов\n " +
                        "четное, а их порядок возрастающий) несколько раз, и последовательность\n " +
                        "из элементов равных искомому начинается с начала массива.");
        Assertions.assertEquals(10, array3.get(array3.binarySearch(10, Integer::compare)),
                "Метод binarySearch() должен возвращать индекс элемента имеющего то же значение," +
                        "что и искомый элемент, в случае - если искомый элемент присутсвует в массиве\n " +
                        "имеющем больше двух элементов(число элементов четное, а их порядок возрастающий)\n " +
                        "несколько раз, и последовательность из элементов равных искомому начинается\n " +
                        "с начала массива.");

        Array<Integer> array4 = new Array<>(Integer.class, 0);
        array4.addAll(0,10,20,30,40,40,40,70,80,90);
        Assertions.assertNotEquals(-1, array4.binarySearch(40, Integer::compare),
                "Метод binarySearch() не должен возвращать -1, если искомый элемент\n " +
                        "присутсвует в массиве имеющем больше двух элементов(число элементов\n " +
                        "четное, а их порядок возрастающий) несколько раз, и последовательность\n " +
                        "из элементов равных искомому начинается где-то с середины массива.");
        Assertions.assertEquals(40, array4.get(array4.binarySearch(40, Integer::compare)),
                "Метод binarySearch() должен возвращать индекс элемента имеющего то же значение," +
                        "что и искомый элемент, в случае - если искомый элемент присутсвует в массиве\n " +
                        "имеющем больше двух элементов(число элементов четное, а их порядок возрастающий)\n " +
                        "несколько раз, и последовательность из элементов равных искомому начинается\n " +
                        "где-то с середины массива.");

        Array<Integer> array5 = new Array<>(Integer.class, 0);
        array5.addAll(0,10,20,30,40,40,40,70,70,70);
        Assertions.assertNotEquals(-1, array5.binarySearch(70, Integer::compare),
                "Метод binarySearch() не должен возвращать -1, если искомый элемент\n " +
                        "присутсвует в массиве имеющем больше двух элементов(число элементов\n " +
                        "четное, а их порядок возрастающий) несколько раз, и последовательность\n " +
                        "из элементов равных искомому находится в конце массива.");
        Assertions.assertEquals(70, array5.get(array5.binarySearch(70, Integer::compare)),
                "Метод binarySearch() должен возвращать индекс элемента имеющего то же значение," +
                        "что и искомый элемент, в случае - если искомый элемент присутсвует в массиве\n " +
                        "имеющем больше двух элементов(число элементов четное, а их порядок возрастающий)\n " +
                        "несколько раз, и последовательность из элементов равных искомому находится\n " +
                        "в конце массива.");


        Array<Integer> array6 = new Array<>(Integer.class, 0);
        array6.addAll(0,10,20,30,40,50,60,70,80);
        Assertions.assertEquals(-1, array6.binarySearch(-10, Integer::compare),
                "Метод binarySearch() должен возвращать -1, если искомый элемент\n " +
                        "меньше наименьшего элемента в массиве имеющего больше двух элементов,\n " +
                        "при этом число элементов не четное, а их порядок возрастающий.");
        Assertions.assertEquals(-1, array6.binarySearch(90, Integer::compare),
                "Метод binarySearch() должен возвращать -1, если искомый элемент\n " +
                        "больше наибольшего элемента в массиве имеющего больше двух элементов,\n " +
                        "при этом число элементов не четное, а их порядок возрастающий.");
        Assertions.assertEquals(-1, array6.binarySearch(57, Integer::compare),
                "Метод binarySearch() должен возвращать -1, если искомый элемент\n " +
                        "отсутсвует в массиве имеющем больше двух элементов(число\n " +
                        "элементов не четное, а их порядок возрастающий), при этом искомый элемент\n " +
                        "мог бы расположиться где-то посередине.");
        Assertions.assertEquals(0, array6.binarySearch(0, Integer::compare),
                "Метод binarySearch() должен возвращать 0, если искомый элемент\n " +
                        "присутствует в единственном экземпляре и находится в начале массива,\n " +
                        "имеющем больше двух элементов(число элементов не четное, а их порядок\n" +
                        "возрастающий).");
        Assertions.assertEquals(8, array6.binarySearch(80, Integer::compare),
                "Метод binarySearch() должен возвращать последний индекс массива,\n " +
                        "если искомый элемент присутствует в единственном экземпляре и находится\n " +
                        "в конце массива, имеющем больше двух элементов(число элементов не четное,\n " +
                        "а их порядок возрастающий).");
        Assertions.assertEquals(5, array6.binarySearch(50, Integer::compare),
                "Метод binarySearch() должен правильно находить индекс искомого элемента,\n " +
                        "если тот присутсвует в единственном экземпляре в массиве имеющем больше\n " +
                        "двух элементов(число элементов не четное, а их порядок возрастающий) и\n " +
                        "находится примерно где-то посередине.");
        for(int i = 0; i < array6.getLength(); i++) {
            Assertions.assertEquals(i, array6.binarySearch(i * 10, Integer::compare),
                    "Метод binarySearch должен находить индекс каждого элемента, присутсвующего\n " +
                            "в массиве один раз, с учетом того, что число элементов массива не четно,\n " +
                            "порядок возрастающий. Элемент для которого данный метод работает неправильно,\n " +
                            "равен " + (i * 10));
        }

        Array<Integer> array7 = new Array<>(Integer.class, 0);
        array7.addAll(10,10,10,30,40,50,60,70,80);
        Assertions.assertNotEquals(-1, array7.binarySearch(10, Integer::compare),
                "Метод binarySearch() не должен возвращать -1, если искомый элемент\n " +
                        "присутсвует в массиве имеющем больше двух элементов(число элементов\n " +
                        "не четное, а их порядок возрастающий) несколько раз, и последовательность\n " +
                        "из элементов равных искомому начинается с начала массива.");
        Assertions.assertEquals(10, array7.get(array7.binarySearch(10, Integer::compare)),
                "Метод binarySearch() должен возвращать индекс элемента имеющего то же значение," +
                        "что и искомый элемент, в случае - если искомый элемент присутсвует в массиве\n " +
                        "имеющем больше двух элементов(число элементов не четное, а их порядок возрастающий)\n " +
                        "несколько раз, и последовательность из элементов равных искомому начинается\n " +
                        "с начала массива.");

        Array<Integer> array8 = new Array<>(Integer.class, 0);
        array8.addAll(0,10,20,40,40,40,60,70,80);
        Assertions.assertNotEquals(-1, array8.binarySearch(40, Integer::compare),
                "Метод binarySearch() не должен возвращать -1, если искомый элемент\n " +
                        "присутсвует в массиве имеющем больше двух элементов(число элементов\n " +
                        "не четное, а их порядок возрастающий) несколько раз, и последовательность\n " +
                        "из элементов равных искомому начинается где-то с середины массива.");
        Assertions.assertEquals(40, array8.get(array8.binarySearch(40, Integer::compare)),
                "Метод binarySearch() должен возвращать индекс элемента имеющего то же значение," +
                        "что и искомый элемент, в случае - если искомый элемент присутсвует в массиве\n " +
                        "имеющем больше двух элементов(число элементов не четное, а их порядок возрастающий)\n " +
                        "несколько раз, и последовательность из элементов равных искомому начинается\n " +
                        "где-то с середины массива.");


        Array<Integer> array9 = new Array<>(Integer.class, 0);
        array9.addAll(0,10,20,30,40,50,60,60,60);
        Assertions.assertNotEquals(-1, array9.binarySearch(60, Integer::compare),
                "Метод binarySearch() не должен возвращать -1, если искомый элемент\n " +
                        "присутсвует в массиве имеющем больше двух элементов(число элементов\n " +
                        "не четное, а их порядок возрастающий) несколько раз, и последовательность\n " +
                        "из элементов равных искомому находится в конце массива.");
        Assertions.assertEquals(60, array9.get(array9.binarySearch(60, Integer::compare)),
                "Метод binarySearch() должен возвращать индекс элемента имеющего то же значение," +
                        "что и искомый элемент, в случае - если искомый элемент присутсвует в массиве\n " +
                        "имеющем больше двух элементов(число элементов не четное, а их порядок возрастающий)\n " +
                        "несколько раз, и последовательность из элементов равных искомому находится\n " +
                        "в конце массива.");
    }

    @Test
    public void binarySearch_DescendingOrder() {
        Array<Integer> array = new Array<>(Integer.class, 0);
        array.addAll(10, 0);

        Assertions.assertEquals(-1, array.binarySearch(-1, (Integer a, Integer b) -> b - a),
                "Метод binarySearch() должен возвращать -1, если искомый элемент меньше\n " +
                        "наименьшего элемента в массиве из двух элементов отсортирванных в\n " +
                        "убывающем порядке.");
        Assertions.assertEquals(-1, array.binarySearch(11, (Integer a, Integer b) -> b - a),
                "Метод binarySearch() должен возвращать -1, если искомый элемент больше\n " +
                        "наибольшего элемента в массиве из двух элементов отсортирванных в\n " +
                        "убывающем порядке.");
        Assertions.assertEquals(-1, array.binarySearch(5, (Integer a, Integer b) -> b - a),
                "Метод binarySearch() должен возвращать -1, если искомый элемент отсутсвует\n " +
                        "в массиве из двух элементов в убывающем порядке, но мог бы поместиться," +
                        "где-то посередине.");
        Assertions.assertEquals(0, array.binarySearch(10, (Integer a, Integer b) -> b - a),
                "Метод binarySearch() должен возвращать 0, если искомый элемент находится\n " +
                        "в начеле массива из двух элементов, отсортированного в убывающем порядке.");
        Assertions.assertEquals(1, array.binarySearch(0, (Integer a, Integer b) -> b - a),
                "Метод binarySearch() должен возвращать 1, если искомый элемент находится\n " +
                        "в конце массива из двух элементов, отсортированного в убывающем порядке.");


        Array<Integer> array2 = new Array<>(Integer.class, 0);
        array2.addAll(90,80,70,60,50,40,30,20,10,0);
        Assertions.assertEquals(-1, array2.binarySearch(-10, (Integer a, Integer b) -> b - a),
                "Метод binarySearch() должен возвращать -1, если искомый элемент\n " +
                        "меньше наименьшего элемента в массиве имеющего больше двух элементов,\n " +
                        "при этом число элементов четное, а их порядок убывающий.");
        Assertions.assertEquals(-1, array2.binarySearch(100, (Integer a, Integer b) -> b - a),
                "Метод binarySearch() должен возвращать -1, если искомый элемент\n " +
                        "больше наибольшего элемента в массиве имеющего больше двух элементов,\n " +
                        "при этом число элементов четное, а их порядок убывающий.");
        Assertions.assertEquals(-1, array2.binarySearch(57, (Integer a, Integer b) -> b - a),
                "Метод binarySearch() должен возвращать -1, если искомый элемент\n " +
                        "отсутсвует в массиве имеющем больше двух элементов(число\n " +
                        "элементов четное, а их порядок убывающий), при этом искомый элемент\n " +
                        "мог бы расположиться где-то посередине.");
        Assertions.assertEquals(0, array2.binarySearch(90, (Integer a, Integer b) -> b - a),
                "Метод binarySearch() должен возвращать 0, если искомый элемент\n " +
                        "присутствует в единственном экземпляре и находится в начале массива,\n " +
                        "имеющем больше двух элементов(число элементов четное, а их порядок\n" +
                        "убывающий).");
        Assertions.assertEquals(9, array2.binarySearch(0, (Integer a, Integer b) -> b - a),
                "Метод binarySearch() должен возвращать последний индекс массива,\n " +
                        "если искомый элемент присутствует в единственном экземпляре и находится\n " +
                        "в конце массива, имеющем больше двух элементов(число элементов четное,\n " +
                        "а их порядок убывающий).");
        Assertions.assertEquals(4, array2.binarySearch(50, (Integer a, Integer b) -> b - a),
                "Метод binarySearch() должен правильно находить индекс искомого элемента,\n " +
                        "если тот присутсвует в массиве в единтсвенном экземпляре имеющем больше\n " +
                        "двух элементов(число элементов четное, а их порядок убывающий) и находится\n " +
                        "примерно где-то посередине.");
        for(int i = array2.getLength() - 1; i >= 0; i--) {
            Assertions.assertEquals(
                    array2.getLength() - 1 - i, array2.binarySearch(i * 10, (Integer a, Integer b) -> b - a),
                    "Метод binarySearch должен находить индекс каждого элемента, присутсвующего\n " +
                            "в массиве один раз, с учетом того, что число элементов массива четно,\n " +
                            "порядок убывающий. Элемент для которого данный метод работает неправильно,\n " +
                            "равен " + (i * 10));
        }

        Array<Integer> array3 = new Array<>(Integer.class, 0);
        array3.addAll(70,70,70,60,50,40,30,20,10,0);
        Assertions.assertNotEquals(-1, array3.binarySearch(70, (Integer a, Integer b) -> b - a),
                "Метод binarySearch() не должен возвращать -1, если искомый элемент\n " +
                        "присутсвует в массиве имеющем больше двух элементов(число элементов\n " +
                        "четное, а их порядок убывающий) несколько раз, и последовательность\n " +
                        "из элементов равных искомому начинается с начала массива.");
        Assertions.assertEquals(70, array3.get(array3.binarySearch(70, (Integer a, Integer b) -> b - a)),
                "Метод binarySearch() должен возвращать индекс элемента имеющего то же значение," +
                        "что и искомый элемент, в случае - если искомый элемент присутсвует в массиве\n " +
                        "имеющем больше двух элементов(число элементов четное, а их порядок убывающий)\n " +
                        "несколько раз, и последовательность из элементов равных искомому начинается\n " +
                        "с начала массива.");

        Array<Integer> array4 = new Array<>(Integer.class, 0);
        array4.addAll(90,80,70,60,50,50,50,20,10,0);
        Assertions.assertNotEquals(-1, array4.binarySearch(50, (Integer a, Integer b) -> b - a),
                "Метод binarySearch() не должен возвращать -1, если искомый элемент\n " +
                        "присутсвует в массиве имеющем больше двух элементов(число элементов\n " +
                        "четное, а их порядок убывающий) несколько раз, и последовательность\n " +
                        "из элементов равных искомому начинается где-то с середины массива.");
        Assertions.assertEquals(50, array4.get(array4.binarySearch(50, (Integer a, Integer b) -> b - a)),
                "Метод binarySearch() должен возвращать индекс элемента имеющего то же значение," +
                        "что и искомый элемент, в случае - если искомый элемент присутсвует в массиве\n " +
                        "имеющем больше двух элементов(число элементов четное, а их порядок убывающий)\n " +
                        "несколько раз, и последовательность из элементов равных искомому начинается\n " +
                        "где-то с середины массива.");

        Array<Integer> array5 = new Array<>(Integer.class, 0);
        array5.addAll(90,80,70,60,50,40,30,10,10,10);
        Assertions.assertNotEquals(-1, array5.binarySearch(10, (Integer a, Integer b) -> b - a),
                "Метод binarySearch() не должен возвращать -1, если искомый элемент\n " +
                        "присутсвует в массиве имеющем больше двух элементов(число элементов\n " +
                        "четное, а их порядок убывающий) несколько раз, и последовательность\n " +
                        "из элементов равных искомому находится в конце массива.");
        Assertions.assertEquals(10, array5.get(array5.binarySearch(10, (Integer a, Integer b) -> b - a)),
                "Метод binarySearch() должен возвращать индекс элемента имеющего то же значение," +
                        "что и искомый элемент, в случае - если искомый элемент присутсвует в массиве\n " +
                        "имеющем больше двух элементов(число элементов четное, а их порядок убывающий)\n " +
                        "несколько раз, и последовательность из элементов равных искомому находится\n " +
                        "в конце массива.");

        Array<Integer> array6 = new Array<>(Integer.class, 0);
        array6.addAll(100,90,80,70,60,50,40,30,20,10,0);
        Assertions.assertEquals(-1, array6.binarySearch(-10, (Integer a, Integer b) -> b - a),
                "Метод binarySearch() должен возвращать -1, если искомый элемент\n " +
                        "меньше наименьшего элемента в массиве имеющего больше двух элементов,\n " +
                        "при этом число элементов не четное, а их порядок убывающий.");
        Assertions.assertEquals(-1, array6.binarySearch(110, (Integer a, Integer b) -> b - a),
                "Метод binarySearch() должен возвращать -1, если искомый элемент\n " +
                        "больше наибольшего элемента в массиве имеющего больше двух элементов,\n " +
                        "при этом число элементов не четное, а их порядок убывающий.");
        Assertions.assertEquals(-1, array6.binarySearch(57, (Integer a, Integer b) -> b - a),
                "Метод binarySearch() должен возвращать -1, если искомый элемент\n " +
                        "отсутсвует в массиве имеющем больше двух элементов(число\n " +
                        "элементов не четное, а их порядок убывающий), при этом искомый элемент\n " +
                        "мог бы расположиться где-то посередине.");
        Assertions.assertEquals(0, array6.binarySearch(100, (Integer a, Integer b) -> b - a),
                "Метод binarySearch() должен возвращать 0, если искомый элемент\n " +
                        "присутствует в единственном экземпляре и находится в начале массива,\n " +
                        "имеющем больше двух элементов(число элементов не четное, а их порядок\n" +
                        "убывающий).");
        Assertions.assertEquals(10, array6.binarySearch(0, (Integer a, Integer b) -> b - a),
                "Метод binarySearch() должен возвращать последний индекс массива,\n " +
                        "если искомый элемент присутствует в единственном экземпляре и находится\n " +
                        "в конце массива, имеющем больше двух элементов(число элементов не четное,\n " +
                        "а их порядок убывающий).");
        Assertions.assertEquals(5, array6.binarySearch(50, (Integer a, Integer b) -> b - a),
                "Метод binarySearch() должен правильно находить индекс искомого элемента,\n " +
                        "если тот присутсвует в единтсвенном экземпляре в массиве имеющем больше\n " +
                        "двух элементов(число элементов не четное, а их порядок убывающий) и находится\n " +
                        "примерно где-то посередине.");
        for(int i = array6.getLength() - 1; i >= 0; i--) {
            Assertions.assertEquals(
                    array6.getLength() - 1 - i, array6.binarySearch(i * 10, (Integer a, Integer b) -> b - a),
                    "Метод binarySearch должен находить индекс каждого элемента, присутсвующего\n " +
                            "в массиве один раз, с учетом того, что число элементов массива не четно,\n " +
                            "порядок убывающий. Элемент для которого данный метод работает неправильно,\n " +
                            "равен " + (i * 10));
        }

        Array<Integer> array7 = new Array<>(Integer.class, 0);
        array7.addAll(90,90,90,70,60,50,40,30,20,10,0);
        Assertions.assertNotEquals(-1, array7.binarySearch(90, (Integer a, Integer b) -> b - a),
                "Метод binarySearch() не должен возвращать -1, если искомый элемент\n " +
                        "присутсвует в массиве имеющем больше двух элементов(число элементов\n " +
                        "не четное, а их порядок убывающий) несколько раз, и последовательность\n " +
                        "из элементов равных искомому начинается с начала массива.");
        Assertions.assertEquals(90, array7.get(array7.binarySearch(90, (Integer a, Integer b) -> b - a)),
                "Метод binarySearch() должен возвращать индекс элемента имеющего то же значение," +
                        "что и искомый элемент, в случае - если искомый элемент присутсвует в массиве\n " +
                        "имеющем больше двух элементов(число элементов не четное, а их порядок убывающий)\n " +
                        "несколько раз, и последовательность из элементов равных искомому начинается\n " +
                        "с начала массива.");

        Array<Integer> array8 = new Array<>(Integer.class, 0);
        array8.addAll(100,90,80,70,50,50,50,30,20,10,0);
        Assertions.assertNotEquals(-1, array8.binarySearch(50, (Integer a, Integer b) -> b - a),
                "Метод binarySearch() не должен возвращать -1, если искомый элемент\n " +
                        "присутсвует в массиве имеющем больше двух элементов(число элементов\n " +
                        "не четное, а их порядок убывающий) несколько раз, и последовательность\n " +
                        "из элементов равных искомому начинается где-то с середины массива.");
        Assertions.assertEquals(50, array8.get(array8.binarySearch(50, (Integer a, Integer b) -> b - a)),
                "Метод binarySearch() должен возвращать индекс элемента имеющего то же значение," +
                        "что и искомый элемент, в случае - если искомый элемент присутсвует в массиве\n " +
                        "имеющем больше двух элементов(число элементов не четное, а их порядок убывающий)\n " +
                        "несколько раз, и последовательность из элементов равных искомому начинается\n " +
                        "где-то с середины массива.");

        Array<Integer> array9 = new Array<>(Integer.class, 0);
        array9.addAll(100,90,80,70,60,50,40,30,10,10,10);
        Assertions.assertNotEquals(-1, array9.binarySearch(10, (Integer a, Integer b) -> b - a),
                "Метод binarySearch() не должен возвращать -1, если искомый элемент\n " +
                        "присутсвует в массиве имеющем больше двух элементов(число элементов\n " +
                        "не четное, а их порядок убывающий) несколько раз, и последовательность\n " +
                        "из элементов равных искомому находится в конце массива.");
        Assertions.assertEquals(10, array9.get(array9.binarySearch(10, (Integer a, Integer b) -> b - a)),
                "Метод binarySearch() должен возвращать индекс элемента имеющего то же значение," +
                        "что и искомый элемент, в случае - если искомый элемент присутсвует в массиве\n " +
                        "имеющем больше двух элементов(число элементов не четное, а их порядок убывающий)\n " +
                        "несколько раз, и последовательность из элементов равных искомому находится\n " +
                        "в конце массива.");
    }

    @Test
    public void binarySearch_AllItemsAreTheSame() {
        Array<Integer> array = new Array<>(Integer.class, 0);
        array.addAll(100,100);
        Assertions.assertNotEquals(-1, array.binarySearch(100, Integer::compare),
                "Метод binarySearch() не должен возвращать -1, если искомый элемент\n " +
                        "совпадает с каждым элементом в массиве из двух элементов.");
        Assertions.assertEquals(100, array.get(array.binarySearch(100, Integer::compare)),
                "Метод binarySearch() должен возвращать индекс элемента имеющего то же значение,\n " +
                        "что и искомый элемент, в случае - если искомый элемент совпадает с каждым\n " +
                        "элементом в массиве из двух элементов.");

        Array<Integer> array2 = new Array<>(Integer.class, 0);
        array2.addAll(100,100,100,100,100,100,100,100,100,100);
        Assertions.assertNotEquals(-1, array2.binarySearch(100, Integer::compare),
                "Метод binarySearch() не должен возвращать -1, если искомый элемент\n " +
                        "присутсвует в массиве имеющем больше двух элементов(число элементов\n " +
                        "четное), при этом все элеметы массива равны искомому.");
        Assertions.assertEquals(100, array2.get(array.binarySearch(100, Integer::compare)),
                "Метод binarySearch() должен возвращать индекс элемента имеющего то же значение," +
                        "что и искомый элемент, в случае - если искомый элемент присутсвует в массиве\n " +
                        "имеющем больше двух элементов(число элементов четное), при этом все элеметы\n " +
                        "массива равны искомому.");

        Array<Integer> array3 = new Array<>(Integer.class, 0);
        array3.addAll(100,100,100,100,100,100,100,100,100,100,100);
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
        Array<Integer> array = new Array<>(Integer.class, 0);
        array.addAll(0,1,2,3);

        array.expandTo(2);
        Assertions.assertEquals(4, array.getLength(),
                "Если метод expandTo(int newLength) вызывается со значением меньшим его текущей длины, " +
                        "то метод expandTo(int newLength) не должен изменять порядок и значение текущих " +
                        "элементов массива.");
        Assertions.assertEquals(0, array.get(0),
                "Если метод expandTo(int newLength) вызывается со значением меньшим его текущей длины, " +
                        "то метод expandTo(int newLength) не должен изменять порядок и значение текущих " +
                        "элементов массива.");
        Assertions.assertEquals(1, array.get(1),
                "Если метод expandTo(int newLength) вызывается со значением меньшим его текущей длины, " +
                        "то метод expandTo(int newLength) не должен изменять порядок и значение текущих " +
                        "элементов массива.");
        Assertions.assertEquals(2, array.get(2),
                "Если метод expandTo(int newLength) вызывается со значением меньшим его текущей длины, " +
                        "то метод expandTo(int newLength) не должен изменять порядок и значение текущих " +
                        "элементов массива.");
        Assertions.assertEquals(3, array.get(3),
                "Если метод expandTo(int newLength) вызывается со значением меньшим его текущей длины, " +
                        "то метод expandTo(int newLength) не должен изменять порядок и значение текущих " +
                        "элементов массива.");

        Assertions.assertDoesNotThrow(() -> array.expandTo(-1),
                "Вызов метода expandTo(int newLength) с отрицательным значением не должен приводить " +
                        "к исключению.");
        Assertions.assertEquals(4, array.getLength(),
                "Вызов метода expandTo(int newLength) с отрицательным значением не должен изменять " +
                        "рамзер массива.");
        Assertions.assertEquals(0, array.get(0),
                "Вызов метода expandTo(int newLength) с отрицательным значением не должен изменять " +
                        "порядок и значение текущих элементов массива.");
        Assertions.assertEquals(1, array.get(1),
                "Вызов метода expandTo(int newLength) с отрицательным значением не должен изменять " +
                        "порядок и значение текущих элементов массива.");
        Assertions.assertEquals(2, array.get(2),
                "Вызов метода expandTo(int newLength) с отрицательным значением не должен изменять " +
                        "порядок и значение текущих элементов массива.");
        Assertions.assertEquals(3, array.get(3),
                "Вызов метода expandTo(int newLength) с отрицательным значением не должен изменять " +
                        "порядок и значение текущих элементов массива.");

        array.expandTo(6);
        Assertions.assertEquals(6, array.getLength(),
                "Вызов метода expandTo(int newLength) со значением большим его текущей длины, должен " +
                        "изменить его размер на заданное значение.");
        Assertions.assertEquals(0, array.get(0),
                "Вызов метода expandTo(int newLength) со значением большим его текущей длины не должен " +
                        "изменять порядок и значение элементов массива находившихся в нем до вызова метода.");
        Assertions.assertEquals(1, array.get(1),
                "Вызов метода expandTo(int newLength) со значением большим его текущей длины не должен " +
                        "изменять порядок и значение элементов массива находившихся в нем до вызова метода.");
        Assertions.assertEquals(2, array.get(2),
                "Вызов метода expandTo(int newLength) со значением большим его текущей длины не должен " +
                        "изменять порядок и значение элементов массива находившихся в нем до вызова метода.");
        Assertions.assertEquals(3, array.get(3),
                "Вызов метода expandTo(int newLength) со значением большим его текущей длины не должен " +
                        "изменять порядок и значение элементов массива находившихся в нем до вызова метода.");
        Assertions.assertNull(array.get(4),
                "После расширения массива с помощью expandTo(int newLength) значение элементов начиная " +
                        "с индекса равного старой длине массива и до последнего элемента - должны иметь значение " +
                        "null.");
        Assertions.assertNull(array.get(4),
                "После расширения массива с помощью expandTo(int newLength) значение элементов начиная " +
                        "с индекса равного старой длине массива и до последнего элемента - должны иметь значение " +
                        "null.");
    }

    @Test
    public void compressTo() {
        Array<Integer> array = new Array<>(Integer.class, 0);
        array.addAll(0,1,2,3);

        array.compressTo(5);
        Assertions.assertEquals(4, array.getLength(),
                "Вызов метода compressTo(int newLength) со значением большим текущей длины не должен " +
                        "изменять размер массива.");
        Assertions.assertEquals(0, array.get(0),
                "Вызов метода compressTo(int newLength) со значением большим текущей длины не должен " +
                        "влиять на порядок и значение элементов массива.");
        Assertions.assertEquals(1, array.get(1),
                "Вызов метода compressTo(int newLength) со значением большим текущей длины не должен " +
                        "влиять на порядок и значение элементов массива.");
        Assertions.assertEquals(2, array.get(2),
                "Вызов метода compressTo(int newLength) со значением большим текущей длины не должен " +
                        "влиять на порядок и значение элементов массива.");
        Assertions.assertEquals(3, array.get(3),
                "Вызов метода compressTo(int newLength) со значением большим текущей длины не должен " +
                        "влиять на порядок и значение элементов массива.");

        array.compressTo(4);
        Assertions.assertEquals(4, array.getLength(),
                "Вызов метода compressTo(int newLength) со значением равным текущей длине не должен " +
                        "изменять размер массива.");
        Assertions.assertEquals(0, array.get(0),
                "Вызов метода compressTo(int newLength) со значением равным текущей длине не должен " +
                        "влиять на порядок и значение элементов массива.");
        Assertions.assertEquals(1, array.get(1),
                "Вызов метода compressTo(int newLength) со значением равным текущей длине не должен " +
                        "влиять на порядок и значение элементов массива.");
        Assertions.assertEquals(2, array.get(2),
                "Вызов метода compressTo(int newLength) со значением равным текущей длине не должен " +
                        "влиять на порядок и значение элементов массива.");
        Assertions.assertEquals(3, array.get(3),
                "Вызов метода compressTo(int newLength) со значением равным текущей длине не должен " +
                        "влиять на порядок и значение элементов массива.");

        array.compressTo(2);
        Assertions.assertEquals(2, array.getLength(),
                "Вызов метода compressTo(int newLength) со значением меньшим текущей длины должен " +
                        "изменить размер массива на заданное значение.");
        Assertions.assertEquals(0, array.get(0),
                "Вызов метода compressTo(int newLength) со значением меньшим текущей длины не должен " +
                        "влиять на порядок и значение элементов массива которых меньше заданного значения.");
        Assertions.assertEquals(1, array.get(1),
                "Вызов метода compressTo(int newLength) со значением меньшим текущей длины не должен " +
                        "влиять на порядок и значение элементов массива которых меньше заданного значения.");

        array.expandTo(4);
        Assertions.assertNull(array.get(2),
                "Вызов метода compressTo(int newLength) со значением меньшим текущей длины должен " +
                        "устанавливать значение null для всех элементов индекс которых больше или равен " +
                        "заданному значению.");
        Assertions.assertNull(array.get(3),
                "Вызов метода compressTo(int newLength) со значением меньшим текущей длины должен " +
                        "устанавливать значение null для всех элементов индекс которых больше или равен " +
                        "заданному значению.");

        Array<Integer> array2 = new Array<>(Integer.class, 0);
        array2.addAll(0,1,2,3);
        array2.compressTo(0);
        Assertions.assertEquals(0, array2.getLength(),
                "Вызов метода compressTo(int newLength) со значением раным 0 должен устанавливать размер " +
                        "массива в 0.");
        array2.expandTo(4);
        Assertions.assertNull(array2.get(0),
                "Вызов метода compressTo(int newLength) со значением раным 0 должен устанавливать значение " +
                        "null для всех элементов массива.");
        Assertions.assertNull(array2.get(1),
                "Вызов метода compressTo(int newLength) со значением раным 0 должен устанавливать значение " +
                        "null для всех элементов массива.");
        Assertions.assertNull(array2.get(2),
                "Вызов метода compressTo(int newLength) со значением раным 0 должен устанавливать значение " +
                        "null для всех элементов массива.");
        Assertions.assertNull(array2.get(3),
                "Вызов метода compressTo(int newLength) со значением раным 0 должен устанавливать значение " +
                        "null для всех элементов массива.");
    }

    @Test
    public void iterator_hasNext() {
        Array<String> array = new Array<>(String.class, 10);
        Assertions.assertTrue(array.iterator().hasNext(),
                "Для массива не нулевой длины метод hashNext() только что созданного итератора " +
                        "должен возвращать true.");

        Array<String> emptyArray = new Array<>(String.class, 0);
        Assertions.assertFalse(emptyArray.iterator().hasNext(),
                "Для массива нулевой длины метод hashNext() только что созданного итератора " +
                        "должен возвращать false.");


        Array<String> array2 = new Array<>(String.class, 10);
        array2.compressTo(0);
        Assertions.assertFalse(array2.iterator().hasNext(),
                "Для массива реазмер которого был уменьшен до 0 с помощью метода compressTo(int newLength) " +
                        "метод hashNext() только что созданного итератора должен возвращать false.");

        Array<String> array3 = new Array<>(String.class, 10);
        for(int i = array3.getLength() - 1; i >= 0; --i) array3.orderedRemove(0);
        Assertions.assertFalse(array3.iterator().hasNext(),
                "Для массива реазмер которого был уменьшен до 0 с помощью метода orderedRemove(int index) " +
                        "метод hashNext() только что созданного итератора должен возвращать false.");

        Array<String> array4 = new Array<>(String.class, 10);
        for(int i = array4.getLength() - 1; i >= 0; --i) array4.quickRemove(0);
        Assertions.assertFalse(array4.iterator().hasNext(),
                "Для массива реазмер которого был уменьшен до 0 с помощью метода quickRemove(int index) " +
                        "метод hashNext() только что созданного итератора должен возвращать false.");

        Array<String> array5 = new Array<>(String.class, 10);
        array5.clear();
        Assertions.assertFalse(array5.iterator().hasNext(),
                "Для массива реазмер которого был уменьшен до 0 с помощью метода clear() " +
                        "метод hashNext() только что созданного итератора должен возвращать false.");
    }

    @Test
    public void iterator_next() {
        Array<String> array = new Array<>(String.class, 10);
        final Iterator<String> iterator = array.iterator();
        array.set(0, "cat");
        Assertions.assertThrows(ConcurrentModificationException.class, iterator::next,
                "При любой модификации объекта Array, метод next() итератора полученного до этой" +
                        "модификации должен генерировать исключение.");

        Iterator<String> iterator2 = array.iterator();
        while(iterator2.hasNext()) iterator2.next();
        Assertions.assertThrows(NoSuchElementException.class, iterator2::next,
                "После перебора всех элементов метод next() итератора должен генерировать исключение.");

        Array<String> emptyArray = new Array<>(String.class, 0);
        Iterator<String> iterator3 = emptyArray.iterator();
        Assertions.assertThrows(NoSuchElementException.class, iterator3::next,
                "Для пустого массива метод next() итератора должен генерировать исключение.");

        Array<String> array2 = new Array<>(String.class, 10);
        array2.addAll("0", "1", "2", "3", "4");
        Array<String> expectedItems = new Array<>(String.class, 0);
        Iterator<String> iterator4 = array2.iterator();
        while(iterator4.hasNext()) expectedItems.add(iterator4.next());
        Assertions.assertEquals(array2, expectedItems,
                "Итератор должен перебирать все элементы массива в линейном порядке начиная с элемента " +
                        "с индексом 0.");
    }

    @Test
    public void forEach() {
        Array<Integer> array = new Array<>(Integer.class, 0);
        array.addAll(0,1,2,3,4,5,6,7,8,9);
        final int[] countIterations = new int[1];
        array.forEach((Integer value) -> ++countIterations[0]);
        Assertions.assertEquals(10, countIterations[0],
                "Кол-во итераций метода forEach(Consumer<T> action) должно равняться длине массива(getLength()).");

        Array<Integer> emptyArray = new Array<>(Integer.class, 0);
        emptyArray.forEach((Integer value) -> Assertions.fail(
                "Если объект Array имеет нулевую длину, то метод forEach(Consumer<T> action) не должен " +
                "выполнять ни одной итерации."));

        Array<Integer> array2 = new Array<>(Integer.class, 0);
        array2.addAll(0,1,2,3,4,5,6,7,8,9);
        Array<Integer> actualItems = new Array<>(Integer.class, 0);
        array2.forEach(actualItems::add);
        Assertions.assertEquals(array, actualItems,
                "Метод forEach(Consumer<T> action) должен перебирать все элементы массива.");

        Assertions.assertThrows(ConcurrentModificationException.class,
                () -> array.forEach((Integer value)->array.add(1000)));
    }

    @Test
    public void equals() {
        Array<Integer> array = new Array<>(Integer.class, 0);
        array.addAll(0,1,2,3,4,5,6,7,8,9);
        Array<Integer> array2 = new Array<>(Integer.class, 0);
        array2.addAll(0,1,2,3,4,5,6,7,8,9);

        Assertions.assertEquals(array, array2,
                "Не верно работает метод equals().");

        Assertions.assertEquals(array, array,
                "Не соблюдаетс свойство рефлексивности для метода equals().");
        Assertions.assertEquals(array2, array2,
                "Не соблюдаетс свойство рефлексивности для метода equals().");

        Assertions.assertEquals(array.equals(array2), array2.equals(array),
                "Не соблюдается свойство симметрисности для метода equals().");

        Array<Integer> array3 = new Array<>(Integer.class, 0);
        array3.addAll(0,1,2,3,4,5,6,7,8,9);
        array3.expandTo(1000);
        Assertions.assertNotEquals(array, array3,
                "Метод  equals() должен возвращать false для массивов разной длины.");
        Assertions.assertEquals(array3.equals(array), array.equals(array3),
                "Не соблюдается свойство симметрисности для метода equals(), в случае, когда массивы " +
                        "не равны.");

        Array<Integer> array4 = new Array<>(Integer.class, 0);
        array4.addAll(0,1,2,3,10,10,10,9,8,7);
        Assertions.assertNotEquals(array, array4,
                "Метод  equals() должен возвращать false для массивов одинаковой длины, но с разным " +
                        "набором и/или последовательностью элементов.");
        Assertions.assertEquals(array4.equals(array), array.equals(array4),
                "Не соблюдается свойство симметрисности для метода equals(), в случае, когда массивы " +
                        "не равны.");

        Array<Integer> array5 = new Array<>(Integer.class, 0);
        array5.addAll(0,1,2,3,4,5,6,7,8,9);
        Array<Integer> array6 = new Array<>(Integer.class, 0);
        array6.addAll(0,1,2,3,4,5,6,7,8,9);
        Array<Integer> array7 = new Array<>(Integer.class, 0);
        array7.addAll(0,1,2,3,4,5,6,7,8,9);
        Assertions.assertTrue(array5.equals(array6) && array6.equals(array7) && array5.equals(array7),
                "Не соблюдается свойство транзитивности для метода equals().");
    }

    @Test
    public void hashCode_Properties() {
        Array<Integer> array = new Array<>(Integer.class, 0);
        array.addAll(0,1,2,3,4,5,6,7,8,9);
        Array<Integer> array2 = new Array<>(Integer.class, 0);
        array2.addAll(0,1,2,3,4,5,6,7,8,9);
        Array<Integer> array3 = new Array<>(Integer.class, 0);
        array3.addAll(9,8,7,6,5,4,3,2,1,0);

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

}