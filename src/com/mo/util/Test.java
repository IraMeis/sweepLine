package com.mo.util;

import com.mo.obj2d.RLineSegment2D;
import com.mo.sweep.SweepLine;

import java.util.Set;

public class Test {

    // 2 способа инициализации - множество сегментов или массив дабловых массивов (обертка над множеством)

    // (closed) polygon
    static double[][] segments1 = new double[][]{
            {3.5,0,4,8},
            {4,8,8,5},
            {5,2,8,5},
            {5,2,6,2},
            {5,1,6,2},
            {3.5,0,5,1}
    };

    // horizontal, vertical, more than 2 segments through 1 point
    static Set<RLineSegment2D> segments2 = Set.of(
            new RLineSegment2D(-5.5,-5.5,5,5),
            new RLineSegment2D(-5,5,5,-5),
            new RLineSegment2D(-1,0,1,0),
            new RLineSegment2D(0,0,0,6),
            new RLineSegment2D(4,1,4,-5),
            new RLineSegment2D(-1,0,6.7,0)
    );

    //parallel
    static Set<RLineSegment2D> segments3 = Set.of(
            new RLineSegment2D(0,5,10,5),
            new RLineSegment2D(0,1,7,1)
    );

    static void test(Set<RLineSegment2D> segments) {
        SweepLine SL = new SweepLine().perform(segments);
        System.out.println(SL.hasIntersections());
        System.out.println(SL.getIntersectionPoints());
        System.out.println(SL.getIntersections());
    }

    static void test(double[][] arr) {
        SweepLine SL = new SweepLine().perform(arr);
        System.out.println(SL.hasIntersections());
        System.out.println(SL.getIntersectionPoints());
        System.out.println(SL.getIntersections());
    }

    public static void testRun (){
        System.out.println("\n--test1--");
        test(segments1);
        System.out.println("\n--test2--");
        test(segments2);
        System.out.println("\n--test3--");
        test(segments3);
    }
}
