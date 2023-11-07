package com.tonnfccardreactnativetest.api.utils;

import java.util.Random;
import java.util.regex.Pattern;

public class StringHelper {
    private static Pattern patternHex = Pattern.compile("[0-9a-fA-F]+");
    private static Pattern patternNumeric = Pattern.compile("[0-9]+");

    private static final char[] hexDigits = { '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    private static Random random = new Random();

    public static String randomHexString(int size) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < size; i++) {
            sb.append(hexDigits[random.nextInt(hexDigits.length)]);
        }
        return sb.toString();
    }

    public static boolean isHexString(String str) {
        if (str == null || str.length() % 2 != 0) return false;
        return patternHex.matcher(str).matches();
    }

    public static boolean isNumericString(String str) {
        if (str == null) return false;
        return patternNumeric.matcher(str).matches();
    }
}
