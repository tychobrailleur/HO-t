package core.util;

import core.model.TranslationFacility;

import java.time.Duration;
import java.util.ArrayList;

public class HumanDuration {

    private static final String DURATION_SUB_FORMAT = "%s%s";

    private long days;
    private long hours;
    private long minutes;
    private long seconds;

    public HumanDuration() {
    }

    public HumanDuration(long days, long hours, long minutes, long seconds) {
        this.days = days;
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
    }

    public long getDays() {
        return days;
    }

    public void setDays(long days) {
        this.days = days;
    }

    public long getHours() {
        return hours;
    }

    public void setHours(long hours) {
        this.hours = hours;
    }

    public long getMinutes() {
        return minutes;
    }

    public void setMinutes(long minutes) {
        this.minutes = minutes;
    }

    public long getSeconds() {
        return seconds;
    }

    public void setSeconds(long seconds) {
        this.seconds = seconds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        HumanDuration that = (HumanDuration) o;
        return days == that.days &&
                hours == that.hours &&
                minutes == that.minutes &&
                seconds == that.seconds;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(days, hours, minutes, seconds);
    }

    public static HumanDuration of(Duration duration) {
        return fromSeconds(duration.toSeconds());
    }

    public static HumanDuration fromSeconds(long duration) {
        long seconds = duration;
        final long days = seconds / 86400L;
        seconds -= days * 86400L;
        final long hours = seconds / 3600L;
        seconds -= hours * 3600L;
        final long minutes = seconds / 60L;
        seconds -= minutes * 60L;
        return HumanDuration.builder()
                .days(days)
                .hours(hours)
                .minutes(minutes)
                .seconds(seconds)
                .build();
    }

    public String toHumanString() {
        ArrayList<String> strings = new ArrayList<>();
        if (days != 0) {
            final var unit = getLanguageString("Duration.days_abbreviation");
            strings.add(String.format(DURATION_SUB_FORMAT, days, unit));
        }
        if (hours != 0) {
            final var unit = getLanguageString("Duration.hours_abbreviation");
            strings.add(String.format(DURATION_SUB_FORMAT, hours, unit));
        }
        if (minutes != 0) {
            final var unit = getLanguageString("Duration.minutes_abbreviation");
            strings.add(String.format(DURATION_SUB_FORMAT, minutes, unit));
        }
        if (seconds != 0) {
            final var unit = getLanguageString("Duration.seconds_abbreviation");
            strings.add(String.format(DURATION_SUB_FORMAT, seconds, unit));
        }
        return String.join(", ", strings);
    }

    private static String getLanguageString(String key) {
        return TranslationFacility.tr(key);
    }

    public static HumanDurationBuilder builder() {
        return new HumanDurationBuilder();
    }

    public static class HumanDurationBuilder {
        private long days;
        private long hours;
        private long minutes;
        private long seconds;

        public HumanDurationBuilder days(long days) {
            this.days = days;
            return this;
        }

        public HumanDurationBuilder hours(long hours) {
            this.hours = hours;
            return this;
        }

        public HumanDurationBuilder minutes(long minutes) {
            this.minutes = minutes;
            return this;
        }

        public HumanDurationBuilder seconds(long seconds) {
            this.seconds = seconds;
            return this;
        }

        public HumanDuration build() {
            return new HumanDuration(days, hours, minutes, seconds);
        }
    }
}
