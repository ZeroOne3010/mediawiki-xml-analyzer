package zeroone3010.mediawiki_xml_analyzer;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import zeroone3010.mediawiki_xml_analyzer.domain.DailyDataPoints;
import zeroone3010.mediawiki_xml_analyzer.domain.Revision;

@RunWith(Enclosed.class)
public class MediaWikiXmlAnalyzerTest {
    private static final String MAIN_NAMESPACE = "Main";
    private static final String TITLE1 = "title1";
    private static final String TITLE2 = "title2";
    private static final String TITLE3 = "title3";
    private static final String TITLE4 = "title4";
    private static final String USER1 = "user1";
    private static final String USER2 = "user2";
    private static final String USER3 = "user3";
    private static final LocalDate DATE1 = LocalDate.of(2000, 1, 1);
    private static final LocalDate DATE2 = LocalDate.of(2000, 2, 1);
    private static final LocalDate DATE3 = LocalDate.of(2000, 3, 1);
    private static final LocalDate DATE4 = LocalDate.of(2000, 4, 1);
    private static final LocalDate DATE5 = LocalDate.of(2000, 5, 1);
    private static final LocalTime TIME = LocalTime.of(12, 34);
    private static final ZoneId ZONE_ID = ZoneId.systemDefault();

    protected static List<Revision> oneUserRevisions;
    protected static List<Revision> twoUserRevisions;
    protected static List<Revision> threeUserRevisions;

    @Ignore
    public static abstract class Base {
        
        protected MediaWikiXmlAnalyzer analyzer = new MediaWikiXmlAnalyzer();
        
        @Before
        public void init() {
            oneUserRevisions = new ArrayList<>();
            oneUserRevisions.add(revision(DATE1, TITLE1, USER1));
            oneUserRevisions.add(revision(DATE2, TITLE2, USER1));
            oneUserRevisions.add(revision(DATE2, TITLE2, USER1));
            oneUserRevisions.add(revision(DATE3, TITLE1, USER1));

            twoUserRevisions = new ArrayList<>();
            twoUserRevisions.add(revision(DATE1, TITLE1, USER1));
            twoUserRevisions.add(revision(DATE2, TITLE2, USER2));
            twoUserRevisions.add(revision(DATE3, TITLE3, USER1));
            twoUserRevisions.add(revision(DATE4, TITLE4, USER2));
            twoUserRevisions.add(revision(DATE5, TITLE3, USER1));
            twoUserRevisions.add(revision(DATE5, TITLE2, USER1));
            twoUserRevisions.add(revision(DATE5, TITLE1, USER2));

            threeUserRevisions = new ArrayList<>();
            threeUserRevisions.add(revision(DATE1, TITLE1, USER1));
            threeUserRevisions.add(revision(DATE2, TITLE1, USER2));
            threeUserRevisions.add(revision(DATE2, TITLE1, USER1));
            threeUserRevisions.add(revision(DATE3, TITLE1, USER3));
            threeUserRevisions.add(revision(DATE3, TITLE1, USER3));
            threeUserRevisions.add(revision(DATE4, TITLE2, USER1));
            threeUserRevisions.add(revision(DATE4, TITLE3, USER2));
        }
    }

    public static class GetCumulativeEditsPerUserPerDay extends Base {
        @Test
        public void one_user_with_all_available_datapoints() {
            final List<DailyDataPoints> expected = new ArrayList<>();
            expected.add(buildDailyDataPoints(DATE1, USER1, 1L));
            expected.add(buildDailyDataPoints(DATE2, USER1, 3L));
            expected.add(buildDailyDataPoints(DATE3, USER1, 4L));

            final List<DailyDataPoints> actual = analyzer
                    .analyze(oneUserRevisions).getEditsByUser();

            assertThat(actual, equalTo(expected));
        }

