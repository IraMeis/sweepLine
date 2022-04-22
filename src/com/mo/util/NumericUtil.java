package com.mo.util;

public class NumericUtil {

    public static int maxDen = 1000;

    /**
     * Конвертирует десятичную запись числа в рациональную,
     * например, 1.666 -> [1, 2, 3]
     *
     * Converts decimal representation of number into rational,
     * ex. 1.666 -> [1, 2, 3]
     *
     * @param value исходное число | original number
     * @param max_den максимально допустимый знаменатель | max denominator of number
     * @return long[]{integer_part, numerator, denominator}
     */
    public static long[] decimalToIntNumDen(double value, int max_den) {

        long m00 = 1;
        long m01 = 0;
        long m10 = 0;
        long m11 = 1;
        long[] number = new long[3];
        long ai;
        double x;
        int sign = value >= 0 ? 1 : -1;
        x = Math.abs(value);
        long t;

        while (m10 * (ai = (long)x) + m11 <= max_den) {
            t =m00 * ai + m01;

            m01 = m00;
            m00 = t;

            t = m10 * ai + m11;

            m11 = m10;
            m10 = t;

            if (x == (double)ai)
                break;
            // AF: division by zero
            x = 1 / (x - (double)ai);

            if (x > (double)0x7FFFFFFF)
                break;
            // AF: representation failure
        }

        if ((number[0] = (m00 / m10)) != 0) {

            number[1] = (m00 - number[0] * m10);
            number[0] *= sign;
            number[2] = m10;
            return number;
        }

        number[1]  = (sign * m00);
        number[2]  = m10;
        return number;
    }

    public static long[] decimalToIntNumDen(double value) {
        return decimalToIntNumDen(value, maxDen);
    }

    public static long[] decimalToNumDen(double value) {
        long[] number = decimalToIntNumDen(value, maxDen);

        if (number[0]==0)
            return new long[]{number[1], number[2]};

        if (number[0]<0)
            return new long[]{number[0]*number[2]-number[1], number[2]};

        return new long[]{number[0]*number[2]+number[1], number[2]};
    }

    public static String toRationalStr(double value, boolean fullRational) {

        long[] number =  NumericUtil.decimalToIntNumDen(value);

        if (number[1] == 0)
            return String.valueOf(number[0]);
        if (number[0] == 0)
            return number[1] + "/" + number[2];
        if (fullRational)
            return (number[1] + Math.abs(number[0]) * number[2]) * (number[0] >= 0 ? 1 : -1) + "/" + number[2];
        return number[0] + " " + number[1] + "/" + number[2];
    }

    public static String toRationalStr(double value) {
        return toRationalStr(value, true);
    }

}
