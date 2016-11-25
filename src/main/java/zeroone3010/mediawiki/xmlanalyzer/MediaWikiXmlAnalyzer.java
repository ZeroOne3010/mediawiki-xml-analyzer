package zeroone3010.mediawiki.xmlanalyzer;

import static java.util.Comparator.comparing;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;

import zeroone3010.mediawiki.xmlanalyzer.collectors.CumulativeArticlesCollector;
import zeroone3010.mediawiki.xmlanalyzer.collectors.CumulativeEditsCollector;
import zeroone3010.mediawiki.xmlanalyzer.domain.AnalysisResult;
import zeroone3010.mediawiki.xmlanalyzer.domain.DailyDataPoints;
import zeroone3010.mediawiki.xmlanalyzer.domain.Revision;

public final class MediaWikiXmlAnalyzer {
    private static final Logger logger = Logger.getLogger("MediaWikiXmlAnalyzer");

    public static void main(final String[] args) {
        final MediaWikiXmlAnalyzer analyzer = new MediaWikiXmlAnalyzer();

        if (System.getProperty("file") == null) {
            System.out.println("\nUsage:\njava -Dfile=\"input_file.xml\" -jar name_of_the_jar_file.jar\n");
            System.exit(1);
        }
        logger.info("Started...");
        analyzer.analyze(new File(System.getProperty("file")));
    }

    AnalysisResult analyze(final File file) {
        List<Revision> parsedXml = parseXml(file);
        Collections.sort(parsedXml, comparing(Revision::getDate));
        logger.info("Sorted data...");
        return analyze(parsedXml);
    }

    AnalysisResult analyze(final List<Revision> revisions) {
        logger.info("Calculating cumulative article counts per day...");
        final List<DailyDataPoints> cumulativeArticleCounts = getCumulativeArticleCountsPerDay(revisions);

        logger.info("Calculating cumulative edit counts per day...");
        final List<DailyDataPoints> cumulativeEditsPerDay = getCumulativeEditsPerUserPerDay(revisions, true);

        logger.info("Calculating cumulative edits by user type...");
        final List<DailyDataPoints> cumulativeEditsByUserType = cumulativeEditsPerDay
                .stream()
                .map(day -> new DailyDataPoints(day.getDate(), day.getDataPoints().entrySet().stream()
                        .filter(Predicate.isEqual("Wikia").negate()).collect(Collectors.toMap(entry -> {
                            if (entry.getKey().equals("anonymous")) {
                                return "anonymous";
                            }
                            return "registered";
                        }, Entry::getValue, (a, b) -> a + b)))).collect(Collectors.toList());

        cumulativeArticleCounts.stream().forEach(ddp -> {
            final Long countMain = Optional.ofNullable(ddp.getDataPoints().get("Main")).orElse(0l);
            final Long countFile = Optional.ofNullable(ddp.getDataPoints().get("File")).orElse(0l);
            final Long countRedir = Optional.ofNullable(ddp.getDataPoints().get("Redirect")).orElse(0l);
            System.out.printf("%s\t%s\t%s\t%s\n", ddp.getDate(), countMain, countFile, countRedir);
        });

        return new AnalysisResult(cumulativeArticleCounts, cumulativeEditsByUserType, cumulativeEditsPerDay);
    }

    private List<DailyDataPoints> getCumulativeEditsPerUserPerDay(final List<Revision> revisions,
            final boolean includeAllPreviousDataPointsForEachDay) {
        return revisions.stream().collect(new CumulativeEditsCollector(includeAllPreviousDataPointsForEachDay));
    }

    private List<Revision> parseXml(final File xmlFile) {
        final InputSource source;
        final List<Revision> revisions;
        try {
            source = new InputSource(new FileInputStream(xmlFile));
            final SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
            final MediaWikiHandler handler = new MediaWikiHandler();
            saxParser.parse(source, handler);
            revisions = handler.getRevisions();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
        return revisions;
    }

    private List<DailyDataPoints> getCumulativeArticleCountsPerDay(final List<Revision> revisions) {
        return revisions.stream().collect(new CumulativeArticlesCollector());
    }
}
