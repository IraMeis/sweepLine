package com.mo.util;

import com.mo.obj2d.RLineSegment2D;
import com.mo.sweep.Event;
import com.mo.sweep.EventQueue;
import com.mo.sweep.SweepLine;

import java.util.Set;

public class Test {

    // (closed) polygon
    static Set<RLineSegment2D> segments1 = Set.of(
            new RLineSegment2D(3,0,4,8),
            new RLineSegment2D(4,8,8,5),
            new RLineSegment2D(5,2,8,5),
            new RLineSegment2D(5,2,6,2),
            new RLineSegment2D(5,1,6,2),
            new RLineSegment2D(3,0,5,1)
    );

    // horizontal, vertical, more than 2 segments through 1 point
    static Set<RLineSegment2D> segments2 = Set.of(
            new RLineSegment2D(-5.5,-5.5,5,5),
            new RLineSegment2D(-5,5,5,-5),
            new RLineSegment2D(-1,0,1,0),
            new RLineSegment2D(0,0,0,6),
            new RLineSegment2D(4,1,4,-5),
            new RLineSegment2D(-1,0,6.7,0)
    );

    static void test(Set<RLineSegment2D> segments) {
        SweepLine SL = SweepLine.perform(segments);
        System.out.println(SL.hasIntersections());
        System.out.println(SL.getIntersectionPoints());
    }

    public static void testRun (){
        test(segments1);
        test(segments2);
    }
}
