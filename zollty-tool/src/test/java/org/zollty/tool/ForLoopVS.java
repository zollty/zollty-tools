package org.zollty.tool;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * 几种for循环效率的比较
 * 
 * @author zollty
 * @since 2020年9月8日
 */
@Ignore
public class ForLoopVS {

    int xx = Integer.MIN_VALUE;
    List<Integer> list = new ArrayList<>();

    @Before
    public void init() {
        for (int i = 0; i < 5000000; i++) {
            list.add(i);
        }
    }

    @Test
    public void test_1LinkedList_for0() {
        xx = Integer.MIN_VALUE;
        long startTime = System.currentTimeMillis();
        int len = list.size();// / 10;
        for (int i = 0; i < len; i++) {
            xx += list.get(i);
        }
        System.out.println("耗时：" + (System.currentTimeMillis() - startTime));
    }

    @Test
    public void test_1LinkedList_for1() {
        xx = Integer.MIN_VALUE;
        long startTime = System.currentTimeMillis();
        for (Integer itr : list) {
            xx += itr;
        }
        System.out.println("耗时：" + (System.currentTimeMillis() - startTime));
    }

    @Test
    public void test_3LinkedList_Iterator() {
        xx = Integer.MIN_VALUE;
        long startTime = System.currentTimeMillis();
        Iterator<Integer> iterator = list.iterator();
        while (iterator.hasNext()) {
            Integer next = iterator.next();
            xx += next;
        }
        System.out.println("耗时：" + (System.currentTimeMillis() - startTime));
    }

    @Test
    public void test_2LinkedList_forEach() {
        xx = Integer.MIN_VALUE;
        long startTime = System.currentTimeMillis();
        list.forEach(integer -> {
            xx += integer;
        });
        System.out.println("耗时：" + (System.currentTimeMillis() - startTime));
    }

    @Test
    public void test_2LinkedList_stream() {
        xx = Integer.MIN_VALUE;
        long startTime = System.currentTimeMillis();
        list.stream().forEach(integer -> {
            xx += integer;
        });
        System.out.println("耗时：" + (System.currentTimeMillis() - startTime));
    }

}
