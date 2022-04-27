/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2022 Agorapulse.
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
package com.agorapulse.gru.minions;

import com.agorapulse.gru.Client;
import com.agorapulse.gru.content.ContentUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.DocumentType;
import org.jsoup.nodes.Entities;
import org.jsoup.nodes.Node;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.ComparisonResult;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.DifferenceEvaluators;

import javax.xml.transform.Source;
import java.util.ArrayList;
import java.util.List;

/**
 * Minion responsible for JSON requests and responses.
 */
public class HtmlMinion extends AbstractContentMinion<Client> {
    public HtmlMinion() {
        super(Client.class);
    }

    protected String normalize(String html) {
        Document document = Jsoup.parse(html != null ? html : "");

        List<Node> documentNodes = new ArrayList<>();
        for (Node node : document.childNodes()) {
            if (node instanceof DocumentType) {
                documentNodes.add(node);
            }
        }

        for (Node node : documentNodes) {
            node.remove();
        }

        document.outputSettings(new Document.OutputSettings().syntax(Document.OutputSettings.Syntax.xml).escapeMode(Entities.EscapeMode.xhtml));
        return document.outerHtml();
    }

    protected void similar(String actual, String expected) throws AssertionError {
        String entitiesDeclaration = ContentUtils.getText(HtmlMinion.class.getResourceAsStream("entities.dtd"));
        Source actualSource = Input.fromString(entitiesDeclaration + actual).build();
        Source expectedSource = Input.fromString(entitiesDeclaration + expected).build();
        Diff diff = DiffBuilder
            .compare(actualSource)
            .ignoreComments()
            .ignoreWhitespace()
            .normalizeWhitespace()
            .withTest(expectedSource)
            .withDifferenceEvaluator(DifferenceEvaluators.chain(DifferenceEvaluators.Default, (comparison, outcome) -> {
                if (outcome.equals(ComparisonResult.DIFFERENT)) {
                    if (comparison.getTestDetails().getValue().equals("${xml-unit.ignore}")) {
                        return ComparisonResult.EQUAL;
                    }

                }

                return outcome;
            })).build();

        if (diff.hasDifferences()) {
            System.err.println("ACTUAL:");
            System.err.println(actual);
            System.err.println("EXPECTED:");
            System.err.println(expected);
            throw new AssertionError("HTML files are different:\n\n " + diff);
        }
    }

    public final int getIndex() {
        return HTML_MINION_INDEX;
    }

}
