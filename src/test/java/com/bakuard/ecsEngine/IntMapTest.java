package com.bakuard.ecsEngine;

import com.bakuard.ecsEngine.core.utils.IntMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

class IntMapTest {

    @Test
    public void put_get_uniqueKey() {
        IntMap<String> intMap = new IntMap<>();
        for(int i = -50000; i < 50000; i++) intMap.put(i, "Cat #" + i);
        IntMap<String> intMap2 = new IntMap<>();
        Assertions.assertNull(intMap2.put(0, "a"));
        Assertions.assertNull(intMap2.put(100000, "b"));
        Assertions.assertNull(intMap2.put(-127400709, "c"));
        Assertions.assertNull(intMap2.put(561, "d"));
        Assertions.assertNull(intMap2.put(5002, "e"));
        Assertions.assertNull(intMap2.put(17, "f"));
        Assertions.assertNull(intMap2.put(-97, "g"));
        Assertions.assertNull(intMap2.put(1, "h"));
        Assertions.assertNull(intMap2.put(Integer.MIN_VALUE, "i"));
        Assertions.assertNull(intMap2.put(Integer.MAX_VALUE, "a"));

        for(int i = -50000; i < 50000; i++) Assertions.assertEquals("Cat #" + i, intMap.get(i));
        Assertions.assertEquals("a", intMap2.get(0));
        Assertions.assertEquals("b", intMap2.get(100000));
        Assertions.assertEquals("c", intMap2.get(-127400709));
        Assertions.assertEquals("d", intMap2.get(561));
        Assertions.assertEquals("e", intMap2.get(5002));
        Assertions.assertEquals("f", intMap2.get(17));
        Assertions.assertEquals("g", intMap2.get(-97));
        Assertions.assertEquals("h", intMap2.get(1));
        Assertions.assertEquals("i", intMap2.get(Integer.MIN_VALUE));
        Assertions.assertEquals("a", intMap2.get(Integer.MAX_VALUE));
    }

    @Test
    public void put_get_duplicateKey() {
        IntMap<String> intMap = new IntMap<>();
        for(int i = 0; i < 50000; i++) Assertions.assertNull(intMap.put(i, "Cat #" + i));

        Assertions.assertNull(intMap.put(50000, "Cat#50000"));
        for(int i = 50001; i < 100000; i++)
            Assertions.assertEquals("Cat#" + (i - 1), intMap.put(50000, "Cat#" + i));

        Assertions.assertEquals("Cat#99999", intMap.get(50000));
    }

    @Test
    public void remove_existsKey() {
        IntMap<String> intMap = new IntMap<>();
        for(int i = -50000; i < 50000; i++) intMap.put(i, "Cat #" + i);
        IntMap<String> intMap2 = new IntMap<>();
        intMap2.put(0, "a");
        intMap2.put(100000, "b");
        intMap2.put(-127400709, "c");
        intMap2.put(561, "d");
        intMap2.put(5002, "e");
        intMap2.put(17, "f");
        intMap2.put(-97, "g");
        intMap2.put(1, "h");
        intMap2.put(Integer.MIN_VALUE, "i");
        intMap2.put(Integer.MAX_VALUE, "a");

        for(int i = -50000; i < 50000; i++) intMap.remove(i);
        intMap2.remove(0);
        intMap2.remove(100000);
        intMap2.remove(-127400709);
        intMap2.remove(561);
        intMap2.remove(5002);
        intMap2.remove(17);
        intMap2.remove(-97);
        intMap2.remove(1);
        intMap2.remove(Integer.MIN_VALUE);
        intMap2.remove(Integer.MAX_VALUE);

        for(int i = -50000; i < 50000; i++) Assertions.assertNull(intMap.get(i));
        Assertions.assertNull(intMap2.get(0));
        Assertions.assertNull( intMap2.get(100000));
        Assertions.assertNull(intMap2.get(-127400709));
        Assertions.assertNull(intMap2.get(561));
        Assertions.assertNull(intMap2.get(5002));
        Assertions.assertNull(intMap2.get(17));
        Assertions.assertNull(intMap2.get(-97));
        Assertions.assertNull(intMap2.get(1));
        Assertions.assertNull(intMap2.get(Integer.MIN_VALUE));
        Assertions.assertNull(intMap2.get(Integer.MAX_VALUE));
    }

    @Test
    public void remove_noExistsKey() {
        IntMap<String> intMap = new IntMap<>();
        for(int i = 0; i < 50000; i++) intMap.put(i, "Cat #" + i);

        for(int i = 50000; i < 100000; i++) Assertions.assertNull(intMap.remove(i));
        for(int i = 0; i < 50000; i++) Assertions.assertNotNull(intMap.get(i));
    }

