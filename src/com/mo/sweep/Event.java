/*
 * Copyright (c) 2010, Bart Kiers
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 * Project      : CompGeom; a computational geometry library using
 *                arbitrary-precision arithmetic where possible,
 *                written in Java.
 * Developed by : Bart Kiers, bart@big-o.nl
 */
package com.mo.sweep;

import com.mo.obj2d.RLineSegment2D;
import com.mo.obj2d.RPoint2D;
import com.mo.util.Rational;

/**
 * <p>
 * Represents an event for sweep line algorithms. An event can be one of three
 * types: <code>{@link Type#START}</code> when a line segment
 * is inserted in the sweep line, <code>{@link Type#END}</code>
 * when a line segment should be removed from the sweep line and
 * <code>{@link Type#INTERSECTION}</code> when 2 (or more)
 * line segments should swap places in the sweep line because after the intersection
 * of the line segments, the segment that was above the other segment on the left of
 * the intersection would now "dive" below the other segment at the right of the
 * intersection.
 * </p>
 * <p>
 * Author: Bart Kiers, bart@big-o.nl <br />
 * Date: Mar 11, 2010
 * </p>
 */
public class Event implements Comparable<Event> {

    /**
     * An enum representing the type of an com.mo.sweep.Event.
     */
    public enum Type {
        START, END, INTERSECTION
    }

    /**
     * The {@link Type} of this com.mo.sweep.Event.
     */
    public final Type type;

    /**
     * The point associated with this com.mo.sweep.Event: {@link RLineSegment2D#p1}
     * in case of a START-com.mo.sweep.Event, {@link RLineSegment2D#p2} in case of
     * an END-com.mo.sweep.Event, else it will be the intersection point between two (or more)
     * line segments for an INTERSECTION-com.mo.sweep.Event.
     */
    public final RPoint2D point;

    /**
     * The line segment of this com.mo.sweep.Event. If this is an
     * {@link Type#INTERSECTION}-com.mo.sweep.Event, it will be
     * <code>null</code>.
     */
    public final RLineSegment2D segment;

    /**
     * The sweep line.
     */
    protected final SweepLine sweepLine;

    /**
     * Creates a new instance of an com.mo.sweep.Event.
     *
     * @param t the type of this com.mo.sweep.Event.
     * @param p the point of this com.mo.sweep.Event.
     * @param s the line segment of this com.mo.sweep.Event (is null when <code>t</code>
     *          is <code>{@link Type#INTERSECTION}</code>).
     * @param sl the vertical sweep line, sweeping from left to right.
     */
    protected Event(Type t, RPoint2D p, RLineSegment2D s, SweepLine sl) {
        type = t;
        point = p;
        segment = s;
        sweepLine = sl;
    }

