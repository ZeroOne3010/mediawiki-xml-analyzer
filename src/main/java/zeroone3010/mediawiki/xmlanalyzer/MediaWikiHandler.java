package zeroone3010.mediawiki.xmlanalyzer;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import zeroone3010.mediawiki.xmlanalyzer.domain.Revision;
import zeroone3010.mediawiki.xmlanalyzer.domain.Revision.Builder;

public final class MediaWikiHandler extends DefaultHandler {
    private List<Revision> revisions = new LinkedList<>();

    private static final String TIMESTAMP = "timestamp";
    private static final String USERNAME = "username";
    private static final String IP = "ip";
    private static final String TITLE = "title";
    private static final String NAMESPACE = "namespace"; // In the declaration list
    private static final String NS = "ns"; // Property of a particular page
    private static final String REDIRECT = "redirect";

    private static final String ANONYMOUS = "anonymous";

    private static final Collection<String> TAGS = new HashSet<String>() {
        {
            add(TITLE);
            add(TIMESTAMP);
            add(USERNAME);
            add(NAMESPACE);
            add(NS);
        }
    };

    private final Builder revisionBuilder = Revision.builder();
    private StringBuilder elementContents;
    private boolean interestingElement = false;
    private String namespaceKey;

    private final Map<String, String> namespaces = new HashMap<>();

    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes attributes)
            throws SAXException {
        if (TAGS.contains(qName)) {
            elementContents = new StringBuilder();
            interestingElement = true;
        } else {
            interestingElement = false;
        }
        if (NAMESPACE.equals(qName)) {
            namespaceKey = attributes.getValue("key");
        }
    }

    @Override
    public void characters(final char ch[], final int start, final int length) throws SAXException {
        if (interestingElement) {
            elementContents.append(ch, start, length);
        }
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        switch (qName) {
        case NAMESPACE: {
            final String namespaceName = elementContents.toString().isEmpty() ? "Main" : elementContents.toString();
            namespaces.put(namespaceKey, namespaceName);
            break;
        }
        case NS:
            final String namespaceId = elementContents.toString();
            final String namespaceName = Optional.ofNullable(namespaces.get(namespaceId)).orElse(namespaceId);
            revisionBuilder.namespace(namespaceName);
            break;
        case TITLE:
            revisionBuilder.title(elementContents.toString());
            break;
        case REDIRECT:
            revisionBuilder.redirect(true);
            break;
        case TIMESTAMP:
            revisionBuilder.date(ZonedDateTime.parse(elementContents));
            break;
        case USERNAME:
            revisionBuilder.username(elementContents.toString());
            revisions.add(revisionBuilder.build());
            revisionBuilder.redirect(false);
            break;
        case IP:
            revisionBuilder.username(ANONYMOUS);
            revisions.add(revisionBuilder.build());
            revisionBuilder.redirect(false);
            break;
        }
    }

    public List<Revision> getRevisions() {
        return revisions;
    }
}
