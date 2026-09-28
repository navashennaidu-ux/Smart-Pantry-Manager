package com.example.smartpantrymanager.utils;

import java.text.DecimalFormat;

public class QuantityFormatter {

    private static final DecimalFormat DECIMAL_FORMAT =
            new DecimalFormat("0.##");

    private QuantityFormatter() {
        // Utility class - prevent object creation
    }

    public static String format(double quantity) {
        return DECIMAL_FORMAT.format(quantity);
    }
}