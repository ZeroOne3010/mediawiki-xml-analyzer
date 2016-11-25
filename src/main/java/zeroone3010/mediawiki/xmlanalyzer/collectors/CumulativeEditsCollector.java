package zeroone3010.mediawiki.xmlanalyzer.collectors;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import zeroone3010.mediawiki.xmlanalyzer.domain.DailyDataPoints;
import zeroone3010.mediawiki.xmlanalyzer.domain.Revision;

public class CumulativeEditsCollector implements
        Collector<Revision, SortedMap<LocalDate, Map<String, Long>>, List<DailyDataPoints>> {
    final Map<String, Long> totalEditsPerUser = new HashMap<>();
    final SortedMap<LocalDate, Map<String, Long>> dailyCumulativeEditCounts = new TreeMap<>();

    private final boolean includeAllPreviousUsersForEachDay;

    public CumulativeEditsCollector(final boolean includeAllPreviousUsersForEachDay) {
        this.includeAllPreviousUsersForEachDay = includeAllPreviousUsersForEachDay;
    }
    
    @Override
    public BiConsumer<SortedMap<LocalDate, Map<String, Long>>, Revision> accumulator() {
        return (map, revision) -> {
            final String username = revision.getUsername();
            totalEditsPerUser.merge(username, 1L, (oldValue, newValue) -> oldValue + 1L);
            final LocalDate localDate = revision.getDate().toLocalDate();
            Map<String, Long> thisDay = dailyCumulativeEditCounts.get(localDate);
            if (thisDay == null) {
                thisDay = new HashMap<>();
                if (includeAllPreviousUsersForEachDay) {
                    thisDay.putAll(totalEditsPerUser);
                }
                dailyCumulativeEditCounts.put(localDate, thisDay);
            }
            thisDay.put(revision.getUsername(), totalEditsPerUser.get(username));
        };
    }

    @Override
    public Set<Characteristics> characteristics() {
        return EnumSet.noneOf(Characteristics.class);
    }

    @Override
    public BinaryOperator<SortedMap<LocalDate, Map<String, Long>>> combiner() {
        return (m1, m2) -> {
            return m1;
        };
    }

    @Override
    public Function<SortedMap<LocalDate, Map<String, Long>>, List<DailyDataPoints>> finisher() {
        return (a -> {
            final List<DailyDataPoints> result = new ArrayList<>();
            for (final Entry<LocalDate, Map<String, Long>> entry : a.entrySet()) {
                result.add(new DailyDataPoints(entry.getKey(), entry.getValue()));
            }
            return result;
        });
    }

    @Override
    public Supplier<SortedMap<LocalDate, Map<String, Long>>> supplier() {
        return () -> {
            return dailyCumulativeEditCounts;
        };
    }
}