        @Test
        public void two_users_with_all_available_datapoints() {
            final List<DailyDataPoints> expected = new ArrayList<>();
            expected.add(buildDailyDataPoints(DATE1, USER1, 1L));
            expected.add(buildDailyDataPoints(DATE2, USER1, 1L, USER2, 1L));
            expected.add(buildDailyDataPoints(DATE3, USER1, 2L, USER2, 1L));
            expected.add(buildDailyDataPoints(DATE4, USER1, 2L, USER2, 2L));
            expected.add(buildDailyDataPoints(DATE5, USER1, 4L, USER2, 3L));

            final List<DailyDataPoints> actual = analyzer
                    .analyze(twoUserRevisions).getEditsByUser();

            assertThat(actual, equalTo(expected));
        }

        @Test
        public void three_users_with_all_available_datapoints() {
            final List<DailyDataPoints> expected = new ArrayList<>();
            expected.add(buildDailyDataPoints(DATE1, USER1, 1L));
            expected.add(buildDailyDataPoints(DATE2, USER1, 2L, USER2, 1L));
            expected.add(buildDailyDataPoints(DATE3, USER1, 2L, USER2, 1L, USER3, 2L));
            expected.add(buildDailyDataPoints(DATE4, USER1, 3L, USER2, 2L, USER3, 2L));

            final List<DailyDataPoints> actual = analyzer
                    .analyze(threeUserRevisions).getEditsByUser();

            assertThat(actual, equalTo(expected));
        }
    }
    
    public static class GetCumulativeArticleCountsPerDay extends Base {
        @Test
        public void two_articles() {
            final List<DailyDataPoints> expected = new ArrayList<>();
            expected.add(buildDailyDataPoints(DATE1, MAIN_NAMESPACE, 1L));
            expected.add(buildDailyDataPoints(DATE2, MAIN_NAMESPACE, 2L));
            expected.add(buildDailyDataPoints(DATE3, MAIN_NAMESPACE, 2L));

            final List<DailyDataPoints> actual = analyzer
                    .analyze(oneUserRevisions).getArticleCounts();

            assertThat(actual, equalTo(expected));
        }
        
        @Test
        public void three_articles() {
            final List<DailyDataPoints> expected = new ArrayList<>();
            expected.add(buildDailyDataPoints(DATE1, MAIN_NAMESPACE, 1L));
            expected.add(buildDailyDataPoints(DATE2, MAIN_NAMESPACE, 1L));
            expected.add(buildDailyDataPoints(DATE3, MAIN_NAMESPACE, 1L));
            expected.add(buildDailyDataPoints(DATE4, MAIN_NAMESPACE, 3L));
            
            final List<DailyDataPoints> actual = analyzer
                    .analyze(threeUserRevisions).getArticleCounts();
            
            assertThat(actual, equalTo(expected));
        }

        @Test
        public void four_articles() {
            final List<DailyDataPoints> expected = new ArrayList<>();
            expected.add(buildDailyDataPoints(DATE1, MAIN_NAMESPACE, 1L));
            expected.add(buildDailyDataPoints(DATE2, MAIN_NAMESPACE, 2L));
            expected.add(buildDailyDataPoints(DATE3, MAIN_NAMESPACE, 3L));
            expected.add(buildDailyDataPoints(DATE4, MAIN_NAMESPACE, 4L));
            expected.add(buildDailyDataPoints(DATE5, MAIN_NAMESPACE, 4L));
            
            final List<DailyDataPoints> actual = analyzer
                    .analyze(twoUserRevisions).getArticleCounts();
            
            assertThat(actual, equalTo(expected));
        }
    }

    static Revision revision(final LocalDate date, final String title, final String user) {
        return new Revision(title, MAIN_NAMESPACE, user, ZonedDateTime.of(date, TIME, ZONE_ID), false);
    }

    static DailyDataPoints buildDailyDataPoints(final LocalDate date, final Object... keysAndValues) {
        final Map<String, Long> dataPoints = new TreeMap<>();
        for (int i = 0; i < keysAndValues.length; i += 2) {
            dataPoints.put((String) keysAndValues[i], (Long) keysAndValues[i + 1]);
        }
        return new DailyDataPoints(date, dataPoints);
    }
}
