package com.jimji.jimji.DailyBudgetTrackerPro;


import org.joda.time.*;


/**
 * Created by jimji on 31/10/2016.
 */

public class DateTracker {
    private LocalDate date;

    public DateTracker() {
        this.date = new LocalDate();
    }

    public static int daysBetweenUsingJoda (LocalDate d1, LocalDate d2){
        Days jodaDays = Days.daysBetween(d1, d2);
        int days = jodaDays.getDays();
        return days;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalDate getDate() {
        return date;
    }





    //commit date to shared preferences
    //compare date with current date
    //commit new current date
}
