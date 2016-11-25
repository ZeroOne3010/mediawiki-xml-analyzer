package zeroone3010.mediawiki.xmlanalyzer.collectors;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import zeroone3010.mediawiki.xmlanalyzer.domain.DailyDataPoints;
import zeroone3010.mediawiki.xmlanalyzer.domain.ArticleTitle;
import zeroone3010.mediawiki.xmlanalyzer.domain.Revision;

public class CumulativeArticlesCollector implements
        Collector<Revision, SortedMap<LocalDate, Map<String, Long>>, List<DailyDataPoints>> {
    final SortedMap<LocalDate, Map<String, Long>> dailyCumulativeArticleCounts = new TreeMap<>();
    final Map<String, Set<ArticleTitle>> seenArticles = new HashMap<>();

    @Override
    public BiConsumer<SortedMap<LocalDate, Map<String, Long>>, Revision> accumulator() {
        return (map, revision) -> {
            final ArticleTitle title = revision.getArticleTitle();
            final LocalDate localDate = revision.getDate().toLocalDate();
            dailyCumulativeArticleCounts.putIfAbsent(localDate, new HashMap<>());

            String key = title.getNamespace();
            if (revision.isRedirect()) {
                key = "Redirect";
            }
            
            if (seenArticles.containsKey(key)) {
                seenArticles.get(key).add(title);
            } else {
                final Set<ArticleTitle> newSet = new HashSet<>();
                newSet.add(title);
                seenArticles.put(key, newSet);
            }
            
            seenArticles
                    .entrySet()
                    .stream()
                    .map(entry -> Collections.singletonMap(
                            entry.getKey(), (long) seenArticles.get(entry.getKey()).size()))
                    .forEach(seen -> dailyCumulativeArticleCounts.get(localDate).putAll(seen));
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
            a.entrySet().stream().forEach(entry -> result.add(new DailyDataPoints(entry.getKey(), entry.getValue())));
            return result;
        });
    }

    @Override
    public Supplier<SortedMap<LocalDate, Map<String, Long>>> supplier() {
        return () -> {
            return dailyCumulativeArticleCounts;
        };
    }
}