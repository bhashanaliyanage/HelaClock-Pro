package lk.sanoj.helaclok.pro.HelaClockPro.Models;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Clock {
    String hours;
    String minutes;
    String meridiem;

    public Clock() {
        updateTime();
    }

    public Time getTime() {
        return new Time(hours, minutes, meridiem);
    }

    public void updateTime() {
        Date now = new Date();
        hours = new SimpleDateFormat("hh", Locale.US).format(now);
        minutes = new SimpleDateFormat("mm", Locale.US).format(now);
        meridiem = new SimpleDateFormat("a", Locale.US).format(now);
    }

    public void setTime(String time) {
        hours = time.substring(0, 2);
        minutes = time.substring(3, 5);
        meridiem = time.substring(6);
        Log.d("setTime", "setTime: " + hours + ":" + minutes + ":" + meridiem);
    }
}