    @Test
    public void getSize() {
        IntMap<String> intMap = new IntMap<>();
        Assertions.assertEquals(0, intMap.getSize());

        for(int i = 0; i < 10117; i++) intMap.put(i, "Cat #" + i);
        Assertions.assertEquals(10117, intMap.getSize());

        for(int i = 5000; i < 10117; i++) intMap.remove(i);
        Assertions.assertEquals(5000, intMap.getSize());

        for(int i = 5000; i < 7500; i++) intMap.put(i, "Cat #" + i);
        Assertions.assertEquals(7500, intMap.getSize());

        for(int i = 0; i < 7500; i++) intMap.remove(i);
        Assertions.assertEquals(0, intMap.getSize());
    }

    @Test
    public void isEmpty() {
        IntMap<String> intMap = new IntMap<>();
        Assertions.assertTrue(intMap.isEmpty());

        intMap.put(0, "crocodile");
        Assertions.assertFalse(intMap.isEmpty());

        intMap.remove(1);
        Assertions.assertFalse(intMap.isEmpty());

        intMap.remove(0);
        Assertions.assertTrue(intMap.isEmpty());
    }

    @Test
    public void clear() {
        IntMap<String> intMap = new IntMap<>();
        for(int i = 0; i < 50000; i++) intMap.put(i, "Cat #" + i);
        intMap.clear();

        for(int i = 0; i < 50000; i++) Assertions.assertNull(intMap.get(i));
        Assertions.assertTrue(intMap.isEmpty());
        Assertions.assertEquals(0, intMap.getSize());
    }

    @Test
    public void containsKey() {
        IntMap<String> intMap = new IntMap<>();
        for(int i = 0; i < 50000; i++) Assertions.assertFalse(intMap.containsKey(i));

        for(int i = 0; i < 50000; i++) intMap.put(i, "Cat #" + i);
        for(int i = 0; i < 50000; i++) Assertions.assertTrue(intMap.containsKey(i));

        intMap.clear();
        for(int i = 0; i < 50000; i++) Assertions.assertFalse(intMap.containsKey(i));
    }

