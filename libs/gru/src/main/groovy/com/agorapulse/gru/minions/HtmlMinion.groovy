/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2021 Agorapulse.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.agorapulse.gru.minions

import com.agorapulse.gru.Client
import groovy.transform.CompileStatic
import groovy.util.logging.Log
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.DocumentType
import org.jsoup.nodes.Entities
import org.jsoup.nodes.Node
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
        List<Node> documentNodes = []
        document.childNodes().each { Node it ->
            if (it instanceof DocumentType) {
                documentNodes.add(it as Node)
            }
        }
        documentNodes.each { it.remove() }
        document.outputSettings(new Document.OutputSettings().syntax(Document.OutputSettings.Syntax.xml).escapeMode(Entities.EscapeMode.xhtml))
        return document.outerHtml()
    }

    @SuppressWarnings('GStringExpressionWithinString')
    protected void similar(String actual, String expected) throws AssertionError {
        String entitiesDeclaration = HtmlMinion.getResourceAsStream('entities.dtd').text
        Source actualSource = Input.fromString(entitiesDeclaration + actual).build()
        Source expectedSource = Input.fromString(entitiesDeclaration + expected).build()
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
            throw new AssertionError((Object) "HTML files are different:\n\n ${diff}")
        }
    }

}