    /**
     * <p>
     * Compares two Events by their natural order. The following rules apply:
     * </p>
     * <p>
     * <code>this</code> &lt; <code>that</code> if:
     * <ul>
     * <li>
     * <code>this</code> com.mo.sweep.Event's intersection with the sweep line is below
     * the intersection point of <code>that</code>
     * </li>
     * <li>
     * if <code>this</code> and <code>that</code> intersect at the same
     * point in the sweep line, then <code>this</code> &lt; <code>that</code>:
     * <ul>
     * <li>if <code>sweepLine.isBefore()</code> and the slope of <code>this</code>
     * segment is more than <code>that</code>'s slope</li>
     * <li>else if the slope of <code>this</code> segment is less than
     * <code>that<code>'s slope</li>
     * </ul>
     * </li>
     * <li>if their slopes are also the same, let <code>this</code> &lt; <code>that</code>
     * if <code>this</code> segment's left most point is further to the left than
     * <code>that</code> segment's left most point</li>
     * <li>and if their left most point is also the same, let
     * <code>this</code> &lt; <code>that</code> if <code>this</code> segment's right most
     * point is further to the right than <code>that</code> segment's right most point.
     * Note that this last check will always produce a unique com.mo.sweep.Event</li>
     * </ul>
     * </p>
     *
     * @param that the other com.mo.sweep.Event to compare <code>this</code> with.
     * @return -1 if <code>this</code> &lt; <code>that</code>, 1 if
     *         <code>this</code> &gt; <code>that</code> and in case
     *         <code>this</code> equals <code>that</code>, 0 is returned.
     *
     */
    @Override
    public int compareTo(Event that) {
        // Equal Events.
        if (this == that || this.equals(that)) return 0;

        // Get both intersection points.
        RPoint2D ipThis = sweepLine.intersection(this);
        RPoint2D ipThat = sweepLine.intersection(that);

        // Get the difference between the com.mo.sweep.Event's locations (y coordinate)
        // they pass through the sweep line.
        Rational deltaY = ipThis.y.subtract(ipThat.y);
        
        if (!deltaY.equals(Rational.ZERO)) {
            // The intersect the sweep line at different points, let the
            // com.mo.sweep.Event with the lower intersection come before the other.
            return deltaY.isNegative() ? -1 : 1;
        }
        else {
            Rational thisSlope = this.segment.line.slope;
            Rational thatSlope = that.segment.line.slope;

            // If the slopes differ, let the one with the com.mo.sweep.Event with the least
            // slope become before the other
            if(!thisSlope.equals(thatSlope)) {
                if(sweepLine.isBefore()) {
                    return thisSlope.isMoreThan(thatSlope) ? -1 : 1;
                } else {
                    return thisSlope.isMoreThan(thatSlope) ? 1 : -1;
                }
            }

            // Equal slopes and going through the same point in the sweep
            // line: find an arbitrary attribute that differs 'this' from that.

            // Check if the first (left most) point differ.
            Rational deltaXP1 = this.segment.p1.x.subtract(that.segment.p1.x);
            if(!deltaXP1.equals(Rational.ZERO)) {
                return deltaXP1.isNegative() ? -1 : 1;
            }

            // Their left most points are the same, then their right most
            // points must be different.
            Rational deltaXP2 = this.segment.p2.x.subtract(that.segment.p2.x);
            return deltaXP2.isNegative() ? -1 : 1;
        }
    }

    /**
     * <p>
     * Checks <code>this</code> for equality with <code>com.mo.sweep.Event that = (com.mo.sweep.Event)o;</code>.
     * Returns <code>true</code> iff: <code>this.segment.equals(that.segment)</code>
     * in case both <code>this</code> and <code>that</code> are either of type
     * <code>{@link Type#START}</code> or of type
     * <code>{@link Type#END}</code>, or <code>true</code>
     * if <code>this.point.equals(that.point)</code> in case both Events are of
     * type <code>{@link Type#INTERSECTION}</code>.
     * </p>
     *
     * @param o the other object.
     * @return <code>true</code> iff: <code>this.segment.equals(that.segment)</code>
     *         in case both <code>this</code> and <code>that</code> are either of type
     *         <code>{@link Type#START}</code> or of type
     *         <code>{@link Type#END}</code>, or <code>true</code>
     *         if <code>this.point.equals(that.point)</code> in case both Events are of
     *         type <code>{@link Type#INTERSECTION}</code>.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event that = (Event) o;
        if ((this.type == Type.INTERSECTION && that.type != Type.INTERSECTION) ||
                (this.type != Type.INTERSECTION && that.type == Type.INTERSECTION)) {
            return false;
        } else if (this.type == Type.INTERSECTION && that.type == Type.INTERSECTION) {
            return this.point.equals(that.point);
        } else {
            return this.segment.equals(that.segment);
        }
    }

    /**
     * Returns <code>point.hashCode()</code> if this.type is
     * <code>{@link Type#INTERSECTION}</code>, else
     * <code>segment.hashCode()</code>.
     *
     * @return <code>point.hashCode()</code> if this.type is
     *         <code>{@link Type#INTERSECTION}</code>, else
     *         <code>segment.hashCode()</code>.
     */
    @Override
    public int hashCode() {
        return this.type == Type.INTERSECTION ? point.hashCode() : segment.hashCode();
    }

    /**
     * Returns a String representation of this object.
     *
     * @return a String representation of this object.
     */
    @Override
    public String toString() {
        return String.format("{%s, %s, %s}", type, point, segment);
    }
}
