package lk.sanoj.helaclok.pro.HelaClockPro.Models;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Clock {
    String date;
    String datem;
    String datean;

    public Clock() {
        LocalTime now;
        DateTimeFormatter hourFormatter;
        DateTimeFormatter minuteFormatter;
        DateTimeFormatter amPmFormatter;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            now = LocalTime.now();
            hourFormatter = DateTimeFormatter.ofPattern("hh", Locale.US);
            minuteFormatter = DateTimeFormatter.ofPattern("mm", Locale.US);
            amPmFormatter = DateTimeFormatter.ofPattern("a", Locale.US);

            date = now.format(hourFormatter);
            datem = now.format(minuteFormatter);
            datean = now.format(amPmFormatter);
        }
    }

    public Time getTime() {
        return new Time(date, datem, datean);
    }
}
