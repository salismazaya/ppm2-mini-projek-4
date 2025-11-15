package com.example.unserakus;

import android.os.Build;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Helpers {
    public static String toTimeAgo(String isoDate) throws UnsupportedClassVersionError {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            OffsetDateTime odt = OffsetDateTime.parse(isoDate);
            Instant instant = odt.toInstant();

            LocalDateTime time = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            LocalDateTime now = LocalDateTime.now();

            long seconds = ChronoUnit.SECONDS.between(time, now);
            long minutes = ChronoUnit.MINUTES.between(time, now);
            long hours   = ChronoUnit.HOURS.between(time, now);
            long days    = ChronoUnit.DAYS.between(time, now);
            long months  = ChronoUnit.MONTHS.between(time, now);
            long years   = ChronoUnit.YEARS.between(time, now);

            // Baru saja
            if (seconds < 10) return "baru saja";
            if (seconds < 60) return seconds + " detik lalu";
            if (minutes == 1) return "1 menit lalu";
            if (minutes < 60) return minutes + " menit lalu";
            if (hours == 1) return "1 jam lalu";
            if (hours < 24) return hours + " jam lalu";
            if (days == 1) return "1 hari lalu";
            if (days < 30) return days + " hari lalu";
            if (months == 1) return "1 bulan lalu";
            if (months < 12) return months + " bulan lalu";
            if (years == 1) return "1 tahun lalu";
            if (years <= 5) return years + " tahun lalu";

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return time.format(fmt);
        }

        throw new UnsupportedClassVersionError("Versi tidak support");
    }
}