package zeroone3010.mediawiki_xml_analyzer.domain;

public final class ArticleTitle {

    private final String namespace;
    private final String title;

    public ArticleTitle(final String namespace, final String title) {
        this.namespace = namespace;
        this.title = title;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((namespace == null) ? 0 : namespace.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ArticleTitle)) {
            return false;
        }
        final ArticleTitle other = (ArticleTitle) obj;
        return this.namespace.equals(other.namespace) && this.title.equals(other.title);
    }

    @Override
    public String toString() {
        return "ArticleTitle [namespace=" + namespace + ", title=" + title + "]";
    }

}
