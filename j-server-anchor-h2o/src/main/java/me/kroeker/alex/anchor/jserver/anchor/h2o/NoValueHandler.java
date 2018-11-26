package me.kroeker.alex.anchor.jserver.anchor.h2o;

class NoValueHandler {

    private static final int MIN = -999;

    private NoValueHandler() {

    }

    static boolean isNumberNa(Number value) {
        final double dValue = value.doubleValue();
        double dif;
        if (dValue < 0) {
            final double max = Double.max(Math.abs(dValue), Math.abs(MIN));
            final double min = Double.min(Math.abs(dValue), Math.abs(MIN));
            dif = max - min;
        } else {
            dif = dValue - MIN;
        }
        return dif < 0.001;
    }

    static String getNumberNa() {
        return String.valueOf(MIN);
    }

}
