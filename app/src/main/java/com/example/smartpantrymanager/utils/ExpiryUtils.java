package com.example.smartpantrymanager.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ExpiryUtils {

    private ExpiryUtils() {
        // Utility class - prevent object creation
    }

    public static String getExpiryDisplay(String expiryDate) {

        if (expiryDate == null || expiryDate.trim().isEmpty()) {
            return "Expiry: Not specified";
        }

        SimpleDateFormat dateFormat =
                new SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                );

        dateFormat.setLenient(false);

        try {

            Date expiry =
                    dateFormat.parse(expiryDate);

            if (expiry == null) {
                return "Expiry: " + expiryDate;
            }

            Calendar today =
                    Calendar.getInstance();

            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            long difference =
                    expiry.getTime()
                            - today.getTimeInMillis();

            long daysRemaining =
                    TimeUnit.MILLISECONDS.toDays(
                            difference
                    );

            if (daysRemaining < 0) {

                return "Expiry: "
                        + expiryDate
                        + " — Expired";

            } else if (daysRemaining == 0) {

                return "Expiry: "
                        + expiryDate
                        + " — Expires today";

            } else if (daysRemaining == 1) {

                return "Expiry: "
                        + expiryDate
                        + " — Expires tomorrow";

            } else if (daysRemaining <= 7) {

                return "Expiry: "
                        + expiryDate
                        + " — Expires in "
                        + daysRemaining
                        + " days";

            } else {

                return "Expiry: "
                        + expiryDate
                        + " — "
                        + daysRemaining
                        + " days remaining";
            }

        } catch (ParseException e) {

            return "Expiry: " + expiryDate;
        }
    }

    public static boolean isExpiringSoon(
            String expiryDate) {

        if (expiryDate == null
                || expiryDate.trim().isEmpty()) {

            return false;
        }

        SimpleDateFormat dateFormat =
                new SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                );

        dateFormat.setLenient(false);

        try {

            Date expiry =
                    dateFormat.parse(expiryDate);

            if (expiry == null) {
                return false;
            }

            Calendar today =
                    Calendar.getInstance();

            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            long difference =
                    expiry.getTime()
                            - today.getTimeInMillis();

            long daysRemaining =
                    TimeUnit.MILLISECONDS.toDays(
                            difference
                    );

            return daysRemaining >= 0
                    && daysRemaining <= 7;

        } catch (ParseException e) {

            return false;
        }
    }
}