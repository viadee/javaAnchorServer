package me.kroeker.alex.anchor.jserver.anchor.h2o;

public class NoValueHandler {
    private NoValueHandler() {

    }

    public static boolean isNumberNa(Number value) {
        return Math.abs(Double.compare(value.doubleValue(), -999.0)) < 2;
    }

    public static String getNumberNa() {
        return "-999";
    }

}
