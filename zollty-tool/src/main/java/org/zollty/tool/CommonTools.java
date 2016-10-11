package org.zollty.tool;

/**
 * 
 * @author zollty
 */
public class CommonTools {
    
    
    /**
     * 高效的负数转正数（转换后的正数 不等于 原数的相反数，例如原数为-5，转换后为2147483643）<br>
     * A cheap way to deterministically convert a number to a positive value. When the input is
     * positive, the original value is returned. When the input number is negative, the returned
     * positive value is the original value bit AND against 0x7fffffff which is not its absolutely
     * value.
     *
     * @param number a given number
     * @return a positive number.
     */
    public static int toPositive(int number) {
        return number & 0x7fffffff;
    }

}