    @Test
    public void iterator_next_notEmptyIntMap() {
        Random random = new Random(1000L);
        IntMap<Integer> intMap = new IntMap<>();
        HashSet<Integer> pattern = new HashSet<>();
        for(int i = 0; i < 50000; i++) {
            int value = (int)(random.nextDouble() * 1000000);
            intMap.put(i, value);
            pattern.add(value);
        }

        HashSet<Integer> check= new HashSet<>();
        Iterator<IntMap.Node<Integer>> iterator = intMap.iterator();
        while(iterator.hasNext()) check.add(iterator.next().getValue());

        Assertions.assertEquals(pattern, check);
        Assertions.assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    public void iterator_next_emptyIntMap() {
        IntMap<Integer> intMap = new IntMap<>();
        Iterator<IntMap.Node<Integer>> iterator = intMap.iterator();

        Assertions.assertFalse(iterator.hasNext());
        Assertions.assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    public void iterator_hasNext() {
        IntMap<String> intMap = new IntMap<>();
        Assertions.assertFalse(intMap.iterator().hasNext());

        intMap.put(0, "Cast");
        Assertions.assertTrue(intMap.iterator().hasNext());

        intMap.clear();
        Assertions.assertFalse(intMap.iterator().hasNext());
    }

    @Test
    public void fillArrayWithValues() {
        IntMap<String> intMap = new IntMap<>();
        for(int i = 0; i < 10117; i++) intMap.put(i, "Cat #" + i);
        String[] array = new String[1];
        Assertions.assertThrows(
                IllegalArgumentException.class, ()-> intMap.fillArrayWithValues(array),
                "Если размер входного массива меньше размера объекта IntMap, то должно генерироваться " +
                        "исключение IllegalArgumentException.");

        IntMap<String> intMap2 = new IntMap<>();
        intMap2.put(0, "Cat #0");
        intMap2.put(1, "Cat #1");
        intMap2.put(2, "Cat #2");
        String[] array2 = {"a", "b", "c", "Cat #1000"};
        intMap2.fillArrayWithValues(array2);
        Assertions.assertEquals(Set.of("Cat #0", "Cat #1", "Cat #2"), Set.of(Arrays.copyOfRange(array2, 0, 3)),
                "В случае, когда длина передаваемого массива больше размера IntMap, метод " +
                        "fillArrayWithValues() не верно заполняет переданный массив.");
        Assertions.assertEquals("Cat #1000", array2[3],
                "В случае, когда длина передаваемого массива больше размера IntMap, метод " +
                        "fillArrayWithValues() не должен перезаписывать значения элементов массива, индекс которых " +
                        "больше или равен размеру IntMap.");

        IntMap<String> intMap3 = new IntMap<>();
        intMap3.put(0, "Cat #0");
        intMap3.put(1, "Cat #1");
        intMap3.put(2, "Cat #2");
        String[] array3 = {"a", "b", "c"};
        intMap3.fillArrayWithValues(array3);
        Assertions.assertEquals(Set.of("Cat #0", "Cat #1", "Cat #2"), Set.of(array3),
                "В случае, когда длина передаваемого массива равна размеру IntMap, метод " +
                        "fillArrayWithValues() не верно заполняет переданный массив.");

        IntMap<String> intMap4 = new IntMap<>();
        String[] array4 = {"a", "b", "c"};
        intMap4.fillArrayWithValues(array4);
        Assertions.assertEquals(Set.of("a", "b", "c"), Set.of(array4),
                "В случае, когда IntMap пуст, а передаваемый массив не пуст, метод " +
                        "fillArrayWithValues() не должен вносить никаких изменений в передаваемый массив.");


        IntMap<String> intMap5 = new IntMap<>();
        String[] array5 = new String[0];
        Assertions.assertDoesNotThrow(()-> intMap5.fillArrayWithValues(array5),
                "Если IntMap пуст и массив имеет нулевую длину, то не должно выбрасываться исключений.");

        IntMap<String> intMap6 = new IntMap<>();
        Set<String> check6 = new HashSet<>();
        intMap6.put(0, "Cat #0");
        intMap6.put(1, "Cat #1");
        intMap6.put(2, "Cat #2");
        intMap6.put(3, "Cat #3");
        intMap6.remove(0);
        check6.add("Cat #1");
        check6.add("Cat #2");
        check6.add("Cat #3");
        String[] array6 = new String[100];
        intMap6.fillArrayWithValues(array6);
        Assertions.assertEquals(check6, Set.of(Arrays.copyOfRange(array6, 0, 3)),
                "Не верно работает метод fillArrayWithValues() у объектов IntMap из которых " +
                        "удалялись элементы.");
        for(int i = 3; i < array6.length; i++) {
            Assertions.assertNull(array6[i],
                    "Не верно работает метод fillArrayWithValues() у объектов IntMap из которых " +
                            "удалялись элементы.");
        }

        IntMap<String> intMap7 = new IntMap<>();
        String[] array7 = {"Cat #1", "Cat #2", "Cat #3", "Cat #4"};
        intMap7.fillArrayWithValues(array7);
        Assertions.assertArrayEquals(new String[]{"Cat #1", "Cat #2", "Cat #3", "Cat #4"}, array7,
                "Метод fillArrayWithValues() не должен перезаписывать значение элеметов передаваемого " +
                        "массива, индекс которых больше или равен размеру объекта IntMap.");
    }

    @Test
    public void forEach_NotEmptyIntMap() {
        IntMap<String> intMap = new IntMap<>();
        HashSet<String> pattern = new HashSet<>();
        for(int i = 0; i < 50000; i++) {
            intMap.put(i, "Cat #" + i);
            pattern.add("Cat #" + i);
        }

        HashSet<String> check= new HashSet<>();
        intMap.forEach((IntMap.Node<String> node) -> {
            check.add(node.getValue());
        });

        Assertions.assertEquals(pattern, check);
    }

    @Test
    public void forEach_EmptyIntMap() {
        IntMap<String> intMap = new IntMap<>();
        final int[] countIterations = new int[1];
        intMap.forEach((IntMap.Node<String> cat) -> ++countIterations[0]);

        Assertions.assertEquals(0, countIterations[0],
                "Если IntMap пуст, то метод forEach() не должен выполнять ни одной итерации.");

        for(int i = 0; i < 50000; i++) intMap.put(i, "Cat #" + i);
        intMap.clear();
        intMap.forEach((IntMap.Node<String> cat) -> ++countIterations[0]);

        Assertions.assertEquals(0, countIterations[0],
                "Если IntMap пуст, то метод forEach() не должен выполнять ни одной итерации.");
    }

    @Test
    public void forEach_ConcurrentModificationException() {
        IntMap<String> intMap = new IntMap<>();
        for(int i = 0; i < 50000; i++) intMap.put(i, "Cat #" + i);

        Assertions.assertThrows(
                ConcurrentModificationException.class,
                ()-> intMap.forEach((IntMap.Node<String> cat)-> {
                        int value = (int)(Math.random()*30000);
                        intMap.remove(value);
                        intMap.put(value + 1, "Cat #" + value);
                    }),
                "В случае модификации IntMap во время выполнения forEach должно генерироваться исключение."
                );
    }

}