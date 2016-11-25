package zeroone3010.mediawiki.xmlanalyzer.domain;

import java.time.ZonedDateTime;

public final class Revision implements Comparable<Revision> {
    private final ArticleTitle title;
    private final String username;
    private final ZonedDateTime date;
    private final boolean redirect;

    public Revision(final String title, final String namespace, final String username, final ZonedDateTime date, final boolean redirect) {
        this.title = new ArticleTitle(namespace, title);
        this.username = username;
        this.date = date;
        this.redirect = redirect;
    }

    public ArticleTitle getArticleTitle() {
        return title;
    }

    public String getUsername() {
        return username;
    }

    public ZonedDateTime getDate() {
        return date;
    }
    

    public boolean isRedirect() {
        return redirect;
    }

    @Override
    public int compareTo(final Revision other) {
        return this.date.compareTo(other.getDate());
    }
    
    public static Builder builder() {
        return new Builder();
    }
    @Override
    public String toString() {
        return "username: " + username + "; title: " + title + "; date: " + date + "; redirect: " + redirect;
    }
    
    public static class Builder {
        private String title;
        private String namespace;
        private String username;
        private ZonedDateTime date;
        private boolean redirect;

        public Builder title(final String value) {
            this.title = value;
            return this;
        }

        public Builder namespace(final String value) {
            this.namespace = value;
            return this;
        }

        public Builder username(final String value) {
            this.username = value;
            return this;
        }

        public Builder date(final ZonedDateTime value) {
            this.date = value;
            return this;
        }

        public Builder redirect(final boolean value) {
            this.redirect = value;
            return this;
        }

        public Revision build() {
            return new Revision(title, namespace, username, date, redirect);
        }
    }
}
