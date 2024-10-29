package architecture.domain;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class TimeConvertor {

    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
    private static final Calendar calendar = Calendar.getInstance();

    public static int getYearFromReleaseDate(String releaseDate) {
        try {
            Date date = Objects.requireNonNull(dateFormat.parse(releaseDate));
            calendar.setTime(date);
            return calendar.get(Calendar.YEAR);
        } catch (ParseException e) {
            return 0;
        }
    }
}
