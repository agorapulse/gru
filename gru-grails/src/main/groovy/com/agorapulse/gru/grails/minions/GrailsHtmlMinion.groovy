package com.agorapulse.gru.grails.minions

import com.agorapulse.gru.Client
import com.agorapulse.gru.GruContext
import com.agorapulse.gru.Squad
import com.agorapulse.gru.grails.Grails
import com.agorapulse.gru.minions.HtmlMinion
import groovy.transform.CompileStatic
import groovy.util.logging.Log

/**
 * Minion responsible for HTML responses which is able to generate HTML from view.
 */
@Log @CompileStatic
class GrailsHtmlMinion extends HtmlMinion {

    @Override
    @SuppressWarnings('Instanceof')
    protected String readResponseText(Client client, Squad squad, GruContext context) {
        Grails grails = client as Grails
        String actualResponseText = client.response.text

        if (!actualResponseText && context.result instanceof Map) {
            return grails.unitTest.render(view: squad.ask(UrlMappingsMinion) { getActionName(grails.unitTest) }, model: context.result as Map)
        }
        return actualResponseText
    }

}
