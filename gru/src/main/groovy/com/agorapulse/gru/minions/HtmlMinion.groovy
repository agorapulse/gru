package com.agorapulse.gru.minions

import com.agorapulse.gru.Client
import groovy.transform.CompileStatic
import groovy.util.logging.Log
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.DocumentType
import org.xmlunit.builder.DiffBuilder
import org.xmlunit.builder.Input
import org.xmlunit.diff.DifferenceEvaluator
import org.xmlunit.diff.DifferenceEvaluators
import org.xmlunit.diff.Diff
import org.xmlunit.diff.Comparison
import org.xmlunit.diff.ComparisonResult

import javax.xml.transform.Source

/**
 * Minion responsible for JSON requests and responses.
 */
@Log @CompileStatic
class HtmlMinion extends AbstractContentMinion<Client> {

    final int index = HTML_MINION_INDEX

    HtmlMinion() {
        super(Client)
    }

    @SuppressWarnings('Instanceof')
    protected String normalize(String html) {
        Document document = Jsoup.parse(html ?: '')
        document.childNodes().each {
            if (it instanceof DocumentType) {
                it.remove()
            }
        }
        document.outputSettings(new Document.OutputSettings().syntax(Document.OutputSettings.Syntax.xml))
        document.outerHtml()
    }

    @SuppressWarnings('GStringExpressionWithinString')
    protected void similar(String actual, String expected) throws AssertionError {
        Source actualSource = Input.fromString(actual).build()
        Source expectedSource = Input.fromString(expected).build()
        Diff diff = DiffBuilder.compare(actualSource)
        .ignoreComments()
        .ignoreWhitespace()
        .normalizeWhitespace()
        .withTest(expectedSource)
        .withDifferenceEvaluator(DifferenceEvaluators.chain(DifferenceEvaluators.Default, new DifferenceEvaluator() {
            @Override
            ComparisonResult evaluate(Comparison comparison, ComparisonResult outcome) {
                if (outcome == ComparisonResult.DIFFERENT) {
                    if (comparison.testDetails.value == '${xml-unit.ignore}') {
                        return ComparisonResult.EQUAL
                    }
                }
                return outcome
            }
        }))
        .build()

        if (diff.hasDifferences()) {
            log.info 'ACTUAL:'
            log.info actual
            log.info 'EXPECTED:'
            log.info expected
            throw new AssertionError("HTML files are different:\n\n ${diff.toString()}")
        }
    }
}
