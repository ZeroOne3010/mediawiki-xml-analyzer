package zeroone3010.mediawiki.xmlanalyzer.domain;

import java.util.List;

public final class AnalysisResult {
    private final List<DailyDataPoints> articleCounts;
    private final List<DailyDataPoints> editsByUserType;
    private final List<DailyDataPoints> editsByUser;
    
    public AnalysisResult(final List<DailyDataPoints> articleCounts, 
            final List<DailyDataPoints> editsByUserType,
            final List<DailyDataPoints> editsByUser) {
                this.articleCounts = articleCounts;
                this.editsByUserType = editsByUserType;
                this.editsByUser = editsByUser;
    }

    public List<DailyDataPoints> getArticleCounts() {
        return articleCounts;
    }

    public List<DailyDataPoints> getEditsByUserType() {
        return editsByUserType;
    }

    public List<DailyDataPoints> getEditsByUser() {
        return editsByUser;
    }
}
