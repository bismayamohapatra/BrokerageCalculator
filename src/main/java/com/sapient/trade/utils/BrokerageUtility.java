package com.sapient.trade.utils;

/**
 * This is an utility class to be used across the BrokerageCalculator utility
 */
public class BrokerageUtility {

    /**
     *  Private constructor to stop instantiation
     */
    private BrokerageUtility() {
        throw new UnsupportedOperationException();
    }

    /**
     *
     * @param str
     * @return if null or empty
     */
    public static boolean isNullOrEmpty(final String str){
        return str == null || str.isEmpty();
    }

}
