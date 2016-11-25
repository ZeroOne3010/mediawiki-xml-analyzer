package zeroone3010.mediawiki.xmlanalyzer.domain;

import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;

public class DailyDataPoints {
    private final LocalDate date;
    private final Map<String, Long> dataPoints = new TreeMap<>();

    public DailyDataPoints(final LocalDate date, final Map<String, Long> dataPoints) {
        this.date = date;
        this.dataPoints.putAll(dataPoints);
    }

    public LocalDate getDate() {
        return date;
    }

    public Map<String, Long> getDataPoints() {
        return dataPoints;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + dataPoints.hashCode();
        result = prime * result + date.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DailyDataPoints)) {
            return false;
        }
        final DailyDataPoints other = (DailyDataPoints) obj;
        return this.date.equals(other.date) && this.dataPoints.equals(other.dataPoints);
    }

    @Override
    public String toString() {
        return "DailyDataPoints [date=" + date + ", dataPoints=" + dataPoints + "]";
    }
}
