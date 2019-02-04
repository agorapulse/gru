package com.agorapulse.gru.grails.minions.jsonview

/**
 * JsonViewGrailsPlugin class holder.
 */
@SuppressWarnings(['ClassForName', 'PublicMethodsBeforeNonPublicMethods'])
class JsonViewSupport {

    public static final Class JSON_VIEWS_PLUGIN_TYPE

    static {
        try {
            JSON_VIEWS_PLUGIN_TYPE = Class.forName('grails.plugin.json.view.JsonViewGrailsPlugin')
        } catch (ClassNotFoundException ignored) {
            JSON_VIEWS_PLUGIN_TYPE = null
        }
    }

    static boolean isEnabled() {
        JSON_VIEWS_PLUGIN_TYPE != null
    }

}
